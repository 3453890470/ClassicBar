package tfar.classicbar.client;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD: Debug production guard must be present in onDebugPlayerTick.
 *
 * RED phase: This test asserts that onDebugPlayerTick has an active
 * (non-comment) production environment guard (e.g. isProduction()).
 * The test is expected to FAIL before the guard is restored.
 *
 * GREEN phase: After restoring FMLLoader.getCurrent().isProduction(),
 * this test should PASS.
 */
class DebugProductionGuardBehaviorTest {

  private static String readEventHandlerSource() throws IOException {
    String projectDir = System.getProperty("classicbar.projectDir");
    assertNotNull(projectDir, "classicbar.projectDir test property must be set");
    Path path = Path.of(projectDir, "src/main/java/tfar/classicbar/client/EventHandler.java");
    assertTrue(Files.exists(path), "EventHandler.java must exist at " + path);
    return Files.readString(path);
  }

  @Test
  void onDebugPlayerTickMustHaveActiveProductionGuard() throws IOException {
    String source = readEventHandlerSource();
    String[] lines = source.split("\n", -1);

    boolean inMethod = false;
    boolean foundGuard = false;

    for (String line : lines) {
      String trimmed = line.trim();

      // Detect method start
      if (trimmed.contains("public static void onDebugPlayerTick")) {
        inMethod = true;
        continue;
      }

      if (!inMethod) continue;

      // Detect end of method (closing brace at proper depth)
      if (trimmed.equals("}")) {
        break;
      }

      // Check for active (non-comment) production guard
      // Must be a call to isProduction() that is NOT in a comment
      if (trimmed.contains("isProduction()") && !trimmed.startsWith("//") && !trimmed.startsWith("*")) {
        foundGuard = true;
        break;
      }
    }

    assertTrue(foundGuard,
      "onDebugPlayerTick must contain an active production guard (isProduction()) " +
      "to prevent debug effects (wither/poison/hunger etc.) from executing " +
      "in the production environment. The current guard is only a comment.");
  }

  @Test
  void fmlloaderImportMustBePresent() throws IOException {
    String source = readEventHandlerSource();

    boolean hasImport = source.contains("import net.neoforged.fml.loading.FMLLoader;");
    assertTrue(hasImport,
      "EventHandler.java must import net.neoforged.fml.loading.FMLLoader " +
      "to call FMLLoader.getCurrent().isProduction() for the debug guard.");
  }
}

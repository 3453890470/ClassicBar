package tfar.classicbar;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PureClientConvergenceTest {

  private static final Pattern CLIENT_ONLY_MOD_PATTERN = Pattern.compile("@Mod\\s*\\(\\s*value\\s*=\\s*ClassicBar\\.MODID\\s*,\\s*dist\\s*=\\s*Dist\\.CLIENT\\s*\\)");
  private static final Pattern CLIENT_SIDE_PATTERN = Pattern.compile("side=\"CLIENT\"");

  @Test
  void modEntryIsClientOnlyWithoutServerSyncBootstrap() throws IOException {
    String classicBarSource = readProjectFile("src/main/java/tfar/classicbar/ClassicBar.java");

    assertTrue(CLIENT_ONLY_MOD_PATTERN.matcher(classicBarSource).find(), "ClassicBar entry should be marked client-only");
    assertTrue(classicBarSource.contains("ClassicBarClient.init(modBus);"), "ClassicBar should bootstrap the client helper directly");
    assertFalse(classicBarSource.contains("ClassicBarNetwork"), "ClassicBar should not register optional payload sync");
    assertFalse(classicBarSource.contains("SyncHandler"), "ClassicBar should not register server tick sync");
    assertFalse(classicBarSource.contains("bootstrapClient("), "ClassicBar should not keep the reflective bootstrap path");
  }

  @Test
  void legacyOptionalServerSyncSourcesAreRemoved() {
    assertFalse(projectFile("src/main/java/tfar/classicbar/network/ClassicBarNetwork.java").toFile().exists(), "ClassicBarNetwork should be removed from active sources");
    assertFalse(projectFile("src/main/java/tfar/classicbar/network/SyncHandler.java").toFile().exists(), "SyncHandler should be removed from active sources");
    assertFalse(projectFile("src/main/java/tfar/classicbar/network/VanillaFoodDataPayload.java").toFile().exists(), "VanillaFoodDataPayload should be removed from active sources");
  }

  @Test
  void metadataDeclaresClientOnlyDependencies() throws IOException {
    String metadata = readProjectFile("src/main/resources/META-INF/neoforge.mods.toml");

    Matcher matcher = CLIENT_SIDE_PATTERN.matcher(metadata);
    int clientSideDeclarations = 0;
    while (matcher.find()) {
      clientSideDeclarations++;
    }

    assertEquals(5, clientSideDeclarations, "NeoForge metadata should declare client-only dependencies for farmersdelight, kaleidoscope_cookery, vampirism, thirst, and enigmaticlegacyplus");
    assertFalse(metadata.contains("side=\"BOTH\""), "NeoForge metadata should not keep BOTH-side dependency declarations");
  }

  private static String readProjectFile(String relativePath) throws IOException {
    return Files.readString(projectFile(relativePath));
  }

  private static Path projectFile(String relativePath) {
    String projectDir = System.getProperty("classicbar.projectDir");
    if (projectDir == null || projectDir.isBlank()) {
      throw new IllegalStateException("Missing classicbar.projectDir test property");
    }
    return Path.of(projectDir).resolve(relativePath);
  }
}

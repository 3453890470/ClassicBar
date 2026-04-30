package tfar.classicbar;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompatQuarantineBehaviorTest {

  private static final Set<String> EXPECTED_QUARANTINE_EXCLUDES = Set.of(
          "tfar/classicbar/impl/overlays/mod/**"
  );
  private static final List<String> ACTIVE_VANILLA_OVERLAYS = List.of(
          "Health",
          "Armor",
          "Absorption",
          "Hunger",
          "ArmorToughness",
          "MountHealth",
          "Air"
  );
  private static final List<String> COMPAT_SOURCE_TOKENS = List.of(
          "import tfar.classicbar.impl.overlays.mod",
          "VampirismHelper",
          "new Blood(",
          "new Thirst(",
          "new StaminaB(",
          "new Feathers("
  );
  private static final List<String> NETWORK_RESTORE_TOKENS = List.of(
          "ClassicBarNetwork",
          "SyncHandler",
          "VanillaFoodDataPayload",
          "RegisterPayloadHandlersEvent",
          "PlayerTickEvent",
          "PlayerLoggedOutEvent",
          "PacketDistributor",
          "NetworkRegistry",
          "SimpleChannel",
          "presentOnServer"
  );

  @Test
  void buildGradleKeepsCompatSourcesQuarantinedAndDocumentsTheBoundary() throws IOException {
    String buildGradle = readProjectFile("build.gradle");

    assertEquals(EXPECTED_QUARANTINE_EXCLUDES, extractSourceSetExcludes(buildGradle), "Active compile should keep mod overlay sources quarantined");
    assertTrue(buildGradle.contains("TASK-04 compat quarantine"), "build.gradle should document that the quarantine is an intentional TASK-04 boundary");
    assertTrue(buildGradle.contains("separate recovery task"), "build.gradle should document that each compat restore remains a separate recovery task");
  }

  @Test
  void eventHandlerRegistersOnlyVanillaOverlaysAndMutationWouldExposeCompatRestore() throws IOException {
    String eventHandler = readProjectFile("src/main/java/tfar/classicbar/client/EventHandler.java");

    for (String overlayClass : ACTIVE_VANILLA_OVERLAYS) {
      assertTrue(eventHandler.contains("new " + overlayClass + "()"), "Active EventHandler should keep " + overlayClass + " in the vanilla overlay registry");
    }

    List<String> hits = findForbiddenTokens(eventHandler, COMPAT_SOURCE_TOKENS);
    assertTrue(hits.isEmpty(), "Active EventHandler should not import or instantiate quarantined compat overlays: " + hits);

    String brokenEventHandler = eventHandler + "\nregisterAll(new Blood());\n";
    assertTrue(findForbiddenTokens(brokenEventHandler, COMPAT_SOURCE_TOKENS).contains("new Blood("), "Compat verifier should fail if a quarantined overlay is re-registered");
  }

  @Test
  void activeSourcesDoNotReferenceQuarantinedCompatOrNetworkRestores() throws IOException {
    List<String> violations = new ArrayList<>();
    Path sourceRoot = projectFile("src/main/java");

    try (var files = Files.walk(sourceRoot)) {
      for (Path file : files.filter(path -> path.toString().endsWith(".java")).toList()) {
        String relativePath = sourceRoot.relativize(file).toString().replace('\\', '/');
        if (isQuarantinedSource(relativePath)) {
          continue;
        }

        String text = Files.readString(file);
        for (String token : COMPAT_SOURCE_TOKENS) {
          if (text.contains(token)) {
            violations.add(relativePath + " -> compat token " + token);
          }
        }
        for (String token : NETWORK_RESTORE_TOKENS) {
          if (text.contains(token)) {
            violations.add(relativePath + " -> network token " + token);
          }
        }
      }
    }

    assertTrue(violations.isEmpty(), "Active sources should stay on the pure-client vanilla path without compat/network restores: " + violations);
  }

  @Test
  void metadataDoesNotClaimThirdPartyCompatSupport() throws IOException {
    String metadata = readProjectFile("src/main/resources/META-INF/neoforge.mods.toml");

    assertTrue(validateThirdPartyDependencyBlocks(metadata).isEmpty(), "Metadata should not declare third-party compat as restored or required");

    String brokenMetadata = metadata + "\n[[dependencies.classicbar]]\nmodId=\"parcool\"\ntype=\"optional\"\nversionRange=\"[1,)\"\nordering=\"NONE\"\nside=\"CLIENT\"\n";
    assertFalse(validateThirdPartyDependencyBlocks(brokenMetadata).isEmpty(), "Dependency validation should fail if any quarantined compat dependency block is added");
  }

  @Test
  void modSupportMessagingKeepsCompatReservedAndDocumentsFeathersGap() throws IOException {
    String enUs = readProjectFile("src/main/resources/assets/classicbar/lang/en_us.json");
    String zhCn = readProjectFile("src/main/resources/assets/classicbar/lang/zh_cn.json");

    assertFalse(enUs.contains("TASK-03") || enUs.contains("TASK-04"), "en_us should not leak internal task labels into user-visible lang text");
    assertFalse(zhCn.contains("TASK-03") || zhCn.contains("TASK-04"), "zh_cn should not leak internal task labels into user-visible lang text");
    assertTrue(enUs.contains("Reserved for future compatibility work. No active effect in this build."), "en_us should use stable reserved compat messaging");
    assertTrue(zhCn.contains("为未来兼容功能预留。本版本中没有实际效果。"), "zh_cn should use stable reserved compat messaging");
    assertTrue(enUs.contains("Reserved compatibility section for Tough As Nails. No active effect in this build."), "en_us should keep Tough As Nails reserved without task labels");
    assertTrue(enUs.contains("Reserved compatibility section for Vampirism. No active effect in this build."), "en_us should keep Vampirism reserved without task labels");
    assertTrue(enUs.contains("Reserved compatibility section for ParCool. No active effect in this build."), "en_us should keep ParCool reserved without task labels");
    assertTrue(enUs.contains("no compatible NeoForge 1.21.1 build is currently available"), "en_us should explain why Feathers stays unavailable in this build");
    assertTrue(enUs.contains("Enabling this toggle does not restore Tough As Nails support in this build."), "en_us should explain that reserved toggles stay inactive in this build");
    assertTrue(zhCn.contains("为 Tough As Nails 兼容功能预留。本版本中没有实际效果。"), "zh_cn should keep Tough As Nails reserved without task labels");
    assertTrue(zhCn.contains("为 Vampirism 兼容功能预留。本版本中没有实际效果。"), "zh_cn should keep Vampirism reserved without task labels");
    assertTrue(zhCn.contains("为 ParCool 兼容功能预留。本版本中没有实际效果。"), "zh_cn should keep ParCool reserved without task labels");
    assertTrue(zhCn.contains("目前没有兼容的 NeoForge 1.21.1 构件"), "zh_cn should explain why Feathers stays unavailable in this build");
    assertTrue(zhCn.contains("启用此开关也不会在当前版本中恢复 Tough As Nails 支持。"), "zh_cn should explain that reserved toggles stay inactive in this build");

    String brokenEnUs = enUs.replace("No active effect in this build.", "No active effect yet.");
    assertFalse(collectModSupportMessagingFailures(brokenEnUs, zhCn).isEmpty(), "Messaging verifier should fail if stable reserved compat wording regresses");
  }

  private static Set<String> extractSourceSetExcludes(String buildGradle) {
    Matcher matcher = Pattern.compile("exclude '([^']+)'").matcher(buildGradle);
    Set<String> excludes = new LinkedHashSet<>();
    while (matcher.find()) {
      excludes.add(matcher.group(1));
    }
    return excludes;
  }

  private static Set<String> findMissingQuarantineExcludes(String buildGradle) {
    Set<String> missing = new LinkedHashSet<>(EXPECTED_QUARANTINE_EXCLUDES);
    missing.removeAll(extractSourceSetExcludes(buildGradle));
    return missing;
  }

  private static List<String> findForbiddenTokens(String text, List<String> tokens) {
    List<String> hits = new ArrayList<>();
    for (String token : tokens) {
      if (text.contains(token)) {
        hits.add(token);
      }
    }
    return hits;
  }

  private static boolean isQuarantinedSource(String relativePath) {
    return relativePath.startsWith("tfar/classicbar/impl/overlays/mod/");
  }

  private static List<String> validateThirdPartyDependencyBlocks(String metadata) {
    List<String> failures = new ArrayList<>();
    Matcher matcher = Pattern.compile("\\[\\[dependencies\\.classicbar\\]\\](.*?)(?=\\n\\[\\[dependencies\\.classicbar\\]\\]|\\z)", Pattern.DOTALL).matcher(metadata);
    while (matcher.find()) {
      String block = matcher.group(1);
      for (String modId : List.of("toughasnails", "vampirism", "parcool", "feathers")) {
        if (block.contains("modId=\"" + modId + "\"")) {
          failures.add(modId + " dependency block must stay absent during the first-pass compat quarantine");
        }
      }
    }
    return failures;
  }

  private static List<String> collectModSupportMessagingFailures(String enUs, String zhCn) {
    List<String> failures = new ArrayList<>();
    if (enUs.contains("TASK-03") || enUs.contains("TASK-04")) {
      failures.add("en_us leaked internal task labels");
    }
    if (!enUs.contains("Reserved compatibility section for Tough As Nails. No active effect in this build.")) {
      failures.add("missing en_us stable Tough As Nails wording");
    }
    if (!enUs.contains("no compatible NeoForge 1.21.1 build is currently available")) {
      failures.add("missing en_us Feathers build gap wording");
    }
    if (zhCn.contains("TASK-03") || zhCn.contains("TASK-04")) {
      failures.add("zh_cn leaked internal task labels");
    }
    if (!zhCn.contains("为 Tough As Nails 兼容功能预留。本版本中没有实际效果。")) {
      failures.add("missing zh_cn stable Tough As Nails wording");
    }
    if (!zhCn.contains("目前没有兼容的 NeoForge 1.21.1 构件")) {
      failures.add("missing zh_cn Feathers build gap wording");
    }
    return failures;
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

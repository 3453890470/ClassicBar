package tfar.classicbar;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IndependentIconResourcesTest {

  private static final byte[] PNG_SIGNATURE = new byte[]{(byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n'};

  private static final Map<String, String> ACTIVE_BAR_CONSTANTS = new LinkedHashMap<>();
  private static final Map<String, String> COMPAT_BAR_CONSTANTS = new LinkedHashMap<>();
  private static final List<String> REQUIRED_ACTIVE_ICON_FILES = List.of(
          "health",
          "armor",
          "absorption",
          "food",
          "armor_toughness",
          "mount_health",
          "air",
          "fallback"
  );
  private static final List<String> FORBIDDEN_ICON_REFERENCES = List.of(
          "minecraft:",
          "textures/gui/icons.png",
          "InventoryMenu",
          "TextureAtlas",
          "graphics.blit(",
          "drawTexturedModalRect"
  );
  private static final Map<String, String> ACTIVE_VANILLA_OVERLAY_FILES = Map.of(
          "health", "src/main/java/tfar/classicbar/impl/overlays/vanilla/Health.java",
          "armor", "src/main/java/tfar/classicbar/impl/overlays/vanilla/Armor.java",
          "absorption", "src/main/java/tfar/classicbar/impl/overlays/vanilla/Absorption.java",
          "food", "src/main/java/tfar/classicbar/impl/overlays/vanilla/Hunger.java",
          "armor_toughness", "src/main/java/tfar/classicbar/impl/overlays/vanilla/ArmorToughness.java",
          "health_mount", "src/main/java/tfar/classicbar/impl/overlays/vanilla/MountHealth.java",
          "air", "src/main/java/tfar/classicbar/impl/overlays/vanilla/Air.java"
  );

  static {
    ACTIVE_BAR_CONSTANTS.put("health", "HEALTH");
    ACTIVE_BAR_CONSTANTS.put("armor", "ARMOR");
    ACTIVE_BAR_CONSTANTS.put("absorption", "ABSORPTION");
    ACTIVE_BAR_CONSTANTS.put("food", "FOOD");
    ACTIVE_BAR_CONSTANTS.put("armor_toughness", "ARMOR_TOUGHNESS");
    ACTIVE_BAR_CONSTANTS.put("health_mount", "MOUNT_HEALTH");
    ACTIVE_BAR_CONSTANTS.put("air", "AIR");

    COMPAT_BAR_CONSTANTS.put("blood", "BLOOD");
    COMPAT_BAR_CONSTANTS.put("thirst", "THIRST");
    COMPAT_BAR_CONSTANTS.put("stamina", "STAMINA");
    COMPAT_BAR_CONSTANTS.put("feathers", "FEATHERS");
  }

  @Test
  void activeVanillaDefaultsUseStableClassicBarIcons() throws IOException {
    String barIcons = readProjectFile("src/main/java/tfar/classicbar/resources/BarIcons.java");
    String classicBarsConfig = readProjectFile("src/main/java/tfar/classicbar/config/ClassicBarsConfig.java");

    assertTrue(barIcons.contains("textures/gui/icons/\" + name + \".png\""), "BarIcons should build stable ClassicBar icon paths");
    assertTrue(classicBarsConfig.contains("ClassicBar.MODID.equals(resourceLocation.getNamespace())"), "Config validator should require the ClassicBar namespace");
    assertTrue(classicBarsConfig.contains("resourceLocation.getPath().startsWith(\"textures/gui/icons/\")"), "Config validator should require the independent icon directory");
    assertTrue(classicBarsConfig.contains("resourceLocation.getPath().endsWith(\".png\")"), "Config validator should require PNG icon files");

    for (Map.Entry<String, String> entry : ACTIVE_BAR_CONSTANTS.entrySet()) {
      String overlayName = entry.getKey();
      String constant = entry.getValue();
      assertTrue(barIcons.contains("public static final ResourceLocation " + constant), "BarIcons should expose a dedicated icon constant for " + overlayName);
      assertTrue(classicBarsConfig.contains("BarIcons." + constant), "ClassicBarsConfig should default " + overlayName + " to a ClassicBar icon resource");
    }
  }

  @Test
  void requiredIndependentIconFilesExistAndAreValidPngs() throws IOException {
    List<String> failures = verifyIconFiles(projectIconDirectory(), REQUIRED_ACTIVE_ICON_FILES);
    assertNoFailures(failures);
  }

  @Test
  void retainedCompatFallbackIconsAlsoHaveStablePngPlaceholders() throws IOException {
    String barIcons = readProjectFile("src/main/java/tfar/classicbar/resources/BarIcons.java");
    String classicBarsConfig = readProjectFile("src/main/java/tfar/classicbar/config/ClassicBarsConfig.java");
    List<String> requiredCompatIcons = new ArrayList<>();

    for (Map.Entry<String, String> entry : COMPAT_BAR_CONSTANTS.entrySet()) {
      String overlayName = entry.getKey();
      String constant = entry.getValue();
      if (barIcons.contains("case \"" + overlayName + "\"") || classicBarsConfig.contains("BarIcons." + constant)) {
        requiredCompatIcons.add(overlayName);
      }
    }

    List<String> failures = verifyIconFiles(projectIconDirectory(), requiredCompatIcons);
    assertNoFailures(failures);
  }

  @Test
  void activeVanillaIconRenderingAvoidsAtlasAndHardcodedUvPaths() throws IOException {
    String buildGradle = readProjectFile("build.gradle");
    String modUtils = readProjectFile("src/main/java/tfar/classicbar/util/ModUtils.java");
    String barOverlay = readProjectFile("src/main/java/tfar/classicbar/api/BarOverlay.java");
    String barOverlayImpl = readProjectFile("src/main/java/tfar/classicbar/impl/BarOverlayImpl.java");

    assertTrue(buildGradle.contains("Each remaining integration stays isolated until a separate recovery task proves the NeoForge port."), "mod overlay quarantine documentation should remain in build.gradle");
    assertTrue(modUtils.contains("CURRENT_TEXTURE = BarIcons.FALLBACK"), "ModUtils should start from the ClassicBar fallback icon");
    assertTrue(modUtils.contains("drawStandaloneIcon"), "ModUtils should expose the standalone icon drawing helper");
    assertTrue(barOverlay.contains("ModUtils.CURRENT_TEXTURE = getIconRL();"), "BarOverlay should bind icon textures from the bar resource id");
    assertTrue(barOverlayImpl.contains("\"textures/gui/health.png\""), "bar strip reuse should remain allowed for ClassicBar-owned bar textures");
    assertTrue(Pattern.compile("if \\(ConfigCache\\.icons\\) \\{\\s*Color\\.reset\\(\\);\\s*bindIconTexture\\(\\);\\s*renderIcon\\(", Pattern.DOTALL).matcher(barOverlayImpl).find(),
            "BarOverlayImpl should reset color before drawing independent icons");

    for (Map.Entry<String, String> entry : ACTIVE_VANILLA_OVERLAY_FILES.entrySet()) {
      String overlayName = entry.getKey();
      String overlaySource = readProjectFile(entry.getValue());
      String renderIconBody = extractMethodBody(overlaySource, "public void renderIcon");

      assertFalse(renderIconBody.isBlank(), overlayName + " should still define renderIcon");
      boolean usesDirectCall = renderIconBody.contains("ModUtils.drawStandaloneIcon");
      boolean usesWrappedCall = renderIconBody.contains("ModUtils.drawIconWithTexture");
      boolean usesFlashCall = renderIconBody.contains("ModUtils.drawIconWithFlash");
      assertTrue(usesDirectCall || usesWrappedCall || usesFlashCall, overlayName + " should draw the standalone ClassicBar icon texture");

      for (String forbiddenReference : FORBIDDEN_ICON_REFERENCES) {
        assertFalse(renderIconBody.contains(forbiddenReference), overlayName + " renderIcon should not use " + forbiddenReference);
      }
    }
  }

  @Test
  void verifierWouldFailIfAStablePlaceholderWasTemporarilyBroken(@TempDir Path tempDir) throws IOException {
    Path iconDir = tempDir.resolve("icons");
    Files.createDirectories(iconDir);
    for (String name : REQUIRED_ACTIVE_ICON_FILES) {
      Files.copy(projectIconFile(name), iconDir.resolve(name + ".png"));
    }

    Files.delete(iconDir.resolve("fallback.png"));

    List<String> failures = verifyIconFiles(iconDir, REQUIRED_ACTIVE_ICON_FILES);
    assertTrue(failures.stream().anyMatch(message -> message.contains("fallback.png")), "Verifier should fail when a required stable placeholder disappears");
  }

  @Test
  void verifierRejectsWrongSizedPlaceholder(@TempDir Path tempDir) throws IOException {
    Path iconDir = tempDir.resolve("icons");
    Files.createDirectories(iconDir);
    writeArgbPlaceholder(iconDir.resolve("fallback.png"), 8, 9, 0x00000000, 0xFFFFFFFF);

    List<String> failures = verifyIconFiles(iconDir, List.of("fallback"));
    assertTrue(failures.stream().anyMatch(message -> message.contains("9x9")), "Verifier should reject wrong-size placeholders: " + failures);
  }

  @Test
  void verifierRejectsPlaceholderWithoutAlphaChannel(@TempDir Path tempDir) throws IOException {
    Path iconDir = tempDir.resolve("icons");
    Files.createDirectories(iconDir);
    writeRgbPlaceholder(iconDir.resolve("fallback.png"), 9, 9, 0x000000, 0xFFFFFF);

    List<String> failures = verifyIconFiles(iconDir, List.of("fallback"));
    assertTrue(failures.stream().anyMatch(message -> message.contains("alpha")), "Verifier should reject placeholders without an alpha channel: " + failures);
  }

  @Test
  void verifierRejectsAllOpaquePlaceholder(@TempDir Path tempDir) throws IOException {
    Path iconDir = tempDir.resolve("icons");
    Files.createDirectories(iconDir);
    writeArgbPlaceholder(iconDir.resolve("fallback.png"), 9, 9, 0xFF202020, 0xFFFFFFFF);

    List<String> failures = verifyIconFiles(iconDir, List.of("fallback"));
    assertTrue(failures.stream().anyMatch(message -> message.contains("transparent pixel")), "Verifier should reject placeholders that never become transparent: " + failures);
  }

  @Test
  void verifierRejectsAllTransparentPlaceholder(@TempDir Path tempDir) throws IOException {
    Path iconDir = tempDir.resolve("icons");
    Files.createDirectories(iconDir);
    writeArgbPlaceholder(iconDir.resolve("fallback.png"), 9, 9, 0x00000000, 0x00000000);

    List<String> failures = verifyIconFiles(iconDir, List.of("fallback"));
    assertTrue(failures.stream().anyMatch(message -> message.contains("non-transparent pixel")), "Verifier should reject placeholders that never become visible: " + failures);
  }

  private static List<String> verifyIconFiles(Path iconDirectory, List<String> iconNames) throws IOException {
    List<String> failures = new ArrayList<>();
    for (String iconName : iconNames) {
      Path iconFile = iconDirectory.resolve(iconName + ".png");
      if (!Files.exists(iconFile)) {
        failures.add("Missing icon file: " + iconFile);
        continue;
      }

      byte[] bytes = Files.readAllBytes(iconFile);
      if (bytes.length == 0) {
        failures.add("Empty icon file: " + iconFile);
        continue;
      }

      if (bytes.length < PNG_SIGNATURE.length) {
        failures.add("Truncated PNG file: " + iconFile);
        continue;
      }

      if (!Arrays.equals(Arrays.copyOf(bytes, PNG_SIGNATURE.length), PNG_SIGNATURE)) {
        failures.add("Invalid PNG signature: " + iconFile);
        continue;
      }

      PngStructure structure = inspectPngStructure(bytes);
      if (structure.failure() != null) {
        failures.add(structure.failure() + ": " + iconFile);
        continue;
      }

      BufferedImage image = ImageIO.read(iconFile.toFile());
      if (image == null) {
        failures.add("Unreadable PNG image: " + iconFile);
        continue;
      }

      if (image.getWidth() != 9 || image.getHeight() != 9) {
        failures.add("Expected 9x9 icon canvas: " + iconFile + " (was " + image.getWidth() + "x" + image.getHeight() + ")");
      }

      if (!image.getColorModel().hasAlpha()) {
        failures.add("Expected PNG with alpha support: " + iconFile);
        continue;
      }

      boolean sawTransparentPixel = false;
      boolean sawNonTransparentPixel = false;
      for (int y = 0; y < image.getHeight(); y++) {
        for (int x = 0; x < image.getWidth(); x++) {
          int alpha = (image.getRGB(x, y) >>> 24) & 0xFF;
          if (alpha == 0) {
            sawTransparentPixel = true;
          }
          if (alpha > 0) {
            sawNonTransparentPixel = true;
          }
        }
      }

      if (!sawTransparentPixel) {
        failures.add("Expected at least one transparent pixel: " + iconFile);
      }
      if (!sawNonTransparentPixel) {
        failures.add("Expected at least one non-transparent pixel: " + iconFile);
      }
    }
    return failures;
  }

  private static PngStructure inspectPngStructure(byte[] bytes) {
    int offset = PNG_SIGNATURE.length;
    boolean sawIhdr = false;
    boolean sawIdat = false;
    boolean sawIend = false;
    String firstChunkType = null;

    while (offset < bytes.length) {
      if (offset + 8 > bytes.length) {
        return new PngStructure("Truncated PNG chunk header", false, false, false);
      }

      int length = readInt(bytes, offset);
      if (length < 0) {
        return new PngStructure("Negative PNG chunk length", false, false, false);
      }

      int dataStart = offset + 8;
      int dataEnd = dataStart + length;
      int crcEnd = dataEnd + 4;
      if (dataEnd < dataStart || crcEnd < dataEnd || crcEnd > bytes.length) {
        return new PngStructure("Truncated PNG chunk payload", sawIhdr, sawIdat, sawIend);
      }

      String chunkType = new String(bytes, offset + 4, 4, StandardCharsets.ISO_8859_1);
      if (firstChunkType == null) {
        firstChunkType = chunkType;
      }

      if ("IHDR".equals(chunkType)) {
        sawIhdr = true;
      } else if ("IDAT".equals(chunkType)) {
        sawIdat = true;
      } else if ("IEND".equals(chunkType)) {
        sawIend = true;
        offset = crcEnd;
        break;
      }

      offset = crcEnd;
    }

    if (!"IHDR".equals(firstChunkType)) {
      return new PngStructure("PNG missing leading IHDR chunk", sawIhdr, sawIdat, sawIend);
    }
    if (!sawIhdr) {
      return new PngStructure("PNG missing IHDR chunk", false, sawIdat, sawIend);
    }
    if (!sawIdat) {
      return new PngStructure("PNG missing IDAT chunk", true, false, sawIend);
    }
    if (!sawIend) {
      return new PngStructure("PNG missing IEND chunk", true, true, false);
    }
    if (offset != bytes.length) {
      return new PngStructure("PNG contains trailing bytes after IEND", true, true, true);
    }
    return new PngStructure(null, true, true, true);
  }

  private static int readInt(byte[] bytes, int offset) {
    return ((bytes[offset] & 0xFF) << 24)
            | ((bytes[offset + 1] & 0xFF) << 16)
            | ((bytes[offset + 2] & 0xFF) << 8)
            | (bytes[offset + 3] & 0xFF);
  }

  private record PngStructure(String failure, boolean sawIhdr, boolean sawIdat, boolean sawIend) {
  }

  private static void writeArgbPlaceholder(Path path, int width, int height, int backgroundArgb, int accentArgb) throws IOException {
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        image.setRGB(x, y, backgroundArgb);
      }
    }
    image.setRGB(width / 2, height / 2, accentArgb);
    ImageIO.write(image, "png", path.toFile());
  }

  private static void writeRgbPlaceholder(Path path, int width, int height, int backgroundRgb, int accentRgb) throws IOException {
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        image.setRGB(x, y, backgroundRgb);
      }
    }
    image.setRGB(width / 2, height / 2, accentRgb);
    ImageIO.write(image, "png", path.toFile());
  }

  private static String extractMethodBody(String source, String signature) {
    Matcher matcher = Pattern.compile(Pattern.quote(signature) + "[^\\{]*\\{", Pattern.DOTALL).matcher(source);
    if (!matcher.find()) {
      return "";
    }
    int start = matcher.end();
    int depth = 1;
    int pos = start;
    while (depth > 0 && pos < source.length()) {
      char c = source.charAt(pos);
      if (c == '{') depth++;
      else if (c == '}') depth--;
      pos++;
    }
    return source.substring(start, pos - 1);
  }

  private static void assertNoFailures(List<String> failures) {
    assertTrue(failures.isEmpty(), String.join(System.lineSeparator(), failures));
  }

  private static Path projectIconDirectory() {
    return projectFile("src/main/resources/assets/classicbar/textures/gui/icons");
  }

  private static Path projectIconFile(String iconName) {
    return projectIconDirectory().resolve(iconName + ".png");
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

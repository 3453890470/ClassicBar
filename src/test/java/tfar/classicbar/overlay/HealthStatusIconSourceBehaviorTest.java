package tfar.classicbar.overlay;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Source-structure test verifying that {@code Health.renderText()} and
 * {@code ModUtils.renderEffectIcons()} use the correct icon slot coordinates.
 * <p>
 * RED phase: with the current (buggy) code, {@code Health.renderText()}
 * computes {@code textX = baseX + effectCount * 4} (RHS), which shifts
 * the text by the effect count even though the replacement icon should be
 * at {@code baseX}. This test asserts the FIXED pattern.
 * <p>
 * The bug: in {@code ModUtils.renderEffectIcons()}, when effects replace
 * the base icon, the first effect starts at {@code baseX + effectCount * OVERLAP}
 * instead of {@code baseX}, causing a 4px shift (for 1 effect) that places
 * the icon in "the second heart position."
 */
class HealthStatusIconSourceBehaviorTest {

    private static final Pattern RENDER_TEXT_SIG =
        Pattern.compile("public void renderText\\([^)]+\\)\\s*\\{", Pattern.DOTALL);

    /**
     * {@code Health.renderText()} must use {@code baseX} as the text anchor,
     * NOT {@code baseX +/- effectCount * 4}.
     * <p>
     * Current (buggy) code computes {@code textX = baseX + effectCount * 4} for RHS.
     * Fixed code should use {@code textX = baseX}.
     */
    @Test
    @DisplayName("RED: Health.renderText() must not shift textX by effectCount")
    void healthRenderTextDoesNotShiftByEffectCount() throws IOException {
        String source = readHealthSource();
        String body = extractRenderTextBody(source);
        if (body.isBlank()) {
            fail("Could not extract renderText method body from Health.java");
        }

        // Check for the BUGGY pattern: textX = baseX +/- effectCount * 4
        // This should NOT be present after fix
        boolean usesShiftedTextX = body.contains("textX = baseX + effectCount * 4")
                                || body.contains("textX = baseX - effectCount * 4");

        // Check for the CORRECT pattern: textX = baseX
        // This should be present after fix
        boolean usesFixedTextX = body.contains("int textX = baseX;")
                              || body.contains("int textX=baseX;");

        // Before fix: usesShiftedTextX is true (buggy)
        // After fix:  usesFixedTextX is true, usesShiftedTextX is false
        // RED assertion: the buggy pattern should NOT be present after fix
        assertFalse(usesShiftedTextX,
            "Health.renderText() must NOT use textX = baseX +/- effectCount * 4.\n"
            + "The text anchor should be baseX (same as the replacement icon).\n"
            + "Method body:\n" + body);
    }

    /**
     * {@code ModUtils.renderEffectIcons()} must start the first effect
     * at {@code baseX}, not at {@code baseX +/- effectCount * OVERLAP}.
     */
    @Test
    @DisplayName("RED: renderEffectIcons must start first effect at baseX")
    void renderEffectIconsStartsAtBaseX() throws IOException {
        String source = readModUtilsSource();
        String body = extractRenderEffectIconsBody(source);
        if (body.isBlank()) {
            fail("Could not extract renderEffectIcons method body from ModUtils.java");
        }

        // The buggy code computes currentX = baseX +/- effectCount * OVERLAP
        // The fixed code should compute currentX = baseX
        boolean startsWithBaseXOnly = body.contains("int currentX = baseX;");

        assertTrue(startsWithBaseXOnly,
            "renderEffectIcons() must start first effect at baseX (currentX = baseX).\n"
            + "Buggy code uses currentX = baseX +/- effectCount * OVERLAP.\n"
            + "Method body:\n" + body);
    }

    // ---- helpers ----

    private static String readHealthSource() throws IOException {
        String projectDir = System.getProperty("classicbar.projectDir");
        if (projectDir == null || projectDir.isBlank()) {
            // Fallback: try to resolve from working directory
            projectDir = findProjectDirFromWorkingDir();
        }
        Path source = Path.of(projectDir,
            "src/main/java/tfar/classicbar/impl/overlays/vanilla/Health.java");
        return Files.readString(source);
    }

    private static String readModUtilsSource() throws IOException {
        String projectDir = System.getProperty("classicbar.projectDir");
        if (projectDir == null || projectDir.isBlank()) {
            projectDir = findProjectDirFromWorkingDir();
        }
        Path source = Path.of(projectDir,
            "src/main/java/tfar/classicbar/util/ModUtils.java");
        return Files.readString(source);
    }

    private static String findProjectDirFromWorkingDir() {
        // Try several strategies to find the project root
        String[] candidates = {
            ".",
            "..",
            "../..",
            "E:/GitHub/ClassicBar/ClassicBar-26.1.2",
            System.getProperty("user.dir")
        };
        for (String dir : candidates) {
            Path testPath = Path.of(dir, "build.gradle");
            if (Files.exists(testPath)) {
                return Path.of(dir).toAbsolutePath().normalize().toString();
            }
        }
        throw new IllegalStateException(
            "Cannot determine project directory. Set -Dclassicbar.projectDir=<path>");
    }

    /** Extracts the body of the renderText method. */
    private static String extractRenderTextBody(String source) {
        Matcher sigMatcher = RENDER_TEXT_SIG.matcher(source);
        if (!sigMatcher.find()) {
            return "";
        }
        int bodyStart = sigMatcher.end();
        int depth = 1;
        int pos = bodyStart;
        while (depth > 0 && pos < source.length()) {
            char c = source.charAt(pos);
            if (c == '{') depth++;
            else if (c == '}') depth--;
            pos++;
        }
        return source.substring(bodyStart, pos - 1);
    }

    /** Extracts the body of the renderEffectIcons method. */
    private static String extractRenderEffectIconsBody(String source) {
        Pattern sigPattern =
            Pattern.compile("public static void renderEffectIcons\\([^)]+\\)\\s*\\{", Pattern.DOTALL);
        Matcher sigMatcher = sigPattern.matcher(source);
        if (!sigMatcher.find()) {
            return "";
        }
        int bodyStart = sigMatcher.end();
        int depth = 1;
        int pos = bodyStart;
        while (depth > 0 && pos < source.length()) {
            char c = source.charAt(pos);
            if (c == '{') depth++;
            else if (c == '}') depth--;
            pos++;
        }
        return source.substring(bodyStart, pos - 1);
    }
}

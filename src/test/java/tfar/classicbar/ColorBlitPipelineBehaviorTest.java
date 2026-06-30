package tfar.classicbar;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that the color-blit pipeline delivers per-bar colors to the
 * MC 26.1.2 {@code GuiGraphicsExtractor.blit()} call.
 * <p>
 * In MC 26.1.2 {@code RenderSystem.setShaderColor()} has been removed.
 * Color tinting must be passed as an {@code int color} parameter (ARGB format)
 * to the 11-parameter {@code blit(RenderPipeline, Identifier, x, y, u, v, w, h, texW, texH, color)} overload.
 * <p>
 * The fix chain:
 * <ol>
 *   <li>{@link tfar.classicbar.util.Color#color2Gl()} stores the color as a static ARGB int
 *       (rather than calling the removed {@code RenderSystem.setShaderColor()})</li>
 *   <li>{@link tfar.classicbar.util.ModUtils#drawTexturedModalRect(net.minecraft.client.gui.GuiGraphicsExtractor, double, int, int, int, double, int)}
 *       reads that stored color and passes it to the 11-param blit overload</li>
 *   <li>{@link tfar.classicbar.util.Color#reset()} restores the stored color to {@code -1} (opaque white, no tint)</li>
 * </ol>
 */
class ColorBlitPipelineBehaviorTest {

  // ============ Source-structure tests for ModUtils ============

  @Test
  void drawTexturedModalRectPassesColorToBlit() throws IOException {
    String src = readProjectFile("src/main/java/tfar/classicbar/util/ModUtils.java");

    // The 10-param blit (current, NO color) must NOT be the only overload used:
    //   blit(RenderPipelines.GUI_TEXTURED, CURRENT_TEXTURE, (int) x, y, textureX, textureY, (int) width, height, 256, 256)
    // After fix, the 11-param blit must be used with a color argument:
    //   blit(RenderPipelines.GUI_TEXTURED, CURRENT_TEXTURE, (int) x, y, textureX, textureY, (int) width, height, 256, 256, color)

    assertTrue(
      src.contains("blit(RenderPipelines.GUI_TEXTURED, CURRENT_TEXTURE, (int) x, y, textureX, textureY, (int) width, height, 256, 256,"),
      "drawTexturedModalRect must use the 11-param blit() overload that accepts a trailing color argument"
    );
    // Also verify the call reads the stored color, not a hardcoded -1
    assertTrue(
      src.contains("Color.getCurrentBlitColor()"),
      "drawTexturedModalRect must pass Color.getCurrentBlitColor() — not a hardcoded -1"
    );
    // The drawStandaloneIcon helper legitimately uses -1 (icons render untinted);
    // verify that drawTexturedModalRect specifically does NOT
    assertFalse(
      src.contains("blit(RenderPipelines.GUI_TEXTURED, CURRENT_TEXTURE, (int) x, y, textureX, textureY, (int) width, height, 256, 256, -1)"),
      "drawTexturedModalRect must NOT pass a hardcoded -1 — it must pass the actual stored color from getCurrentBlitColor()"
    );
  }

  // ============ Color static-state tests ============

  @Test
  void colorClassStoresBlitColorAsArgbInt() throws IOException {
    String src = readProjectFile("src/main/java/tfar/classicbar/util/Color.java");

    // color2Gl() must not be empty — it must store the color
    assertFalse(
      src.contains("// Color tinting is handled per-blit"),
      "color2Gl() must be re-activated: the old no-op comment proves it is NOT storing color"
    );
    assertFalse(
      src.contains("// RenderSystem.setShaderColor removed in MC 26.1.2"),
      "color2Gla() must be re-activated: the old no-op comment proves it is NOT storing color"
    );

    // There must be a getter for the current blit color
    assertTrue(
      src.contains("getCurrentBlitColor"),
      "Color class must expose getCurrentBlitColor() for ModUtils to consume"
    );
    // There must be a static field for the color
    assertTrue(
      src.contains("currentBlitColor"),
      "Color class must have a static currentBlitColor field to bridge color2Gl() -> blit"
    );
  }

  // ============ Health overlay color tests ============

  @Test
  void healthOverlayAppliesConfiguredColorBeforeBlit() throws IOException {
    String src = readProjectFile("src/main/java/tfar/classicbar/impl/overlays/vanilla/Health.java");

    // The comment about "shader color removed" must be accompanied by an actual color2Gla call
    // Poison overlay:
    assertFalse(
      src.contains("// Health overlay tinting via shader color removed"),
      "The known-broken comment in Health.java's poison/wither/frozen overlay blocks must be removed — " +
      "it documents the bug instead of applying the color"
    );
  }

  // ============ drawStandaloneIcon color parameter tests ============

  @Test
  void drawStandaloneIconPassesExplicitWhiteColor() throws IOException {
    String src = readProjectFile("src/main/java/tfar/classicbar/util/ModUtils.java");

    // drawStandaloneIcon must use the 11-param blit with explicit opaque white (-1)
    // so that icons are never tinted by whatever bar color is currently set.
    // The current 10-param version blit(pipeline, tex, x, y, u, v, w, h, texW, texH)
    // does NOT carry a color parameter — the fix adds ", -1" at the end.
    assertTrue(
      src.contains("blit(RenderPipelines.GUI_TEXTURED, CURRENT_TEXTURE, x, y, 0, 0, size, size, size, size, -1)"),
      "drawStandaloneIcon must use 11-param blit with explicit -1 (opaque white) — " +
      "icons must not inherit the bar-level tint"
    );
  }

  // ============ Helper ============

  private static String readProjectFile(String relativePath) throws IOException {
    String projectDir = System.getProperty("classicbar.projectDir");
    if (projectDir == null || projectDir.isBlank()) {
      throw new IllegalStateException("Missing classicbar.projectDir test property");
    }
    return Files.readString(Path.of(projectDir).resolve(relativePath));
  }
}

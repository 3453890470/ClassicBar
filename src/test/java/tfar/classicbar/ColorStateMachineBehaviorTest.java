package tfar.classicbar;

import org.junit.jupiter.api.Test;
import tfar.classicbar.util.Color;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Runtime behaviour tests for the {@link Color} static state machine.
 * <p>
 * Verifies that {@link Color#color2Gl()}, {@link Color#color2Gla(float)},
 * {@link Color#reset()}, and {@link Color#getCurrentBlitColor()} produce
 * correct ARGB int values independently of the Minecraft runtime.
 * <p>
 * Every test starts with {@link Color#reset()} to guarantee clean state.
 */
class ColorStateMachineBehaviorTest {

  @Test
  void defaultColorIsOpaqueWhite() {
    Color.reset();
    assertEquals(-1, Color.getCurrentBlitColor(),
        "Default currentBlitColor must be -1 (signed) = 0xFFFFFFFF = opaque white (no tint)");
  }

  @Test
  void whiteColor2GlProducesMinusOne() {
    Color.reset();
    Color.WHITE.color2Gl();
    assertEquals(-1, Color.getCurrentBlitColor(),
        "Color.WHITE.color2Gl() must store -1 (0xFFFFFFFF)");
  }

  @Test
  void redColor2GlProducesCorrectArgb() {
    Color.reset();
    Color.RED.color2Gl();
    assertEquals(0xFFFF0000, Color.getCurrentBlitColor(),
        "Color.RED.color2Gl() must store 0xFFFF0000 (full alpha, full red, no green, no blue)");
  }

  @Test
  void yellowColor2GlProducesCorrectArgb() {
    Color.reset();
    Color.YELLOW.color2Gl();
    assertEquals(0xFFFFFF00, Color.getCurrentBlitColor(),
        "Color.YELLOW.color2Gl() must store 0xFFFFFF00");
  }

  @Test
  void blackColor2GlProducesCorrectArgb() {
    Color.reset();
    Color.BLACK.color2Gl();
    assertEquals(0xFF000000, Color.getCurrentBlitColor(),
        "Color.BLACK.color2Gl() must store 0xFF000000 (full alpha, no RGB)");
  }

  @Test
  void customColor2GlProducesCorrectArgb() {
    Color.reset();
    Color.from(0x12, 0x34, 0x56).color2Gl();
    assertEquals(0xFF123456, Color.getCurrentBlitColor(),
        "Color.from(0x12, 0x34, 0x56).color2Gl() must store 0xFF123456");
  }

  @Test
  void color2GlaHalfAlphaProducesCorrectArgb() {
    Color.reset();
    Color.RED.color2Gla(0.5f);
    assertEquals(0x7FFF0000, Color.getCurrentBlitColor(),
        "Color.RED.color2Gla(0.5f) must store 0x7FFF0000 (alpha=127, red=255)");
  }

  @Test
  void color2GlaClampsAlphaAboveMax() {
    Color.reset();
    Color.RED.color2Gla(2.0f);
    assertEquals(0xFFFF0000, Color.getCurrentBlitColor(),
        "color2Gla(2.0f) must clamp alpha to 255 -> 0xFFFF0000");
  }

  @Test
  void color2GlaClampsAlphaBelowMin() {
    Color.reset();
    Color.RED.color2Gla(-0.5f);
    assertEquals(0x00FF0000, Color.getCurrentBlitColor(),
        "color2Gla(-0.5f) must clamp alpha to 0 -> 0x00FF0000");
  }

  @Test
  void resetRestoresWhiteDefault() {
    Color.RED.color2Gl();
    Color.reset();
    assertEquals(-1, Color.getCurrentBlitColor(),
        "reset() must restore currentBlitColor to -1 (0xFFFFFFFF = opaque white)");
  }

  @Test
  void sequentialColorChangesProduceCorrectValues() {
    Color.reset();
    Color.RED.color2Gl();
    assertEquals(0xFFFF0000, Color.getCurrentBlitColor(), "first: RED");

    Color.YELLOW.color2Gl();
    assertEquals(0xFFFFFF00, Color.getCurrentBlitColor(), "second: YELLOW");

    Color.from(0, 255, 0).color2Gl();
    assertEquals(0xFF00FF00, Color.getCurrentBlitColor(), "third: GREEN");
  }

  @Test
  void resetAfterSequenceRestoresWhite() {
    Color.RED.color2Gl();
    Color.YELLOW.color2Gl();
    Color.from(0, 255, 0).color2Gl();
    Color.reset();
    assertEquals(-1, Color.getCurrentBlitColor(),
        "reset() after a sequence must restore -1");
  }
}

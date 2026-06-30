package tfar.classicbar.api;

import org.junit.jupiter.api.Test;
import tfar.classicbar.util.Color;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class BarColorOverlayBehaviorTest {

  @Test
  void invalidConfigFallsBackToNoOpOverlay() {
    Color base = Color.from(25, 50, 75);
    BarColorOverlay overlay = BarColorOverlay.fromConfigValue("not-a-color");

    assertSame(base, overlay.applyTo(base), "Invalid color overlays should leave the original fill color untouched");
    assertEquals(BarColorOverlay.DEFAULT_CONFIG_VALUE, overlay.rawValue(), "Invalid color overlays should normalize back to the no-op config value");
  }

  @Test
  void sixDigitColorReplacesTheBaseFillColor() {
    BarColorOverlay overlay = BarColorOverlay.fromConfigValue("#112233");

    assertEquals(Color.from(0x11, 0x22, 0x33), overlay.applyTo(Color.from(200, 180, 160)), "#RRGGBB should fully replace the bar fill color");
  }

  @Test
  void eightDigitColorAlphaBlendsWithTheBaseFillColor() {
    BarColorOverlay overlay = BarColorOverlay.fromConfigValue("#80FFFFFF");

    assertEquals(Color.from(128, 128, 128), overlay.applyTo(Color.from(0, 0, 0)), "#AARRGGBB should alpha-blend with the base fill color instead of replacing transparency state");
  }
}

package tfar.classicbar.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigCacheLayoutTrackingBehaviorTest {

  @AfterEach
  void resetActiveLayoutOverlays() {
    ConfigCache.setActiveLayoutOverlays(Set.of());
  }

  @Test
  void setActiveLayoutOverlaysReplacesThePreviousSnapshot() {
    ConfigCache.setActiveLayoutOverlays(new LinkedHashSet<>(Set.of("health", "absorption")));
    assertTrue(ConfigCache.isOverlayActiveInLayout("health"), "health should be active after the first layout snapshot");
    assertTrue(ConfigCache.isOverlayActiveInLayout("absorption"), "absorption should be active after the first layout snapshot");
    assertFalse(ConfigCache.isOverlayActiveInLayout("food"), "food should not be active before the reload-like replacement");

    ConfigCache.setActiveLayoutOverlays(new LinkedHashSet<>(Set.of("food")));
    assertFalse(ConfigCache.isOverlayActiveInLayout("health"), "a reload-like replacement should drop overlays that are no longer in layout");
    assertFalse(ConfigCache.isOverlayActiveInLayout("absorption"), "a reload-like replacement should drop previous shared-layer overlays too");
    assertTrue(ConfigCache.isOverlayActiveInLayout("food"), "food should become active after the replacement snapshot");
  }
}

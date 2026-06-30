package tfar.classicbar.client;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class EventHandlerCancellationBehaviorTest {

  @Test
  void healthOverrideAndAbsorptionOverrideStillCancelSharedPlayerHealthWhenAbsorptionIsAbsent() throws Exception {
    assertTrue(shouldCancelSharedPlayerHealth("OVERRIDE", true, true, "OVERRIDE", true, false), "If no absorption hearts are present, Health OVERRIDE should still be allowed to cancel the shared PLAYER_HEALTH layer");
  }

  @Test
  void sharedPlayerHealthCancelsOnlyWhenBothBarsOverrideAndRenderWhileAbsorptionIsPresent() throws Exception {
    assertTrue(shouldCancelSharedPlayerHealth("OVERRIDE", true, true, "OVERRIDE", true, true), "Visible absorption hearts may only disappear when both shared bars actively override and render");
  }

  @Test
  void layoutRemovalPreventsSharedPlayerHealthCancellationEvenForOverrideMode() throws Exception {
    assertFalse(shouldCancelSharedPlayerHealth("OVERRIDE", false, true, "OVERRIDE", true, true), "A health bar removed from layout should not cancel the shared PLAYER_HEALTH layer");
  }

  @Test
  void healthOverrideDoesNotCancelSharedPlayerHealthWhenVisibleAbsorptionMustBePreserved() throws Exception {
    assertFalse(shouldCancelSharedPlayerHealth("OVERRIDE", true, true, "DISABLED", true, true), "DISABLED absorption should keep visible vanilla absorption hearts on the shared PLAYER_HEALTH layer");
    assertFalse(shouldCancelSharedPlayerHealth("OVERRIDE", true, true, "OVERRIDE", false, true), "An absorption bar removed from layout should still preserve visible vanilla absorption hearts on the shared PLAYER_HEALTH layer");
  }

  @Test
  void disabledBarsDoNotRenderClassicBarOrCancelVanilla() throws Exception {
    for (String overlayId : new String[]{"food", "armor", "air"}) {
      assertFalse(rendersClassicBar("DISABLED"), overlayId + " DISABLED should not render through ClassicBar");
      assertFalse(shouldCancelIndependentVanillaLayer("DISABLED", true, true), overlayId + " DISABLED should not cancel vanilla");
    }
  }

  private static boolean rendersClassicBar(String modeName) throws Exception {
    Method method = policyMethod("rendersClassicBar", String.class);
    return (boolean) method.invoke(null, modeName);
  }

  private static boolean shouldCancelIndependentVanillaLayer(String modeName, boolean activeInLayout, boolean shouldRender) throws Exception {
    Method method = policyMethod("shouldCancelIndependentVanillaLayer", String.class, boolean.class, boolean.class);
    return (boolean) method.invoke(null, modeName, activeInLayout, shouldRender);
  }

  private static boolean shouldCancelSharedPlayerHealth(String healthMode, boolean healthActiveInLayout, boolean healthShouldRender,
                                                        String absorptionMode, boolean absorptionActiveInLayout, boolean absorptionShouldRender) throws Exception {
    Method method = policyMethod("shouldCancelSharedPlayerHealth", String.class, boolean.class, boolean.class, String.class, boolean.class, boolean.class);
    return (boolean) method.invoke(null, healthMode, healthActiveInLayout, healthShouldRender, absorptionMode, absorptionActiveInLayout, absorptionShouldRender);
  }

  private static Method policyMethod(String name, Class<?>... parameterTypes) throws Exception {
    try {
      Class<?> policyClass = Class.forName("tfar.classicbar.client.VanillaLayerCancellationPolicy");
      Method method = policyClass.getDeclaredMethod(name, parameterTypes);
      method.setAccessible(true);
      return method;
    } catch (ClassNotFoundException e) {
      fail("Expected VanillaLayerCancellationPolicy helper for layout-aware vanilla cancellation tests");
      throw e;
    } catch (NoSuchMethodException e) {
      fail("Expected VanillaLayerCancellationPolicy." + name + " helper for layout-aware vanilla cancellation tests");
      throw e;
    }
  }
}

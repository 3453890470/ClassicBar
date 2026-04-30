package tfar.classicbar.client;

final class VanillaLayerCancellationPolicy {

    private VanillaLayerCancellationPolicy() {
    }

    static boolean rendersClassicBar(String modeName) {
        return !isDisabled(modeName);
    }

    static boolean shouldCancelIndependentVanillaLayer(String modeName, boolean activeInLayout, boolean shouldRender) {
        return isOverride(modeName) && activeInLayout && shouldRender;
    }

    static boolean shouldCancelSharedPlayerHealth(String healthMode, boolean healthActiveInLayout, boolean healthShouldRender,
                                                  String absorptionMode, boolean absorptionActiveInLayout, boolean absorptionShouldRender) {
        if (!shouldCancelIndependentVanillaLayer(healthMode, healthActiveInLayout, healthShouldRender)) {
            return false;
        }

        if (!absorptionShouldRender) {
            return true;
        }

        // 如果 ClassicBar 会渲染吸收条（非 DISABLED 且在布局中），就可以安全取消整个原版层
        return !isDisabled(absorptionMode) && absorptionActiveInLayout;
    }

    private static boolean isOverride(String modeName) {
        return "OVERRIDE".equals(modeName);
    }

    private static boolean isDisabled(String modeName) {
        return "DISABLED".equals(modeName);
    }
}

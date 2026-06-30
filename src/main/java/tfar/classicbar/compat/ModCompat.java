package tfar.classicbar.compat;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;
import tfar.classicbar.config.ClassicBarsConfig;

/**
 * 跨模组效果检测工具类。
 * 优先通过编译期引用模组 API（compileOnly），
 * 回退到 BuiltInRegistries 运行时查找。
 * 两种方式均可安全处理模组未安装的情况。
 */
public class ModCompat {

    private static Holder<MobEffect> nourishment;
    private static Holder<MobEffect> satiatedShield;

    private static Holder<MobEffect> getEffect(Identifier id) {
        return BuiltInRegistries.MOB_EFFECT.get(id).orElse(null);
    }

    private static void ensureEffects() {
        if (nourishment == null)
            nourishment = getEffect(Identifier.parse("farmersdelight:nourishment"));
        if (satiatedShield == null)
            satiatedShield = getEffect(Identifier.parse("kaleidoscope_cookery:satiated_shield"));
    }

    /**
     * 检测玩家是否拥有农夫乐事的「滋养」效果。
     * 受配置开关 farmersdelight.enabled 控制。
     */
    public static boolean hasNourishment(Player player) {
        if (!ClassicBarsConfig.isReservedModSupportEnabled("farmersdelight")) return false;
        ensureEffects();
        return nourishment != null && player.hasEffect(nourishment);
    }

    /**
     * 检测玩家是否拥有森罗物语:厨房的「饱腹代偿」效果。
     * 受配置开关 kaleidoscope_cookery.enabled 控制。
     */
    public static boolean hasSatiatedShield(Player player) {
        if (!ClassicBarsConfig.isReservedModSupportEnabled("kaleidoscope_cookery")) return false;
        ensureEffects();
        return satiatedShield != null && player.hasEffect(satiatedShield);
    }

    public static Holder<MobEffect> getNourishmentEffect() {
        ensureEffects();
        return nourishment;
    }

    public static Holder<MobEffect> getSatiatedShieldEffect() {
        ensureEffects();
        return satiatedShield;
    }

    /**
     * 检查农夫乐事模组是否在运行时已加载。
     */
    public static boolean isFarmersDelightLoaded() {
        ensureEffects();
        return nourishment != null;
    }

    /**
     * 检查森罗物语:厨房模组是否在运行时已加载。
     */
    public static boolean isKaleidoscopeCookeryLoaded() {
        ensureEffects();
        return satiatedShield != null;
    }

    /**
     * 检查 Vampirism 模组是否在运行时已加载。
     * <p>
     * 不同于 effect-based 检测，Vampirism 通过 API 接口访问，
     * 因此直接检查 ModList 即可作为第一道守卫。
     */
    public static boolean isVampirismLoaded() {
        return ModList.get().isLoaded("vampirism");
    }

    /**
     * 检查 EnigmaticLegacy+ 模组是否在运行时已加载。
     */
    public static boolean isEnigmaticLegacyPlusLoaded() {
        return ModList.get().isLoaded("enigmaticlegacyplus");
    }

    /**
     * 检测玩家是否拥有 EnigmaticLegacy+ 的「禁忌诅咒」状态。
     * <p>
     * EnigmaticLegacy+ API unavailable for MC 26.1.2 — always returns false.
     *
     * @param player 目标玩家
     * @return 是否处于禁忌诅咒状态；模组未加载或 API 异常时返回 false
     */
    public static boolean hasForbiddenCurse(Player player) {
        if (!isEnigmaticLegacyPlusLoaded()) return false;
        // EnigmaticLegacy+ API not available in this build
        return false;
    }

    /**
     * 设置玩家的禁忌诅咒状态（仅调试/开发用）。
     * <p>
     * EnigmaticLegacy+ API unavailable for MC 26.1.2 — no-op in this build.
     *
     * @param player 目标玩家
     * @param cursed 是否设为禁忌诅咒状态
     */
    public static void setForbiddenCurse(Player player, boolean cursed) {
        if (!isEnigmaticLegacyPlusLoaded()) return;
        // EnigmaticLegacy+ API not available in this build — no-op
    }
}

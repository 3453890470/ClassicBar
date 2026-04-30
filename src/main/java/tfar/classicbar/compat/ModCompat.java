package tfar.classicbar.compat;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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

    private static Holder<MobEffect> getEffect(ResourceLocation id) {
        return BuiltInRegistries.MOB_EFFECT.getHolder(id).orElse(null);
    }

    private static void ensureEffects() {
        if (nourishment == null)
            nourishment = getEffect(ResourceLocation.parse("farmersdelight:nourishment"));
        if (satiatedShield == null)
            satiatedShield = getEffect(ResourceLocation.parse("kaleidoscope_cookery:satiated_shield"));
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
}

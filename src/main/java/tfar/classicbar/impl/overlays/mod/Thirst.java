package tfar.classicbar.impl.overlays.mod;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import tfar.classicbar.client.HudRenderContext;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.resources.BarIcons;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ModUtils;

/**
 * ClassicBar overlay for third-party mod thirst/hydration systems.
 * <p>
 * Supports <b>Tough As Nails</b> ({@code toughasnails}) and
 * <b>Thirst Was Taken</b> ({@code thirst}) mods.
 * Renders the player's thirst level and hydration as classic-style progress bars.
 * Mutually exclusive with {@link Blood} and {@link tfar.classicbar.impl.overlays.mod.ForbiddenHunger} overlays.
 * This overlay replaces the vanilla food bar when a thirst system is active.
 *
 * <h3>API Routing</h3>
 * <ul>
 *   <li>TAN (toughasnails): {@code ThirstHelper.getThirst(player)} — returns {@code IThirst}</li>
 *   <li>TWT (thirst): {@code player.getData(ModAttachment.PLAYER_THIRST)} — returns {@code PlayerThirst}</li>
 * </ul>
 * TAN is checked first if both mods are loaded.
 */
public class Thirst extends BarOverlayImpl {

    public Thirst() {
        super("thirst_level");
    }

    /** 消耗闪烁追踪 */
    private double lastThirstLevel = 0;
    private long thirstUpdateCounter = 0;

    /** 口渴值内部封装 */
    private record ThirstData(int thirst, int hydration, float exhaustion) {}

    // ========================================================================
    //  API Routing — TAN 优先
    // ========================================================================

    /**
     * 获取口渴数据。TAN 优先（如果两模组均加载）。
     */
    private static ThirstData getThirstData(Player player) {
        // [TAN 兼容暂时搁置] Tough As Nails 的意志坚定系统暂不激活
        if (ModList.get().isLoaded("toughasnails")) {
            try {
                toughasnails.api.thirst.IThirst tan = toughasnails.api.thirst.ThirstHelper.getThirst(player);
                if (tan != null) {
                    return new ThirstData(tan.getThirst(), (int) tan.getHydration(), tan.getExhaustion());
                }
            } catch (Throwable t) {
                // fall through
            }
        }
        if (ModList.get().isLoaded("thirst")) {
            try {
                dev.ghen.thirst.foundation.common.capability.IThirst twt =
                        (dev.ghen.thirst.foundation.common.capability.IThirst)
                                player.getData(dev.ghen.thirst.foundation.common.capability.ModAttachment.PLAYER_THIRST);
                if (twt != null) {
                    return new ThirstData(twt.getThirst(), twt.getQuenched(), twt.getExhaustion());
                }
            } catch (Throwable t) {
                // fall through
            }
        }
        return new ThirstData(0, 0, 0);
    }

    /**
     * 运行时检测：玩家是否处于活跃口渴状态（模组已加载且可获取数据）。
     * 供 {@link #shouldRender} 及外部互斥检测调用。
     */
    public static boolean isThirstActive(Player player) {
        // [TAN 兼容暂时搁置] Tough As Nails 的意志坚定系统暂不激活
        if (ModList.get().isLoaded("toughasnails")) {
            try {
                return toughasnails.api.thirst.ThirstHelper.getThirst(player) != null;
            } catch (Throwable t) {
                return false;
            }
        }
        if (ModList.get().isLoaded("thirst")) {
            try {
                dev.ghen.thirst.foundation.common.capability.IThirst twt =
                        (dev.ghen.thirst.foundation.common.capability.IThirst)
                                player.getData(dev.ghen.thirst.foundation.common.capability.ModAttachment.PLAYER_THIRST);
                return twt != null;
            } catch (Throwable t) {
                return false;
            }
        }
        return false;
    }

    // ========================================================================
    //  BarOverlayImpl 实现
    // ========================================================================

    @Override
    public boolean shouldRender(Player player) {
        return isThirstActive(player);
    }

    @Override
    public void renderBar(HudRenderContext context, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
        ThirstData data = getThirstData(player);
        double thirst = data.thirst();
        double maxThirst = 20;
        double hydration = data.hydration();

        double barWidthT = getBarWidth(player);
        double barWidthH = 0; // 提升作用域，供 drink preview 使用

        // 检测口渴值下降 → 触发图标闪烁
        int updateCounter = context.getGuiTicks();
        if (thirst < lastThirstLevel) {
            thirstUpdateCounter = updateCounter + 2;
        }
        lastThirstLevel = thirst;

        int xStart = screenWidth / 2 + getHOffset();
        int yStart = screenHeight - vOffset;

        // 背景
        Color.reset();
        renderFullBarBackground(graphics, xStart, yStart);

        // 主条：口渴值
        Color thirstColor = getSecondaryBarColor(0, player);
        Color hydrationColor = getPrimaryBarColor(0, player);

        double f = xStart + (rightHandSide() ? BarOverlayImpl.WIDTH - barWidthT : 0);
        applyConfiguredBarColor(thirstColor);
        renderPartialBar(graphics, f + 2, yStart + 2, barWidthT);

        // 子条：解渴值（渲染在主条上方，类似饱食度条叠加在饥饿条上的方式）
        if (hydration > 0 && ClassicBarsConfig.showHydrationBar.get()) {
            barWidthH = ModUtils.getWidth(hydration, maxThirst);
            f = xStart + (rightHandSide() ? BarOverlayImpl.WIDTH - barWidthH : 0);
            applyConfiguredBarColor(hydrationColor);
            renderPartialBar(graphics, f + 2, yStart + 2, barWidthH);
        }

        // --- Drink preview ---
        ItemStack held = player.getMainHandItem();
        if (!held.isEmpty() && ConfigCache.showHeldDrinkOverlay) {
            try {
                if (dev.ghen.thirst.api.ThirstHelper.itemRestoresThirst(held)) {
                    int previewThirst = dev.ghen.thirst.api.ThirstHelper.getThirst(held);
                    int previewQuenched = dev.ghen.thirst.api.ThirstHelper.getQuenched(held);

                    int currentThirstVal = data.thirst();
                    int newThirstVal = Math.min((int) maxThirst, currentThirstVal + previewThirst);
                    double thirstPreview = Math.max(0, newThirstVal - currentThirstVal);

                    double currentQuenched = data.hydration();
                    double cappedCurrentQuenched = Math.min(20, currentQuenched);
                    double cappedNewQuenched = Math.min(20, cappedCurrentQuenched + previewQuenched);
                    double quenchedPreview = Math.max(0, cappedNewQuenched - cappedCurrentQuenched);

                    if (thirstPreview > 0 || quenchedPreview > 0) {
                        float breath = ModUtils.getBreathingAlpha(context.getGuiTicks());

                        renderPreviewBar(graphics, thirstPreview, maxThirst, barWidthT, thirstColor, breath, xStart, yStart);

                        renderPreviewBar(graphics, quenchedPreview, maxThirst, barWidthH, hydrationColor, breath, xStart, yStart);

                        renderWhitePreviewOverlay(graphics, thirstPreview, quenchedPreview, maxThirst, barWidthT, breath, xStart, yStart);
                    }
                }
            } catch (Throwable t) {
                // TWT 未加载或无数据时静默跳过
            }
        }
    }

    @Override
    public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        ThirstData data = getThirstData(player);
        int baseX = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        boolean hasDebuff = player.hasEffect(MobEffects.HUNGER) || player.hasEffect(MobEffects.POISON);
        textHelper(graphics, baseX, yStart, data.thirst(), 20,
                getConfiguredTextColor(hasDebuff ? ConfigCache.thirstDebuff : ConfigCache.thirst),
                barSettings.textFormat);
    }

    @Override
    public double getBarWidth(Player player) {
        ThirstData data = getThirstData(player);
        return Math.min(BarOverlayImpl.WIDTH, ModUtils.getWidth(data.thirst(), 20));
    }

    @Override
    public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int baseX = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        int guiTicks = ModUtils.getGuiTicks();
        boolean flashing = thirstUpdateCounter > (long) guiTicks;

        // 当口渴值下降时闪烁图标（目前 normal 与 blinking 使用同一纹理，
        // 待 BarIcons.THIRST_BLINKING 添加后替换第二个参数即可启用闪烁效果）
        ModUtils.drawIconWithFlash(graphics, baseX, yStart, 9,
                BarIcons.THIRST, BarIcons.THIRST, flashing, guiTicks);
    }

    // ========================================================================
    //  颜色路由
    // ========================================================================

    /**
     * 解渴值（hydration）颜色 — 作为子条渲染在主条上方。
     * 对应 Hunger 中 {@code getPrimaryBarColor} 管理饱食度颜色的模式。
     */
    @Override
    public Color getPrimaryBarColor(int index, Player player) {
        boolean hasDebuff = player.hasEffect(MobEffects.HUNGER) || player.hasEffect(MobEffects.POISON);
        return hasDebuff ? ConfigCache.hydrationDebuff : ConfigCache.hydration;
    }

    /**
     * 口渴值（thirst）颜色 — 主条颜色。
     * 对应 Hunger 中 {@code getSecondaryBarColor} 管理饥饿颜色的模式。
     */
    @Override
    public Color getSecondaryBarColor(int index, Player player) {
        boolean hasDebuff = player.hasEffect(MobEffects.HUNGER) || player.hasEffect(MobEffects.POISON);
        return hasDebuff ? ConfigCache.thirstDebuff : ConfigCache.thirst;
    }
}

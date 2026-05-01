package tfar.classicbar.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Holder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import tfar.classicbar.ClassicBar;
import tfar.classicbar.compat.ModCompat;
import tfar.classicbar.api.BarOverlay;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.overlays.vanilla.Absorption;
import tfar.classicbar.impl.overlays.vanilla.Air;
import tfar.classicbar.impl.overlays.vanilla.Armor;
import tfar.classicbar.impl.overlays.vanilla.ArmorToughness;
import tfar.classicbar.impl.overlays.vanilla.Health;
import tfar.classicbar.impl.overlays.vanilla.Hunger;
import tfar.classicbar.impl.overlays.vanilla.MountHealth;
import auviotre.enigmatic.legacy.contents.item.food.ForbiddenFruit;
import auviotre.enigmatic.legacy.registries.EnigmaticAttachments;
import tfar.classicbar.impl.overlays.mod.Blood;
import tfar.classicbar.impl.overlays.mod.ForbiddenHunger;
import tfar.classicbar.impl.overlays.mod.Thirst;
import tfar.classicbar.util.ModUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class EventHandler {

  private static final ResourceLocation HUD_LAYER = ResourceLocation.fromNamespaceAndPath(ClassicBar.MODID, "hud");
  private static final LayeredDraw.Layer HUD_RENDERER = EventHandler::render;

  private static final List<BarOverlay> all = new ArrayList<>();
  public static final Map<String, BarOverlay> registry = new HashMap<>();

  private static final List<BarOverlay> errored = new ArrayList<>();

  private EventHandler() {
  }

  public static void bootstrap() {
    if (!registry.isEmpty()) {
      return;
    }

    ClassicBar.logger.info("Registering Vanilla Overlays");
    registerAll(new Health(), new Armor(), new Absorption(), new Hunger(), new ArmorToughness(), new MountHealth(), new Air());
    if (ModList.get().isLoaded("vampirism")) {
      register(new Blood());
    }
    if (ModList.get().isLoaded("enigmaticlegacyplus")) {
      register(new ForbiddenHunger());
    }
    if (ModList.get().isLoaded("toughasnails") || ModList.get().isLoaded("thirst")) {
      register(new Thirst());
    }
  }

  public static void register(BarOverlay iBarOverlay) {
    registry.put(iBarOverlay.name(), iBarOverlay);
  }

  public static void registerAll(BarOverlay... iBarOverlay) {
    Arrays.stream(iBarOverlay).forEach(overlay -> {
      if (overlay != null) {
        registry.put(overlay.name(), overlay);
      }
    });
  }

  public static void registerGuiLayers(RegisterGuiLayersEvent event) {
    bootstrap();
    event.registerAbove(VanillaGuiLayers.FOOD_LEVEL, HUD_LAYER, HUD_RENDERER);
    // 在配置加载前主动设置所有已注册 overlay 为活跃，防止原版层取消竞态
    ConfigCache.setActiveLayoutOverlays(new LinkedHashSet<>(registry.keySet()));
  }

  public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
    Player player = ModUtils.mc.player;
    if (player == null) return;
    if (player.getAbilities().instabuild || player.isSpectator()) return;
    ModUtils.mc.getProfiler().push("classicbars_hud");

    HudRenderContext context = new HudRenderContext();
    int screenWidth = graphics.guiWidth();
    int screenHeight = graphics.guiHeight();

    for (BarOverlay overlay : all) {
      boolean rightHand = overlay.rightHandSide();
      try {
        overlay.render(context, graphics, player, screenWidth, screenHeight, context.getOffset(rightHand));
      } catch (RuntimeException e) {
        ClassicBar.logger.error("Removing broken overlay " + overlay.name(), e);
        errored.add(overlay);
      }
    }
    if (!errored.isEmpty()) all.removeAll(errored);
    ModUtils.mc.getProfiler().pop();
  }

  public static void cacheConfigs() {
    bootstrap();
    all.clear();
    registry.values().forEach(barOverlay -> barOverlay.setBarSettings(ClassicBarsConfig.getBarSettings(barOverlay.name())));
    Set<String> appliedOverlays = new LinkedHashSet<>();
    applyConfiguredBars(ClassicBarsConfig.getLeftOrder(), false, appliedOverlays);
    applyConfiguredBars(ClassicBarsConfig.getRightOrder(), true, appliedOverlays);
    // 追加通道：处理非 ACTIVE_BAR_IDS 的额外 overlay
    for (BarOverlay overlay : registry.values()) {
      String name = overlay.name();
      if (!ClassicBarsConfig.isActiveBarId(name) && !appliedOverlays.contains(name)) {
        BarSettings settings = ClassicBarsConfig.getBarSettings(name);
        overlay.setBarSettings(settings);
        if (settings.rendersClassicBar() && appliedOverlays.add(name)) {
          all.add(overlay.setSide(true)); // 默认右侧
        }
      }
    }
    ConfigCache.setActiveLayoutOverlays(appliedOverlays);
    all.removeAll(errored);
    ConfigCache.setActiveLayoutOverlays(collectRenderableLayoutOverlays());
    ConfigCache.bake();
  }

  private static Set<String> collectRenderableLayoutOverlays() {
    Set<String> renderableLayoutOverlays = new LinkedHashSet<>();
    for (BarOverlay overlay : all) {
      renderableLayoutOverlays.add(overlay.name());
    }
    return renderableLayoutOverlays;
  }

  private static void applyConfiguredBars(List<? extends String> barOrder, boolean rightSide, Set<String> appliedOverlays) {
    for (String overlayId : barOrder) {
      BarOverlay overlay = registry.get(overlayId);
      if (overlay == null) {
        continue;
      }

      BarSettings settings = ClassicBarsConfig.getBarSettings(overlayId);
      overlay.setBarSettings(settings);
      if (settings.rendersClassicBar() && appliedOverlays.add(overlayId)) {
        all.add(overlay.setSide(rightSide));
      }
    }
  }

  /**
   * 最低优先级拦截 FOOD_LEVEL 层，运行于 EL+ 的 NORMAL 处理器之后。
   * <p>
   * EL+ 的 ClientEventHandler 使用 {@code @SubscribeEvent(receiveCanceled = true)}
   * 监听 FOOD_LEVEL 的 Pre 事件，即使在 HIGH 优先级取消后仍会渲染其覆盖层。
   * 此方法在 LOWEST 优先级再次取消，确保 {@link ForbiddenHunger} 或 {@link Blood}
   * 活跃时代理位置被标记为取消，使 ClassicBar 的独立渲染覆盖在 EL+ 的渲染之上。
   */
  public static void finalizeFoodLevelCancellation(RenderGuiLayerEvent.Pre event) {
    if (!event.getName().equals(VanillaGuiLayers.FOOD_LEVEL)) return;
    Player player = ModUtils.mc.player;
    if (player == null) return;

    boolean forbiddenActive = isForbiddenHungerActive(player);
    boolean bloodActive = isBloodActive(player);

    if (forbiddenActive || bloodActive) {
      event.setCanceled(true);
    }
  }

  private static boolean isForbiddenHungerActive(Player player) {
    if (!ModList.get().isLoaded("enigmaticlegacyplus")) return false;
    if (!ClassicBarsConfig.isReservedModSupportEnabled("enigmaticlegacyplus")) return false;
    try {
      return ForbiddenFruit.isForbiddenCursed(player);
    } catch (Throwable t) {
      return false;
    }
  }

  private static boolean isBloodActive(Player player) {
    if (!ModList.get().isLoaded("vampirism")) return false;
    BarOverlay overlay = registry.get("blood");
    if (overlay == null) return false;
    return overlay.shouldRender(player);
  }

  public static void disableVanillaLayers(RenderGuiLayerEvent.Pre event) {
    ResourceLocation loc = event.getName();
    Player player = ModUtils.mc.player;

    // ★ 最优先：取消 EnigmaticLegacy+ 的所有 GUI 层（不依赖 player 状态）
    //   确保创造/旁观模式下 EL+ 覆盖层也被取消
    if (ModCompat.isEnigmaticLegacyPlusLoaded()
        && "enigmaticlegacyplus".equals(loc.getNamespace())) {
      event.setCanceled(true);
      return;
    }

    // ★ 取消 Vampirism 原生 blood_bar 层（不依赖 player 状态）
    if (ModCompat.isVampirismLoaded()
        && "vampirism".equals(loc.getNamespace())
        && "blood_bar".equals(loc.getPath())
        && ClassicBarsConfig.getBarSettings("blood").rendersClassicBar()) {
      event.setCanceled(true);
      return;
    }
    if (player == null || player.getAbilities().instabuild || player.isSpectator()) {
      return;
    }

    ResourceLocation layerName = event.getName();
    if (VanillaGuiLayers.PLAYER_HEALTH.equals(layerName)) {
      if (shouldCancelSharedPlayerHealthLayer(resolveOverlayRenderState("health", player), resolveOverlayRenderState("absorption", player))) {
        event.setCanceled(true);
      }
      return;
    }

    if (VanillaGuiLayers.ARMOR_LEVEL.equals(layerName) && shouldCancelIndependentVanillaLayer(resolveOverlayRenderState("armor", player))) {
      event.setCanceled(true);
      return;
    }

    // 取消 FarmersDelight 的滋养覆盖层（由配置控制，默认不取消）
    ResourceLocation fdNourishment = ResourceLocation.parse("farmersdelight:nourishment");
    if (fdNourishment.equals(layerName)
        && ConfigCache.disableFdNourishmentOverlay
        && shouldCancelIndependentVanillaLayer(resolveOverlayRenderState("food", player))) {
      event.setCanceled(true);
      return;
    }

    if (VanillaGuiLayers.VEHICLE_HEALTH.equals(layerName) && shouldCancelIndependentVanillaLayer(resolveOverlayRenderState("health_mount", player))) {
      event.setCanceled(true);
      return;
    }

    if (VanillaGuiLayers.AIR_LEVEL.equals(layerName)
        && (shouldCancelIndependentVanillaLayer(resolveOverlayRenderState("air", player))
            || shouldCancelIndependentVanillaLayer(resolveOverlayRenderState("thirst_level", player)))) {
      event.setCanceled(true);
    }

  }

  private static OverlayRenderState resolveOverlayRenderState(String overlayId, Player player) {
    BarOverlay overlay = registry.get(overlayId);
    if (overlay == null) {
      return new OverlayRenderState(overlayId, "DISABLED", false, false, false);
    }

    BarSettings settings = ClassicBarsConfig.getBarSettings(overlayId);
    boolean activeInLayout = ConfigCache.isOverlayActiveInLayout(overlayId);
    boolean requestsVanillaCancel = settings.cancelsVanillaLayer();
    boolean shouldRender = overlay.shouldRender(player);
    return new OverlayRenderState(overlayId, settings.mode.name(), requestsVanillaCancel, activeInLayout, shouldRender);
  }

  private static boolean shouldCancelIndependentVanillaLayer(OverlayRenderState overlayState) {
    return overlayState.cancelsVanillaLayer();
  }

  private static boolean shouldCancelSharedPlayerHealthLayer(OverlayRenderState healthState, OverlayRenderState absorptionState) {
    return VanillaLayerCancellationPolicy.shouldCancelSharedPlayerHealth(
            healthState.modeName(),
            healthState.activeInLayout(),
            healthState.shouldRender(),
            absorptionState.modeName(),
            absorptionState.activeInLayout(),
            absorptionState.shouldRender());
  }

  private record OverlayRenderState(String overlayId, String modeName, boolean requestsVanillaCancel, boolean activeInLayout, boolean shouldRender) {
    private boolean rendersClassicBar() {
      return VanillaLayerCancellationPolicy.rendersClassicBar(modeName);
    }

    private boolean cancelsVanillaLayer() {
      return requestsVanillaCancel
              && VanillaLayerCancellationPolicy.shouldCancelIndependentVanillaLayer(modeName, activeInLayout, shouldRender);
    }
  }

  // === Debug: development testing effects ===

  // Apply vanilla debug effect
  private static void debugEffect(Player player, ConfigValue<Boolean> config, Holder<MobEffect> effect, int duration) {
    if (config.get()) {
      player.addEffect(new MobEffectInstance(effect, duration, 0));
    }
  }

  // Apply mod debug effect (checks mod loaded + effect not null)
  private static void debugModEffect(Player player, ConfigValue<Boolean> config, String modId, Holder<MobEffect> effect, int duration) {
    if (config.get() && ModList.get().isLoaded(modId) && effect != null) {
      player.addEffect(new MobEffectInstance(effect, duration, 0));
    }
  }

  @SubscribeEvent
  public static void onDebugPlayerTick(PlayerTickEvent.Post event) {
    Player player = event.getEntity();
    if (player.level().isClientSide) return;
    // 非开发环境不执行调试效果
    if (FMLLoader.isProduction()) return;

    // Period effects: every 1 minute (1200 ticks)
    boolean isIntervalTick = (player.tickCount % 1200 == 0);

    // 1-6: Periodic effects
    if (isIntervalTick) {
      debugEffect(player, ClassicBarsConfig.debugWitherEnabled, MobEffects.WITHER, 2400);
      debugEffect(player, ClassicBarsConfig.debugPoisonEnabled, MobEffects.POISON, 2400);
      debugEffect(player, ClassicBarsConfig.debugHungerEnabled, MobEffects.HUNGER, 2400);
      debugModEffect(player, ClassicBarsConfig.debugNourishmentEnabled, "farmersdelight", ModCompat.getNourishmentEffect(), 2400);
      debugModEffect(player, ClassicBarsConfig.debugSatiatedShieldEnabled, "kaleidoscope_cookery", ModCompat.getSatiatedShieldEffect(), 2400);
    }

    // 冻伤 — 独立处理（类燃烧机制，需每 tick 维持）
    if (ClassicBarsConfig.debugFrozenEnabled.get()) {
        player.setTicksFrozen(player.getTicksRequiredToFreeze());
    }

    // 7. Vampire level — checked every tick for immediate enable/disable
    if (ModList.get().isLoaded("vampirism")) {
      try {
        boolean wantVampire = ClassicBarsConfig.debugVampireEnabled.get();
        var handler = de.teamlapen.vampirism.api.VampirismAPI.factionPlayerHandler(player);
        int currentLevel = handler.getCurrentLevel();
        if (wantVampire && currentLevel < 1) {
          handler.setFactionAndLevel(de.teamlapen.vampirism.api.VReference.VAMPIRE_FACTION, 1);
        } else if (!wantVampire && currentLevel > 0) {
          handler.setFactionAndLevel(de.teamlapen.vampirism.api.VReference.VAMPIRE_FACTION, 0);
        }
      } catch (Throwable t) {
        // Debug feature — silent fail
      }
    }

    // 8. Forbidden curse — checked every tick for immediate enable/disable
    if (ModList.get().isLoaded("enigmaticlegacyplus")) {
      try {
        boolean wantCurse = ClassicBarsConfig.debugForbiddenCurseEnabled.get();
        boolean hasCurse = ForbiddenFruit.isForbiddenCursed(player);
        if (wantCurse && !hasCurse) {
          player.getData(EnigmaticAttachments.ENIGMATIC_DATA).setForbiddenCursed(true);
        } else if (!wantCurse && hasCurse) {
          player.getData(EnigmaticAttachments.ENIGMATIC_DATA).setForbiddenCursed(false);
        }
      } catch (Throwable t) {
        // Debug feature — silent fail
      }
    }
  }
}

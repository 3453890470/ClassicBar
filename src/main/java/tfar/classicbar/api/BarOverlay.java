package tfar.classicbar.api;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.client.HudRenderContext;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ModUtils;

public interface BarOverlay {

  void setBarSettings(BarSettings barSettings);
  boolean shouldRender(Player player);
  boolean rightHandSide();
  BarOverlay setSide(boolean right);

  void render(HudRenderContext context, GuiGraphicsExtractor graphics, Player player, int screenWidth, int screenHeight, int vOffset);

  Identifier getIconRL();
  default void bindIconTexture() {
    ModUtils.CURRENT_TEXTURE = getIconRL();
  }

  default void bindBarTexture() {
    ModUtils.CURRENT_TEXTURE = BarOverlayImpl.ICON_BAR;
  }

  double getBarWidth(Player player);

  Color getPrimaryBarColor(int index,Player player);

  Color getSecondaryBarColor(int index,Player player);

  boolean isFitted();

  String name();
}

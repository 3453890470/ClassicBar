package tfar.classicbar.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;

public class HudRenderContext {

    private int leftHeight = 39;
    private int rightHeight = 39;

    public void setupOverlayRenderState() {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }

    public int getGuiTicks() {
        return Minecraft.getInstance().gui.getGuiTicks();
    }

    public void increment(boolean right, int amount) {
        if (right) {
            rightHeight += amount;
        } else {
            leftHeight += amount;
        }
    }

    public int getOffset(boolean right) {
        return right ? rightHeight : leftHeight;
    }
}

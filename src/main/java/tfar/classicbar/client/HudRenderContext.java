package tfar.classicbar.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;

public class HudRenderContext {

    private int leftHeight = 39;
    private int rightHeight = 39;
    private int compatLeftOffset = 0;
    private int compatRightOffset = 0;

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
        int base = right ? rightHeight : leftHeight;
        int compatOffset = right ? compatRightOffset : compatLeftOffset;
        return base + compatOffset;
    }

    public void setCompatOffset(boolean right, int offset) {
        if (right) {
            compatRightOffset = offset;
        } else {
            compatLeftOffset = offset;
        }
    }

    public boolean hasAnyCompatOffset() {
        return compatLeftOffset != 0 || compatRightOffset != 0;
    }
}

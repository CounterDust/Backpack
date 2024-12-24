package com.backpack.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class Button extends GuiButton {

    private static final ResourceLocation BUTTON_TEXTURE = new ResourceLocation("backpack:textures/gui/icons.png");

    public Button(int buttonId, int x, int y, String buttonText) {
        super(buttonId, x, y, 12, 10, buttonText);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            // 绑定按钮纹理
            mc.getTextureManager().bindTexture(BUTTON_TEXTURE);

            // 绘制按钮
            this.drawTexturedModalRect(this.x, this.y, 2, 3, 12, 10);

        }
    }
}
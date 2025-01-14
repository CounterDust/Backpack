package com.backpack.gui.button;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CustomButton extends GuiButton {

    private static final Logger LOGGER = LogManager.getLogger();
    //
    private final ResourceLocation texture;
    private final int textureX, textureY;

    /**
     * 构造函数，初始化按钮属性。
     *
     * @param buttonId 按钮的唯一ID，用于在点击事件中识别按钮。
     * @param x        按钮在屏幕上的x坐标。
     * @param y        按钮在屏幕上的y坐标。
     * @param widthIn  按钮的宽度。
     * @param heightIn 按钮的高度。
     * @param buttonText 按钮显示的文本（本例中为空字符串，因为我们使用的是纹理）。
     * @param texture  按钮使用的纹理资源位置。
     * @param textureX 纹理图集中按钮图像的x坐标。
     * @param textureY 纹理图集中按钮图像的y坐标。
     */
    public CustomButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText,
                        ResourceLocation texture, int textureX, int textureY) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        this.texture = texture;
        this.textureX = textureX;
        this.textureY = textureY;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            mc.getTextureManager().bindTexture(this.texture);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.x, this.y, this.textureX, this.textureY, this.width, this.height);

            boolean hovered = mouseX >= this.x && mouseY >= this.y &&
                    mouseX < this.x + this.width && mouseY < this.y + this.height;
            if (hovered) {
                // 绘制填充矩形
                drawRect(this.x, this.y, this.x + this.width, this.y + this.height, 0x586299FF);
            }
        }
    }
}

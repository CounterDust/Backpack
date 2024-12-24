package com.backpack.gui.select;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class GuiSelect extends GuiScreen {

    // 日志记录器
    public static final Logger LOGGER = LogManager.getLogger();

    // 定义背包GUI的纹理资源位置
    private static final ResourceLocation TEXTURE = new ResourceLocation("backpack:textures/gui/selectgui.png");

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // 绘制默认背景
        this.drawDefaultBackground();

        // 绑定纹理
        this.mc.getTextureManager().bindTexture(TEXTURE);

        // 计算原始GUI的位置和大小
        int guiWidth = 222; // GUI的宽度
        int guiHeight = 196; // GUI的高度

        // 应用缩放
        float scale = 0.3f; // 缩放因子，例如0.8表示缩小到80%

        // 计算缩放后的GUI尺寸
        int scaledGuiWidth = (int)(guiWidth * scale);
        int scaledGuiHeight = (int)(guiHeight * scale);

        // 计算缩放后的中心位置
        int x = (this.width - scaledGuiWidth) / 2; // 水平居中
        int y = (this.height - scaledGuiHeight) / 2; // 垂直居中

        // 保存当前变换矩阵
        GlStateManager.pushMatrix();

        // 平移至目标位置
        GlStateManager.translate(x, y, 0);

        // 应用缩放
        GlStateManager.scale(scale, scale, scale);

        // 绘制GUI背景
        // 注意: 由于我们已经通过translate移动到了正确的位置，这里传入0, 0作为起始点
        this.drawTexturedModalRect(0, 0, 0, 0, guiWidth, guiHeight);

        // 恢复原始变换矩阵
        GlStateManager.popMatrix();

        // 调用父类的drawScreen方法以确保其他必要的绘制操作
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        // 如果按下ESC键，关闭GUI
        if (keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            this.mc.displayGuiScreen(null);
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }
}

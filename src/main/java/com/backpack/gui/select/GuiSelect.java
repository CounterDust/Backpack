package com.backpack.gui.select;

import com.backpack.container.ContainerSelect;
import com.backpack.gui.button.CustomButton;
import com.backpack.inventory.backpack.InventoryBackpackFunction;
import com.backpack.keybindings.KeyBindings;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GuiSelect extends GuiContainer {

    // 日志记录器
    private static final Logger LOGGER = LogManager.getLogger();
    // 定义背包GUI的纹理资源位置
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("backpack:textures/gui/select.png");
    // 定义按钮的纹理资源位置
    private static final ResourceLocation BUTTON_TEXTURE = new ResourceLocation("backpack:textures/gui/icons.png");

    public GuiSelect(InventoryPlayer playerInventory, InventoryBackpackFunction backpackInventory) {
        super(new ContainerSelect(playerInventory, backpackInventory));
        this.xSize = 100;
        this.ySize = 88;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(new CustomButton(0, this.guiLeft + 43, this.guiTop + 37, 14, 14, "",
                BUTTON_TEXTURE, 17, 1));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        // 检查是否是打开背包的键被按下
        if (keyCode == KeyBindings.OPEN_BACKPACK.getKeyCode()) {
            // 关闭背包 GUI
            this.mc.player.closeScreen();
            return;
        }
        // 如果不是关闭背包的键，则调用父类的 keyTyped 方法
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {

        }
    }
}
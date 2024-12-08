package com.backpack.gui;

import com.backpack.container.BackpackContainer;
import com.backpack.inventory.InventoryBackpack;
import com.backpack.keybindings.KeyBindings;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

/**
 *GuiBackpack类扩展了GuiContainer，用于渲染背包的GUI。
 *这个类负责绘制背包界面的背景、前景以及处理用户输入。
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GuiBackpack extends GuiContainer {

    // 日志记录器
    public static final Logger LOGGER = LogManager.getLogger();

    // 定义背包GUI的纹理资源位置
    private static final ResourceLocation TEXTURE = new ResourceLocation("backpack:textures/gui/backpackgui.png");

    /**
     * 当前打开的背包 ItemStack
     */
    private final ItemStack openBackpackStack;

    /**
     * 构造函数，初始化背包GUI。
     *
     * @param playerInventory 玩家的库存
     * @param backpack        背包的库存
     * @param backpackSlotIndex 当前打开的背包所在的槽位索引
     */
    public GuiBackpack(InventoryPlayer playerInventory, InventoryBackpack backpack, int backpackSlotIndex) {
        super(new BackpackContainer(playerInventory, backpack));
        this.xSize = 176; // 宽度
        this.ySize = 186; // 高度

        // 获取当前打开的背包 ItemStack
        this.openBackpackStack = playerInventory.getStackInSlot(backpackSlotIndex);
    }

    /**
     * 绘制GUI容器的前景层。
     * 这里主要用于绘制背包界面的标题和玩家库存的标题。
     *
     * @param mouseX 鼠标X坐标
     * @param mouseY 鼠标Y坐标
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        ITextComponent backpackTitle = new TextComponentTranslation("container.backpack");
        ITextComponent inventoryTitle = new TextComponentTranslation("container.inventory");

        fontRenderer.drawString(backpackTitle.getFormattedText(), (this.xSize / 2 - this.fontRenderer.getStringWidth(backpackTitle.getFormattedText()) / 2), 6, 4210752);
        fontRenderer.drawString(inventoryTitle.getFormattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    /**
     * 绘制GUI容器的背景层。
     * 这个方法主要用于绘制背包界面的背景纹理。
     *
     * @param partialTicks 插值因子，用于平滑动画
     * @param mouseX       鼠标X坐标
     * @param mouseY       鼠标Y坐标
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        // 检查是否是打开背包的键被按下
        if (keyCode == KeyBindings.OPEN_BACKPACK.getKeyCode()) {
            // 关闭背包 GUI
            this.mc.player.closeScreen();
        } else {
            // 如果不是关闭背包的键，则调用父类的 keyTyped 方法
            super.keyTyped(typedChar, keyCode);
        }
    }

    /**
     * 处理鼠标点击事件，包括右键点击。
     *
     * @param slotIn      被点击的槽位
     * @param slotId      槽位ID
     * @param mouseButton 鼠标按钮（0: 左键, 1: 右键, 其他: 其他按钮）
     * @param type        点击类型
     */
    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        // 检查是否为快捷栏槽位且背包正在打开
        if (slotIn != null && slotIn.getHasStack() && (type == ClickType.PICKUP)) {
            ItemStack stack = slotIn.getStack();
            Item item = stack.getItem();

            // 检查槽位中的物品是否是当前打开的背包
            if (ItemStack.areItemStacksEqual(stack, this.openBackpackStack)) {
                LOGGER.info("Preventing pickup of the currently opened backpack.");
                return; // 阻止拾取
            }
        }

        // 调用父类的方法以处理其他情况
        super.handleMouseClick(slotIn, slotId, mouseButton, type);
    }
}
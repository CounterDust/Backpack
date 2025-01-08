package com.backpack.gui.backpack;

import com.backpack.container.ContainerBackpack;
import com.backpack.gui.button.Button;
import com.backpack.inventory.backpack.InventoryBackpackFunction;
import com.backpack.keybindings.KeyBindings;
import com.backpack.network.MemoryMessage;
import com.backpack.network.PacketHandler;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import org.lwjgl.opengl.GL11;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

/**
 * GuiBackpack类扩展了GuiContainer，用于渲染背包的GUI。
 * 这个类负责绘制背包界面的背景、前景以及处理用户输入。
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GuiBackpack extends GuiContainer {

    // 定义背包GUI的纹理资源位置
    private static final ResourceLocation TEXTURE = new ResourceLocation("backpack:textures/gui/backpack.png");
    // 当前打开的背包 ItemStack
    private final ItemStack openBackpackStack;
    // 记录槽位记忆编辑模式是否开启
    private int isEditMode = -1;

    private final InventoryBackpackFunction backpackInventory;

    /**
     * 构造函数，初始化背包GUI。
     *
     * @param playerInventory   玩家的库存
     * @param backpackInventory          背包的库存
     * @param backpackSlotIndex 当前打开的背包所在的槽位索引
     */
    public GuiBackpack(InventoryPlayer playerInventory, InventoryBackpackFunction backpackInventory, int backpackSlotIndex) {
        super(new ContainerBackpack(playerInventory, backpackInventory));
        this.xSize = 176; // 宽度
        this.ySize = 186; // 高度

        // 获取当前打开的背包 ItemStack
        this.openBackpackStack = playerInventory.getStackInSlot(backpackSlotIndex);
        this.backpackInventory = backpackInventory;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(new Button(0, this.guiLeft + 26, this.guiTop + 5, ""));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
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

        fontRenderer.drawString(backpackTitle.getFormattedText(), 8, 6, 4210752);
        fontRenderer.drawString(inventoryTitle.getFormattedText(), 8, this.ySize - 96 + 2, 4210752);

        // 开始绘制前设置渲染状态
        GlStateManager.pushMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting(); // 禁用光照
        GlStateManager.disableDepth(); // 禁用深度测试
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableBlend(); // 启用混合
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0); // 混合模式

        for (int slotId = 0; slotId < this.backpackInventory.getSizeInventory(); ++slotId) {
            Slot slot = this.inventorySlots.getSlot(slotId);
            ItemStack itemstack = slot.getStack();
            ItemStack memoryItem = this.backpackInventory.getMemoryItem(slotId);

            if (itemstack.isEmpty() && !(memoryItem.isEmpty())) {
                int x = slot.xPos;
                int y = slot.yPos;

                // 绘制物品
                this.mc.getRenderItem().renderItemAndEffectIntoGUI(memoryItem, x, y);

                // 叠加半透明黑色遮罩
                drawRect(x, y, x + 16, y + 16, 0x99000000); // 半透明黑色遮罩（99 表示透明度）
            }
        }

        // 结束绘制后恢复渲染状态
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
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
        if (isEditMode != -1) {
            ((ContainerBackpack) this.inventorySlots).handleSlotInteraction(slotId, mouseButton);
            PacketHandler.sendToServer(new MemoryMessage(slotId, mouseButton));
            return;
        } else {
            // 检查是否为快捷栏槽位且背包正在打开
            if (slotId >=0 && slotIn.getHasStack() && (type == ClickType.PICKUP)) {
                ItemStack stack = slotIn.getStack();
                // 检查槽位中的物品是否是当前打开的背包
                if (ItemStack.areItemStacksEqual(stack, openBackpackStack)) {
                    return; // 阻止拾取
                }
            }
        }
        // 调用父类的方法以处理其他情况
        super.handleMouseClick(slotIn, slotId, mouseButton, type);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        // 检查是否是打开背包的键被按下
        if (keyCode == KeyBindings.OPEN_BACKPACK.getKeyCode()) {
            // 关闭背包 GUI
            this.mc.player.closeScreen();
            return;
        }
        // 检查是否是丢弃物品的键被按下
        if (keyCode == mc.gameSettings.keyBindDrop.getKeyCode()) {
            Slot slot = getSlotUnderMouse();
            if (slot != null && slot.getHasStack()) {
                ItemStack itemStack = slot.getStack();
                // 检查槽位中的物品是否是当前打开的背包
                if (ItemStack.areItemStacksEqual(itemStack, this.openBackpackStack)) {
                    // 阻止丢弃行为
                    return;
                }
            }
        }
        // 如果不是关闭背包的键，则调用父类的 keyTyped 方法
        super.keyTyped(typedChar, keyCode);
    }


    /**
     * 按钮点击事件处理。
     *
     * @param button 被点击的按钮
     */
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            if (isEditMode == -1) {
                isEditMode = 0;
            } else if (isEditMode == 0) {
                isEditMode = -1;
            }
        }
    }
}
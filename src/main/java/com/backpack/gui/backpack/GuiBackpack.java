package com.backpack.gui.backpack;

import com.backpack.container.BackpackContainer;
import com.backpack.gui.button.Button;
import com.backpack.inventory.InventoryBackpack;
import com.backpack.keybindings.KeyBindings;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

/**
 * GuiBackpack类扩展了GuiContainer，用于渲染背包的GUI。
 * 这个类负责绘制背包界面的背景、前景以及处理用户输入。
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GuiBackpack extends GuiContainer {

    // 日志记录器
    public static final Logger LOGGER = LogManager.getLogger();

    // 定义背包GUI的纹理资源位置
    private static final ResourceLocation TEXTURE = new ResourceLocation("backpack:textures/gui/backpackgui.png");

    // 记录槽位记忆编辑模式是否开启
    private boolean isMemoryEditMode = false;

    /**
     * 当前打开的背包 ItemStack
     */
    private final ItemStack openBackpackStack;

    /**
     * 构造函数，初始化背包GUI。
     *
     * @param playerInventory   玩家的库存
     * @param backpack          背包的库存
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

        fontRenderer.drawString(backpackTitle.getFormattedText(), 8, 6, 4210752);
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
                    LOGGER.info("Preventing discard of the currently opened backpack by drop key.");
                    return;  // 不调用父类方法，阻止丢弃
                }
            }
        }
        // 如果不是关闭背包的键，则调用父类的 keyTyped 方法
        super.keyTyped(typedChar, keyCode);
    }

    /**
     * 处理鼠标点击事件，包括右键点击。
     *
     * @param slot      被点击的槽位
     * @param slotId    槽位ID
     * @param mouseButton 鼠标按钮（0: 左键, 1: 右键, 其他: 其他按钮）
     * @param type      点击类型
     */
    @Override
    protected void handleMouseClick(Slot slot, int slotId, int mouseButton, ClickType type) {
        // 检查是否为快捷栏槽位且背包正在打开
        if (slot != null && isMemoryEditMode) {
            NBTTagCompound nbt = this.openBackpackStack.getTagCompound();
            if (nbt == null) {
                nbt = new NBTTagCompound();
                this.openBackpackStack.setTagCompound(nbt);
            }

            String memorySlotKey = "MemorySlot" + slotId;

            // 右键点击：设置记忆插槽
            if (mouseButton == 1 && slot.getHasStack()) {
                ItemStack stack = slot.getStack();

                // 保存物品到 NBT
                NBTTagCompound memorySlotNbt = new NBTTagCompound();
                stack.writeToNBT(memorySlotNbt);
                nbt.setTag(memorySlotKey, memorySlotNbt);

                // 打印日志
                LOGGER.info("槽位 {} 设置为记忆插槽，记忆物品: {}", slotId, stack.getItem().getRegistryName());
            }
            // 左键点击：清空记忆插槽
            else if (mouseButton == 0) {
                // 从 NBT 中移除记忆插槽
                nbt.removeTag(memorySlotKey);
                LOGGER.info("槽位 {} 的记忆已清空", slotId);
            }

            // 阻止默认的点击行为
            return;
        } else if (slot != null && this.openBackpackStack.hasTagCompound()) {
            // 检查是否是记忆插槽
            NBTTagCompound nbt = this.openBackpackStack.getTagCompound();
            if (nbt.hasKey("MemorySlot" + slotId)) {
                NBTTagCompound memorySlotNbt = nbt.getCompoundTag("MemorySlot" + slotId);
                ItemStack memoryItem = new ItemStack(memorySlotNbt);

                // 如果槽位有记忆物品，检查玩家手中的物品是否与记忆物品匹配
                ItemStack heldItem = mc.player.inventory.getItemStack();
                if (!heldItem.isEmpty() && !ItemStack.areItemsEqual(heldItem, memoryItem)) {
                    // 如果物品不匹配，阻止放入
                    LOGGER.info("槽位 {} 只能放置记忆物品: {}", slotId, memoryItem.getItem().getRegistryName());
                    return; // 阻止放入
                }
            }
        }

        // 调用父类的方法以处理其他情况
        super.handleMouseClick(slot, slotId, mouseButton, type);
    }

    @Override
    public void initGui() {
        super.initGui();

        // 读取 NBT 中的记忆插槽信息
        NBTTagCompound nbt = this.openBackpackStack.getTagCompound();
        if (nbt != null) {
            for (int i = 0; i < this.inventorySlots.inventorySlots.size(); i++) {
                Slot slot = this.inventorySlots.inventorySlots.get(i);
                String memorySlotKey = "MemorySlot" + i;
                if (nbt.hasKey(memorySlotKey)) {
                    // 读取记忆插槽的物品
                    NBTTagCompound memorySlotNbt = nbt.getCompoundTag(memorySlotKey);
                    ItemStack memoryItem = new ItemStack(memorySlotNbt);

                    // 打印日志
                    LOGGER.info("恢复槽位 {} 的记忆物品: {}", i, memoryItem.getItem().getRegistryName());
                }
            }
        }

        // 添加按钮
        this.buttonList.add(new Button(0, this.guiLeft + 26, this.guiTop + 5, "开启槽位记忆编辑"));
    }

    /**
     * 按钮点击事件处理。
     *
     * @param button 被点击的按钮
     */
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            // 切换槽位记忆编辑模式
            isMemoryEditMode = !isMemoryEditMode;

            // 更新按钮文本
            button.displayString = isMemoryEditMode ? "关闭槽位记忆编辑" : "开启槽位记忆编辑";

            // 打印日志
            LOGGER.info("槽位记忆编辑模式: {}", isMemoryEditMode);
        }
    }
}
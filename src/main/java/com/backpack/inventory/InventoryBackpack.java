package com.backpack.inventory;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * InventoryBackpack 类实现了 IInventory 接口，用于实现一个基础的背包功能。
 * 背包拥有36个槽位，允许物品堆叠，并且所有物品都可以放入。
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class InventoryBackpack implements IInventory {

    // 日志记录器
    public static final Logger LOGGER = LogManager.getLogger();


    private final String name = "Backpack";


    private final boolean customName;


    private final NonNullList<ItemStack> inventoryContents = NonNullList.withSize(36, ItemStack.EMPTY);


    private final ItemStack backpackStack;

    /**
     * 构造函数初始化背包。
     *
     * @param backpackStack 背包物品堆
     */
    public InventoryBackpack(ItemStack backpackStack) {
        this.backpackStack = backpackStack;
        this.customName = false;

        // 从 NBT 数据中读取库存信息
        if (backpackStack.hasTagCompound()) {
            NBTTagCompound nbt = backpackStack.getTagCompound();
            InventoryManager.readFromNBT(this,nbt);
        }
    }

    /**
     * 获取库存名称。
     *
     * @return 库存名称
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * 检查库存是否有自定义名称。
     *
     * @return 是否有自定义名称
     */
    @Override
    public boolean hasCustomName() {
        return this.customName;
    }

    /**
     * 获取用于显示的库存名称。
     *
     * @return 库存名称的文本组件
     */
    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString(this.hasCustomName() ? this.getName() : "container.backpack");
    }

    /**
     * 获取背包的大小（即槽位数量）。
     *
     * @return 槽位数量
     */
    @Override
    public int getSizeInventory() {
        return this.inventoryContents.size();
    }

    /**
     * 检查库存是否为空。
     *
     * @return 如果所有槽位都为空，则返回true；否则返回false
     */
    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.inventoryContents) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取指定槽位的物品堆。
     *
     * @param index 槽位索引
     * @return 槽位中的物品堆
     */
    @Override
    public @Nonnull ItemStack getStackInSlot(int index) {
        return this.inventoryContents.get(index);
    }

    /**
     * 减少指定槽位中的物品数量。
     *
     * @param index 槽位索引
     * @param count 减少的数量
     * @return 剩余的物品堆
     */
    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = this.getStackInSlot(index);
        if (!stack.isEmpty()) {
            if (stack.getCount() <= count) {
                this.setInventorySlotContents(index, ItemStack.EMPTY);
            } else {
                stack = stack.splitStack(count);
            }
            this.markDirty();
        }
        return stack;
    }

    /**
     * 清空指定槽位的物品。
     *
     * @param index 槽位索引
     * @return 被清空的物品堆
     */
    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = this.getStackInSlot(index);
        if (!stack.isEmpty()) {
            this.setInventorySlotContents(index, ItemStack.EMPTY);
            this.markDirty();
        }
        return stack;
    }

    /**
     * 清空指定槽位的物品。
     *
     * @param index 槽位索引
     */
    @Override
    public void setInventorySlotContents(int index,  ItemStack stack) {
        // 设置槽位中的物品
        this.inventoryContents.set(index, stack);
    }

    /**
     * 获取每个库存槽位可以堆叠的最大物品数量。
     *
     * @return 每个槽位的物品堆叠上限
     */
    @Override
    public int getInventoryStackLimit() {
        return 64; // 默认堆叠限制为64
    }

    /**
     * 标记库存已更改，通常会触发保存。
     */
    @Override
    public void markDirty() {
        if (this.backpackStack != null) {
            NBTTagCompound nbt = this.backpackStack.getTagCompound();
            if (nbt == null) {
                nbt = new NBTTagCompound();
                this.backpackStack.setTagCompound(nbt);
                LOGGER.info("创建了新的 NBT 标签用于背包。");
            }
            // 将背包状态写入 NBT
            InventoryManager.writeToNBT(this, nbt);
        }
    }

    /**
     * 检查是否有玩家正在使用库存。
     *
     * @return 是否有玩家使用库存
     */
    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return true;
    }

    /**
     * 打开库存时调用。
     *
     * @param player 玩家对象
     */
    @Override
    public void openInventory(EntityPlayer player) {

    }

    /**
     * 关闭库存时调用。
     *
     * @param player 玩家对象
     */
    @Override
    public void closeInventory(EntityPlayer player) {
        this.markDirty();
    }

    /**
     * 判断物品是否可以放入指定槽位。
     *
     * @param index 槽位索引
     * @param stack 物品堆
     * @return 如果物品不是背包，则返回true；否则返回false
     */
    @Override
    public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
        return true;
    }

    /**
     * 获取指定ID的字段值。
     *
     * @param id 字段ID
     * @return 总是返回0，因为背包没有自定义字段
     */
    @Override
    public int getField(int id) {
        return 0;
    }

    /**
     * 设置指定ID的字段值。
     *
     * @param id    字段ID
     * @param value 字段值
     */
    @Override
    public void setField(int id, int value) {
    }

    /**
     * 获取字段数量。
     *
     * @return 总是返回0，因为背包没有自定义字段
     */
    @Override
    public int getFieldCount() {
        return 0;
    }

    /**
     * 清空背包内所有槽位的物品。
     */
    @Override
    public void clear() {
        for (int i = 0; i < this.getSizeInventory(); ++i) {
            this.setInventorySlotContents(i, ItemStack.EMPTY);
        }
        this.markDirty();  // 确保清空后保存数据
    }
}
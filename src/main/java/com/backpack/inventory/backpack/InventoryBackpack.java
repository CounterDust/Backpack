package com.backpack.inventory.backpack;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
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
public class InventoryBackpack implements IInventory, INBTSerializable<NBTTagCompound> {

    // 日志记录器
    private static final Logger LOGGER = LogManager.getLogger();

    private final String name = "Backpack";
    private final boolean customName = false;
    private final NonNullList<ItemStack> inventoryContents;
    private final ItemStack openBackpackStack;

    // 定义 NBT 标签名称，用于存储背包中的物品列表
    private static final String ITEMS_TAG = "Items";

    /**
     * 构造函数初始化背包。
     *
     * @param openBackpackStack 背包物品堆
     */
    public InventoryBackpack(ItemStack openBackpackStack) {
        this.openBackpackStack = openBackpackStack;
        this.inventoryContents = NonNullList.withSize(36, ItemStack.EMPTY);
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
        return new TextComponentString(hasCustomName() ? getName() : "container.backpack");
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
        ItemStack stack = getStackInSlot(index);
        if (!stack.isEmpty()) {
            if (stack.getCount() <= count) {
                setInventorySlotContents(index, ItemStack.EMPTY);
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
        ItemStack stack = getStackInSlot(index);
        if (!stack.isEmpty()) {
            setInventorySlotContents(index, ItemStack.EMPTY);
            this.markDirty();
        }
        return stack;
    }

    /**
     * 设置指定槽位的物品。
     *
     * @param index 槽位索引
     * @param stack 物品堆
     */
    @Override
    public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
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
        if (openBackpackStack != null) {
            NBTTagCompound nbt = openBackpackStack.getTagCompound();
            if (nbt == null) {
                nbt = new NBTTagCompound();
                openBackpackStack.setTagCompound(nbt);
            }
            nbt.merge(this.serializeNBT());
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
        for (int i = 0; i < getSizeInventory(); ++i) {
            setInventorySlotContents(i, ItemStack.EMPTY);
        }
        this.markDirty();
    }

    /**
     * 从 NBT 数据中读取背包的库存信息，并将其填充到背包的槽位中。
     *
     * @param compound NBT 标签复合对象，包含背包的库存信息。
     */
    @Override
    public void deserializeNBT(NBTTagCompound compound) {
        if (compound == null) {
            LOGGER.debug("NBT 复合标签为空，无法读取背包数据");
            return;
        }
        // 检查 NBT 中是否存在物品的标签 (ITEMS_TAG)
        if (compound.hasKey(ITEMS_TAG, Constants.NBT.TAG_LIST)) {
            // 从 NBT 中获取记忆物品的 NBTTagList
            NBTTagList itemList = compound.getTagList(ITEMS_TAG, Constants.NBT.TAG_COMPOUND);
            // 遍历 NBTTagList 中的每一个记忆物品
            for (int i = 0; i < itemList.tagCount(); i++) {
                // 获取当前物品的 NBTTagCompound
                NBTTagCompound itemTag = itemList.getCompoundTagAt(i);
                // 从 NBTTagCompound 中读取槽位索引 (Slot)
                byte slotIndex = itemTag.getByte("Slot");
                // 确保槽位索引在有效范围内
                if (slotIndex >= 0 && slotIndex < getSizeInventory()) {
                    // 使用 NBTTagCompound 创建 ItemStack 对象
                    ItemStack stack = new ItemStack(itemTag);
                    // 将 ItemStack 设置到对应的物品列表中
                    this.setInventorySlotContents(slotIndex, stack);
                }
            }
        }
    }

    /**
     * 将背包的库存信息写入 NBT 数据，以便保存背包的状态。
     *
     * @return 包含背包库存信息的 NBT 标签复合对象。
     */
    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        // 创建一个新的 NBTTagList 用于存储记忆物品信息
        NBTTagList itemList = new NBTTagList();
        // 遍历记忆物品列表中的每一个槽位
        for (int i = 0; i < getSizeInventory(); i++) {
            // 获取当前槽位的记忆物品
            ItemStack stack = getStackInSlot(i);
            // 检查当前槽位是否包含有效的物品栈（非空）
            if (!stack.isEmpty()) {
                // 创建一个新的 NBTTagCompound 用于存储单个记忆物品的信息
                NBTTagCompound itemTag = new NBTTagCompound();
                // 将槽位索引写入 NBTTagCompound，使用 "Slot" 作为键名
                itemTag.setByte("Slot", (byte) i);
                // 调用 ItemStack 的 writeToNBT 方法，将物品栈的所有数据写入 NBTTagCompound 中
                stack.writeToNBT(itemTag);
                // 将该物品栈的 NBTTagCompound 添加到 memoryItemList 中
                itemList.appendTag(itemTag);
            }
        }
        // 将 itemList 设置为 NBTTagCompound 的一个标签，使用 ITEMS_TAG 作为键名
        compound.setTag(ITEMS_TAG, itemList);
        // 返回包含所有数据的 NBTTagCompound
        return compound;
    }
}
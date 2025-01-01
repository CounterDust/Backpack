package com.backpack.inventory;

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
    public static final Logger LOGGER = LogManager.getLogger();

    private final String name = "Backpack";
    private final boolean customName = false;
    private final NonNullList<ItemStack> inventoryContents = NonNullList.withSize(36, ItemStack.EMPTY);
    public final ItemStack openBackpackStack;

    // 定义 NBT 标签名称，用于存储背包中的物品列表
    private static final String ITEMS_TAG = "Items";

    /**
     * 构造函数初始化背包。
     *
     * @param openBackpackStack 背包物品堆
     */
    public InventoryBackpack(ItemStack openBackpackStack) {
        this.openBackpackStack = openBackpackStack;
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
        return inventoryContents.size();
    }

    /**
     * 检查库存是否为空。
     *
     * @return 如果所有槽位都为空，则返回true；否则返回false
     */
    @Override
    public boolean isEmpty() {
        for (ItemStack stack : inventoryContents) {
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
        return inventoryContents.get(index);
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
            markDirty();
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
            markDirty();
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
        inventoryContents.set(index, stack);
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
        markDirty();
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
        this.markDirty();  // 确保清空后保存数据
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
        if (compound.hasKey(ITEMS_TAG, Constants.NBT.TAG_LIST)) {
            NBTTagList itemList = compound.getTagList(ITEMS_TAG, Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < itemList.tagCount(); i++) {
                NBTTagCompound itemTag = itemList.getCompoundTagAt(i);
                byte slotIndex = itemTag.getByte("Slot");
                if (slotIndex >= 0 && slotIndex < getSizeInventory()) {
                    ItemStack stack = new ItemStack(itemTag);
                    setInventorySlotContents(slotIndex, stack);
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

        // 创建一个新的 NBTTagList 用于存储物品栈信息。
        NBTTagList itemList = new NBTTagList();
        // 遍历背包中的所有槽位，获取每个槽位的物品栈。
        for (int i = 0; i < getSizeInventory(); i++) {
            ItemStack stack = getStackInSlot(i);
            // 如果当前槽位的物品栈不为空，则将其信息写入 NBTTagList。
            if (!stack.isEmpty()) {
                // 创建一个新的 NBTTagCompound 用于存储单个物品栈的信息。
                NBTTagCompound itemTag = new NBTTagCompound();
                // 将槽位索引写入 NBTTagCompound，使用 "Slot" 作为键名。
                itemTag.setByte("Slot", (byte) i);
                // 调用 ItemStack 的 serializeNBT 方法，将物品栈的所有数据写入 NBTTagCompound 中。
                stack.writeToNBT(itemTag);
                // 将该物品栈的 NBTTagCompound 添加到 itemList 中。
                itemList.appendTag(itemTag);
            }
        }
        compound.setTag(ITEMS_TAG, itemList);

        return compound;
    }
}
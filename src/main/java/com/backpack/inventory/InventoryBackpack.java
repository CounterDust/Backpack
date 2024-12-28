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

    // 定义 NBT 标签名称，用于存储背包中的物品列表
    private static final String ITEMS_TAG = "Items";

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
            readFromNBT(this,nbt);
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
            writeToNBT(this, nbt);
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

    /**
     * 从 NBT 数据中读取背包的库存信息，并将其填充到背包的槽位中。
     *
     * @param inventory 背包库存实例，表示要填充的背包。
     * @param compound  NBT 标签复合对象，包含背包的库存信息。
     */
    public static void readFromNBT(InventoryBackpack inventory, NBTTagCompound compound) {
        // 检查 NBT 数据中是否包含 "Items" 标签，且该标签是一个标签列表
        if (compound.hasKey(ITEMS_TAG, Constants.NBT.TAG_LIST)) {
            // 获取 "Items" 标签列表，其中每个标签代表一个槽位的物品信息
            NBTTagList itemList = compound.getTagList(ITEMS_TAG, Constants.NBT.TAG_COMPOUND);
            // 遍历 "Items" 标签列表中的每个标签
            for (int i = 0; i < itemList.tagCount(); i++) {
                // 获取当前标签（每个标签代表一个槽位的物品）
                NBTTagCompound itemTag = itemList.getCompoundTagAt(i);
                // 获取槽位索引（"Slot" 标签），表示该物品所在的槽位
                byte slotIndex = itemTag.getByte("Slot");
                // 检查槽位索引是否有效（必须在 0 到 35 之间）
                if (slotIndex >= 0 && slotIndex < inventory.getSizeInventory()) {
                    // 将物品栈从 NBT 标签中读取出来，并设置到对应的槽位
                    ItemStack stack = new ItemStack(itemTag);
                    inventory.setInventorySlotContents(slotIndex, stack);
                } else {
                    // 如果槽位索引无效，记录警告日志，提示开发者或玩家注意这个问题
                    InventoryBackpack.LOGGER.warn("无效的槽索引: {}", slotIndex);
                }
            }
        }
    }

    /**
     * 将背包的库存信息写入 NBT 数据，以便保存背包的状态。
     *
     * @param inventory 背包库存实例，表示要保存的背包。
     * @param compound  NBT 标签复合对象，用于存储背包的库存信息。
     */
    public static void writeToNBT(InventoryBackpack inventory, NBTTagCompound compound) {
        // 创建一个新的标签列表，用于存储背包中所有非空槽位的物品信息
        NBTTagList itemList = new NBTTagList();
        // 遍历背包中的所有槽位
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            // 获取当前槽位的物品栈
            ItemStack stack = inventory.getStackInSlot(i);
            // 如果槽位中有物品，则将其信息写入 NBT 标签并添加到标签列表中
            if (!stack.isEmpty()) {
                // 创建一个新的 NBT 复合标签，用于存储该槽位的物品信息
                NBTTagCompound itemTag = new NBTTagCompound();
                // 设置槽位索引（"Slot" 标签），表示该物品所在的槽位
                itemTag.setByte("Slot", (byte) i);
                // 将物品栈的信息写入 NBT 标签
                stack.writeToNBT(itemTag);
                // 将该物品的 NBT 标签添加到标签列表中
                itemList.appendTag(itemTag);
            }
        }
        // 将标签列表写入 NBT 复合对象，使用 "Items" 作为标签名称
        compound.setTag(ITEMS_TAG, itemList);
    }
}
package com.backpack.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

/**
 * InventoryManager 类负责管理背包库存的 NBT 数据读写操作。
 * 它提供了两个主要方法：`readFromNBT` 和 `writeToNBT`，用于从 NBT 数据中读取库存信息或将库存信息写入 NBT 数据。
 */
public class InventoryManager {

    // 定义 NBT 标签名称，用于存储背包中的物品列表
    private static final String ITEMS_TAG = "Items";

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
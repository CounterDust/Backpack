package com.backpack.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public class InventoryManager {

    private static final String ITEMS_TAG = "Items";

    /**
     * 从 NBT 数据中读取库存信息。
     *
     * @param inventory 库存实例
     * @param compound  NBT 标签
     */
    public static void readFromNBT(InventoryBackpack inventory, NBTTagCompound compound) {
        if (compound.hasKey(ITEMS_TAG, Constants.NBT.TAG_LIST)) {
            NBTTagList itemList = compound.getTagList(ITEMS_TAG, Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < itemList.tagCount(); i++) {
                NBTTagCompound itemTag = itemList.getCompoundTagAt(i);
                byte slot = itemTag.getByte("Slot");
                if (slot >= 0 && slot < inventory.getSizeInventory()) {
                    inventory.setInventorySlotContents(slot, new ItemStack(itemTag));
                } else {
                    InventoryBackpack.LOGGER.warn("无效的槽索引 {}", slot);
                }
            }
        }
    }

    /**
     * 将库存信息写入 NBT 数据。
     *
     * @param inventory 库存实例
     * @param compound  NBT 标签
     */
    public static void writeToNBT(InventoryBackpack inventory, NBTTagCompound compound) {
        NBTTagList itemList = new NBTTagList();
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setByte("Slot", (byte) i);
                stack.writeToNBT(itemTag);
                itemList.appendTag(itemTag);
            }
        }
        compound.setTag(ITEMS_TAG, itemList);
    }

}

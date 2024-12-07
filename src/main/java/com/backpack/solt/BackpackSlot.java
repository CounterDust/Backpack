package com.backpack.solt;

import com.backpack.item.BackpackItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BackpackSlot extends Slot {
    private static final Logger LOGGER = LogManager.getLogger();

    public BackpackSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        // 禁止放入背包类物品
        if (stack.getItem() instanceof BackpackItem) {
            LOGGER.info("Item is a backpack, cannot place in slot {}", this.getSlotIndex());
            return false;
        }
        return super.isItemValid(stack);
    }

    @Override
    public void putStack(ItemStack stack) {
        // 在这里也可以添加额外的逻辑，但通常来说 isItemValid 应该已经足够了
        if (isItemValid(stack)) {
            super.putStack(stack);
        } else {
            // 如果物品无效，不做任何操作，保持原状
            super.putStack(ItemStack.EMPTY);
        }
    }
}

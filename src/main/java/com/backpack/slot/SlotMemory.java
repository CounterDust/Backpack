package com.backpack.slot;

import net.minecraft.inventory.IInventory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SlotMemory extends SlotBackpack{

    // 日志对象，用于记录信息
    private static final Logger LOGGER = LogManager.getLogger();

    public SlotMemory(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }
}

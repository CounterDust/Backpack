package com.backpack.container;

import com.backpack.inventory.backpack.InventoryBackpackFunction;
import com.backpack.slot.SlotBackpack;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ContainerSelect extends Container {

    private final InventoryBackpackFunction backpackInventory;

    public ContainerSelect(InventoryBackpackFunction backpackInventory) {

        this.backpackInventory = backpackInventory;

        addSlotToContainer(new SlotBackpack(backpackInventory, 0, 42, 7));
        addSlotToContainer(new SlotBackpack(backpackInventory, 1, 62, 11));
        addSlotToContainer(new SlotBackpack(backpackInventory, 2, 73, 31));
        addSlotToContainer(new SlotBackpack(backpackInventory, 3, 72, 51));
        addSlotToContainer(new SlotBackpack(backpackInventory, 4, 52, 65));
        addSlotToContainer(new SlotBackpack(backpackInventory, 5, 32, 65));
        addSlotToContainer(new SlotBackpack(backpackInventory, 6, 12, 51));
        addSlotToContainer(new SlotBackpack(backpackInventory, 7, 11, 31));
        addSlotToContainer(new SlotBackpack(backpackInventory, 8, 22, 11));

    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.backpackInventory.isUsableByPlayer(playerIn);
    }
}

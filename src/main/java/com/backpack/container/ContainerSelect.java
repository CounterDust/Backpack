package com.backpack.container;

import com.backpack.inventory.backpack.InventoryBackpackFunction;
import com.backpack.slot.SlotBackpack;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ContainerSelect extends Container {

    private static final Logger LOGGER = LogManager.getLogger();

    private final InventoryPlayer playerInventory;

    private final InventoryBackpackFunction backpackInventory;

    public ContainerSelect(InventoryPlayer playerInventory, InventoryBackpackFunction backpackInventory) {
        this.playerInventory = playerInventory;
        this.backpackInventory = backpackInventory;

        addSlotToContainer(new SlotBackpack(backpackInventory, 27, 42, 7));
        addSlotToContainer(new SlotBackpack(backpackInventory, 28, 62, 11));
        addSlotToContainer(new SlotBackpack(backpackInventory, 29, 73, 31));
        addSlotToContainer(new SlotBackpack(backpackInventory, 30, 72, 51));
        addSlotToContainer(new SlotBackpack(backpackInventory, 31, 52, 65));
        addSlotToContainer(new SlotBackpack(backpackInventory, 32, 32, 65));
        addSlotToContainer(new SlotBackpack(backpackInventory, 33, 12, 51));
        addSlotToContainer(new SlotBackpack(backpackInventory, 34, 11, 31));
        addSlotToContainer(new SlotBackpack(backpackInventory, 35, 22, 11));

    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.backpackInventory.isUsableByPlayer(playerIn);
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        ItemStack itemstack = ItemStack.EMPTY;

        // 检查槽位是否在快捷栏范围内（0-8）
        if (slotId >= 0 && slotId < 9) {
            Slot selectslot = this.inventorySlots.get(slotId);
            if (selectslot != null) {
                ItemStack selectitemstack = selectslot.getStack();

                // 左键点击
                if (dragType == 0) {
                    if (selectitemstack.isEmpty()) {
                        return itemstack;
                    }
                    itemstack = selectitemstack.copy();

                    // 如果玩家手持的物品为空
                    if (player.getHeldItemMainhand().isEmpty()) {
                        player.setHeldItem(EnumHand.MAIN_HAND, selectitemstack);
                        selectslot.putStack(ItemStack.EMPTY);
                        selectslot.onSlotChanged();

                        if (!player.world.isRemote) {
                            player.closeScreen();
                        }
                    } else {
                        // 快捷栏索引
                        int currentHotbarSlot = player.inventory.currentItem;
                        boolean found = false;
                        int emptySlotIndex = -1;

                        // 在快捷栏中查找空位
                        for (int i = 0; i < 9 && !found; i++) {
                            int leftIndex = currentHotbarSlot - i - 1;
                            if (leftIndex >= 0) {
                                ItemStack leftStack = player.inventory.mainInventory.get(leftIndex);
                                if (leftStack.isEmpty()) {
                                    emptySlotIndex = leftIndex;
                                    found = true;
                                }
                            }

                            if (!found) {
                                int rightIndex = currentHotbarSlot + i + 1;
                                if (rightIndex < 9) {
                                    ItemStack rightStack = player.inventory.mainInventory.get(rightIndex);
                                    if (rightStack.isEmpty()) {
                                        emptySlotIndex = rightIndex;
                                        found = true;
                                    }
                                }
                            }
                        }

                        // 如果快捷栏中没有找到空位，则在物品栏（索引9-35）中查找
                        if (emptySlotIndex == -1) {
                            for (int i = 9; i <= 35; i++) {
                                ItemStack inventoryStack = player.inventory.mainInventory.get(i);
                                if (inventoryStack.isEmpty()) {
                                    emptySlotIndex = i;
                                    break;
                                }
                            }
                        }

                        // 如果找到了空位
                        if (emptySlotIndex != -1){
                            ItemStack mainStack = player.getHeldItemMainhand();
                            player.inventory.mainInventory.set(emptySlotIndex, mainStack);
                            player.setHeldItem(EnumHand.MAIN_HAND, selectitemstack);
                            selectslot.putStack(ItemStack.EMPTY);
                            selectslot.onSlotChanged();

                            if (!player.world.isRemote) {
                                player.closeScreen();
                            }
                        }
                    }
                }
            }
        }
        return itemstack;
    }
}

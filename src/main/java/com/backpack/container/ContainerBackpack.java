package com.backpack.container;

import com.backpack.inventory.backpack.InventoryBackpackFunction;
import com.backpack.slot.SlotBackpack;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ContainerBackpack extends Container {

    // 背包库存实例
    private final InventoryBackpackFunction backpackInventory;

    /**
     * 构造函数
     * 初始化背包容器，包括背包槽位和玩家库存槽位
     *
     * @param playerInventory   玩家库存
     * @param backpackInventory 背包实例
     */
    public ContainerBackpack(InventoryPlayer playerInventory, InventoryBackpackFunction backpackInventory) {
        this.backpackInventory = backpackInventory;

        // 添加背包槽位
        for (int y = 0; y < 4; ++y) {
            for (int x = 0; x < 9; ++x) {
                addSlotToContainer(new SlotBackpack(backpackInventory, x + y * 9, 8 + x * 18, 18 + y * 18));
            }
        }

        // 添加玩家库存槽位
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                addSlotToContainer(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 82 + y * 18 + 22));
            }
        }

        // 添加玩家热键栏槽位
        for (int x = 0; x < 9; ++x) {
            addSlotToContainer(new Slot(playerInventory, x, 8 + x * 18, 140 + 22));
        }
    }

    /**
     * 检查玩家是否可以与背包交互
     *
     * @param playerIn 玩家实体
     * @return 如果背包可以被玩家使用，则返回true
     */
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.backpackInventory.isUsableByPlayer(playerIn);
    }

    /**
     * 将指定槽位的物品堆转移到另一个槽位
     * 用于在玩家按下shift键时，将物品从背包移动到玩家库存，或反之
     *
     * @param playerIn 玩家实体
     * @param index    指定槽位的索引
     * @return 转移后的物品堆，如果无法转移，则返回空物品堆
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        Slot slot = inventorySlots.get(index);

        // 如果槽位为空或没有物品堆，直接返回空物品堆
        if (slot == null || !slot.getHasStack()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getStack();
        ItemStack copy = stack.copy();

        // 获取背包和总槽位的数量
        int backpackSize = this.backpackInventory.getSizeInventory();
        int totalSlots = this.inventorySlots.size();

        // 确定起始和结束槽位索引
        int startIndex, endIndex;
        if (index < backpackSize) {
            // 当前槽位是背包槽位，尝试将其移到玩家库存
            startIndex = backpackSize;  // 起始槽位索引（玩家库存的第一个槽位）
            endIndex = totalSlots;      // 结束槽位索引（总槽位数量）
        } else {
            // 当前槽位是玩家库存槽位，尝试将其移到背包
            startIndex = 0;             // 起始槽位索引（背包的第一个槽位）
            endIndex = backpackSize;    // 结束槽位索引（背包槽位数量）
        }

        // 尝试合并物品堆
        boolean mergeSuccessful = mergeItemStack(stack, startIndex, endIndex, false);

        // 如果无法合并物品堆，则返回空物品堆
        if (!mergeSuccessful) {
            return ItemStack.EMPTY;
        }

        // 如果物品堆已经完全移出当前槽位，则将槽位设置为空
        if (stack.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
        } else {
            // 如果物品堆还有剩余，则更新槽位中的物品堆
            slot.putStack(stack);
            slot.onSlotChanged();  // 通知槽位内容已更改
        }

        // 触发 onTake 事件，表示玩家拿走了物品
        slot.onTake(playerIn, stack);

        // 如果物品堆数量没有变化，返回空物品堆；否则返回转移前的物品堆副本
        return stack.getCount() == copy.getCount() ? ItemStack.EMPTY : copy;
    }

    @Override
    protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        boolean flag = false;
        int i = startIndex;

        if (reverseDirection) {
            i = endIndex - 1;
        }

        boolean flag1 = false;
        boolean flag2 = false;
        if (startIndex == 0) {
            flag1 = true;
            flag2 = true;
        }

        if (stack.isStackable()) {
            while (!stack.isEmpty()) {
                if (reverseDirection) {
                    if (flag1 && i < startIndex) {
                        flag1 = false;
                        i = endIndex - 1;
                    } else if (i < startIndex) {
                        break;
                    }
                } else {
                    if (flag1 && i >= endIndex) {
                        flag1 = false;
                        i = startIndex;
                    } else if (i >= endIndex) {
                        break;
                    }
                }

                Slot slot = this.inventorySlots.get(i);
                ItemStack itemstack = slot.getStack();
                if (flag1){
                    ItemStack memoryitemstack = this.backpackInventory.getMemoryItem(i);
                    if ((itemstack.getItem() == memoryitemstack.getItem()) && !itemstack.isEmpty() && itemstack.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getMetadata() == itemstack.getMetadata()) && ItemStack.areItemStackTagsEqual(stack, itemstack)) {
                        int j = itemstack.getCount() + stack.getCount();
                        int maxSize = Math.min(slot.getSlotStackLimit(), stack.getMaxStackSize());

                        if (j <= maxSize) {
                            stack.setCount(0);
                            itemstack.setCount(j);
                            slot.onSlotChanged();
                            flag = true;
                        } else if (itemstack.getCount() < maxSize) {
                            stack.shrink(maxSize - itemstack.getCount());
                            itemstack.setCount(maxSize);
                            slot.onSlotChanged();
                            flag = true;
                        }
                    }
                } else if (!itemstack.isEmpty() && itemstack.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getMetadata() == itemstack.getMetadata()) && ItemStack.areItemStackTagsEqual(stack, itemstack)) {
                    int j = itemstack.getCount() + stack.getCount();
                    int maxSize = Math.min(slot.getSlotStackLimit(), stack.getMaxStackSize());

                    if (j <= maxSize) {
                        stack.setCount(0);
                        itemstack.setCount(j);
                        slot.onSlotChanged();
                        flag = true;
                    } else if (itemstack.getCount() < maxSize) {
                        stack.shrink(maxSize - itemstack.getCount());
                        itemstack.setCount(maxSize);
                        slot.onSlotChanged();
                        flag = true;
                    }
                }

                if (reverseDirection) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        if (!stack.isEmpty()) {
            if (reverseDirection) {
                i = endIndex - 1;
            } else {
                i = startIndex;
            }

            while (true) {
                if (reverseDirection) {
                    if (i < startIndex && flag2) {
                        flag2 = false;
                        i = endIndex - 1;
                    } else if (i < startIndex) {
                        break;
                    }
                } else {
                    if (i >= endIndex && flag2) {
                        flag2 = false;
                        i = startIndex;
                    } else if (i >= endIndex) {
                        break;
                    }
                }

                Slot slot1 = this.inventorySlots.get(i);
                ItemStack itemstack1 = slot1.getStack();
                if (flag2){
                    ItemStack memoryitemstack1 = this.backpackInventory.getMemoryItem(i);
                    if ((stack.getItem() == memoryitemstack1.getItem()) && itemstack1.isEmpty()) {
                        if (stack.getCount() > slot1.getSlotStackLimit()) {
                            slot1.putStack(stack.splitStack(slot1.getSlotStackLimit()));
                        } else {
                            slot1.putStack(stack.splitStack(stack.getCount()));
                        }

                        slot1.onSlotChanged();
                        flag = true;
                        break;
                    }
                } else if (itemstack1.isEmpty() && slot1.isItemValid(stack)) {
                    if (stack.getCount() > slot1.getSlotStackLimit()) {
                        slot1.putStack(stack.splitStack(slot1.getSlotStackLimit()));
                    } else {
                        slot1.putStack(stack.splitStack(stack.getCount()));
                    }

                    slot1.onSlotChanged();
                    flag = true;
                    break;
                }

                if (reverseDirection) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        return flag;
    }

    public void handleSlotInteraction(int slotId, int mouseButton) {
        if (slotId >= 0 && slotId < this.backpackInventory.getSizeInventory()) {
            if (mouseButton == 0) {
                Slot slot = this.inventorySlots.get(slotId);
                ItemStack stack = slot.getStack();
                if (!stack.isEmpty()) {
                    this.backpackInventory.setMemoryItem(slotId, stack);
                }
            } else if (mouseButton == 1) {
                this.backpackInventory.clearMemoryItem(slotId);
            }
        }
    }
}
package com.backpack.container;

import com.backpack.inventory.InventoryBackpack;
import com.backpack.item.BackpackItem;
import com.backpack.solt.BackpackSlot;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BackpackContainer extends Container {

    // 日志记录器
    public static final Logger LOGGER = LogManager.getLogger();

    // 背包库存实例
    private final InventoryBackpack backpackInventory;

    /**
     * 构造函数
     * 初始化背包容器，包括背包槽位和玩家库存槽位
     *
     * @param playerInventory 玩家库存
     * @param backpack        背包实例
     */
    public BackpackContainer(InventoryPlayer playerInventory, InventoryBackpack backpack) {
        this.backpackInventory = backpack;

        // 添加背包槽位，使用自定义的 BackpackSlot 类
        for (int y = 0; y < 4; ++y) {
            for (int x = 0; x < 9; ++x) {
                addSlotToContainer(new BackpackSlot(backpack, x + y * 9, 8 + x * 18, 18 + y * 18));
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
    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        // 检查玩家是否可以使用背包
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
        if (slot == null || !slot.getHasStack()) {
            LOGGER.info("Slot is empty or null, returning empty item stack.");
            return ItemStack.EMPTY;
        }

        ItemStack originalStack = slot.getStack();
        ItemStack copyStack = originalStack.copy();

        // 定义背包槽位和玩家库存槽位的范围
        int backpackSize = this.backpackInventory.getSizeInventory();
        int totalSlots = this.inventorySlots.size();

        // 如果物品来自背包槽位
        if (index < backpackSize) {
            LOGGER.info("Item from backpack slot, trying to merge into player inventory.");
            // 尝试将物品合并到玩家库存中
            if (!this.mergeItemStack(originalStack, backpackSize, totalSlots, false)) {
                LOGGER.warn("Failed to merge item to player inventory.");
                return ItemStack.EMPTY;
            }
        } else {
            // 如果物品来自玩家库存
            LOGGER.info("Item from player inventory, checking if it can be merged into backpack.");
            if (originalStack.getItem() instanceof BackpackItem) {
                // 防止背包嵌套
                LOGGER.info("Nested backpack detected and prevented.");
                // 不允许将背包放入背包槽位
                return ItemStack.EMPTY;
            } else {
                // 尝试将物品合并到背包中
                if (!this.mergeItemStack(originalStack, 0, backpackSize, false)) {
                    LOGGER.warn("Failed to merge item to backpack.");
                    return ItemStack.EMPTY;
                }
            }
        }

        // 检查物品是否完全转移
        if (originalStack.getCount() == 0) {
            slot.putStack(ItemStack.EMPTY);
            LOGGER.info("Item fully transferred, slot now empty.");
        } else {
            slot.putStack(originalStack);
            slot.onSlotChanged();
            LOGGER.info("Item partially transferred, remaining count: {}", originalStack.getCount());
        }

        // 更新玩家手中的物品
        if (originalStack.getCount() == copyStack.getCount()) {
            LOGGER.info("No items were transferred, returning empty item stack.");
            return ItemStack.EMPTY;
        }

        slot.onTake(playerIn, originalStack);
        LOGGER.info("Item taken by player, final stack: {}", originalStack);

        return copyStack;
    }

    /**
     * 当 GUI 关闭时调用此方法
     * 用于保存背包库存数据到 ItemStack 的 NBT 标签中
     *
     * @param playerIn 玩家实体
     */
    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        // 当 GUI 关闭时，保存背包库存数据
        this.backpackInventory.markDirty();
        this.backpackInventory.writeToNBT();
    }

    /**
     * 获取背包库存实例
     *
     * @return 背包库存
     */
    public InventoryBackpack getBackpackInventory() {
        return this.backpackInventory;
    }
}

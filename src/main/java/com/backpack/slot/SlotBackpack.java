package com.backpack.slot;

import com.backpack.inventory.InventoryBackpackFunction;
import com.backpack.item.ItemModBackpack;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * 用于背包的特殊槽位类
 * 继承自 Minecraft 的标准槽位类，用于定义背包中物品的放置规则
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SlotBackpack extends Slot {

    private final InventoryBackpackFunction backpackInventory;
    private final int slotId;

    /**
     * 构造函数
     * 初始化背包槽位
     *
     * @param backpackInventory 背包的库存对象
     * @param index       槽位索引
     * @param xPosition   槽位的X坐标
     * @param yPosition   槽位的Y坐标
     */
    public SlotBackpack(InventoryBackpackFunction backpackInventory, int index, int xPosition, int yPosition) {
        super(backpackInventory, index, xPosition, yPosition);
        this.backpackInventory = backpackInventory;
        this.slotId = index;
    }

    /**
     * 检查物品是否可以放入此槽位
     * 重写此方法以禁止放入背包类物品
     *
     * @param stack 待检查的物品堆
     * @return 如果物品有效则返回true，否则返回false
     */
    @Override
    public boolean isItemValid(ItemStack stack) {
        // 如果尝试放入的是背包本身，则不允许放入
        if (stack.getItem() instanceof ItemModBackpack) {
            return false;
        }

        // 获取当前槽位的记忆物品
        ItemStack memoryItem = this.backpackInventory.getMemoryItem(slotId);

        // 如果记忆物品为空，则允许放入任何物品
        if (memoryItem.isEmpty()) {
            return true;
        }

        // 比较放入的物品与记忆物品是否相同
        return ItemStack.areItemStacksEqual(stack, memoryItem);
    }

    /**
     * 将物品放入槽位
     * 重写此方法以确保只放入有效的物品
     *
     * @param stack 要放入的物品堆
     */
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

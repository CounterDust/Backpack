package com.backpack.container;

import com.backpack.inventory.InventoryBackpackFunction;
import com.backpack.slot.SlotBackpack;
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
public class ContainerBackpack extends Container {

    // 日志记录器
    private static final Logger LOGGER = LogManager.getLogger();

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
        // 获取指定索引的槽位
        Slot slot = inventorySlots.get(index);

        // 如果槽位为空或槽位中没有物品，则直接返回空物品堆
        if (slot == null || !slot.getHasStack()) {
            return ItemStack.EMPTY;
        }

        // 获取槽位中的原始物品堆，并创建一个副本用于后续比较
        ItemStack originalStack = slot.getStack();
        ItemStack copyStack = originalStack.copy();

        // 获取背包槽位的数量和总槽位数量
        int backpackSize = this.backpackInventory.getSizeInventory();  // 背包槽位数量
        int totalSlots = this.inventorySlots.size();  // 总槽位数量（包括背包和玩家库存

        // 尝试将物品从背包移到玩家库存，或从玩家库存移到背包
        // 参数解释：
        // - originalStack: 需要转移的物品堆
        // - fromIndex: 起始槽位索引
        // - toIndex: 结束槽位索引
        // - useBlacklist: 是否使用黑名单（false 表示不使用）
        // 如果是背包中的物品（index < backpackSize），则尝试将其移到玩家库存（从 backpackSize 开始到 totalSlots 结束）
        // 如果是玩家库存中的物品（index >= backpackSize），则尝试将其移到背包（从 0 开始到 backpackSize 结束）
        if (!mergeItemStack(originalStack, index < backpackSize ? backpackSize : 0,  // 起始槽位索引
                index < backpackSize ? totalSlots : backpackSize,  // 结束槽位索引
                false)) {  // 不使用黑名单
            return ItemStack.EMPTY;  // 如果无法合并物品堆，则返回空物品堆
        }

        // 如果物品堆已经完全移出当前槽位（即槽位中的物品数量为 0），则将槽位设置为空
        if (originalStack.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);  // 将槽位设置为空
        } else {
            // 如果物品堆还有剩余，则更新槽位中的物品堆，并通知槽位内容发生变化
            slot.putStack(originalStack);
            slot.onSlotChanged();  // 通知槽位内容已更改
        }

        // 如果物品堆的数量没有变化（即转移失败），则返回空物品堆
        if (originalStack.getCount() == copyStack.getCount()) {
            return ItemStack.EMPTY;
        }

        // 触发槽位的 onTake 事件，表示玩家拿走了物品
        slot.onTake(playerIn, originalStack);

        // 返回转移前的物品堆副本，表示成功转移的物品
        return copyStack;
    }
}
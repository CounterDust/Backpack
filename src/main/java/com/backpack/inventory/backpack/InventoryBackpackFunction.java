package com.backpack.inventory.backpack;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class InventoryBackpackFunction extends InventoryBackpack {
    // 日志记录器
    private static final Logger LOGGER = LogManager.getLogger();

    // 用于存储记忆物品的列表
    private final NonNullList<ItemStack> memoryItems;

    // 记忆物品的 NBT 标签名称
    private static final String MEMORY_ITEMS_TAG = "MemoryItems";

    public InventoryBackpackFunction(ItemStack backpackStack) {
        super(backpackStack);
        this.memoryItems = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        // 从 NBT 数据中读取库存信息
        if (backpackStack.hasTagCompound()) {
            NBTTagCompound nbt = backpackStack.getTagCompound();
            deserializeNBT(nbt);
        }
    }

    public void setMemoryItem(int slotId, ItemStack item) {
        if (memoryItems.get(slotId).isEmpty()) {
            this.memoryItems.set(slotId, item.copy());
            this.markDirty();
        }
    }

    public void clearMemoryItem(int slotId) {
        if (!(memoryItems.get(slotId).isEmpty())){
            this.memoryItems.set(slotId, ItemStack.EMPTY);
            this.markDirty();
        }
    }

    public ItemStack getMemoryItem(int slotId) {
        return this.memoryItems.get(slotId);
    }

    @Override
    public void deserializeNBT(NBTTagCompound compound) {
        // 调用父类的 deserializeNBT 方法，确保父类的数据被正确反序列化
        super.deserializeNBT(compound);
        // 检查 NBT 中是否存在记忆物品的标签 (MEMORY_ITEMS_TAG)
        if (compound.hasKey(MEMORY_ITEMS_TAG,Constants.NBT.TAG_LIST)) {
            // 从 NBT 中获取记忆物品的 NBTTagList
            NBTTagList memoryItemList = compound.getTagList(MEMORY_ITEMS_TAG, Constants.NBT.TAG_COMPOUND);
            // 遍历 NBTTagList 中的每一个记忆物品
            for (int i = 0; i < memoryItemList.tagCount(); i++) {
                // 获取当前物品的 NBTTagCompound
                NBTTagCompound memoryItemTag = memoryItemList.getCompoundTagAt(i);
                // 从 NBTTagCompound 中读取槽位索引 (Slot)
                byte slotIndex = memoryItemTag.getByte("Slot");
                // 确保槽位索引在有效范围内
                if (slotIndex >= 0 && slotIndex < this.getSizeInventory()) {
                    // 使用 NBTTagCompound 创建 ItemStack 对象
                    ItemStack stack = new ItemStack(memoryItemTag);
                    // 将 ItemStack 设置到对应的记忆物品列表中
                    this.memoryItems.set(slotIndex, stack);
                }
            }
        }
    }

    @Override
    public NBTTagCompound serializeNBT() {
        // 调用父类的 serializeNBT 方法，获取父类的 NBTTagCompound
        NBTTagCompound compound = super.serializeNBT();
        // 创建一个新的 NBTTagList 用于存储记忆物品信息
        NBTTagList memoryItemList = new NBTTagList();
        // 遍历记忆物品列表中的每一个槽位
        for (int i = 0; i < this.memoryItems.size(); i++) {
            // 获取当前槽位的记忆物品
            ItemStack stack = this.memoryItems.get(i);
            // 检查当前槽位是否包含有效的物品栈（非空）
            if (!stack.isEmpty() && i >= 0 && i < this.getSizeInventory()) {
                // 创建一个新的 NBTTagCompound 用于存储单个记忆物品的信息
                NBTTagCompound memoryItemTag = new NBTTagCompound();
                // 将槽位索引写入 NBTTagCompound，使用 "Slot" 作为键名
                memoryItemTag.setByte("Slot", (byte) i);
                // 调用 ItemStack 的 writeToNBT 方法，将物品栈的所有数据写入 NBTTagCompound 中
                stack.writeToNBT(memoryItemTag);
                // 将该物品栈的 NBTTagCompound 添加到 memoryItemList 中
                memoryItemList.appendTag(memoryItemTag);
            }
        }
        // 将 memoryItemList 设置为 NBTTagCompound 的一个标签，使用 MEMORY_ITEMS_TAG 作为键名
        compound.setTag(MEMORY_ITEMS_TAG, memoryItemList);
        // 返回包含所有数据的 NBTTagCompound
        return compound;
    }
}

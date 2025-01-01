package com.backpack.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class InventoryBackpackFunction extends InventoryBackpack{
    // 日志记录器
    public static final Logger LOGGER = LogManager.getLogger();

    private final NonNullList<SlotType> slotTypes = NonNullList.withSize(36, SlotType.COMMON);

    private static final String TYPES_TAG = "Types";

    public enum SlotType {
        COMMON,
        MEMORY
    }

    public InventoryBackpackFunction(ItemStack openBackpackStack) {
        super(openBackpackStack);
        // 从 NBT 数据中读取库存信息
        if (openBackpackStack.hasTagCompound()) {
            NBTTagCompound nbt = openBackpackStack.getTagCompound();
            deserializeNBT(nbt);
        }
    }

    @Override
    public void deserializeNBT(NBTTagCompound compound) {
        super.deserializeNBT(compound);

        if (compound.hasKey(TYPES_TAG, Constants.NBT.TAG_LIST)) {
            NBTTagList typeList = compound.getTagList(TYPES_TAG, Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < typeList.tagCount(); i++) {
                NBTTagCompound typeTag = typeList.getCompoundTagAt(i);
                byte slotIndex = typeTag.getByte("Slot");
                String typeName = typeTag.getString("Type");
                slotTypes.set(slotIndex, SlotType.valueOf(typeName));
            }
        }
    }

    @Override
    public NBTTagCompound serializeNBT() {
        // 首先调用父类的 serializeNBT 方法，获取父类已序列化的 NBT 数据
        NBTTagCompound compound = super.serializeNBT();

        // 创建一个新的 NBTTagList 用于存储槽位类型信息
        NBTTagList typeList = new NBTTagList();
        // 遍历所有槽位，将每个槽位的类型信息写入 NBTTagList 中
        for (int i = 0; i < this.getSizeInventory(); i++) {
            // 获取当前槽位的类型
            SlotType type = slotTypes.get(i);
            // 创建一个新的 NBTTagCompound 用于存储单个槽位的类型信息
            NBTTagCompound typeTag = new NBTTagCompound();
            // 将槽位索引写入 NBTTagCompound，使用 "Slot" 作为键名
            typeTag.setByte("Slot", (byte) i);
            // 将槽位类型名称写入 NBTTagCompound，使用 "Type" 作为键名
            typeTag.setString("Type", type.name());
            // 将该槽位的 NBTTagCompound 添加到 NBTTagList 中
            typeList.appendTag(typeTag);
        }
        // 将包含所有槽位类型信息的 NBTTagList 写入主 NBTTagCompound 中，使用 "Types" 作为键名
        compound.setTag(TYPES_TAG, typeList);

        return compound;
    }
}

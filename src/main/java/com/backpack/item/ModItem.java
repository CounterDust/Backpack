package com.backpack.item;

import com.backpack.register.RegisterItem;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.Item;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * 自定义物品类，用于创建模组中的新物品。
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ModItem extends Item {

    /**
     * 构造函数，初始化自定义物品。
     *
     * @param name 物品的注册名和非本地化名
     */
    public ModItem(String name) {
        // 调用父类构造函数
        super();

        // 设置物品所属的物品栏
        setCreativeTab(RegisterItem.BACKPACK_TAB);

        // 设置物品的注册名
        setRegistryName(name);

        // 设置物品的非本地化名（用于语言文件）
        // 添加模组前缀以避免冲突
        setUnlocalizedName("backpack."+name);

        // 将物品添加到物品列表中
        RegisterItem.ITEMS_LIST.add(this);
    }
}

package com.backpack.tab;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * 自定义物品栏类，继承自CreativeTabs
 * 用于创建一个带有自定义图标和标签的物品栏
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ModTabs extends CreativeTabs {

    // 物品栏标签的翻译键值
    private final String translationKey;
    // 物品栏图标的物品
    private final Item iconItem;

    /**
     * 构造函数，传入物品栏的唯一标识符、标签键值和图标物品
     * @param label 物品栏的唯一标识符
     * @param translationKey 物品栏标签的翻译键值
     * @param iconItem 物品栏图标的物品
     */
    public ModTabs(String label, String translationKey, Item iconItem) {
        super(label);
        this.translationKey = translationKey;
        this.iconItem = iconItem;
    }

    /**
     * 设置物品栏的图标（显示在物品栏选择界面的小图标）
     * @return ItemStack 物品栏的图标物品堆
     */
    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getTabIconItem() {
        return new ItemStack(iconItem);  // 使用传递进来的图标物品
    }

    /**
     * 设置物品栏的标签（显示在物品栏顶部的文本）
     * @return String 物品栏标签的翻译键值
     */
    @Override
    public String getTranslatedTabLabel() {
        return this.translationKey;  // 返回传递进来的键值
    }
}


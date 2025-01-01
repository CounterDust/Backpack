package com.backpack.register;

import com.backpack.item.ItemModBackpack;
import com.backpack.item.ItemMod;
import com.backpack.tab.ModTabs;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * 物品注册类。
 * 该类负责在游戏初始化时注册所有自定义物品，并在客户端注册物品模型。
 */
@Mod.EventBusSubscriber
public class RegisterItem {

    // 日志记录器
    public static final Logger LOGGER = LogManager.getLogger();

    // 物品栏实例
    public static final ModTabs BACKPACK_TAB = new ModTabs("backpack_tab", "itemGroup.Backpack", Items.DIAMOND);

    // 物品列表
    public static final List<Item> ITEMS_LIST = new ArrayList<>();

    // 物品实例
    // 背包类
    public static final ItemMod backpack = new ItemModBackpack("backpack");
    // 普通类
    //public static final ModItem backpack1 = new ModItem("backpack1");

    /**
     * 注册所有物品。
     *
     * @param event 注册事件
     */
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(ITEMS_LIST.toArray(new Item[0]));
        LOGGER.info("Registered {} items.", ITEMS_LIST.size());
    }

    /**
     * 注册物品模型。
     *
     * @param event 模型注册事件
     */
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event) {
        for (Item item : ITEMS_LIST) {
            ModelLoader.setCustomModelResourceLocation(
                    item,
                    0,
                    new ModelResourceLocation(item.getRegistryName(), "inventory")
            );
        }
        LOGGER.info("Registered model for item: {}", ITEMS_LIST.size());
    }

}

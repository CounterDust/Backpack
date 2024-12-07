package com;

import com.backpack.gui.GuiRegistry;
import com.backpack.keybindings.BackpackClientEvents;
import com.backpack.keybindings.KeyBindings;
import com.backpack.network.PacketHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Backpack模组的主类，负责模组的初始化和GUI处理
 */
@Mod(modid = Backpack.MODID, name = Backpack.NAME, version = Backpack.VERSION)
public class Backpack {

    // 定义模组的ID、名称和版本
    public static final String MODID = "backpack";
    public static final String NAME = "Backpack";
    public static final String VERSION = "1.0.0";

    // 定义GUI ID
    public static final int GUI_ID_BACKPACK = 0;

    // 模组实例
    @Mod.Instance(MODID)
    public static Backpack INSTANCE;

    // 预初始化事件处理方法
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // 初始化网络包
        PacketHandler.init();
    }

    // 初始化事件处理方法
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

        // 注册GUI处理类
        GuiRegistry.registerGuiHandlers(this);

        // 初始化键绑定
        KeyBindings.init();

        // 注册客户端事件处理器
        MinecraftForge.EVENT_BUS.register(BackpackClientEvents.class);
    }

    // 后期初始化事件处理方法
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }
}

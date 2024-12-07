package com.backpack.keybindings;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class KeyBindings {

    // 定义键绑定
    public static final KeyBinding OPEN_BACKPACK = new KeyBinding(
            // 键绑定的名称，用于语言文件
            "key.openBackpack",
            // 默认的按键，这里设置为P键
            Keyboard.KEY_B,
            // 键绑定的分类
            "key.categories.backpack"
    );

    /**
     * 初始化并注册所有的键绑定。
     */
    public static void init() {
        ClientRegistry.registerKeyBinding(OPEN_BACKPACK);
        // 如果有更多键绑定，继续在这里注册
    }
}

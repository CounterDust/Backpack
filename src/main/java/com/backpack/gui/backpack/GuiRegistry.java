package com.backpack.gui.backpack;

import com.Backpack;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class GuiRegistry {
    public static void registerGuiHandlers(Backpack mod) {
        NetworkRegistry.INSTANCE.registerGuiHandler(mod, new GuiHandler());
    }
}

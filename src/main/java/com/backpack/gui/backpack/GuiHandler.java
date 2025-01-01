package com.backpack.gui.backpack;

import com.Backpack;
import com.backpack.container.ContainerBackpack;
import com.backpack.inventory.ClientInventoryBackpack;
import com.backpack.inventory.InventoryBackpackFunction;
import com.backpack.item.ItemModBackpack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiHandler implements IGuiHandler {

    // 日志记录器
    public static final Logger LOGGER = LogManager.getLogger();

    // 注册 GuiHandler
    public static void register(Backpack mod) {
        NetworkRegistry.INSTANCE.registerGuiHandler(mod, new GuiHandler());
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == Backpack.GUI_ID_BACKPACK) {
            ItemStack backpackItem = player.inventory.getStackInSlot(x);
            if (!backpackItem.isEmpty() && backpackItem.getItem() instanceof ItemModBackpack) {
                return new ContainerBackpack(player.inventory, new InventoryBackpackFunction(backpackItem));
            } else {
                LOGGER.warn("服务器，玩家 {} 在槽位 {} 中没有背包。", player.getName(), x);
            }
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == Backpack.GUI_ID_BACKPACK) {
            ItemStack backpackItem = player.inventory.getStackInSlot(x);
            if (!backpackItem.isEmpty() && backpackItem.getItem() instanceof ItemModBackpack) {
                return new GuiBackpack(player.inventory, new ClientInventoryBackpack(backpackItem), x);
            } else {
                LOGGER.warn("客户端，玩家 {} 在槽位 {} 中没有背包。", player.getName(), x);
            }
        }
        return null;
    }
}
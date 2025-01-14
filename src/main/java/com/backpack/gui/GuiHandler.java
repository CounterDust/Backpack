package com.backpack.gui;

import com.Backpack;
import com.backpack.container.ContainerBackpack;
import com.backpack.container.ContainerSelect;
import com.backpack.gui.backpack.GuiBackpack;
import com.backpack.gui.select.GuiSelect;
import com.backpack.inventory.backpack.InventoryBackpackFunction;
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

    private static final Logger LOGGER = LogManager.getLogger();

    public static void register(Backpack mod) {
        NetworkRegistry.INSTANCE.registerGuiHandler(mod, new GuiHandler());
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case Backpack.GUI_ID_BACKPACK:
                ItemStack backpackItem = player.inventory.getStackInSlot(x);
                if (!backpackItem.isEmpty() && backpackItem.getItem() instanceof ItemModBackpack) {
                    return new ContainerBackpack(player.inventory, new InventoryBackpackFunction(backpackItem));
                } else {
                    LOGGER.warn("服务器，玩家打开背包界面 {} 在槽位 {} 中没有背包。", player.getName(), x);
                }
                break;
            case Backpack.GUI_ID_SELECT:
                ItemStack backpackItem1 = player.inventory.getStackInSlot(x);
                if (!backpackItem1.isEmpty() && backpackItem1.getItem() instanceof ItemModBackpack) {
                    return new ContainerSelect(player.inventory, new InventoryBackpackFunction(backpackItem1));
                } else {
                    LOGGER.warn("服务器，玩家打开选择界面 {} 在槽位 {} 中没有背包。", player.getName(), x);
                }
                break;
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID){
            case Backpack.GUI_ID_BACKPACK:
                ItemStack backpackItem = player.inventory.getStackInSlot(x);
                if (!backpackItem.isEmpty() && backpackItem.getItem() instanceof ItemModBackpack) {
                    return new GuiBackpack(player.inventory, new InventoryBackpackFunction(backpackItem), x);
                } else {
                    LOGGER.warn("客户端，玩家打开背包界面 {} 在槽位 {} 中没有背包。", player.getName(), x);
                }
                break;
            case Backpack.GUI_ID_SELECT:
                ItemStack backpackItem1 = player.inventory.getStackInSlot(x);
                if (!backpackItem1.isEmpty() && backpackItem1.getItem() instanceof ItemModBackpack) {
                    return new GuiSelect(player.inventory, new InventoryBackpackFunction(backpackItem1));
                } else {
                    LOGGER.warn("客户端，玩家打开选择界面 {} 在槽位 {} 中没有背包。", player.getName(), x);
                }
                break;
        }
        return null;
    }
}
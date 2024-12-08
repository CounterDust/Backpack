package com.backpack.gui;

import com.Backpack;
import com.backpack.container.BackpackContainer;
import com.backpack.inventory.InventoryBackpack;
import com.backpack.item.BackpackItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiHandler implements IGuiHandler {

    // 日志记录器
    public static final Logger LOGGER = LogManager.getLogger();

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == Backpack.GUI_ID_BACKPACK) {
            // 使用传递的槽位索引来获取背包物品
            ItemStack backpackItem = player.inventory.getStackInSlot(x);  // x 是槽位索引

            if (!backpackItem.isEmpty() && backpackItem.getItem() instanceof BackpackItem) {
                return new BackpackContainer(player.inventory, new InventoryBackpack(backpackItem));
            } else {
                LOGGER.warn("server,Player {} does not have a backpack in slot {}.", player.getName(), x);
            }
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == Backpack.GUI_ID_BACKPACK) {
            // 使用传递的槽位索引来获取背包物品
            ItemStack backpackItem = player.inventory.getStackInSlot(x);  // x 是槽位索引

            if (!backpackItem.isEmpty() && backpackItem.getItem() instanceof BackpackItem) {
                return new GuiBackpack(player.inventory, new InventoryBackpack(backpackItem),x);
            } else {
                LOGGER.warn("client,Player {} does not have a backpack in slot {}.", player.getName(), x);
            }
        }
        return null;
    }
}

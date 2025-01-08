package com.backpack.keybindings;

import com.backpack.gui.backpack.GuiBackpack;
import com.backpack.item.ItemModBackpack;
import com.backpack.network.OpenBackpackMessage;
import com.backpack.network.OpenSelectMessage;
import com.backpack.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class BackpackClientEvents {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {

        if (KeyBindings.OPEN_BACKPACK.isKeyDown() && findFirstBackpack()) {
            PacketHandler.sendToServer(new OpenBackpackMessage());
            return;
        }
        if (KeyBindings.OPEN_SELECT.isKeyDown() && findFirstBackpack()) {
            PacketHandler.sendToServer(new OpenSelectMessage());
        }
    }

    private static boolean findFirstBackpack() {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack itemStack = player.inventory.getStackInSlot(i);
            if (!itemStack.isEmpty() && itemStack.getItem() instanceof ItemModBackpack) {
                return true;
            }
        }
        return false;
    }
}
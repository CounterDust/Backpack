package com.backpack.keybindings;

import com.backpack.item.BackpackItem;
import com.backpack.network.PacketHandler;
import com.backpack.network.OpenBackpackMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(Side.CLIENT)
public class BackpackClientEvents {

    // 日志记录器
    public static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen == null && KeyBindings.OPEN_BACKPACK.isPressed()) {
            EntityPlayer player = mc.player;
            if (player != null) {
                // 查找玩家物品栏中的第一个背包物品
                int backpackSlotIndex = findFirstBackpack(player);
                if (backpackSlotIndex != -1) {
                    // 发送请求给服务器，请求打开背包 GUI，并传递槽位索引
                    PacketHandler.sendToServer(new OpenBackpackMessage(backpackSlotIndex));
                } else {
                    // 如果没有找到背包物品，记录日志
                    LOGGER.info("Player {} does not have a backpack in their inventory.", player.getName());
                }
            } else {
                // 如果玩家对象为空，记录错误日志
                LOGGER.error("Player object is null.");
            }
        }
    }

    /**
     * 查找玩家物品栏中的第一个背包物品所在的槽位索引。
     *
     * @param player 玩家对象
     * @return 背包物品所在的槽位索引，如果没有找到则返回 -1
     */
    private static int findFirstBackpack(EntityPlayer player) {
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack itemStack = player.inventory.getStackInSlot(i);
            if (!itemStack.isEmpty() && itemStack.getItem() instanceof BackpackItem) {
                return i; // 返回找到的第一个背包的槽位索引
            }
        }
        return -1; // 如果没有找到背包，返回 -1
    }
}

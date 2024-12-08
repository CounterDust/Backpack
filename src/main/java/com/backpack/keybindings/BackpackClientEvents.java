package com.backpack.keybindings;

import com.backpack.item.BackpackItem;
import com.backpack.network.PacketHandler;
import com.backpack.network.OpenBackpackMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(Side.CLIENT)
public class BackpackClientEvents {

    // 日志记录器
    public static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        // 只在每个 tick 的结束阶段处理键绑定事件
        if (event.phase == TickEvent.Phase.END && KeyBindings.OPEN_BACKPACK.isPressed()) {
            // 获取当前 Minecraft 实例和玩家对象
            Minecraft mc = Minecraft.getMinecraft();
            EntityPlayer player = mc.player;

            // 检查玩家对象是否为空
            if (player != null) {

                // 遍历玩家物品栏中的所有槽位，查找第一个背包物品
                for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                    ItemStack itemStack = player.inventory.getStackInSlot(i);

                    // 检查当前槽位是否包含非空物品，并且该物品是否是背包类型
                    if (!itemStack.isEmpty() && itemStack.getItem() instanceof BackpackItem) {
                        // 找到背包物品所在的槽位索引

                        // 发送请求给服务器，请求打开背包 GUI，并传递槽位索引
                        PacketHandler.sendToServer(new OpenBackpackMessage(i));

                        // 找到第一个背包后，立即退出循环，避免继续遍历
                        return;
                    }
                }
                // 如果没有找到背包物品，记录日志
                LOGGER.info("Player {} does not have a backpack in their inventory.", player.getName());
            } else {
                // 如果玩家对象为空，记录错误日志
                LOGGER.error("Player object is null.");
            }
        }
    }
}

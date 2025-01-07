package com.backpack.keybindings;

import com.backpack.gui.select.GuiSelect;
import com.backpack.item.ItemModBackpack;
import com.backpack.network.OpenBackpackMessage;
import com.backpack.network.PacketHandler;
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

    // 标记按键是否被按下
    private static boolean wasKeyPressedLastTick = false;

    // 记录开始第一次按下的时间
    private static long pressStartTime = -1;

    // 记录是否已经输出过长按日志
    private static boolean hasLoggedLongPress = false;

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        // 获取当前帧的按键状态
        boolean currentKeyPressed = KeyBindings.OPEN_BACKPACK.isPressed();

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;

        // 检测按键事件
        if (currentKeyPressed != wasKeyPressedLastTick) {
            if (currentKeyPressed) {
                // 按键首次按下，记录按下时间
                pressStartTime = System.currentTimeMillis();
                hasLoggedLongPress = false;  // 重置长按日志标记
                LOGGER.info("按");
            } else {
                // 按键松开
                LOGGER.info("松");

                // 检查是否是短按
                long pressDuration = System.currentTimeMillis() - pressStartTime;
                if (pressDuration < 1000) {
                    // 短按逻辑：发送打开背包请求
                    int backpackSlotIndex = findFirstBackpack(player);
                    if (backpackSlotIndex != -1) {
                        // 发送请求给服务器，请求打开背包 GUI，并传递槽位索引
                        PacketHandler.sendToServer(new OpenBackpackMessage(backpackSlotIndex));
                    } else {
                        // 如果没有找到背包物品，记录日志
                        LOGGER.info("玩家 {} 的库存中没有背包。", player.getName());
                    }
                }

                // 重置计时器和长按标记
                pressStartTime = -1;
                hasLoggedLongPress = false;
            }

            // 更新上一帧的按键状态
            wasKeyPressedLastTick = currentKeyPressed;
        } else if (currentKeyPressed && !hasLoggedLongPress) {
            // 检查是否长按
            long currentTime = System.currentTimeMillis();
            long pressDuration = currentTime - pressStartTime;

            if (pressDuration >= 1000) {
                // 长按逻辑：输出日志并标记已输出
                LOGGER.info("长按");

                // 打开 SelectGUI
                mc.displayGuiScreen(new GuiSelect());

                hasLoggedLongPress = true;
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
            if (!itemStack.isEmpty() && itemStack.getItem() instanceof ItemModBackpack) {
                return i; // 返回找到的第一个背包的槽位索引
            }
        }
        return -1; // 如果没有找到背包，返回 -1
    }
}
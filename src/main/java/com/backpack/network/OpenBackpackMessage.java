package com.backpack.network;

import com.Backpack;
import com.backpack.item.ItemModBackpack;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 玩家打开背包的网络消息
 */
public class OpenBackpackMessage implements IMessage {

    // 日志记录器
    public static final Logger LOGGER = LogManager.getLogger();

    // 背包物品所在的槽位索引
    private int slotIndex;

    // 默认构造函数，用于网络通信
    public OpenBackpackMessage() {
    }

    /**
     * 构造函数，用于创建携带槽位索引的实例
     *
     * @param slotIndex 背包物品所在的槽位索引
     */
    public OpenBackpackMessage(int slotIndex) {
        this.slotIndex = slotIndex;
    }

    /**
     * 从字节缓冲区中读取数据到消息对象
     *
     * @param buf 字节缓冲区
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        this.slotIndex = buf.readInt();
    }

    /**
     * 将消息对象的数据写入字节缓冲区
     *
     * @param buf 字节缓冲区
     */
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(slotIndex);
    }

    // 处理服务器端的消息
    public static class Handler implements IMessageHandler<OpenBackpackMessage, IMessage> {
        /**
         * 当消息到来时，服务器端调用此方法进行处理
         *
         * @param message 收到的消息
         * @param ctx     消息上下文
         * @return 回复的消息，此处为null
         */
        @Override
        public IMessage onMessage(OpenBackpackMessage message, MessageContext ctx) {
            // 在服务器线程中处理消息
            ctx.getServerHandler().player.getServerWorld().addScheduledTask(() -> {
                EntityPlayer player = ctx.getServerHandler().player;

                // 使用传递的槽位索引来获取背包物品
                ItemStack backpackItem = player.inventory.getStackInSlot(message.slotIndex);

                if (!backpackItem.isEmpty() && backpackItem.getItem() instanceof ItemModBackpack) {
                    // 打开背包GUI
                    player.openGui(Backpack.INSTANCE, Backpack.GUI_ID_BACKPACK, player.getEntityWorld(), message.slotIndex, 0, 0);
                } else {
                    // 如果没有找到背包物品，记录错误日志
                    OpenBackpackMessage.LOGGER.warn("玩家 {} 在槽位 {} 中没有背包。", player.getName(), message.slotIndex);
                }
            });
            return null;
        }
    }
}

package com.backpack.network;

import com.backpack.container.ContainerBackpack;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * 用于处理背包槽位交互的网络消息
 */
public class MemoryMessage implements IMessage {

    // 槽位ID
    private int slotId;
    // 鼠标按钮（0: 左键, 1: 右键, 2: 中键等）
    private int mouseButton;

    /**
     * 默认构造函数，用于反序列化
     */
    public MemoryMessage() {
    }

    /**
     * 构造函数，用于创建携带槽位ID和鼠标按钮的实例
     *
     * @param slotId     槽位ID
     * @param mouseButton 鼠标按钮
     */
    public MemoryMessage(int slotId, int mouseButton) {
        this.slotId = slotId;
        this.mouseButton = mouseButton;
    }

    /**
     * 从字节缓冲区中读取数据到消息对象
     *
     * @param buf 字节缓冲区
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        this.slotId = buf.readInt();
        this.mouseButton = buf.readInt();
    }

    /**
     * 将消息对象的数据写入字节缓冲区
     *
     * @param buf 字节缓冲区
     */
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(slotId);
        buf.writeInt(mouseButton);
    }

    // 处理服务器端的消息
    public static class Handler implements IMessageHandler<MemoryMessage, IMessage> {
        /**
         * 当消息到来时，服务器端调用此方法进行处理
         *
         * @param message 收到的消息
         * @param ctx     消息上下文
         * @return 回复的消息，此处为null
         */
        @Override
        public IMessage onMessage(MemoryMessage message, MessageContext ctx) {
            // 在服务器线程中处理消息
            ctx.getServerHandler().player.getServerWorld().addScheduledTask(() -> {
                EntityPlayerMP player = ctx.getServerHandler().player;
                Container container = player.openContainer;

                if (container instanceof ContainerBackpack) {
                    // 调用背包容器中的处理方法
                    ((ContainerBackpack) container).handleSlotInteraction(message.slotId, message.mouseButton);
                }
            });
            return null;
        }
    }
}
package com.backpack.network;

import com.Backpack;
import com.backpack.item.BackpackItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OpenBackpackMessage implements IMessage {

    // 日志记录器
    public static final Logger LOGGER = LogManager.getLogger();

    // 背包物品所在的槽位索引
    private int slotIndex;

    public OpenBackpackMessage() {}

    public OpenBackpackMessage(int slotIndex) {
        this.slotIndex = slotIndex;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.slotIndex = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(slotIndex);
    }

    // 处理服务器端的消息
    public static class Handler implements IMessageHandler<OpenBackpackMessage, IMessage> {
        @Override
        public IMessage onMessage(OpenBackpackMessage message, MessageContext ctx) {
            // 在服务器线程中处理消息
            ctx.getServerHandler().player.getServerWorld().addScheduledTask(() -> {
                EntityPlayer player = ctx.getServerHandler().player;

                // 使用传递的槽位索引来获取背包物品
                ItemStack backpackItem = player.inventory.getStackInSlot(message.slotIndex);

                if (!backpackItem.isEmpty() && backpackItem.getItem() instanceof BackpackItem) {
                    // 打开背包GUI
                    player.openGui(Backpack.INSTANCE, Backpack.GUI_ID_BACKPACK, player.getEntityWorld(), message.slotIndex, 0, 0);
                } else {
                    // 如果没有找到背包物品，记录错误日志
                    OpenBackpackMessage.LOGGER.warn("Player {} does not have a backpack in slot {}.", player.getName(), message.slotIndex);
                }
            });
            return null;
        }
    }
}

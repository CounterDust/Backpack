package com.backpack.network;

import com.Backpack;
import com.backpack.item.ItemModBackpack;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class OpenSelectMessage implements IMessage {

    // 背包物品所在的槽位索引
    private int slotIndex;

    public OpenSelectMessage() {
    }

    public OpenSelectMessage(int slotIndex) {
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

    public static class Handler implements IMessageHandler<OpenSelectMessage, IMessage> {
        @Override
        public IMessage onMessage(OpenSelectMessage message, MessageContext ctx) {
            // 在服务器线程中处理消息
            ctx.getServerHandler().player.getServerWorld().addScheduledTask(() -> {
                EntityPlayer player = ctx.getServerHandler().player;

                ItemStack backpackItem = player.inventory.getStackInSlot(message.slotIndex);

                if (!backpackItem.isEmpty() && backpackItem.getItem() instanceof ItemModBackpack) {
                    // 打开 Select GUI
                    player.openGui(Backpack.INSTANCE, Backpack.GUI_ID_SELECT, player.getEntityWorld(), message.slotIndex, 0, 0);
                }
            });
            return null;
        }
    }
}

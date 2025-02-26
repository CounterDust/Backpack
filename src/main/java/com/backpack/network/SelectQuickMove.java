package com.backpack.network;

import com.backpack.container.ContainerBackpack;
import com.backpack.container.ContainerSelect;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SelectQuickMove implements IMessage {

    public SelectQuickMove() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public static class Handler implements IMessageHandler<SelectQuickMove, IMessage> {

        @Override
        public IMessage onMessage(SelectQuickMove message, MessageContext ctx) {

            ctx.getServerHandler().player.getServerWorld().addScheduledTask(() -> {
                EntityPlayerMP player = ctx.getServerHandler().player;
                Container container = player.openContainer;

                if (container instanceof ContainerSelect){
                    ((ContainerSelect) container).buttonClicked(player);
                }
            });

            return null;
        }
    }
}

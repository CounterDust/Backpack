package com.backpack.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
    private static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("backpack");

    public static void init() {
        // 注册消息
        INSTANCE.registerMessage(OpenBackpackMessage.Handler.class, OpenBackpackMessage.class, 0, Side.SERVER);
    }

    public static SimpleNetworkWrapper getInstance() {
        return INSTANCE;
    }

    public static void sendToServer(IMessage message) {
        INSTANCE.sendToServer(message);
    }
}

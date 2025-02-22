package com.backpack.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
    // 创建一个名为 "backpack" 的网络通道
    private static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("Backpack");

    // 初始化方法，用于注册消息
    public static void init() {
        // 注册 OpenBackpackMessage 消息，从客户端发送到服务器
        INSTANCE.registerMessage(OpenBackpackMessage.Handler.class, OpenBackpackMessage.class, 0, Side.SERVER);
        INSTANCE.registerMessage(MemorySlotMessage.Handler.class, MemorySlotMessage.class, 1, Side.SERVER);
        INSTANCE.registerMessage(OpenSelectMessage.Handler.class, OpenSelectMessage.class, 2, Side.SERVER);
    }

    // 获取 SimpleNetworkWrapper 实例
    public static SimpleNetworkWrapper getInstance() {
        return INSTANCE;
    }

    // 从客户端向服务器发送消息
    public static void sendToServer(IMessage message) {
        INSTANCE.sendToServer(message);
    }
}

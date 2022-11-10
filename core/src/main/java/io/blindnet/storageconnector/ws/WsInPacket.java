package io.blindnet.storageconnector.ws;

public interface WsInPacket {
    void callHandler(WsInPayload payload, PacketHandler handler);
}

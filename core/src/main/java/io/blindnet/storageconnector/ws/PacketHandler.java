package io.blindnet.storageconnector.ws;

import io.blindnet.storageconnector.ws.packets.InPacketDataRequest;
import io.blindnet.storageconnector.ws.packets.InPacketWelcome;

public interface PacketHandler {
    void handlePacket(WsInPayload payload, InPacketDataRequest packet);
    void handlePacket(WsInPayload payload, InPacketWelcome packet);
}

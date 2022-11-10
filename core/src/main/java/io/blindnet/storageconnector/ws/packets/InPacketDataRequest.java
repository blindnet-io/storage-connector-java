package io.blindnet.storageconnector.ws.packets;

import io.blindnet.storageconnector.datarequests.DataRequestImpl;
import io.blindnet.storageconnector.ws.PacketHandler;
import io.blindnet.storageconnector.ws.WsInPacket;
import io.blindnet.storageconnector.ws.WsInPayload;

public class InPacketDataRequest implements WsInPacket {
    private DataRequestImpl request;

    public DataRequestImpl getRequest() {
        return request;
    }

    public void setRequest(DataRequestImpl request) {
        this.request = request;
    }

    @Override
    public void callHandler(WsInPayload payload, PacketHandler handler) {
        handler.handlePacket(payload, this);
    }
}

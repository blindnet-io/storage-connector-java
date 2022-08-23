package io.blindnet.storageconnector.ws.packets;

import io.blindnet.storageconnector.datarequests.DataRequestImpl;
import io.blindnet.storageconnector.StorageConnectorImpl;
import io.blindnet.storageconnector.logic.DataQueryLogic;
import io.blindnet.storageconnector.logic.Logic;
import io.blindnet.storageconnector.ws.WsInPacket;

public class InPacketDataRequest implements WsInPacket {
    private DataRequestImpl request;

    public DataRequestImpl getRequest() {
        return request;
    }

    public void setRequest(DataRequestImpl request) {
        this.request = request;
    }

    @Override
    public Logic getLogic(StorageConnectorImpl connector) {
        return new DataQueryLogic(connector, this);
    }
}

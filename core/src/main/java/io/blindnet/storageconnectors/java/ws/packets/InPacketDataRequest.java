package io.blindnet.storageconnectors.java.ws.packets;

import io.blindnet.storageconnectors.java.StorageConnectorImpl;
import io.blindnet.storageconnectors.java.datarequests.DataRequestImpl;
import io.blindnet.storageconnectors.java.logic.DataQueryLogic;
import io.blindnet.storageconnectors.java.logic.Logic;
import io.blindnet.storageconnectors.java.ws.WsInPacket;

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

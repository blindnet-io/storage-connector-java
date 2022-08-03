package io.blindnet.storageconnectors.java.ws.packets;

import io.blindnet.storageconnectors.java.StorageConnectorImpl;
import io.blindnet.storageconnectors.java.dataquery.DataQueryImpl;
import io.blindnet.storageconnectors.java.logic.DataQueryLogic;
import io.blindnet.storageconnectors.java.logic.Logic;
import io.blindnet.storageconnectors.java.ws.WsInPacket;

public class InPacketDataQuery implements WsInPacket {
    private DataQueryImpl query;
    private Action action;

    public DataQueryImpl getQuery() {
        return query;
    }

    public void setQuery(DataQueryImpl query) {
        this.query = query;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    @Override
    public Logic getLogic(StorageConnectorImpl connector) {
        return new DataQueryLogic(connector, this);
    }

    public enum Action {
        DELETE,
        GET,
    }
}

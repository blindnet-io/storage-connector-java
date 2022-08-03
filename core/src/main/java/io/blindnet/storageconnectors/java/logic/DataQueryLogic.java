package io.blindnet.storageconnectors.java.logic;

import io.blindnet.storageconnectors.java.StorageConnectorImpl;
import io.blindnet.storageconnectors.java.dataquery.reply.DataQueryReply;
import io.blindnet.storageconnectors.java.exceptions.WebSocketException;
import io.blindnet.storageconnectors.java.ws.packets.InPacketDataQuery;
import io.blindnet.storageconnectors.java.ws.packets.OutPacketData;
import io.blindnet.storageconnectors.java.ws.packets.OutPacketDataReply;

public class DataQueryLogic extends Logic {
    private final InPacketDataQuery packet;

    public DataQueryLogic(StorageConnectorImpl connector, InPacketDataQuery packet) {
        super(connector);

        this.packet = packet;
    }

    @Override
    public void run() throws WebSocketException {
        DataQueryReply reply;
        switch (packet.getAction()) {
            case GET:
                reply = getConnector().getDataQueryHandler().get(packet.getQuery());
                break;
            case DELETE:
                reply = getConnector().getDataQueryHandler().delete(packet.getQuery());
                break;
            default:
                throw new WebSocketException("Unknown data query action");
        }

        getConnector().sendPacket(new OutPacketDataReply(packet.getQuery().getRequestId(), reply.getType()));

        if(packet.getAction() == InPacketDataQuery.Action.GET && reply.getData() != null) {
            reply.getData().thenAccept(data -> {
                try {
                    getConnector().sendPacket(new OutPacketData(packet.getQuery().getRequestId(), data));
                } catch (WebSocketException e) {
                    getConnector().onError(e);
                }
            });
        }
    }
}

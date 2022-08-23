package io.blindnet.storageconnectors.java.logic;

import io.blindnet.storageconnectors.java.StorageConnectorImpl;
import io.blindnet.storageconnectors.java.datarequests.DataRequest;
import io.blindnet.storageconnectors.java.datarequests.reply.BinaryData;
import io.blindnet.storageconnectors.java.datarequests.reply.DataRequestCallback;
import io.blindnet.storageconnectors.java.datarequests.reply.DataRequestReply;
import io.blindnet.storageconnectors.java.exceptions.APIException;
import io.blindnet.storageconnectors.java.ws.packets.InPacketDataRequest;
import io.blindnet.storageconnectors.java.ws.packets.OutPacketDataRequestReply;

public class DataQueryLogic extends Logic {
    private static final int MAX_BLOCK_SIZE = 4194304;

    private final InPacketDataRequest packet;

    public DataQueryLogic(StorageConnectorImpl connector, InPacketDataRequest packet) {
        super(connector);

        this.packet = packet;
    }

    @Override
    public void run() throws Exception {
        DataRequestReply reply = getConnector().getDataRequestHandler().handle(packet.getRequest(), getConnector());

        getConnector().sendPacket(new OutPacketDataRequestReply(packet.getRequest().getRequestId(), reply.getType()));

        if(packet.getRequest().getAction() == DataRequest.Action.GET && reply.getDataCallbackConsumer() != null) {
            reply.getDataCallbackConsumer().accept(new DataRequestCallback() {
                @Override
                public String sendData(BinaryData data) throws APIException {
                    if(data.isArray()) {
                        return getConnector().getDataAccessClient()
                                .uploadMainData(packet.getRequest().getRequestId(), data.getArray());
                    } else {
                        return getConnector().getDataAccessClient()
                                .uploadMainData(packet.getRequest().getRequestId(), data.getStream());
                    }
                }

                @Override
                public String sendAdditionalData(BinaryData data) throws APIException {
                    if(data.isArray()) {
                        return getConnector().getDataAccessClient()
                                .uploadAdditionalData(packet.getRequest().getRequestId(), data.getArray());
                    } else {
                        return getConnector().getDataAccessClient()
                                .uploadAdditionalData(packet.getRequest().getRequestId(), data.getStream());
                    }
                }
            });
        }
    }
}

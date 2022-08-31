package io.blindnet.storageconnector.logic;

import io.blindnet.storageconnector.datarequests.DataRequest;
import io.blindnet.storageconnector.datarequests.reply.BinaryData;
import io.blindnet.storageconnector.ws.packets.InPacketDataRequest;
import io.blindnet.storageconnector.ws.packets.OutPacketDataRequestReply;
import io.blindnet.storageconnector.StorageConnectorImpl;
import io.blindnet.storageconnector.datarequests.reply.DataRequestCallback;
import io.blindnet.storageconnector.datarequests.reply.DataRequestReply;
import io.blindnet.storageconnector.exceptions.APIException;

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

        getConnector().getDataAccessClient().sendPacket(new OutPacketDataRequestReply(packet.getRequest().getRequestId(), reply.getType()));

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

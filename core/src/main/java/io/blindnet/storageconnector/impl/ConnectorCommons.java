package io.blindnet.storageconnector.impl;

import io.blindnet.storageconnector.ConnectorRunner;
import io.blindnet.storageconnector.StorageConnector;
import io.blindnet.storageconnector.datarequests.DataRequest;
import io.blindnet.storageconnector.datarequests.reply.BinaryData;
import io.blindnet.storageconnector.datarequests.reply.DataRequestCallback;
import io.blindnet.storageconnector.datarequests.reply.DataRequestReply;
import io.blindnet.storageconnector.exceptions.APIException;

class ConnectorCommons {
    static void processRequestReply(DataRequest request, DataRequestReply reply,
                                    StorageConnector connector, ConnectorRunner runner) throws Exception {
        runner.getDataAccessClient().sendDataRequestReply(connector, request.getRequestId(), reply);

        if (request.getAction() == DataRequest.Action.GET &&
                reply.getType() == DataRequestReply.Type.ACCEPT &&
                reply.getDataCallbackConsumer() != null) {
            reply.getDataCallbackConsumer().accept(new DataRequestCallback() {
                @Override
                public void sendData(BinaryData data) throws APIException {
                    if (data.isArray()) {
                        runner.getDataAccessClient()
                                .uploadMainData(connector, request.getRequestId(), data.getArray());
                    } else {
                        runner.getDataAccessClient()
                                .uploadMainData(connector, request.getRequestId(), data.getStream());
                    }
                }

                @Override
                public String sendAdditionalData(BinaryData data) throws APIException {
                    if (data.isArray()) {
                        return runner.getDataAccessClient()
                                .uploadAdditionalData(connector, request.getRequestId(), data.getArray());
                    } else {
                        return runner.getDataAccessClient()
                                .uploadAdditionalData(connector, request.getRequestId(), data.getStream());
                    }
                }
            });
        }
    }
}

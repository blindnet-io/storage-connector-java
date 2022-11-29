package io.blindnet.storageconnector.handlers;

import io.blindnet.storageconnector.ConnectorRunner;
import io.blindnet.storageconnector.GlobalStorageConnector;
import io.blindnet.storageconnector.datarequests.DataRequest;
import io.blindnet.storageconnector.datarequests.reply.DataRequestReply;

public interface GlobalDataRequestHandler {
    DataRequestReply handle(DataRequest request, GlobalStorageConnector connector, ConnectorRunner runner) throws Exception;
}

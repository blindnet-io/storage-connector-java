package io.blindnet.storageconnector.handlers;

import io.blindnet.storageconnector.StorageConnector;
import io.blindnet.storageconnector.datarequests.DataRequest;
import io.blindnet.storageconnector.datarequests.reply.DataRequestReply;

public interface DataRequestHandler {
    DataRequestReply handle(DataRequest request, StorageConnector connector) throws Exception;
}

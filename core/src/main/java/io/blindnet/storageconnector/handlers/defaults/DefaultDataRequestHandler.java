package io.blindnet.storageconnector.handlers.defaults;

import io.blindnet.storageconnector.StorageConnector;
import io.blindnet.storageconnector.datarequests.DataRequest;
import io.blindnet.storageconnector.datarequests.reply.DataRequestReply;
import io.blindnet.storageconnector.handlers.DataRequestHandler;
import org.slf4j.LoggerFactory;

public class DefaultDataRequestHandler implements DataRequestHandler {
    @Override
    public DataRequestReply handle(DataRequest request, StorageConnector connector) {
        LoggerFactory.getLogger(DefaultDataRequestHandler.class).warn("Denying data request");
        return request.deny();
    }
}

package io.blindnet.storageconnectors.java.handlers.defaults;

import io.blindnet.storageconnectors.java.StorageConnector;
import io.blindnet.storageconnectors.java.datarequests.DataRequest;
import io.blindnet.storageconnectors.java.datarequests.reply.DataRequestReply;
import io.blindnet.storageconnectors.java.handlers.DataRequestHandler;
import org.slf4j.LoggerFactory;

public class DefaultDataRequestHandler implements DataRequestHandler {
    @Override
    public DataRequestReply handle(DataRequest request, StorageConnector connector) {
        LoggerFactory.getLogger(DefaultDataRequestHandler.class).warn("Denying data request");
        return request.deny();
    }
}

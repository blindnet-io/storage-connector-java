package io.blindnet.storageconnectors.java.handlers;

import io.blindnet.storageconnectors.java.datarequests.DataRequest;
import io.blindnet.storageconnectors.java.datarequests.reply.DataRequestReply;

public interface DataRequestHandler {
    DataRequestReply handle(DataRequest request);
}

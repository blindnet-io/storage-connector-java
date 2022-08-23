package io.blindnet.storageconnector.datarequests.reply;

import java.util.function.Consumer;

public interface DataRequestReply {
    Type getType();

    Consumer<DataRequestCallback> getDataCallbackConsumer();

    enum Type {
        ACCEPT,
        DENY,
    }
}

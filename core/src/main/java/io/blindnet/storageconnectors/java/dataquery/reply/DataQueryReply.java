package io.blindnet.storageconnectors.java.dataquery.reply;

import java.util.function.Consumer;

public interface DataQueryReply {
    Type getType();

    Consumer<DataQueryCallback> getDataCallbackConsumer();

    enum Type {
        ACCEPT,
        DENY,
    }
}

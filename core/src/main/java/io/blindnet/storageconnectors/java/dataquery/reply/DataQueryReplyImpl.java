package io.blindnet.storageconnectors.java.dataquery.reply;

import java.util.function.Consumer;

public class DataQueryReplyImpl implements DataQueryReply {
    private final Type type;
    private Consumer<DataQueryCallback> dataCallbackConsumer;

    public DataQueryReplyImpl(Type type) {
        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Consumer<DataQueryCallback> getDataCallbackConsumer() {
        return dataCallbackConsumer;
    }

    public DataQueryReply setDataCallbackConsumer(Consumer<DataQueryCallback> dataCallbackConsumer) {
        this.dataCallbackConsumer = dataCallbackConsumer;

        return this;
    }
}

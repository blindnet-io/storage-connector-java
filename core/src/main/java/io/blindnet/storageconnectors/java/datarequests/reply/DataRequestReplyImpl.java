package io.blindnet.storageconnectors.java.datarequests.reply;

import io.blindnet.storageconnectors.java.util.FailableConsumer;

import java.util.function.Consumer;

public class DataRequestReplyImpl implements DataRequestReply {
    private final Type type;
    private Consumer<DataRequestCallback> dataCallbackConsumer;

    public DataRequestReplyImpl(Type type) {
        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Consumer<DataRequestCallback> getDataCallbackConsumer() {
        return dataCallbackConsumer;
    }

    public DataRequestReply setDataCallbackConsumer(Consumer<DataRequestCallback> dataCallbackConsumer) {
        this.dataCallbackConsumer = dataCallbackConsumer;

        return this;
    }
}

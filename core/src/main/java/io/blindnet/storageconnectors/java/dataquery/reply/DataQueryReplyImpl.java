package io.blindnet.storageconnectors.java.dataquery.reply;

import java.util.concurrent.CompletableFuture;

public class DataQueryReplyImpl implements DataQueryReply {
    private final Type type;
    private CompletableFuture<byte[]> data;

    public DataQueryReplyImpl(Type type) {
        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public CompletableFuture<byte[]> getData() {
        return data;
    }

    public DataQueryReplyImpl setData(CompletableFuture<byte[]> data) {
        this.data = data;
        return this;
    }
}

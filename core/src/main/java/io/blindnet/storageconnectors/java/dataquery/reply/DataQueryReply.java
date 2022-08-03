package io.blindnet.storageconnectors.java.dataquery.reply;

import java.util.concurrent.CompletableFuture;

public interface DataQueryReply {
    Type getType();

    CompletableFuture<byte[]> getData();

    enum Type {
        ACCEPT,
        DENY,
    }
}

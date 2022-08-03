package io.blindnet.storageconnectors.java.dataquery.reply;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class DataQueryReplyBuilder {
    public DataQueryReply withData(byte[] data) {
        return withDelayedData(completableFuture -> completableFuture.complete(data));
    }

    public DataQueryReply withDelayedData(Consumer<CompletableFuture<byte[]>> consumer) {
        CompletableFuture<byte[]> future = new CompletableFuture<>();
        consumer.accept(future);

        return new DataQueryReplyImpl(DataQueryReplyImpl.Type.ACCEPT)
                .setData(future);
    }
}

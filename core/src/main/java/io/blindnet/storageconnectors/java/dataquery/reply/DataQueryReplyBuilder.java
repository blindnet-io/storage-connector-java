package io.blindnet.storageconnectors.java.dataquery.reply;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

public class DataQueryReplyBuilder {
    public DataQueryReply withData(byte[] data) {
        return withDelayedData(callback -> callback.sendData(data));
    }

    public DataQueryReply withData(ByteBuffer data) {
        return withDelayedData(callback -> callback.sendData(data));
    }

    public DataQueryReply withData(InputStream dataStream) {
        return withDelayedData(callback -> callback.sendData(dataStream));
    }

    public DataQueryReply withDelayedData(Consumer<DataQueryCallback> consumer) {
        return new DataQueryReplyImpl(DataQueryReplyImpl.Type.ACCEPT)
                .setDataCallbackConsumer(consumer);
    }
}

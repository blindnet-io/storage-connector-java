package io.blindnet.storageconnectors.java.datarequests.reply;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

public class DataRequestReplyBuilder {
    public DataRequestReply withData(byte[] data) {
        return withDelayedData(callback -> callback.sendData(data));
    }

    public DataRequestReply withData(ByteBuffer data) {
        return withDelayedData(callback -> callback.sendData(data));
    }

    public DataRequestReply withData(InputStream dataStream) {
        return withDelayedData(callback -> callback.sendData(dataStream));
    }

    public DataRequestReply withDelayedData(Consumer<DataRequestCallback> consumer) {
        return new DataRequestReplyImpl(DataRequestReplyImpl.Type.ACCEPT)
                .setDataCallbackConsumer(consumer);
    }
}

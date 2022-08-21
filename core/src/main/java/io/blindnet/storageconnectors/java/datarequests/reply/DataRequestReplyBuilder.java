package io.blindnet.storageconnectors.java.datarequests.reply;

import io.blindnet.storageconnectors.java.exceptions.APIException;
import io.blindnet.storageconnectors.java.util.FailableConsumer;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

public class DataRequestReplyBuilder {
    public DataRequestReply withData(BinaryData data) throws APIException {
        return withDelayedData(callback -> callback.sendData(data));
    }

    public DataRequestReply withDelayedData(FailableConsumer<DataRequestCallback> consumer) {
        return new DataRequestReplyImpl(DataRequestReplyImpl.Type.ACCEPT)
                .setDataCallbackConsumer(consumer.sneaky());
    }

    public DataRequestReply withoutData() {
        return new DataRequestReplyImpl(DataRequestReply.Type.ACCEPT);
    }
}

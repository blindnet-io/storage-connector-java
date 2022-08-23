package io.blindnet.storageconnector.datarequests.reply;

import io.blindnet.storageconnector.exceptions.APIException;
import io.blindnet.storageconnector.util.FailableConsumer;

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

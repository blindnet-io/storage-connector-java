package io.blindnet.storageconnectors.java.datarequests;

import io.blindnet.storageconnectors.java.datarequests.query.DataQuery;
import io.blindnet.storageconnectors.java.datarequests.reply.DataRequestReply;
import io.blindnet.storageconnectors.java.datarequests.reply.DataRequestReplyBuilder;

public interface DataRequest {
    String getRequestId();

    DataQuery getQuery();

    Action getAction();

    DataRequestReplyBuilder accept();

    DataRequestReply deny();

    public enum Action {
        DELETE,
        GET,
    }
}

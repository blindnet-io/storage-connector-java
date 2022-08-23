package io.blindnet.storageconnector.datarequests;

import io.blindnet.storageconnector.datarequests.reply.DataRequestReplyBuilder;
import io.blindnet.storageconnector.datarequests.query.DataQuery;
import io.blindnet.storageconnector.datarequests.reply.DataRequestReply;

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

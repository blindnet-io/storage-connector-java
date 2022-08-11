package io.blindnet.storageconnectors.java.datarequests;

import io.blindnet.storageconnectors.java.datarequests.query.DataQuery;
import io.blindnet.storageconnectors.java.datarequests.reply.DataRequestReply;
import io.blindnet.storageconnectors.java.datarequests.reply.DataRequestReplyBuilder;
import io.blindnet.storageconnectors.java.ws.packets.InPacketDataRequest;

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

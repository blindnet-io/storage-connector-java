package io.blindnet.storageconnectors.java.handlers;

import io.blindnet.storageconnectors.java.dataquery.DataQuery;
import io.blindnet.storageconnectors.java.dataquery.reply.DataQueryReply;

public interface DataQueryHandler {
    DataQueryReply get(DataQuery query);
    DataQueryReply delete(DataQuery query);
}

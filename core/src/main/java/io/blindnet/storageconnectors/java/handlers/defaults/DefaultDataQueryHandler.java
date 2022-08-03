package io.blindnet.storageconnectors.java.handlers.defaults;

import io.blindnet.storageconnectors.java.dataquery.DataQuery;
import io.blindnet.storageconnectors.java.dataquery.reply.DataQueryReply;
import io.blindnet.storageconnectors.java.handlers.DataQueryHandler;
import org.slf4j.LoggerFactory;

public class DefaultDataQueryHandler implements DataQueryHandler {
    @Override
    public DataQueryReply get(DataQuery query) {
        return handle();
    }
    public DataQueryReply delete(DataQuery query) {
        return handle();
    }

    private DataQueryReply handle() {
        LoggerFactory.getLogger(DefaultDataQueryHandler.class).warn("Ignoring data query");
        return null;
    }
}

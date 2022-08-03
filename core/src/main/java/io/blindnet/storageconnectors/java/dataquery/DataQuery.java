package io.blindnet.storageconnectors.java.dataquery;

import io.blindnet.storageconnectors.java.dataquery.reply.DataQueryReply;
import io.blindnet.storageconnectors.java.dataquery.reply.DataQueryReplyBuilder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface DataQuery {
    List<String> getSelectors();

    List<String> getSubjects();

    String getProvenance();

    String getTarget();

    Instant getAfter();

    Instant getUntil();

    String getRequestId();

    DataQueryReplyBuilder accept();

    DataQueryReply deny();
}

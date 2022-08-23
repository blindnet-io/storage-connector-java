package io.blindnet.storageconnector.datarequests.query;

import java.time.Instant;
import java.util.List;

public interface DataQuery {
    List<String> getSelectors();

    List<String> getSubjects();

    String getProvenance();

    String getTarget();

    Instant getAfter();

    Instant getUntil();
}

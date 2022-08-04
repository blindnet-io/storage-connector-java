package io.blindnet.storageconnectors.java.dataquery;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.blindnet.storageconnectors.java.dataquery.reply.DataQueryReply;
import io.blindnet.storageconnectors.java.dataquery.reply.DataQueryReplyBuilder;
import io.blindnet.storageconnectors.java.dataquery.reply.DataQueryReplyImpl;
import io.blindnet.storageconnectors.java.logic.DataQueryLogic;

import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataQueryImpl implements DataQuery {
    private List<String> selectors;
    private List<String> subjects;
    private String provenance;
    private String target;
    private Instant after;
    private Instant until;
    @JsonProperty("request_id")
    private String requestId;

    @Override
    public List<String> getSelectors() {
        return selectors;
    }

    public void setSelectors(List<String> selectors) {
        this.selectors = selectors;
    }

    @Override
    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    @Override
    public String getProvenance() {
        return provenance;
    }

    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    @Override
    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public Instant getAfter() {
        return after;
    }

    public void setAfter(Instant after) {
        this.after = after;
    }

    @Override
    public Instant getUntil() {
        return until;
    }

    public void setUntil(Instant until) {
        this.until = until;
    }

    @Override
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @Override
    public DataQueryReplyBuilder accept() {
        return new DataQueryReplyBuilder();
    }

    @Override
    public DataQueryReply deny() {
        return new DataQueryReplyImpl(DataQueryReplyImpl.Type.DENY);
    }
}

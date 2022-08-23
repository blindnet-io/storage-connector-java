package io.blindnet.storageconnector.datarequests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.blindnet.storageconnector.datarequests.reply.DataRequestReplyBuilder;
import io.blindnet.storageconnector.datarequests.query.DataQueryImpl;
import io.blindnet.storageconnector.datarequests.reply.DataRequestReply;
import io.blindnet.storageconnector.datarequests.reply.DataRequestReplyImpl;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataRequestImpl implements DataRequest {
    @JsonProperty("request_id")
    private String requestId;
    private DataQueryImpl query;
    private Action action;

    @Override
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @Override
    public DataQueryImpl getQuery() {
        return query;
    }

    public void setQuery(DataQueryImpl query) {
        this.query = query;
    }

    @Override
    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    @Override
    public DataRequestReplyBuilder accept() {
        return new DataRequestReplyBuilder();
    }

    @Override
    public DataRequestReply deny() {
        return new DataRequestReplyImpl(DataRequestReplyImpl.Type.DENY);
    }
}

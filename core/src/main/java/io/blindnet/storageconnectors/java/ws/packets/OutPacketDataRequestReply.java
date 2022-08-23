package io.blindnet.storageconnectors.java.ws.packets;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.blindnet.storageconnectors.java.datarequests.reply.DataRequestReplyImpl;
import io.blindnet.storageconnectors.java.ws.WsOutPacket;

public class OutPacketDataRequestReply implements WsOutPacket {
    @JsonProperty("request_id")
    private final String requestId;
    private final DataRequestReplyImpl.Type type;

    public OutPacketDataRequestReply(String requestId, DataRequestReplyImpl.Type type) {
        this.requestId = requestId;
        this.type = type;
    }

    public String getRequestId() {
        return requestId;
    }

    public DataRequestReplyImpl.Type getType() {
        return type;
    }

    @Override
    public String getPacketType() {
        return "data_request_reply";
    }
}

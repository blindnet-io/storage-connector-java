package io.blindnet.storageconnectors.java.ws.packets;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.blindnet.storageconnectors.java.dataquery.reply.DataQueryReplyImpl;
import io.blindnet.storageconnectors.java.ws.WsOutPacket;

public class OutPacketDataReply implements WsOutPacket {
    @JsonProperty("request_id")
    private final String requestId;
    private final DataQueryReplyImpl.Type type;

    public OutPacketDataReply(String requestId, DataQueryReplyImpl.Type type) {
        this.requestId = requestId;
        this.type = type;
    }

    public String getRequestId() {
        return requestId;
    }

    public DataQueryReplyImpl.Type getType() {
        return type;
    }

    @Override
    public String getPacketType() {
        return "data_reply";
    }
}

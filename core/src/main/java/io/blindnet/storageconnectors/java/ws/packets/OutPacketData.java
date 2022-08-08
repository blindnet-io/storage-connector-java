package io.blindnet.storageconnectors.java.ws.packets;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.blindnet.storageconnectors.java.ws.WsOutPacket;

public class OutPacketData extends WsOutPacket {
    @JsonProperty("request_id")
    private final String requestId;

    public OutPacketData(String requestId, byte[] data) {
        super(data);

        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }

    @Override
    public String getPacketType() {
        return "data";
    }
}

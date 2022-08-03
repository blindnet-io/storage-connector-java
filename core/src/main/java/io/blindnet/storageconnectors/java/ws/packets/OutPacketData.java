package io.blindnet.storageconnectors.java.ws.packets;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.blindnet.storageconnectors.java.ws.WsOutPacket;

public class OutPacketData implements WsOutPacket {
    @JsonProperty("request_id")
    private final String requestId;
    private final byte[] data;

    public OutPacketData(String requestId, byte[] data) {
        this.requestId = requestId;
        this.data = data;
    }

    public String getRequestId() {
        return requestId;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String getPacketType() {
        return "data";
    }
}

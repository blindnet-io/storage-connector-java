package io.blindnet.storageconnectors.java.ws.packets;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.blindnet.storageconnectors.java.ws.WsOutPacket;

public class OutPacketData extends WsOutPacket {
    @JsonProperty("request_id")
    private final String requestId;

    private final boolean last;

    public OutPacketData(String requestId, byte[] data, boolean last) {
        super(data);

        this.requestId = requestId;
        this.last = last;
    }

    public String getRequestId() {
        return requestId;
    }

    public boolean isLast() {
        return last;
    }

    @Override
    public String getPacketType() {
        return "data";
    }
}

package io.blindnet.storageconnector.ws;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WsOutPayload<T extends WsOutPacket> {
    @JsonProperty("typ")
    private final String type;
    private final T data;

    public WsOutPayload(String type, T data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public T getData() {
        return data;
    }
}

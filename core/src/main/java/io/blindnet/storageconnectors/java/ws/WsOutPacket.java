package io.blindnet.storageconnectors.java.ws;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class WsOutPacket {
    private final byte[] data;

    protected WsOutPacket(byte[] data) {
        this.data = data;
    }

    protected WsOutPacket() {
        this(null);
    }

    @JsonIgnore
    public abstract String getPacketType();

    @JsonIgnore
    public byte[] getData() {
        return data;
    }
}

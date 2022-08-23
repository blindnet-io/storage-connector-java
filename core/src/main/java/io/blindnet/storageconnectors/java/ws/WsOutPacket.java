package io.blindnet.storageconnectors.java.ws;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface WsOutPacket {
    @JsonIgnore
    String getPacketType();
}

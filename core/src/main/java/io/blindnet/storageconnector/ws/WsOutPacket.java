package io.blindnet.storageconnector.ws;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface WsOutPacket {
    @JsonIgnore
    String getPacketType();
}

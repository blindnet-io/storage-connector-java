package io.blindnet.storageconnector.ws;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.blindnet.storageconnector.exceptions.WebSocketException;
import io.blindnet.storageconnector.ws.packets.InPacketDataRequest;
import io.blindnet.storageconnector.ws.packets.InPacketWelcome;

import java.io.IOException;

public class WsInPayload {
    @JsonProperty("typ")
    private String type;
    private JsonNode data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JsonNode getData() {
        return data;
    }

    public void setData(JsonNode data) {
        this.data = data;
    }

    public WsInPacket toPacket(ObjectMapper mapper) throws IOException {
        switch(type) {
            case "data_request":
                return mapper.treeToValue(data, InPacketDataRequest.class);
            case "welcome":
                return mapper.treeToValue(data, InPacketWelcome.class);
            default:
                throw new WebSocketException("Unknown WS packet type");
        }
    }
}

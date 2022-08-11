package io.blindnet.storageconnectors.java.ws;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.blindnet.storageconnectors.java.exceptions.WebSocketException;
import io.blindnet.storageconnectors.java.ws.packets.InPacketDataRequest;

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
            default:
                throw new WebSocketException("Unknown WS packet type");
        }
    }
}

package io.blindnet.storageconnector.ws;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.blindnet.storageconnector.exceptions.WebSocketException;
import io.blindnet.storageconnector.ws.packets.InPacketDataRequest;
import io.blindnet.storageconnector.ws.packets.InPacketWelcome;

import java.io.IOException;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class WsInPayload {
    private UUID appId;
    private UUID connectorId;
    private String connectorName;
    private String connectorType;
    private String connectorConfig;

    @JsonProperty("typ")
    private String type;
    private JsonNode data;

    public UUID getAppId() {
        return appId;
    }

    public void setAppId(UUID appId) {
        this.appId = appId;
    }

    public UUID getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(UUID connectorId) {
        this.connectorId = connectorId;
    }

    public String getConnectorName() {
        return connectorName;
    }

    public void setConnectorName(String connectorName) {
        this.connectorName = connectorName;
    }

    public String getConnectorType() {
        return connectorType;
    }

    public void setConnectorType(String connectorType) {
        this.connectorType = connectorType;
    }

    public String getConnectorConfig() {
        return connectorConfig;
    }

    public void setConnectorConfig(String connectorConfig) {
        this.connectorConfig = connectorConfig;
    }

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

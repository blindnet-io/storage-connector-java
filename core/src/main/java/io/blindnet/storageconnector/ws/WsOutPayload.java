package io.blindnet.storageconnector.ws;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class WsOutPayload<T extends WsOutPacket> {
    @JsonProperty("typ")
    private final String type;
    private final T data;
    private final UUID appId;
    private final UUID connectorId;

    public WsOutPayload(String type, T data, UUID appId, UUID connectorId) {
        this.type = type;
        this.data = data;
        this.appId = appId;
        this.connectorId = connectorId;
    }

    public String getType() {
        return type;
    }

    public T getData() {
        return data;
    }

    public UUID getAppId() {
        return appId;
    }

    public UUID getConnectorId() {
        return connectorId;
    }
}

package io.blindnet.storageconnector.ws.packets;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.blindnet.storageconnector.ws.PacketHandler;
import io.blindnet.storageconnector.ws.WsInPacket;
import io.blindnet.storageconnector.ws.WsInPayload;

import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class InPacketWelcome implements WsInPacket {
    private UUID appId;
    private UUID connectorId;
    private String connectorName;

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

    @Override
    public void callHandler(WsInPayload payload, PacketHandler handler) {
        handler.handlePacket(payload, this);
    }
}

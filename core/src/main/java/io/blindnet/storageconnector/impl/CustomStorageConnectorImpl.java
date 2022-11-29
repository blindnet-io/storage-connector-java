package io.blindnet.storageconnector.impl;

import io.blindnet.storageconnector.CustomStorageConnector;
import io.blindnet.storageconnector.datarequests.DataRequest;
import io.blindnet.storageconnector.datarequests.reply.DataRequestReply;
import io.blindnet.storageconnector.handlers.DataRequestHandler;
import io.blindnet.storageconnector.handlers.defaults.DefaultDataRequestHandler;
import io.blindnet.storageconnector.ws.PacketHandler;
import io.blindnet.storageconnector.ws.WsInPayload;
import io.blindnet.storageconnector.ws.packets.InPacketDataRequest;
import io.blindnet.storageconnector.ws.packets.InPacketWelcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.UUID;

public class CustomStorageConnectorImpl implements CustomStorageConnector, PacketHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomStorageConnectorImpl.class);

    private DataRequestHandler dataRequestHandler = new DefaultDataRequestHandler();

    private UUID applicationId;
    private UUID connectorId;
    private String connectorName;

    private final DataAccessClientImpl dataAccessClient;

    public CustomStorageConnectorImpl(String token, URI endpoint) {
        dataAccessClient = new DataAccessClientImpl(token, endpoint, true, this);
    }

    @Override
    public UUID getApplicationId() {
        return applicationId;
    }

    @Override
    public UUID getConnectorId() {
        return connectorId;
    }

    @Override
    public String getConnectorName() {
        return connectorName;
    }

    @Override
    public DataRequestHandler getDataRequestHandler() {
        return dataRequestHandler;
    }

    @Override
    public CustomStorageConnectorImpl setDataRequestHandler(DataRequestHandler dataRequestHandler) {
        this.dataRequestHandler = dataRequestHandler;
        return this;
    }

    @Override
    public DataAccessClientImpl getDataAccessClient() {
        return dataAccessClient;
    }

    @Override
    public void handlePacket(WsInPayload payload, InPacketDataRequest packet) {
        try {
            DataRequest request = packet.getRequest();
            DataRequestReply reply = getDataRequestHandler().handle(request, this, this);
            ConnectorCommons.processRequestReply(request, reply, this, this);
        } catch(Exception e) {
            getDataAccessClient().getErrorHandler().onError(e);
        }
    }

    @Override
    public void handlePacket(WsInPayload payload, InPacketWelcome packet) {
        applicationId = packet.getAppId();
        connectorId = packet.getConnectorId();
        connectorName = packet.getConnectorName();

        logger.info("Application: {}", applicationId);
        logger.info("Connector: {} - {}", connectorId, connectorName);
    }
}

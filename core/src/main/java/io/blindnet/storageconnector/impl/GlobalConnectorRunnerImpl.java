package io.blindnet.storageconnector.impl;

import io.blindnet.storageconnector.DataAccessClient;
import io.blindnet.storageconnector.GlobalConnectorRunner;
import io.blindnet.storageconnector.datarequests.DataRequest;
import io.blindnet.storageconnector.datarequests.reply.DataRequestReply;
import io.blindnet.storageconnector.handlers.GlobalDataRequestHandler;
import io.blindnet.storageconnector.ws.PacketHandler;
import io.blindnet.storageconnector.ws.WsInPayload;
import io.blindnet.storageconnector.ws.packets.InPacketDataRequest;
import io.blindnet.storageconnector.ws.packets.InPacketWelcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class GlobalConnectorRunnerImpl implements GlobalConnectorRunner, PacketHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalConnectorRunnerImpl.class);

    private final Map<String, Function<String, GlobalDataRequestHandler>> handlerFactories = new HashMap<>();

    private final DataAccessClientImpl dataAccessClient;

    public GlobalConnectorRunnerImpl(String token, URI endpoint) {
        dataAccessClient = new DataAccessClientImpl(token, endpoint, false, this);
    }

    @Override
    public DataAccessClient getDataAccessClient() {
        return dataAccessClient;
    }

    public GlobalConnectorRunnerImpl addHandlerFactory(String type, Function<String, GlobalDataRequestHandler> factory) {
        handlerFactories.put(type, factory);
        dataAccessClient.getWebSocketClient().addHeader("X-Connector-Types", String.join(",", handlerFactories.keySet()));
        return this;
    }

    @Override
    public void handlePacket(WsInPayload payload, InPacketDataRequest packet) {
        Function<String, GlobalDataRequestHandler> factory = handlerFactories.get(payload.getConnectorType());
        if(factory == null) {
            logger.warn("Unknown connector type: " + payload.getConnectorType());
            return;
        }

        GlobalDataRequestHandler handler;
        try {
            handler = factory.apply(payload.getConnectorConfig());
        } catch(Exception e) {
            logger.error("Error while building handler for type " + payload.getConnectorType(), e);
            return;
        }
        if(handler == null) {
            logger.error("Factory returned null handler for type " + payload.getConnectorType());
            return;
        }

        GlobalStorageConnectorImpl connector = new GlobalStorageConnectorImpl(
                payload.getAppId(), payload.getConnectorId(), payload.getConnectorName(),
                payload.getConnectorType(), payload.getConnectorConfig()
        );

        try {
            DataRequest request = packet.getRequest();
            DataRequestReply reply = handler.handle(request, connector, this);
            ConnectorCommons.processRequestReply(request, reply, connector, this);
        } catch(Exception e) {
            getDataAccessClient().getErrorHandler().onError(e);
        }
    }

    @Override
    public void handlePacket(WsInPayload payload, InPacketWelcome packet) {
        logger.info("Welcome!");
    }
}

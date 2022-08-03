package io.blindnet.storageconnectors.java;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.blindnet.storageconnectors.java.exceptions.WebSocketException;
import io.blindnet.storageconnectors.java.handlers.DataQueryHandler;
import io.blindnet.storageconnectors.java.handlers.ErrorHandler;
import io.blindnet.storageconnectors.java.handlers.defaults.DefaultDataQueryHandler;
import io.blindnet.storageconnectors.java.handlers.defaults.DefaultErrorHandler;
import io.blindnet.storageconnectors.java.logic.Logic;
import io.blindnet.storageconnectors.java.ws.WsInPacket;
import io.blindnet.storageconnectors.java.ws.WsOutPacket;
import io.blindnet.storageconnectors.java.ws.WsInPayload;
import io.blindnet.storageconnectors.java.ws.WsOutPayload;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;

public class StorageConnectorImpl extends WebSocketClient implements StorageConnector {
    private final static Logger logger = LoggerFactory.getLogger(StorageConnectorImpl.class);

    private DataQueryHandler dataQueryHandler = new DefaultDataQueryHandler();
    private ErrorHandler errorHandler = new DefaultErrorHandler();

    private final ObjectMapper objectMapper;

    StorageConnectorImpl(URI endpoint) {
        super(endpoint);

        objectMapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .build();
    }

    @Override
    public DataQueryHandler getDataQueryHandler() {
        return dataQueryHandler;
    }

    @Override
    public StorageConnectorImpl setDataQueryHandler(DataQueryHandler dataQueryHandler) {
        this.dataQueryHandler = dataQueryHandler;
        return this;
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    @Override
    public StorageConnectorImpl setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    @Override
    public void start() {
        connect();
    }

    @Override
    public void startBlocking() throws IOException, InterruptedException {
        if(!connectBlocking())
            throw new IOException("Initial WebSocket connection failed");
    }

    @Override
    public void onOpen(ServerHandshake handshake) {}

    @Override
    public void onMessage(String message) {
        logger.info("in: " + message);

        try {
            WsInPayload payload = objectMapper.readValue(message, WsInPayload.class);
            WsInPacket packet = payload.toPacket(objectMapper);
            Logic logic = packet.getLogic(this);
            logic.run();
        } catch (IOException e) {
            errorHandler.onError(new WebSocketException(e));
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        // TODO
    }

    @Override
    public void onError(Exception e) {
        errorHandler.onError(new WebSocketException(e));
    }

    public <T extends WsOutPacket> void sendPacket(T packet) throws WebSocketException {
        try {
            String message = objectMapper.writeValueAsString(new WsOutPayload<T>(packet.getPacketType(), packet));
            logger.info("out: " + message);
            send(message);
        } catch (JsonProcessingException e) {
            throw new WebSocketException(e);
        }
    }
}

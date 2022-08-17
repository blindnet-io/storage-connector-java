package io.blindnet.storageconnectors.java;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.blindnet.storageconnectors.java.exceptions.WebSocketException;
import io.blindnet.storageconnectors.java.handlers.DataRequestHandler;
import io.blindnet.storageconnectors.java.handlers.ErrorHandler;
import io.blindnet.storageconnectors.java.handlers.defaults.DefaultDataRequestHandler;
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
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StorageConnectorImpl extends WebSocketClient implements StorageConnector {
    private static final URI DEFAULT_ENDPOINT = URI.create("wss://blindnet-dac-staging.azurewebsites.net/v1/connectors/ws");

    private static final Logger logger = LoggerFactory.getLogger(StorageConnectorImpl.class);

    private DataRequestHandler dataRequestHandler = new DefaultDataRequestHandler();
    private ErrorHandler errorHandler = new DefaultErrorHandler();
    private ExecutorService executorService = Executors.newCachedThreadPool();

    private final Random random = new Random();
    private final ObjectMapper objectMapper;

    private long retryDelay = 1;

    StorageConnectorImpl() {
        this(DEFAULT_ENDPOINT);
    }

    StorageConnectorImpl(URI endpoint) {
        super(endpoint);

        objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .build();
    }

    @Override
    public DataRequestHandler getDataRequestHandler() {
        return dataRequestHandler;
    }

    @Override
    public StorageConnectorImpl setDataRequestHandler(DataRequestHandler dataRequestHandler) {
        this.dataRequestHandler = dataRequestHandler;
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
    public ExecutorService getExecutorService() {
        return executorService;
    }

    @Override
    public StorageConnector setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
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
    public void connect() {
        logger.info("Connecting to WebSocket");

        super.connect();
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        logger.info("WebSocket connection established");

        retryDelay = 1;
    }

    @Override
    public void onMessage(ByteBuffer buf) {
        try {
            WsInPayload payload = objectMapper.readValue(buf.array(), WsInPayload.class);
            WsInPacket packet = payload.toPacket(objectMapper);
            Logic logic = packet.getLogic(this);
            getExecutorService().execute(logic::runCatch);
        } catch (IOException e) {
            errorHandler.onError(new WebSocketException(e));
        }
    }

    @Override
    public void onMessage(String message) {
        onMessage(ByteBuffer.wrap(message.getBytes()));
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.error("WebSocket connection closed, reconnecting in " + (retryDelay / 1000) + "s");

        Executors.newSingleThreadScheduledExecutor()
                .schedule(this::reconnect, retryDelay, TimeUnit.MILLISECONDS);

        if(!System.getenv().containsKey("DISABLE_RETRY_BACKOFF")) {
            if(retryDelay < 15000)
                retryDelay += random.nextInt(1000);
        }
    }

    @Override
    public void onError(Exception e) {
        errorHandler.onError(new WebSocketException(e));
    }

    public <T extends WsOutPacket> void sendPacket(T packet) throws WebSocketException {
        try {
            byte[] packetBytes = objectMapper.writeValueAsBytes(new WsOutPayload<T>(packet.getPacketType(), packet));
            byte[] packetData = packet.getData();

            ByteBuffer buf = ByteBuffer.allocate(4 + packetBytes.length + (packetData != null ? packetData.length : 0));
            buf.putInt(packetBytes.length);
            buf.put(packetBytes);
            if(packetData != null) buf.put(packetData);
            buf.position(0);

            send(buf);
        } catch (JsonProcessingException e) {
            throw new WebSocketException(e);
        }
    }
}

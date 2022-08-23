package io.blindnet.storageconnector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.blindnet.storageconnector.exceptions.WebSocketException;
import io.blindnet.storageconnector.handlers.DataRequestHandler;
import io.blindnet.storageconnector.logic.Logic;
import io.blindnet.storageconnector.ws.WsInPacket;
import io.blindnet.storageconnector.ws.WsInPayload;
import io.blindnet.storageconnector.ws.WsOutPacket;
import io.blindnet.storageconnector.ws.WsOutPayload;
import io.blindnet.storageconnector.handlers.ErrorHandler;
import io.blindnet.storageconnector.handlers.defaults.DefaultDataRequestHandler;
import io.blindnet.storageconnector.handlers.defaults.DefaultErrorHandler;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StorageConnectorImpl extends WebSocketClient implements StorageConnector {
    private static final URI DEFAULT_ENDPOINT = URI.create("https://blindnet-dac-staging.azurewebsites.net");

    private static final Logger logger = LoggerFactory.getLogger(StorageConnectorImpl.class);

    private final URI endpoint;
    private final DataAccessClient dataAccessClient;

    private DataRequestHandler dataRequestHandler = new DefaultDataRequestHandler();
    private ErrorHandler errorHandler = new DefaultErrorHandler();
    private ExecutorService executorService = Executors.newCachedThreadPool();

    private final Random random = new Random();
    private JsonMapper jsonMapper;

    private long retryDelay = 1;

    StorageConnectorImpl() {
        this(DEFAULT_ENDPOINT);
    }

    StorageConnectorImpl(URI endpoint) {
        super(URI.create(endpoint.toString().replaceFirst("http", "ws")).resolve("/v1/connectors/ws"));

        this.endpoint = endpoint;
        this.dataAccessClient = new DataAccessClient(this);

        jsonMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .build();
    }

    public URI getEndpoint() {
        return endpoint;
    }

    public DataAccessClient getDataAccessClient() {
        return dataAccessClient;
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
    public JsonMapper getJsonMapper() {
        return jsonMapper;
    }

    @Override
    public StorageConnector setJsonMapper(JsonMapper objectMapper) {
        this.jsonMapper = objectMapper;
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
    public void onMessage(String message) {
        try {
            WsInPayload payload = jsonMapper.readValue(message, WsInPayload.class);
            WsInPacket packet = payload.toPacket(jsonMapper);
            Logic logic = packet.getLogic(this);
            getExecutorService().execute(logic::runCatch);
        } catch (IOException e) {
            errorHandler.onError(new WebSocketException(e));
        }
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
            send(jsonMapper.writeValueAsString(new WsOutPayload<T>(packet.getPacketType(), packet)));
        } catch (JsonProcessingException e) {
            throw new WebSocketException(e);
        }
    }
}

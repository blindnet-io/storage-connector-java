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
import java.net.URISyntaxException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StorageConnectorImpl implements StorageConnector {
    private static final URI DEFAULT_ENDPOINT = URI.create("https://blindnet-dac-staging.azurewebsites.net");

    private static final Logger logger = LoggerFactory.getLogger(StorageConnectorImpl.class);

    private final String appId;
    private URI endpoint;
    private DataAccessClient dataAccessClient;

    private DataRequestHandler dataRequestHandler = new DefaultDataRequestHandler();
    private ErrorHandler errorHandler = new DefaultErrorHandler();
    private ExecutorService executorService = Executors.newCachedThreadPool();

    private final Random random = new Random();
    private JsonMapper jsonMapper;

    StorageConnectorImpl(String appId) throws URISyntaxException {
        this.appId = appId;
        this.endpoint = getDefaultEndpoint();

        jsonMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .build();
    }

    @Override
    public String getAppId() {
        return appId;
    }

    private static URI getDefaultEndpoint() throws URISyntaxException {
        String env = System.getenv().get("BN_CONNECTOR_ENDPOINT");
        if(env != null) return new URI(env);
        else return DEFAULT_ENDPOINT;
    }

    @Override
    public void setEndpoint(String endpoint) throws URISyntaxException {
        setEndpoint(new URI(endpoint));
    }

    @Override
    public void setEndpoint(URI endpoint) {
        if(dataAccessClient != null)
            throw new IllegalStateException("Can't set endpoint after connector start");

        this.endpoint = endpoint;
    }

    @Override
    public URI getEndpoint() {
        return endpoint;
    }

    public Random getRandom() {
        return random;
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
        if(dataAccessClient != null)
            throw new IllegalStateException("Connector already started");

        dataAccessClient = new DataAccessClient(this);
        dataAccessClient.ws().connect();
    }

    @Override
    public void startBlocking() throws IOException, InterruptedException {
        if(dataAccessClient != null)
            throw new IllegalStateException("Connector already started");

        dataAccessClient = new DataAccessClient(this);
        if(!dataAccessClient.ws().connectBlocking())
            throw new IOException("Initial WebSocket connection failed");
    }
}

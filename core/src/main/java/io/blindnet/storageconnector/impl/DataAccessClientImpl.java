package io.blindnet.storageconnector.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.blindnet.storageconnector.DataAccessClient;
import io.blindnet.storageconnector.StorageConnector;
import io.blindnet.storageconnector.datarequests.reply.DataRequestReply;
import io.blindnet.storageconnector.exceptions.APIException;
import io.blindnet.storageconnector.exceptions.WebSocketException;
import io.blindnet.storageconnector.handlers.ErrorHandler;
import io.blindnet.storageconnector.handlers.defaults.DefaultErrorHandler;
import io.blindnet.storageconnector.ws.PacketHandler;
import io.blindnet.storageconnector.ws.WsInPacket;
import io.blindnet.storageconnector.ws.WsInPayload;
import io.blindnet.storageconnector.ws.WsOutPacket;
import io.blindnet.storageconnector.ws.WsOutPayload;
import io.blindnet.storageconnector.ws.packets.OutPacketDataRequestReply;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DataAccessClientImpl implements DataAccessClient {
    private final String token;
    private final URI endpoint;
    private final boolean customConnector;
    private final PacketHandler packetHandler;

    private JsonMapper jsonMapper;
    private ErrorHandler errorHandler = new DefaultErrorHandler();
    private ExecutorService executorService = Executors.newCachedThreadPool();

    private final Random random = new Random();
    private final OkHttpClient http = new OkHttpClient();
    private final WebSocketClientImpl ws;

    public DataAccessClientImpl(String token, URI endpoint, boolean customConnector, PacketHandler packetHandler) {
        if(!endpoint.getScheme().startsWith("http"))
            throw new IllegalArgumentException("Invalid endpoint URI: not HTTP(S)");

        this.token = token;
        this.endpoint = endpoint;
        this.customConnector = customConnector;
        this.packetHandler = packetHandler;

        jsonMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .build();

        ws = new WebSocketClientImpl(
                URI.create(endpoint.toString().replaceFirst("http", "ws"))
                        .resolve("/v1/connectors/ws/" + (customConnector ? "custom" : "global")),
                token
        );
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public URI getEndpoint() {
        return endpoint;
    }

    @Override
    public JsonMapper getJsonMapper() {
        return jsonMapper;
    }

    @Override
    public DataAccessClientImpl setJsonMapper(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
        return this;
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    @Override
    public DataAccessClientImpl setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    @Override
    public ExecutorService getExecutorService() {
        return executorService;
    }

    @Override
    public DataAccessClientImpl setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    @Override
    public WebSocketClient getWebSocketClient() {
        return ws;
    }

    private Request.Builder mkRequest(StorageConnector connector, String path) throws APIException {
        try {
            Request.Builder builder = new Request.Builder()
                    .url(endpoint.resolve(path).toURL())
                    .header("Authorization", "Bearer " + token);

            if(!customConnector) {
                builder.header("X-Application-ID", connector.getApplicationId().toString());
                builder.header("X-Connector-ID", connector.getConnectorId().toString());
            }

            return builder;
        } catch (MalformedURLException e) {
            throw new APIException(e);
        }
    }

    private <R> R run(Request request, Class<R> cl) throws APIException {
        try (Response response = execute(request)) {
            return jsonMapper.readValue(Objects.requireNonNull(response.body()).bytes(), cl);
        } catch (IOException e) {
            throw new APIException(e);
        }
    }

    private void run(Request request) throws APIException {
        execute(request).close();
    }

    private Response execute(Request request) throws APIException {
        Response response;
        try {
            response = http.newCall(request).execute();
        } catch (IOException e) {
            throw new APIException(e);
        }

        if(!response.isSuccessful()) {
            response.close();
            throw new APIException("Status code " + response.code());
        }

        return response;
    }

    @Override
    public void sendDataRequestReply(StorageConnector connector, String requestId, DataRequestReply reply) throws APIException {
        sendPacket(connector, new OutPacketDataRequestReply(requestId, reply.getType()));
    }

    @Override
    public void uploadMainData(StorageConnector connector, String requestId, byte[] data) throws APIException {
        run(mkUploadData(connector, requestId, data, "main").build());
    }

    @Override
    public void uploadMainData(StorageConnector connector, String requestId, InputStream data) throws APIException {
        run(mkUploadData(connector, requestId, data, "main").build());
    }

    @Override
    public String uploadAdditionalData(StorageConnector connector, String requestId, byte[] data) throws APIException {
        return run(mkUploadData(connector, requestId, data, "additional").build(), String.class);
    }

    @Override
    public String uploadAdditionalData(StorageConnector connector, String requestId, InputStream data) throws APIException {
        return run(mkUploadData(connector, requestId, data, "additional").build(), String.class);
    }

    private Request.Builder mkUploadData(StorageConnector connector, String requestId, byte[] data, String type) throws APIException {
        return mkRequest(connector, "/v1/connectors/data/" + requestId + "/" + type + "?last=true")
                .post(RequestBody.create(data, MediaType.get("application/octet-stream")));
    }

    private Request.Builder mkUploadData(StorageConnector connector, String requestId, InputStream data, String type) throws APIException {
        return mkRequest(connector, "/v1/connectors/data/" + requestId + "/" + type + "?last=true")
                .post(new RequestBody() {
                    @Override
                    public MediaType contentType() {
                        return MediaType.get("application/octet-stream");
                    }

                    @Override
                    public long contentLength() throws IOException {
                        return data.available() == 0 ? -1 : data.available();
                    }

                    @Override
                    public void writeTo(@NotNull BufferedSink sink) throws IOException {
                        try(Source source = Okio.source(data)) {
                            sink.writeAll(source);
                        }
                    }
                });
    }

    public <T extends WsOutPacket> void sendPacket(StorageConnector connector, T packet) throws WebSocketException {
        try {
            WsOutPayload<T> payload;
            if(customConnector) payload = new WsOutPayload<T>(packet.getPacketType(), packet, null, null);
            else payload = new WsOutPayload<T>(packet.getPacketType(), packet, connector.getApplicationId(), connector.getConnectorId());
            ws.send(getJsonMapper().writeValueAsString(payload));
        } catch (JsonProcessingException e) {
            throw new WebSocketException(e);
        }
    }

    public class WebSocketClientImpl extends WebSocketClient {
        private final Logger logger = LoggerFactory.getLogger(WebSocketClientImpl.class);

        private long retryDelay = 1;

        public WebSocketClientImpl(URI endpoint, String token) {
            super(endpoint);
            addHeader("Authorization", "Bearer " + token);
        }

        @Override
        public void connect() {
            logger.info("Connecting to WebSocket");

            super.connect();
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            logger.info("WebSocket connection established");

            retryDelay = 1;
        }

        @Override
        public void onMessage(String message) {
            try {
                WsInPayload payload = getJsonMapper().readValue(message, WsInPayload.class);
                WsInPacket packet = payload.toPacket(getJsonMapper());
                getExecutorService().execute(() -> packet.callHandler(payload, packetHandler));
            } catch (Exception e) {
                getErrorHandler().onError(new WebSocketException(e));
            }
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            logger.error("WebSocket connection closed (" + reason + "), reconnecting in " + (retryDelay / 1000) + "s");

            Executors.newSingleThreadScheduledExecutor()
                    .schedule(this::reconnect, retryDelay, TimeUnit.MILLISECONDS);

            if(!System.getenv().containsKey("DISABLE_RETRY_BACKOFF")) {
                if(retryDelay < 15000)
                    retryDelay += random.nextInt(1000);
            }
        }

        @Override
        public void onError(Exception e) {
            getErrorHandler().onError(new WebSocketException(e));
        }
    }
}

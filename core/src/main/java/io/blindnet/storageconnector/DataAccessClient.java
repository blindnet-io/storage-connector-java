package io.blindnet.storageconnector;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.blindnet.storageconnector.exceptions.APIException;
import io.blindnet.storageconnector.exceptions.WebSocketException;
import io.blindnet.storageconnector.logic.Logic;
import io.blindnet.storageconnector.ws.WsInPacket;
import io.blindnet.storageconnector.ws.WsInPayload;
import io.blindnet.storageconnector.ws.WsOutPacket;
import io.blindnet.storageconnector.ws.WsOutPayload;
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
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DataAccessClient {
    private final StorageConnectorImpl connector;
    private final OkHttpClient http = new OkHttpClient();
    private final WebSocketClientImpl ws;

    public DataAccessClient(StorageConnectorImpl connector) {
        this.connector = connector;

        if(!connector.getEndpoint().getScheme().startsWith("http"))
            throw new IllegalArgumentException("Invalid endpoint URI: not HTTP(S)");

        ws = new WebSocketClientImpl(
                URI.create(connector.getEndpoint().toString().replaceFirst("http", "ws"))
                        .resolve("/v1/connectors/ws/" + connector.getAppId())
        );
    }

    public OkHttpClient http() {
        return http;
    }

    public WebSocketClientImpl ws() {
        return ws;
    }

    private Request.Builder mkRequest(String endpoint) throws APIException {
        try {
            return new Request.Builder()
                    .url(connector.getEndpoint().resolve(endpoint).toURL());
        } catch (MalformedURLException e) {
            throw new APIException(e);
        }
    }

    private <R> R run(Request request, Class<R> cl) throws APIException {
        try (Response response = http.newCall(request).execute()) {
            if(!response.isSuccessful())
                throw new APIException("Status code " + response.code());

            return connector.getJsonMapper().readValue(Objects.requireNonNull(response.body()).bytes(), cl);
        } catch (IOException e) {
            throw new APIException(e);
        }
    }

    public String uploadMainData(String requestId, byte[] data) throws APIException {
        return uploadData(requestId, data, "main");
    }

    public String uploadMainData(String requestId, InputStream data) throws APIException {
        return uploadData(requestId, data, "main");
    }

    public String uploadAdditionalData(String requestId, byte[] data) throws APIException {
        return uploadData(requestId, data, "additional");
    }

    public String uploadAdditionalData(String requestId, InputStream data) throws APIException {
        return uploadData(requestId, data, "additional");
    }

    private String uploadData(String requestId, byte[] data, String type) throws APIException {
        return run(mkRequest("/v1/connectors/data/" + connector.getAppId() + "/" + requestId + "/" + type + "?last=true")
                .post(RequestBody.create(data, MediaType.get("application/octet-stream")))
                .build(), String.class);
    }

    private String uploadData(String requestId, InputStream data, String type) throws APIException {
        return run(mkRequest("/v1/connectors/data/" + connector.getAppId() + "/" + requestId + "/" + type + "?last=true")
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
                })
                .build(), String.class);
    }

    public <T extends WsOutPacket> void sendPacket(T packet) throws WebSocketException {
        try {
            ws.send(connector.getJsonMapper().writeValueAsString(new WsOutPayload<T>(packet.getPacketType(), packet)));
        } catch (JsonProcessingException e) {
            throw new WebSocketException(e);
        }
    }

    public class WebSocketClientImpl extends WebSocketClient {
        private final Logger logger = LoggerFactory.getLogger(WebSocketClientImpl.class);

        private long retryDelay = 1;

        public WebSocketClientImpl(URI endpoint) {
            super(endpoint);
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
                WsInPayload payload = connector.getJsonMapper().readValue(message, WsInPayload.class);
                WsInPacket packet = payload.toPacket(connector.getJsonMapper());
                Logic logic = packet.getLogic(connector);
                connector.getExecutorService().execute(logic::runCatch);
            } catch (Exception e) {
                connector.getErrorHandler().onError(new WebSocketException(e));
            }
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            logger.error("WebSocket connection closed, reconnecting in " + (retryDelay / 1000) + "s");

            Executors.newSingleThreadScheduledExecutor()
                    .schedule(this::reconnect, retryDelay, TimeUnit.MILLISECONDS);

            if(!System.getenv().containsKey("DISABLE_RETRY_BACKOFF")) {
                if(retryDelay < 15000)
                    retryDelay += connector.getRandom().nextInt(1000);
            }
        }

        @Override
        public void onError(Exception e) {
            connector.getErrorHandler().onError(new WebSocketException(e));
        }
    }
}

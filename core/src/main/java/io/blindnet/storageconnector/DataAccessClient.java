package io.blindnet.storageconnector;

import com.fasterxml.jackson.databind.json.JsonMapper;
import io.blindnet.storageconnector.datarequests.reply.DataRequestReply;
import io.blindnet.storageconnector.exceptions.APIException;
import io.blindnet.storageconnector.handlers.ErrorHandler;
import org.java_websocket.client.WebSocketClient;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;

public interface DataAccessClient {
    URI DEFAULT_ENDPOINT = URI.create("https://stage.storage.blindnet.io");

    static URI getDefaultEndpoint() throws URISyntaxException {
        String env = System.getenv().get("BN_CONNECTOR_ENDPOINT");
        if(env != null) return new URI(env);
        else return DEFAULT_ENDPOINT;
    }

    String getToken();

    URI getEndpoint();

    ErrorHandler getErrorHandler();

    DataAccessClient setErrorHandler(ErrorHandler errorHandler);

    ExecutorService getExecutorService();

    DataAccessClient setExecutorService(ExecutorService executorService);

    JsonMapper getJsonMapper();

    DataAccessClient setJsonMapper(JsonMapper objectMapper);

    WebSocketClient getWebSocketClient();

    void sendDataRequestReply(StorageConnector connector, String requestId, DataRequestReply reply) throws APIException;

    void uploadMainData(StorageConnector connector, String requestId, byte[] data) throws APIException;

    void uploadMainData(StorageConnector connector, String requestId, InputStream data) throws APIException;

    String uploadAdditionalData(StorageConnector connector, String requestId, byte[] data) throws APIException;

    String uploadAdditionalData(StorageConnector connector, String requestId, InputStream data) throws APIException;
}

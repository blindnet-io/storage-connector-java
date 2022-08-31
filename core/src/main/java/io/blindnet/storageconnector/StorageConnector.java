package io.blindnet.storageconnector;

import com.fasterxml.jackson.databind.json.JsonMapper;
import io.blindnet.storageconnector.handlers.DataRequestHandler;
import io.blindnet.storageconnector.handlers.ErrorHandler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;

public interface StorageConnector {
    static StorageConnector create(String appId) throws URISyntaxException {
        return new StorageConnectorImpl(appId);
    }

    String getAppId();

    URI getEndpoint();

    void setEndpoint(String endpoint) throws URISyntaxException;

    void setEndpoint(URI endpoint);

    DataRequestHandler getDataRequestHandler();

    StorageConnector setDataRequestHandler(DataRequestHandler dataRequestHandler);

    ErrorHandler getErrorHandler();

    StorageConnector setErrorHandler(ErrorHandler errorHandler);

    ExecutorService getExecutorService();

    StorageConnector setExecutorService(ExecutorService executorService);

    JsonMapper getJsonMapper();

    StorageConnector setJsonMapper(JsonMapper objectMapper);

    void start();

    void startBlocking() throws IOException, InterruptedException;
}

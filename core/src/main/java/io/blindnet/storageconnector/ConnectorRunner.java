package io.blindnet.storageconnector;

import com.fasterxml.jackson.databind.json.JsonMapper;
import io.blindnet.storageconnector.handlers.ErrorHandler;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

public interface ConnectorRunner {
    DataAccessClient getDataAccessClient();

    default ErrorHandler getErrorHandler() {
        return getDataAccessClient().getErrorHandler();
    };

    default DataAccessClient setErrorHandler(ErrorHandler errorHandler) {
        getDataAccessClient().setErrorHandler(errorHandler);
    }

    default ExecutorService getExecutorService() {
        return getDataAccessClient().getExecutorService();
    }

    default DataAccessClient setExecutorService(ExecutorService executorService) {
        getDataAccessClient().setExecutorService(executorService);
    }

    default JsonMapper getJsonMapper() {
        return getDataAccessClient().getJsonMapper();
    }

    default DataAccessClient setJsonMapper(JsonMapper objectMapper) {
        getDataAccessClient().setJsonMapper(objectMapper);
    }

    default void start() {
        getDataAccessClient().getWebSocketClient().connect();
    };

    default void startBlocking() throws IOException, InterruptedException {
        if(!getDataAccessClient().getWebSocketClient().connectBlocking())
            throw new IOException("Initial WebSocket connection failed");
    }
}


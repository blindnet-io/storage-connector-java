package io.blindnet.storageconnector;

import com.fasterxml.jackson.databind.json.JsonMapper;
import io.blindnet.storageconnector.handlers.ErrorHandler;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

@SuppressWarnings("unchecked")
public interface ConnectorRunner<T extends ConnectorRunner<T>> {
    DataAccessClient getDataAccessClient();

    default ErrorHandler getErrorHandler() {
        return getDataAccessClient().getErrorHandler();
    };

    default T setErrorHandler(ErrorHandler errorHandler) {
        getDataAccessClient().setErrorHandler(errorHandler);
        return (T) this;
    }

    default ExecutorService getExecutorService() {
        return getDataAccessClient().getExecutorService();
    }

    default T setExecutorService(ExecutorService executorService) {
        getDataAccessClient().setExecutorService(executorService);
        return (T) this;
    }

    default JsonMapper getJsonMapper() {
        return getDataAccessClient().getJsonMapper();
    }

    default T setJsonMapper(JsonMapper objectMapper) {
        getDataAccessClient().setJsonMapper(objectMapper);
        return (T) this;
    }

    default void start() {
        getDataAccessClient().getWebSocketClient().connect();
    };

    default void startBlocking() throws IOException, InterruptedException {
        if(!getDataAccessClient().getWebSocketClient().connectBlocking())
            throw new IOException("Initial WebSocket connection failed");
    }
}


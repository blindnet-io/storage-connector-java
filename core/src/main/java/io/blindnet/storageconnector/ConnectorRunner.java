package io.blindnet.storageconnector;

import java.io.IOException;

public interface ConnectorRunner {
    DataAccessClient getDataAccessClient();

    default void start() {
        getDataAccessClient().getWebSocketClient().connect();
    };

    default void startBlocking() throws IOException, InterruptedException {
        if(!getDataAccessClient().getWebSocketClient().connectBlocking())
            throw new IOException("Initial WebSocket connection failed");
    }
}


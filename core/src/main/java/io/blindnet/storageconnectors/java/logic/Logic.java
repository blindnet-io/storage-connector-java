package io.blindnet.storageconnectors.java.logic;

import io.blindnet.storageconnectors.java.StorageConnectorImpl;
import io.blindnet.storageconnectors.java.exceptions.WebSocketException;

public abstract class Logic {
    private final StorageConnectorImpl connector;

    protected Logic(StorageConnectorImpl connector) {
        this.connector = connector;
    }

    protected StorageConnectorImpl getConnector() {
        return connector;
    }

    public abstract void run() throws WebSocketException;
}

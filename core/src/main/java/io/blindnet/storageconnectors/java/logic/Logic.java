package io.blindnet.storageconnectors.java.logic;

import io.blindnet.storageconnectors.java.StorageConnectorImpl;

public abstract class Logic {
    private final StorageConnectorImpl connector;

    protected Logic(StorageConnectorImpl connector) {
        this.connector = connector;
    }

    protected StorageConnectorImpl getConnector() {
        return connector;
    }

    public abstract void run() throws Exception;

    public void runCatch() {
        try {
            run();
        } catch(Exception e) {
            getConnector().onError(e);
        }
    }
}

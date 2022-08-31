package io.blindnet.storageconnector.logic;

import io.blindnet.storageconnector.StorageConnectorImpl;

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
            getConnector().getErrorHandler().onError(e);
        }
    }
}

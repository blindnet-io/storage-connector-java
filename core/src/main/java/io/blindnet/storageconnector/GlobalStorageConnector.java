package io.blindnet.storageconnector;

public interface GlobalStorageConnector extends StorageConnector {
    String getConnectorType();

    String getConnectorConfig();
}

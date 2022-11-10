package io.blindnet.storageconnector;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

public interface StorageConnector {
    static CustomStorageConnector create(String token) throws URISyntaxException {
        return CustomStorageConnector.create(token);
    }

    static CustomStorageConnector create(String token, String endpoint) throws URISyntaxException {
        return CustomStorageConnector.create(token, endpoint);
    }

    static CustomStorageConnector create(String token, URI endpoint) {
        return CustomStorageConnector.create(token, endpoint);
    }

    UUID getApplicationId();

    UUID getConnectorId();

    String getConnectorName();
}

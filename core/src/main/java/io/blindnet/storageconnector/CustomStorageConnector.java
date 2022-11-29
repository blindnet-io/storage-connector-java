package io.blindnet.storageconnector;

import io.blindnet.storageconnector.handlers.DataRequestHandler;
import io.blindnet.storageconnector.impl.CustomStorageConnectorImpl;

import java.net.URI;
import java.net.URISyntaxException;

public interface CustomStorageConnector extends StorageConnector, ConnectorRunner<CustomStorageConnector> {
    static CustomStorageConnector create(String token) throws URISyntaxException {
        return create(token, DataAccessClient.getDefaultEndpoint());
    }

    static CustomStorageConnector create(String token, String endpoint) throws URISyntaxException {
        return create(token, new URI(endpoint));
    }

    static CustomStorageConnector create(String token, URI endpoint) {
        return new CustomStorageConnectorImpl(token, endpoint);
    }

    CustomStorageConnector setDataRequestHandler(DataRequestHandler dataRequestHandler);

    DataRequestHandler getDataRequestHandler();
}

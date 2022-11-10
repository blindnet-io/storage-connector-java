package io.blindnet.storageconnector;

import io.blindnet.storageconnector.handlers.GlobalDataRequestHandler;
import io.blindnet.storageconnector.impl.GlobalConnectorRunnerImpl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Function;

public interface GlobalConnectorRunner extends ConnectorRunner {
    static GlobalConnectorRunner create(String token) throws URISyntaxException {
        return create(token, DataAccessClient.getDefaultEndpoint());
    }

    static GlobalConnectorRunner create(String token, String endpoint) throws URISyntaxException {
        return create(token, new URI(endpoint));
    }

    static GlobalConnectorRunner create(String token, URI endpoint) {
        return new GlobalConnectorRunnerImpl(token, endpoint);
    }

    GlobalConnectorRunner addHandlerFactory(String type, Function<String, GlobalDataRequestHandler> factory);
}

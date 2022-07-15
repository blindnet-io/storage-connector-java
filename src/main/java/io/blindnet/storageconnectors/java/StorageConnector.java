package io.blindnet.storageconnectors.java;

import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

public class StorageConnector {
    private final WebSocketClientImpl ws;

    public static StorageConnector create(String endpoint) throws URISyntaxException {
        return create(new URI(endpoint));
    }

    public static StorageConnector create(URI endpoint) {
        return new StorageConnector(endpoint);
    }

    private StorageConnector(URI endpoint) {
        ws = new WebSocketClientImpl(endpoint);
    }

    public void start() throws InterruptedException {
        LoggerFactory.getLogger(Main.class).info("r=" + ws.connectBlocking());
    }
}

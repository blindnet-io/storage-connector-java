package io.blindnet.storageconnectors.java;

import io.blindnet.storageconnectors.java.dataquery.reply.DataQueryReply;
import io.blindnet.storageconnectors.java.handlers.DataQueryHandler;
import io.blindnet.storageconnectors.java.handlers.ErrorHandler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;

public interface StorageConnector {
    static StorageConnector create(String endpoint) throws URISyntaxException {
        return create(new URI(endpoint));
    }

    static StorageConnector create(URI endpoint) {
        return new StorageConnectorImpl(endpoint);
    }

    DataQueryHandler getDataQueryHandler();

    StorageConnector setDataQueryHandler(DataQueryHandler dataQueryHandler);

    ErrorHandler getErrorHandler();

    StorageConnector setErrorHandler(ErrorHandler errorHandler);

    ExecutorService getExecutorService();

    StorageConnector setExecutorService(ExecutorService executorService);

    void start();

    void startBlocking() throws IOException, InterruptedException;
}

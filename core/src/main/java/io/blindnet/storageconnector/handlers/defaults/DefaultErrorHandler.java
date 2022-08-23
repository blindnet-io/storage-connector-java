package io.blindnet.storageconnector.handlers.defaults;

import io.blindnet.storageconnector.handlers.ErrorHandler;
import org.slf4j.LoggerFactory;

public class DefaultErrorHandler implements ErrorHandler {
    @Override
    public void onError(Exception e) {
        LoggerFactory.getLogger(DefaultErrorHandler.class).error("storage connector error", e);
    }
}

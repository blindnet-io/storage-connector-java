package io.blindnet.storageconnectors.java.exceptions;

import java.io.IOException;

public class WebSocketException extends IOException {
    public WebSocketException(String message) {
        super(message);
    }

    public WebSocketException(Throwable cause) {
        super(cause);
    }
}

package io.blindnet.storageconnector.exceptions;

public class WebSocketException extends APIException {
    public WebSocketException(String message) {
        super(message);
    }

    public WebSocketException(Throwable cause) {
        super(cause);
    }
}

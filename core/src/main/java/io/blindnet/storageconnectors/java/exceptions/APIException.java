package io.blindnet.storageconnectors.java.exceptions;

import java.io.IOException;

public class APIException extends IOException {
    public APIException(String message) {
        super(message);
    }

    public APIException(Throwable cause) {
        super(cause);
    }
}

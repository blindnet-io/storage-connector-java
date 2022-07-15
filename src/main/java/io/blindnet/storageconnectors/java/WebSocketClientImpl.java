package io.blindnet.storageconnectors.java;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class WebSocketClientImpl extends WebSocketClient {
    public WebSocketClientImpl(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
    }

    @Override
    public void onMessage(String message) {
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
    }

    @Override
    public void onError(Exception e) {
        LoggerFactory.getLogger(WebSocketClientImpl.class).error("WebSocket error", e);
    }
}

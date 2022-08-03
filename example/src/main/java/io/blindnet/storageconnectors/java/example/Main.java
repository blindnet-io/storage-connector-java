package io.blindnet.storageconnectors.java.example;

import io.blindnet.storageconnectors.java.StorageConnector;
import org.slf4j.LoggerFactory;

public class Main {
    public static void main(String[] args) throws Exception {
        StorageConnector.create("ws://127.0.0.1:8028/v1/connectors/ws")
                .start();

        LoggerFactory.getLogger(Main.class).info("hey");
    }
}

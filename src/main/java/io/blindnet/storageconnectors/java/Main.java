package io.blindnet.storageconnectors.java;

import org.slf4j.LoggerFactory;

public class Main {
    public static void main(String[] args) throws Exception {
        StorageConnector.create("ws://127.0.0.1")
                .start();

        LoggerFactory.getLogger(Main.class).info("hey");
    }
}

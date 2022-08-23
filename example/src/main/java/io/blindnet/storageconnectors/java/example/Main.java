package io.blindnet.storageconnectors.java.example;

import io.blindnet.storageconnectors.java.StorageConnector;
import io.blindnet.storageconnectors.java.datarequests.reply.BinaryData;
import io.blindnet.storageconnectors.java.handlers.mapping.MappingRequestHandler;

public class Main {
    private static final User DEMO_USER = new User("John Doe");

    public static void main(String[] args) {
        StorageConnector.create()
                .setDataRequestHandler(new MappingRequestHandler.Builder<User>()
                        .setSubjectMapper(id -> DEMO_USER)
                        .addSelectorType("NAME", User::getName)
                        .addSelectorTypeBinary("IMAGE", u -> BinaryData.fromArray(u.getProfilePicture()))
                        .build())
                .start();
    }
}

package io.blindnet.storageconnectors.java.example;

import io.blindnet.storageconnectors.java.StorageConnector;
import io.blindnet.storageconnectors.java.datarequests.reply.BinaryData;
import io.blindnet.storageconnectors.java.handlers.mapping.MappingRequestHandler;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) throws IOException {
        Database.init();

        StorageConnector.create()
                .setDataRequestHandler(new MappingRequestHandler.Builder<User>()
                        .setSubjectMapper(Database.users::findByEmail)
                        .addSelectorType("CONTACT.EMAIL", User::email)
                        .addSelectorType("NAME", User::fullName)
                        .addSelectorTypeBinary("OTHER-DATA.PROOF", u -> BinaryData.fromArray(u.proof()))
                        .build())
                .start();
    }
}

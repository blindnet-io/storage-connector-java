package io.blindnet.storageconnector.example;

import io.blindnet.storageconnector.StorageConnector;
import io.blindnet.storageconnector.datarequests.reply.BinaryData;
import io.blindnet.storageconnector.handlers.mapping.MappingRequestHandler;

import java.io.IOException;

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

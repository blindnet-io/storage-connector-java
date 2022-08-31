package io.blindnet.storageconnector.example;

import io.blindnet.storageconnector.StorageConnector;
import io.blindnet.storageconnector.datarequests.reply.BinaryData;
import io.blindnet.storageconnector.handlers.mapping.MappingRequestHandler;
import io.javalin.Javalin;
import io.javalin.http.HttpCode;
import io.javalin.http.UploadedFile;
import io.javalin.plugin.json.JavalinJackson;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException {
        Database.init();

        StorageConnector.create("97c4cb50-0de9-41b6-9e22-957c8aed8e5e")
                .setDataRequestHandler(new MappingRequestHandler.Builder<User>()
                        .setSubjectMapper(Database.users::findByEmail)
                        .addSelectorType("CONTACT.EMAIL", User::email)
                        .addSelectorType("NAME", User::fullName)
                        .addSelectorTypeBinary("OTHER-DATA.PROOF", u -> BinaryData.fromArray(u.proof()))
                        .build())
                .start();

        Javalin app = Javalin.create(config -> {
            config.enableCorsForAllOrigins();
            config.jsonMapper(new JavalinJackson());
        }).start(8082);

        app.post("/form", ctx -> {
            String firstName = ctx.formParam("first");
            String lastName = ctx.formParam("last");
            String email = ctx.formParam("email");
            UploadedFile proof = ctx.uploadedFile("proof");
            if(firstName == null || lastName == null || email == null || proof == null) {
                ctx.status(HttpCode.BAD_REQUEST);
                return;
            }

            Database.users.insert(firstName, lastName, email, proof.getContent().readAllBytes());
            ctx.status(HttpCode.NO_CONTENT);
        });
    }
}

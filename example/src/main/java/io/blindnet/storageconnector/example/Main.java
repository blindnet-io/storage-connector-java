package io.blindnet.storageconnector.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import io.blindnet.jwt.TokenBuilder;
import io.blindnet.jwt.TokenPrivateKey;
import io.blindnet.storageconnector.StorageConnector;
import io.blindnet.storageconnector.datarequests.reply.BinaryData;
import io.blindnet.storageconnector.handlers.mapping.MappingRequestHandler;
import io.javalin.Javalin;
import io.javalin.core.JavalinConfig;
import io.javalin.http.ContentType;
import io.javalin.http.HttpCode;
import io.javalin.http.UploadedFile;
import io.javalin.plugin.json.JavalinJackson;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
    private static final String TOKEN = System.getenv("BN_CONNECTOR_TOKEN");
    private static final TokenBuilder tokenBuilder = new TokenBuilder(
            System.getenv("APP_UUID"), TokenPrivateKey.fromString(System.getenv("APP_KEY"))
    );

    public static void main(String[] args) throws IOException, URISyntaxException {
        Database.init();

        StorageConnector.create(TOKEN)
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
        });

        app.get("/auth/token", ctx -> {
            String email;
            try {
                email = Auth0Utils.verifyTokenFromHeader(ctx.header("Authorization"));
            } catch(IllegalArgumentException e) {
                e.printStackTrace();
                ctx.status(HttpCode.BAD_REQUEST).result(e.getMessage());
                return;
            }

            ctx.json(TextNode.valueOf(tokenBuilder.user(email)));
        });

        app.post("/auth/admin/token", ctx -> {
            AdminAuthPayload payload;
            try {
                payload = ctx.bodyAsClass(AdminAuthPayload.class);
            } catch(Exception e) {
                ctx.status(HttpCode.BAD_REQUEST);
                return;
            }

            // Obviously, don't do that, and don't use that kind of password. This is a demo.
            if(payload.username().equals("admin") && payload.password().equals("admin123")) {
                ctx.json(TextNode.valueOf(tokenBuilder.app()));
            } else {
                ctx.status(HttpCode.UNAUTHORIZED);
            }
        });

        app.post("/form", ctx -> {
            String firstName = ctx.formParam("first");
            String lastName = ctx.formParam("last");
            String email = ctx.formParam("email");
            UploadedFile proof = ctx.uploadedFile("proof");
            if(firstName == null || lastName == null || email == null) {
                ctx.status(HttpCode.BAD_REQUEST);
                return;
            }

            Database.users.upsert(email, firstName, lastName, proof != null ? proof.getContent().readAllBytes() : null);
            ctx.status(HttpCode.NO_CONTENT);
        });

        app.start(8082);
    }
}

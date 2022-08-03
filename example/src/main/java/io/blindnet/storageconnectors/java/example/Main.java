package io.blindnet.storageconnectors.java.example;

import io.blindnet.storageconnectors.java.StorageConnector;
import io.blindnet.storageconnectors.java.dataquery.DataQuery;
import io.blindnet.storageconnectors.java.dataquery.reply.DataQueryReply;
import io.blindnet.storageconnectors.java.handlers.DataQueryHandler;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws Exception {
        StorageConnector.create("ws://127.0.0.1:8028/v1/connectors/ws")
                .setDataQueryHandler(new MyQueryHandler())
                .start();

        LoggerFactory.getLogger(Main.class).info("hey");
    }

    static class MyQueryHandler implements DataQueryHandler {
        @Override
        public DataQueryReply get(DataQuery query) {
            // For the sake of simplicity, we'll only handle the first selector, and only allow for name and image.
            if(query.getSelectors().size() != 1) return query.deny();
            String selector = query.getSelectors().get(0);

            switch(selector) {
                case "NAME":
                    // That's easy, let's return the data right now.
                    return query.accept().withData("John Doe".getBytes());
                case "IMAGE":
                    // Could take some time, we need some async processing.
                    return query.accept().withDelayedData(future -> {
                        try (Response res = new OkHttpClient().newCall(new Request.Builder()
                                .url("https://picsum.photos/seed/picsum/500").build()).execute()) {
                            future.complete(Objects.requireNonNull(res.body()).bytes());
                        } catch (IOException ignored) {} // Errors ain't nothing
                    });
            }

            // Unhandled query
            return query.deny();
        }

        @Override
        public DataQueryReply delete(DataQuery query) {
            // We deny all deletion requests, for now
            return query.deny();
        }
    }
}

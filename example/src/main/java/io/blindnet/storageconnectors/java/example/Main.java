package io.blindnet.storageconnectors.java.example;

import io.blindnet.storageconnectors.java.StorageConnector;
import io.blindnet.storageconnectors.java.datarequests.DataRequest;
import io.blindnet.storageconnectors.java.datarequests.reply.DataRequestReply;
import io.blindnet.storageconnectors.java.handlers.DataRequestHandler;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        StorageConnector.create()
                .setDataRequestHandler(new MyRequestHandler())
                .start();
    }

    static class MyRequestHandler implements DataRequestHandler {
        @Override
        public DataRequestReply handle(DataRequest request) {
            // We deny all deletion requests, for now
            if(request.getAction() != DataRequest.Action.GET)
                return request.deny();

            // For the sake of simplicity, we'll only handle the first selector, and only allow for name and image.
            if(request.getQuery().getSelectors().size() != 1) return request.deny();
            String selector = request.getQuery().getSelectors().get(0);

            switch(selector) {
                case "NAME":
                    // That's easy, let's return the data right now.
                    return request.accept().withData("John Doe".getBytes());
                case "IMAGE":
                    // Could take some time, we need some async processing.
                    return request.accept().withDelayedData(cb -> {
                        try (Response res = new OkHttpClient().newCall(new Request.Builder()
                                .url("https://picsum.photos/seed/picsum/500").build()).execute()) {
                            cb.sendData(Objects.requireNonNull(res.body()).bytes());
                        } catch (IOException ignored) {} // Errors ain't nothing
                    });
            }

            // Unhandled request
            return request.deny();
        }
    }
}

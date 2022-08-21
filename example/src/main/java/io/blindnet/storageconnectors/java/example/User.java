package io.blindnet.storageconnectors.java.example;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class User {
    private final String name;

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public byte[] getProfilePicture() throws IOException {
        try (Response res = new OkHttpClient().newCall(new Request.Builder()
                .url("https://picsum.photos/seed/picsum/500").build()).execute()) {
            return Objects.requireNonNull(res.body()).bytes();
        }
    }
}

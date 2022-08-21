package io.blindnet.storageconnectors.java;

import io.blindnet.storageconnectors.java.exceptions.APIException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Objects;

public class DataAccessClient {
    private final StorageConnectorImpl connector;
    private final OkHttpClient client = new OkHttpClient();

    public DataAccessClient(StorageConnectorImpl connector) {
        this.connector = connector;
    }

    private Request.Builder mkRequest(String endpoint) throws APIException {
        try {
            return new Request.Builder()
                    .url(connector.getEndpoint().resolve(endpoint).toURL());
        } catch (MalformedURLException e) {
            throw new APIException(e);
        }
    }

    private <R> R run(Request request, Class<R> cl) throws APIException {
        try (Response response = client.newCall(request).execute()) {
            if(!response.isSuccessful())
                throw new APIException("Status code " + response.code());

            return connector.getJsonMapper().readValue(Objects.requireNonNull(response.body()).bytes(), cl);
        } catch (IOException e) {
            throw new APIException(e);
        }
    }

    public String uploadMainData(String requestId, byte[] data) throws APIException {
        return uploadData(requestId, data, "main");
    }

    public String uploadMainData(String requestId, InputStream data) throws APIException {
        return uploadData(requestId, data, "main");
    }

    public String uploadAdditionalData(String requestId, byte[] data) throws APIException {
        return uploadData(requestId, data, "additional");
    }

    public String uploadAdditionalData(String requestId, InputStream data) throws APIException {
        return uploadData(requestId, data, "additional");
    }

    private String uploadData(String requestId, byte[] data, String type) throws APIException {
        return run(mkRequest("/v1/connectors/data/" + requestId + "/" + type + "?last=true")
                .post(RequestBody.create(data, MediaType.get("application/octet-stream")))
                .build(), String.class);
    }

    private String uploadData(String requestId, InputStream data, String type) throws APIException {
        return run(mkRequest("/v1/connectors/data/" + requestId + "/" + type + "?last=true")
                .post(new RequestBody() {
                    @Override
                    public MediaType contentType() {
                        return MediaType.get("application/octet-stream");
                    }

                    @Override
                    public long contentLength() throws IOException {
                        return data.available() == 0 ? -1 : data.available();
                    }

                    @Override
                    public void writeTo(@NotNull BufferedSink sink) throws IOException {
                        try(Source source = Okio.source(data)) {
                            sink.writeAll(source);
                        }
                    }
                })
                .build(), String.class);
    }
}

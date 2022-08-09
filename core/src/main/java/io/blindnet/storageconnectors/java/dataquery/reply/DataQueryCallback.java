package io.blindnet.storageconnectors.java.dataquery.reply;

import java.io.InputStream;
import java.nio.ByteBuffer;

public interface DataQueryCallback {
    void sendData(byte[] data);

    void sendData(ByteBuffer data);

    void sendData(InputStream dataStream);
}

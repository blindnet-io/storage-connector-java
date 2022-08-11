package io.blindnet.storageconnectors.java.datarequests.reply;

import java.io.InputStream;
import java.nio.ByteBuffer;

public interface DataRequestCallback {
    void sendData(byte[] data);

    void sendData(ByteBuffer data);

    void sendData(InputStream dataStream);
}

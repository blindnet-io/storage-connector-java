package io.blindnet.storageconnector.datarequests.reply;

import java.io.InputStream;
import java.nio.ByteBuffer;

public class BinaryData {
    private final byte[] array;
    private final InputStream stream;

    public static BinaryData fromArray(byte[] array) {
        return new BinaryData(array, null);
    }

    public static BinaryData fromByteBuffer(ByteBuffer buf) {
        byte[] array = new byte[buf.remaining()];
        buf.get(array);
        return fromArray(array);
    }

    public static BinaryData fromStream(InputStream stream) {
        return new BinaryData(null, stream);
    }

    private BinaryData(byte[] array, InputStream stream) {
        this.array = array;
        this.stream = stream;
    }

    public boolean isArray() {
        return array != null;
    }

    public byte[] getArray() {
        return array;
    }

    public InputStream getStream() {
        return stream;
    }
}

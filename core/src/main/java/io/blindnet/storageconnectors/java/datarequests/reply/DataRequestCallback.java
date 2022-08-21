package io.blindnet.storageconnectors.java.datarequests.reply;

import io.blindnet.storageconnectors.java.exceptions.APIException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public interface DataRequestCallback {
    String sendData(BinaryData data) throws APIException;

    String sendAdditionalData(BinaryData data) throws APIException;
}

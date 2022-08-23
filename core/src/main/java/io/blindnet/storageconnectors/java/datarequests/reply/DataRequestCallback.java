package io.blindnet.storageconnectors.java.datarequests.reply;

import io.blindnet.storageconnectors.java.exceptions.APIException;

public interface DataRequestCallback {
    String sendData(BinaryData data) throws APIException;

    String sendAdditionalData(BinaryData data) throws APIException;
}

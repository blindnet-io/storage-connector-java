package io.blindnet.storageconnector.datarequests.reply;

import io.blindnet.storageconnector.exceptions.APIException;

public interface DataRequestCallback {
    void sendData(BinaryData data) throws APIException;

    String sendAdditionalData(BinaryData data) throws APIException;
}

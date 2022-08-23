package io.blindnet.storageconnector.datarequests.reply;

import io.blindnet.storageconnector.exceptions.APIException;

public interface DataRequestCallback {
    String sendData(BinaryData data) throws APIException;

    String sendAdditionalData(BinaryData data) throws APIException;
}

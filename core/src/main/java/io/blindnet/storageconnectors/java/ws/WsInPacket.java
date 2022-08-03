package io.blindnet.storageconnectors.java.ws;

import io.blindnet.storageconnectors.java.StorageConnectorImpl;
import io.blindnet.storageconnectors.java.logic.Logic;

public interface WsInPacket {
    Logic getLogic(StorageConnectorImpl connector);
}

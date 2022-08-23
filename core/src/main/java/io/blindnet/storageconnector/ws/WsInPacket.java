package io.blindnet.storageconnector.ws;

import io.blindnet.storageconnector.StorageConnectorImpl;
import io.blindnet.storageconnector.logic.Logic;

public interface WsInPacket {
    Logic getLogic(StorageConnectorImpl connector);
}

package io.blindnet.storageconnector.ws.packets;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.blindnet.storageconnector.StorageConnectorImpl;
import io.blindnet.storageconnector.datarequests.DataRequestImpl;
import io.blindnet.storageconnector.logic.DataQueryLogic;
import io.blindnet.storageconnector.logic.Logic;
import io.blindnet.storageconnector.ws.WsInPacket;
import org.slf4j.LoggerFactory;

public class InPacketWelcome implements WsInPacket {
    @JsonProperty("app_id")
    private String appId;
    @JsonProperty("namespace_id")
    private String namespaceId;
    @JsonProperty("namespace_name")
    private String namespaceName;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getNamespaceId() {
        return namespaceId;
    }

    public void setNamespaceId(String namespaceId) {
        this.namespaceId = namespaceId;
    }

    public String getNamespaceName() {
        return namespaceName;
    }

    public void setNamespaceName(String namespaceName) {
        this.namespaceName = namespaceName;
    }

    @Override
    public Logic getLogic(StorageConnectorImpl connector) {
        return new Logic(connector) {
            @Override
            public void run() throws Exception {
                LoggerFactory.getLogger(InPacketWelcome.class).info("Application: {}", getAppId());
                LoggerFactory.getLogger(InPacketWelcome.class).info("Namespace: {} - {}", getNamespaceId(), getNamespaceName());
            }
        };
    }
}

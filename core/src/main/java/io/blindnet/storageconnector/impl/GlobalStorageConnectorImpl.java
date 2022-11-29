package io.blindnet.storageconnector.impl;

import io.blindnet.storageconnector.GlobalStorageConnector;

import java.util.UUID;

public class GlobalStorageConnectorImpl implements GlobalStorageConnector {
    private final UUID applicationId;
    private final UUID connectorId;
    private final String connectorName;
    private final String connectorType;
    private final String connectorConfig;

    public GlobalStorageConnectorImpl(UUID applicationId, UUID connectorId, String connectorName, String connectorType, String connectorConfig) {
        this.applicationId = applicationId;
        this.connectorId = connectorId;
        this.connectorName = connectorName;
        this.connectorType = connectorType;
        this.connectorConfig = connectorConfig;
    }

    @Override
    public UUID getApplicationId() {
        return applicationId;
    }

    @Override
    public UUID getConnectorId() {
        return connectorId;
    }

    @Override
    public String getConnectorName() {
        return connectorName;
    }

    @Override
    public String getConnectorType() {
        return connectorType;
    }

    @Override
    public String getConnectorConfig() {
        return connectorConfig;
    }
}

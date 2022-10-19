module io.blindnet.storageconnector {
    requires annotations;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires Java.WebSocket;
    requires okhttp3;
    requires okio;
    requires slf4j.api;

    exports io.blindnet.storageconnector;
    exports io.blindnet.storageconnector.datarequests;
    exports io.blindnet.storageconnector.datarequests.query;
    exports io.blindnet.storageconnector.datarequests.reply;
    exports io.blindnet.storageconnector.exceptions;
    exports io.blindnet.storageconnector.handlers;
    exports io.blindnet.storageconnector.handlers.mapping;
    exports io.blindnet.storageconnector.util;
}

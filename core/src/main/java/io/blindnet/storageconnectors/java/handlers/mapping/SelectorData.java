package io.blindnet.storageconnectors.java.handlers.mapping;

import io.blindnet.storageconnectors.java.datarequests.reply.BinaryData;

public class SelectorData {
    private final String selector;

    private final Object object;
    private final BinaryData binary;

    public static SelectorData binary(String selector, BinaryData binary) {
        return new SelectorData(selector, null, binary);
    }

    public static SelectorData serializable(String selector, Object object) {
        return new SelectorData(selector, object, null);
    }

    private SelectorData(String selector, Object object, BinaryData binary) {
        this.selector = selector;
        this.object = object;
        this.binary = binary;
    }

    public String getSelector() {
        return selector;
    }

    public boolean isBinary() {
        return binary != null;
    }

    public Object getObject() {
        return object;
    }

    public BinaryData getBinary() {
        return binary;
    }
}

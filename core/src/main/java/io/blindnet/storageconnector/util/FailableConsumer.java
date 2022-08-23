package io.blindnet.storageconnector.util;

import java.util.function.Consumer;

@FunctionalInterface
public interface FailableConsumer<T> {
    void accept(T t) throws Exception;

    default Consumer<T> sneaky() {
        return t -> {
            try {
                accept(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    };
}

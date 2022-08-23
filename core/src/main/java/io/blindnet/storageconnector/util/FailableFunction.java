package io.blindnet.storageconnector.util;

import java.util.function.Function;

@FunctionalInterface
public interface FailableFunction<T, R> {
    R apply(T t) throws Exception;

    default Function<T, R> sneaky() {
        return t -> {
            try {
                return apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    };
}

package io.blindnet.storageconnector.handlers.mapping;

import io.blindnet.storageconnector.StorageConnector;
import io.blindnet.storageconnector.datarequests.DataRequest;
import io.blindnet.storageconnector.datarequests.query.DataQuery;
import io.blindnet.storageconnector.datarequests.reply.BinaryData;
import io.blindnet.storageconnector.exceptions.APIException;
import io.blindnet.storageconnector.util.FailableFunction;
import io.blindnet.storageconnector.datarequests.reply.DataRequestCallback;
import io.blindnet.storageconnector.datarequests.reply.DataRequestReply;
import io.blindnet.storageconnector.handlers.DataRequestHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementation of {@link DataRequestHandler} that provides semi-automated
 * responses by the use of functions mapping data request specific types to
 * application-defined types and logic.
 * @param <T> Data Subject type
 */
public abstract class MappingRequestHandler<T> implements DataRequestHandler {
    private final Map<String, Function<T, SelectorData>> selectorTypes;

    protected MappingRequestHandler(Map<String, Function<T, SelectorData>> selectorTypes) {
        this.selectorTypes = selectorTypes;
    }

    protected abstract T mapSubject(String id);

    protected abstract void deleteData(T subject, List<String> selectors);

    protected SelectorData mapSelector(T subject, String selector) {
        Function<T, SelectorData> mapper = selectorTypes.get(selector);
        if(mapper == null)
            return null;

        return mapper.apply(subject);
    }

    protected Object dataToJson(SelectorData data, DataRequestCallback callback) {
        if(data.isBinary()) {
            try {
                return callback.sendAdditionalData(data.getBinary());
            } catch (APIException e) {
                throw new RuntimeException(e);
            }
        } else {
            return data.getObject();
        }
    }

    @Override
    public DataRequestReply handle(DataRequest request, StorageConnector connector) throws Exception {
        DataQuery q = request.getQuery();

        List<T> subjects = q.getSubjects().stream().distinct().map(this::mapSubject)
                .filter(Objects::nonNull).collect(Collectors.toList());
        if (subjects.isEmpty())
            return request.deny();

        if (request.getAction() == DataRequest.Action.GET) {
            return request.accept().withDelayedData(callback -> {
                Map<String, List<SelectorData>> data = new HashMap<>();
                q.getSelectors().stream().distinct()
                        .flatMap(selector -> subjects.stream().map(subject -> mapSelector(subject, selector)))
                        .forEach(d -> data.computeIfAbsent(d.getSelector(), x -> new ArrayList<>()).add(d));

                Map<String, Object> output = new HashMap<>();
                for (Map.Entry<String, List<SelectorData>> e : data.entrySet()) {
                    if (e.getValue().size() == 1)
                        output.put(e.getKey(), dataToJson(e.getValue().get(0), callback));
                    else
                        output.put(e.getKey(), e.getValue().stream()
                                .map(v -> dataToJson(v, callback))
                                .collect(Collectors.toList()));
                }

                callback.sendData(BinaryData.fromArray(connector.getJsonMapper().writeValueAsBytes(output)));
            });
        } else {
            for (T subject : subjects) {
                deleteData(subject, q.getSelectors());
            }
            return request.accept().withoutData();
        }
    }

    public static class Builder<T> {
        private Function<String, T> subjectMapper;
        private BiConsumer<T, List<String>> deleter;
        private final Map<String, Function<T, SelectorData>> selectorTypes = new HashMap<>();

        public Builder<T> setSubjectMapper(Function<String, T> subjectMapper) {
            this.subjectMapper = subjectMapper;
            return this;
        }

        public Builder<T> setDeleter(BiConsumer<T, List<String>> deleter) {
            this.deleter = deleter;
            return this;
        }

        public Builder<T> addSelectorType(String type, FailableFunction<T, Object> mapper) {
            this.selectorTypes.put(type, mapper.sneaky().andThen(o -> SelectorData.serializable(type, o)));
            return this;
        }

        public Builder<T> addSelectorTypeBinary(String type, FailableFunction<T, BinaryData> mapper) {
            this.selectorTypes.put(type, mapper.sneaky().andThen(o -> SelectorData.binary(type, o)));
            return this;
        }

        public MappingRequestHandler<T> build() {
            Objects.requireNonNull(subjectMapper, "subjectMapper not set");

            return new MappingRequestHandler<>(selectorTypes) {
                @Override
                protected T mapSubject(String id) {
                    return subjectMapper.apply(id);
                }

                @Override
                protected void deleteData(T subject, List<String> selectors) {
                    deleter.accept(subject, selectors);
                }

                @Override
                public DataRequestReply handle(DataRequest request, StorageConnector connector) throws Exception {
                    if(request.getAction() == DataRequest.Action.DELETE && deleter == null)
                        return request.deny();

                    return super.handle(request, connector);
                }
            };
        }
    }
}

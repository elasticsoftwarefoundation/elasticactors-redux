package org.elasticsoftware.elasticactors.redux.api.actor;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.elasticsoftware.elasticactors.redux.api.context.MessageHandlingContext;
import org.springframework.lang.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@Slf4j
@AllArgsConstructor
public final class Receive<S> {

    public static <S> Builder<S> builder() {
        return new Builder<>();
    }

    private final Map<Class<?>, ConsumerDefinition<S, ?>> onReceiveConsumers;
    private final ConsumerDefinition<S, Object> orElseConsumer;

    @Nullable
    private final MessageBiConsumer<S, Object> preReceiveConsumer;
    @Nullable
    private final MessageBiConsumer<S, Object> postReceiveConsumer;

    public enum Type {
        READ_ONLY,
        READ_WRITE
    }

    @Value
    private static class ConsumerDefinition<S, M> {
        Type type;
        MessageBiConsumer<S, M> consumer;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Type onReceive(MessageHandlingContext<S> messageHandlingContext, Object message) throws Exception {
        Type consumerType = Type.READ_ONLY;
        if (preReceiveConsumer != null) {
            preReceiveConsumer.accept(messageHandlingContext, message);
        }
        boolean consumed = false;
        Class<?> messageClass = message.getClass();
        ConsumerDefinition consumerDefinition = onReceiveConsumers.get(messageClass);
        if (consumerDefinition != null) {
            consumerDefinition.getConsumer().accept(messageHandlingContext, message);
            consumerType = consumerDefinition.getType();
            consumed = true;
        }
        if (!consumed) {
            Class<?> closestClass = null;
            for (Class<?> c : onReceiveConsumers.keySet()) {
                if (c.isAssignableFrom(messageClass)) {
                    if (closestClass == null || closestClass.isAssignableFrom(c)) {
                        closestClass = c;
                    } else if (!c.isAssignableFrom(closestClass)) {
                        log.error(
                                "Ambiguous handlers for class '{}': it extends both '{}' and "
                                        + "'{}', but they're not part of the same chain of "
                                        + "inheritance. Delegating this to orElse.",
                                messageClass.getName(),
                                closestClass.getName(),
                                c.getName());
                        closestClass = null;
                        break;
                    }
                }
            }
            if (closestClass != null) {
                consumerDefinition = onReceiveConsumers.get(closestClass);
                consumerDefinition.getConsumer().accept(messageHandlingContext, message);
                consumerType = consumerDefinition.getType();
                consumed = true;
            }
        }
        if (!consumed) {
            orElseConsumer.getConsumer().accept(messageHandlingContext, message);
            consumerType = orElseConsumer.getType();
        }
        if (postReceiveConsumer != null) {
            postReceiveConsumer.accept(messageHandlingContext, message);
        }
        return consumerType;
    }

    public interface BuildStep<S> {

        Receive<S> build();
    }

    public interface PreReceiveStep<S> extends MessageHandlingStep<S> {

        MessageHandlingStep<S> preReceive(MessageBiConsumer<S, Object> consumer);

        MessageHandlingStep<S> preReceive(MessageConsumer<Object> consumer);

        MessageHandlingStep<S> preReceive(MessageRunnable runnable);
    }

    public interface MessageHandlingStep<S> extends PostReceiveStep<S> {

        <M> MessageHandlingStep<S> onReceive(
                Class<M> tClass,
                Type type,
                MessageBiConsumer<S, M> consumer);

        <M> MessageHandlingStep<S> onReceive(Class<M> tClass, MessageBiConsumer<S, M> consumer);

        <M> MessageHandlingStep<S> onReceive(Class<M> tClass, MessageConsumer<M> consumer);

        <M> MessageHandlingStep<S> onReceive(Class<M> tClass, MessageRunnable runnable);

        PostReceiveStep<S> orElse(Type type, MessageBiConsumer<S, Object> consumer);

        PostReceiveStep<S> orElse(MessageBiConsumer<S, Object> consumer);

        PostReceiveStep<S> orElse(MessageConsumer<Object> consumer);

        PostReceiveStep<S> orElse(MessageRunnable runnable);
    }

    public interface PostReceiveStep<S> extends BuildStep<S> {

        BuildStep<S> postReceive(MessageBiConsumer<S, Object> consumer);

        BuildStep<S> postReceive(MessageConsumer<Object> consumer);

        BuildStep<S> postReceive(MessageRunnable runnable);
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public final static class Builder<S> implements PreReceiveStep<S> {

        private final Map<Class<?>, ConsumerDefinition<S, ?>> onReceiveConsumers =
                new LinkedHashMap<>();

        private final static ConsumerDefinition<?, Object> DEFAULT_OR_ELSE =
                new ConsumerDefinition<>(
                        Type.READ_ONLY,
                        (c, m) -> log.warn(
                                "Actor '{}' received an unhandled message of type '{}'",
                                c.getSelf().getSpec(),
                                m.getClass().getName()));


        @SuppressWarnings("unchecked")
        private ConsumerDefinition<S, Object> orElseConsumer =
                (ConsumerDefinition<S, Object>) DEFAULT_OR_ELSE;

        @Nullable
        private MessageBiConsumer<S, Object> preReceiveConsumer;
        @Nullable
        private MessageBiConsumer<S, Object> postReceiveConsumer;

        private boolean orElseSet = false;
        private boolean built = false;

        @Override
        public Receive<S> build() {
            if (this.built) {
                throw new IllegalStateException("Can only call the build method once");
            }
            this.built = true;
            return new Receive<>(
                    onReceiveConsumers,
                    orElseConsumer,
                    preReceiveConsumer,
                    postReceiveConsumer);
        }

        private void validateState() {
            if (this.built) {
                throw new IllegalStateException("Cannot modify a builder after calling build()");
            }
        }

        @Override
        public MessageHandlingStep<S> preReceive(MessageBiConsumer<S, Object> consumer) {
            validateState();
            requireNonNull(consumer);
            if (this.preReceiveConsumer != null) {
                throw new IllegalStateException("preReceive is already set");
            }
            this.preReceiveConsumer = consumer;
            return this;
        }

        @Override
        public MessageHandlingStep<S> preReceive(MessageConsumer<Object> consumer) {
            requireNonNull(consumer);
            return this.preReceive((c, m) -> consumer.accept(m));
        }

        @Override
        public MessageHandlingStep<S> preReceive(MessageRunnable runnable) {
            requireNonNull(runnable);
            return preReceive((a, m) -> runnable.run());
        }

        @Override
        public <M> MessageHandlingStep<S> onReceive(
                Class<M> tClass,
                Type type,
                MessageBiConsumer<S, M> consumer) {
            return addConsumer(tClass, type, consumer);
        }

        @Override
        public <M> MessageHandlingStep<S> onReceive(
                Class<M> tClass,
                MessageBiConsumer<S, M> consumer) {
            return addConsumer(tClass, Type.READ_WRITE, consumer);
        }

        private <M> Builder<S> addConsumer(
                Class<M> tClass,
                Type type,
                MessageBiConsumer<S, M> consumer) {
            validateState();
            requireNonNull(tClass);
            requireNonNull(type);
            requireNonNull(consumer);
            if (onReceiveConsumers.containsKey(tClass)) {
                throw new IllegalStateException(String.format(
                        "onReceive for class '%s' already set",
                        tClass.getName()));
            }
            onReceiveConsumers.put(tClass, new ConsumerDefinition<>(type, consumer));
            return this;
        }

        @Override
        public <M> MessageHandlingStep<S> onReceive(Class<M> tClass, MessageConsumer<M> consumer) {
            requireNonNull(consumer);
            return onReceive(tClass, Type.READ_ONLY, (a, m) -> consumer.accept(m));
        }

        @Override
        public <M> MessageHandlingStep<S> onReceive(Class<M> tClass, MessageRunnable runnable) {
            requireNonNull(runnable);
            return onReceive(tClass, Type.READ_ONLY, (a, m) -> runnable.run());
        }

        @Override
        public PostReceiveStep<S> orElse(
                Type type,
                MessageBiConsumer<S, Object> consumer) {
            return addOrElse(type, consumer);
        }

        @Override
        public PostReceiveStep<S> orElse(MessageBiConsumer<S, Object> consumer) {
            return addOrElse(Type.READ_WRITE, consumer);
        }

        private Builder<S> addOrElse(Type type, MessageBiConsumer<S, Object> consumer) {
            validateState();
            requireNonNull(type);
            requireNonNull(consumer);
            if (this.orElseSet) {
                throw new IllegalStateException("orElse is already set");
            }
            this.orElseSet = true;
            this.orElseConsumer = new ConsumerDefinition<>(type, consumer);
            return this;
        }

        @Override
        public PostReceiveStep<S> orElse(MessageConsumer<Object> consumer) {
            requireNonNull(consumer);
            return orElse(Type.READ_ONLY, (a, m) -> consumer.accept(m));
        }

        @Override
        public PostReceiveStep<S> orElse(MessageRunnable runnable) {
            requireNonNull(runnable);
            return orElse(Type.READ_ONLY, (a, m) -> runnable.run());
        }

        @Override
        public BuildStep<S> postReceive(MessageBiConsumer<S, Object> consumer) {
            validateState();
            requireNonNull(consumer);
            if (this.postReceiveConsumer != null) {
                throw new IllegalStateException("postReceive is already set");
            }
            this.postReceiveConsumer = consumer;
            return this;
        }

        @Override
        public BuildStep<S> postReceive(MessageConsumer<Object> consumer) {
            requireNonNull(consumer);
            return postReceive((a, m) -> consumer.accept(m));
        }

        @Override
        public BuildStep<S> postReceive(MessageRunnable runnable) {
            requireNonNull(runnable);
            return preReceive((a, m) -> runnable.run());
        }
    }

    @FunctionalInterface
    public interface MessageBiConsumer<S, M> {

        static <M, S> MessageBiConsumer<S, M> noop() {
            return (c, m) -> {
            };
        }

        void accept(MessageHandlingContext<S> messageHandlingContext, M message) throws Exception;

        default MessageBiConsumer<S, M> andThen(MessageBiConsumer<S, M> after) {
            requireNonNull(after);
            return (c, m) -> {
                accept(c, m);
                after.accept(c, m);
            };
        }
    }

    @FunctionalInterface
    public interface MessageConsumer<M> {

        void accept(M message) throws Exception;

        default MessageConsumer<M> andThen(MessageConsumer<M> after) {
            requireNonNull(after);
            return m -> {
                accept(m);
                after.accept(m);
            };
        }
    }

    @FunctionalInterface
    public interface MessageRunnable {

        void run() throws Exception;

        default MessageRunnable andThen(MessageRunnable after) {
            requireNonNull(after);
            return () -> {
                run();
                after.run();
            };
        }
    }

}

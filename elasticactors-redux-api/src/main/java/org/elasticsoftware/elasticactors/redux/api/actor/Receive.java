package org.elasticsoftware.elasticactors.redux.api.actor;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.elasticsoftware.elasticactors.redux.api.context.MessageHandlingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.requireNonNull;

@Slf4j
@AllArgsConstructor
public final class Receive<S> {

    public static <S, A extends ElasticActor<S>> Builder<S> builder(Class<A> actorClass) {
        return new Builder<>(LoggerFactory.getLogger(actorClass));
    }

    public static <S> Builder<S> builder(Logger log) {
        return new Builder<>(log);
    }

    private final ConcurrentMap<Class<?>, ConsumerDefinition<S, ?>> onReceiveConsumers;
    private final ConsumerDefinition<S, Object> orElseConsumer;
    private final ConsumerDefinition<S, Object> onUndeliverableConsumer;

    @Nullable
    private final MessageBiConsumer<S, Object> preReceiveConsumer;
    @Nullable
    private final MessageBiConsumer<S, Object> postReceiveConsumer;

    public enum Type {
        READ,
        WRITE
    }

    @Value
    private static class ConsumerDefinition<S, M> {
        Type type;
        MessageBiConsumer<S, M> consumer;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Type onReceive(MessageHandlingContext<S> messageHandlingContext, Object message)
            throws Exception {
        if (preReceiveConsumer != null) {
            preReceiveConsumer.accept(messageHandlingContext, message);
        }
        Type consumerType = Type.READ;
        boolean consumed = false;
        Class<?> messageClass = message.getClass();
        ConsumerDefinition consumerDefinition = onReceiveConsumers.get(messageClass);
        if (consumerDefinition != null) {
            consumerDefinition.getConsumer().accept(messageHandlingContext, message);
            consumerType = consumerDefinition.getType();
            consumed = true;
        }
        if (!consumed) {
            consumerDefinition = resolveClosestConsumerDefinition(messageClass);
            if (consumerDefinition != null) {
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

    public Type onUndeliverable(MessageHandlingContext<S> messageHandlingContext, Object message)
            throws Exception {
        onUndeliverableConsumer.getConsumer().accept(messageHandlingContext, message);
        return onUndeliverableConsumer.getType();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Nullable
    private ConsumerDefinition resolveClosestConsumerDefinition(Class<?> messageClass) {
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
            ConsumerDefinition closestConsumer = onReceiveConsumers.get(closestClass);
            // Store the resolved consumer on the map so we don't have to try resolving it again
            onReceiveConsumers.put(messageClass, closestConsumer);
            return closestConsumer;
        }
        return null;
    }

    public interface BuildStep<S> {

        Receive<S> build();
    }

    public interface PreReceiveStep<S> extends MessageHandlingStep<S> {

        /**
         * Sets a step to be executed before message handling happen.
         * <br>
         * <strong>The type is always {@link Type#READ}.</strong>
         * <br>
         * Modifying the state in this step should be avoided at all cost.
         * @param consumer
         * @return
         */
        MessageHandlingStep<S> preReceive(MessageBiConsumer<S, Object> consumer);

        MessageHandlingStep<S> preReceive(MessageConsumer<Object> consumer);

        MessageHandlingStep<S> preReceive(MessageRunnable runnable);
    }

    public interface MessageHandlingStep<S> extends PostReceiveStep<S> {

        <M> MessageHandlingStep<S> onMessage(
                Class<M> tClass,
                Type type,
                MessageBiConsumer<S, M> consumer);

        <M> MessageHandlingStep<S> onMessage(Class<M> tClass, MessageBiConsumer<S, M> consumer);

        <M> MessageHandlingStep<S> onMessage(Class<M> tClass, MessageConsumer<M> consumer);

        <M> MessageHandlingStep<S> onMessage(Class<M> tClass, MessageRunnable runnable);

        PostReceiveStep<S> orElse(Type type, MessageBiConsumer<S, Object> consumer);

        PostReceiveStep<S> orElse(MessageBiConsumer<S, Object> consumer);

        PostReceiveStep<S> orElse(MessageConsumer<Object> consumer);

        PostReceiveStep<S> orElse(MessageRunnable runnable);
    }

    public interface PostReceiveStep<S> extends OnUndeliverableStep<S> {

        /**
         * Sets a step to be executed after message handling has happened.
         * <br>
         * <strong>The type is always {@link Type#READ}.</strong>
         * <br>
         * Modifying the state in this step should be avoided at all cost.
         * @param consumer
         * @return
         */
        OnUndeliverableStep<S> postReceive(MessageBiConsumer<S, Object> consumer);

        OnUndeliverableStep<S> postReceive(MessageConsumer<Object> consumer);

        OnUndeliverableStep<S> postReceive(MessageRunnable runnable);
    }

    public interface OnUndeliverableStep<S> extends BuildStep<S> {

        BuildStep<S> onUndeliverable(Type type, MessageBiConsumer<S, Object> consumer);

        BuildStep<S> onUndeliverable(MessageBiConsumer<S, Object> consumer);

        BuildStep<S> onUndeliverable(MessageConsumer<Object> consumer);

        BuildStep<S> onUndeliverable(MessageRunnable runnable);
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public final static class Builder<S> implements PreReceiveStep<S> {

        private final Logger log;

        private final ConsumerDefinition<?, Object> DEFAULT_OR_ELSE =
                new ConsumerDefinition<>(
                        Type.READ,
                        (c, m) -> log.warn(
                                "Actor '{}' received an unhandled message of type '{}'",
                                c.getSelf().getSpec(),
                                m.getClass().getName()));

        private final ConsumerDefinition<?, Object> DEFAULT_ON_UNDELIVERABLE =
                new ConsumerDefinition<>(
                        Type.READ,
                        (c, m) -> log.warn(
                                "Could not deliver message of type '{}' from '{}' to '{}'",
                                m.getClass().getName(),
                                c.getSelf().getSpec(),
                                c.getSender() != null ? c.getSender().getSpec() : null));

        private final ConcurrentMap<Class<?>, ConsumerDefinition<S, ?>> onReceiveConsumers =
                new ConcurrentHashMap<>();
        private final AtomicReference<ConsumerDefinition<S, Object>> orElseConsumer =
                new AtomicReference<>(defaultOrElse());
        private final AtomicReference<ConsumerDefinition<S, Object>> onUndeliverableConsumer =
                new AtomicReference<>(defaultOnUndeliverable());
        private final AtomicReference<MessageBiConsumer<S, Object>> preReceiveConsumer =
                new AtomicReference<>();
        private final AtomicReference<MessageBiConsumer<S, Object>> postReceiveConsumer =
                new AtomicReference<>();

        private final AtomicBoolean built = new AtomicBoolean(false);

        @SuppressWarnings("unchecked")
        private ConsumerDefinition<S, Object> defaultOrElse() {
            return (ConsumerDefinition<S, Object>) DEFAULT_OR_ELSE;
        }

        @SuppressWarnings("unchecked")
        private ConsumerDefinition<S, Object> defaultOnUndeliverable() {
            return (ConsumerDefinition<S, Object>) DEFAULT_ON_UNDELIVERABLE;
        }

        @Override
        public Receive<S> build() {
            if (this.built.getAndSet(true)) {
                throw new IllegalStateException("Can only call the build method once");
            }
            return new Receive<>(
                    onReceiveConsumers,
                    orElseConsumer.get(),
                    onUndeliverableConsumer.get(),
                    preReceiveConsumer.get(),
                    postReceiveConsumer.get());
        }

        private void validateState() {
            if (this.built.get()) {
                throw new IllegalStateException("Cannot modify a builder after calling build()");
            }
        }

        @Override
        public MessageHandlingStep<S> preReceive(MessageBiConsumer<S, Object> consumer) {
            validateState();
            requireNonNull(consumer);
            if (!this.preReceiveConsumer.compareAndSet(null, consumer)) {
                throw new IllegalStateException("preReceive is already set");
            }
            return this;
        }

        @Override
        public MessageHandlingStep<S> preReceive(MessageConsumer<Object> consumer) {
            requireNonNull(consumer);
            return preReceive((c, m) -> consumer.accept(m));
        }

        @Override
        public MessageHandlingStep<S> preReceive(MessageRunnable runnable) {
            requireNonNull(runnable);
            return preReceive((a, m) -> runnable.run());
        }

        @Override
        public <M> MessageHandlingStep<S> onMessage(
                Class<M> tClass,
                Type type,
                MessageBiConsumer<S, M> consumer) {
            validateState();
            requireNonNull(tClass);
            requireNonNull(type);
            requireNonNull(consumer);
            onReceiveConsumers.compute(tClass, (key, old) -> {
                if (old != null) {
                    throw new IllegalStateException(String.format(
                            "onReceive for class '%s' already set",
                            key.getName()));
                }
                return new ConsumerDefinition<>(type, consumer);
            });
            return this;
        }

        @Override
        public <M> MessageHandlingStep<S> onMessage(
                Class<M> tClass,
                MessageBiConsumer<S, M> consumer) {
            return onMessage(tClass, Type.WRITE, consumer);
        }

        @Override
        public <M> MessageHandlingStep<S> onMessage(Class<M> tClass, MessageConsumer<M> consumer) {
            requireNonNull(consumer);
            return onMessage(tClass, Type.READ, (a, m) -> consumer.accept(m));
        }

        @Override
        public <M> MessageHandlingStep<S> onMessage(Class<M> tClass, MessageRunnable runnable) {
            requireNonNull(runnable);
            return onMessage(tClass, Type.READ, (a, m) -> runnable.run());
        }

        @Override
        public PostReceiveStep<S> orElse(
                Type type,
                MessageBiConsumer<S, Object> consumer) {
            validateState();
            requireNonNull(type);
            requireNonNull(consumer);
            if (!this.orElseConsumer.compareAndSet(
                    defaultOrElse(),
                    new ConsumerDefinition<>(type, consumer))) {
                throw new IllegalStateException("orElse is already set");
            }
            return this;
        }

        @Override
        public PostReceiveStep<S> orElse(MessageBiConsumer<S, Object> consumer) {
            return orElse(Type.WRITE, consumer);
        }

        @Override
        public PostReceiveStep<S> orElse(MessageConsumer<Object> consumer) {
            requireNonNull(consumer);
            return orElse(Type.READ, (a, m) -> consumer.accept(m));
        }

        @Override
        public PostReceiveStep<S> orElse(MessageRunnable runnable) {
            requireNonNull(runnable);
            return orElse(Type.READ, (a, m) -> runnable.run());
        }

        @Override
        public OnUndeliverableStep<S> postReceive(MessageBiConsumer<S, Object> consumer) {
            validateState();
            requireNonNull(consumer);
            if (!this.postReceiveConsumer.compareAndSet(null, consumer)) {
                throw new IllegalStateException("postReceive is already set");
            }
            return this;
        }

        @Override
        public OnUndeliverableStep<S> postReceive(MessageConsumer<Object> consumer) {
            requireNonNull(consumer);
            return postReceive((a, m) -> consumer.accept(m));
        }

        @Override
        public OnUndeliverableStep<S> postReceive(MessageRunnable runnable) {
            requireNonNull(runnable);
            return postReceive((a, m) -> runnable.run());
        }

        @Override
        public Builder<S> onUndeliverable(Type type, MessageBiConsumer<S, Object> consumer) {
            validateState();
            requireNonNull(type);
            requireNonNull(consumer);
            if (!this.onUndeliverableConsumer.compareAndSet(
                    defaultOnUndeliverable(),
                    new ConsumerDefinition<>(type, consumer))) {
                throw new IllegalStateException("onUndeliverable is already set");
            }
            return this;
        }

        @Override
        public BuildStep<S> onUndeliverable(MessageBiConsumer<S, Object> consumer) {
            return onUndeliverable(Type.WRITE, consumer);
        }

        @Override
        public BuildStep<S> onUndeliverable(MessageConsumer<Object> consumer) {
            requireNonNull(consumer);
            return onUndeliverable(Type.READ, (a, m) -> consumer.accept(m));
        }

        @Override
        public BuildStep<S> onUndeliverable(MessageRunnable runnable) {
            requireNonNull(runnable);
            return onUndeliverable(Type.READ, (a, m) -> runnable.run());
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

        default MessageBiConsumer<S, M> andThen(MessageConsumer<M> after) {
            requireNonNull(after);
            return (c, m) -> {
                accept(c, m);
                after.accept(m);
            };
        }

        default MessageBiConsumer<S, M> andThen(MessageRunnable after) {
            requireNonNull(after);
            return (c, m) -> {
                accept(c, m);
                after.run();
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

        default MessageConsumer<M>  andThen(MessageRunnable after) {
            requireNonNull(after);
            return m -> {
                accept(m);
                after.run();
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

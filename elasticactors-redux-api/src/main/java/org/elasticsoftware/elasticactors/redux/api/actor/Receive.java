package org.elasticsoftware.elasticactors.redux.api.actor;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.elasticsoftware.elasticactors.redux.api.context.ActorContext;
import org.springframework.lang.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@AllArgsConstructor
public final class Receive<S> {

    public static <S> Builder<S> builder() {
        return new Builder<>();
    }

    private final Map<Class<?>, MessageBiConsumer<S, ?>> onReceiveConsumers;
    @Nullable
    private final MessageBiConsumer<S, Object> orElseConsumer;
    @Nullable
    private final MessageBiConsumer<S, Object> preReceiveConsumer;
    @Nullable
    private final MessageBiConsumer<S, Object> postReceiveConsumer;

    public void onReceive(ActorContext<S> actorContext, Object message) {

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

        <M> MessageHandlingStep<S> onReceive(Class<M> tClass, MessageBiConsumer<S, M> consumer);

        <M> MessageHandlingStep<S> onReceive(Class<M> tClass, MessageConsumer<M> consumer);

        <M> MessageHandlingStep<S> onReceive(Class<M> tClass, MessageRunnable runnable);

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

        private final Map<Class<?>, MessageBiConsumer<S, ?>> onReceiveConsumers =
                new LinkedHashMap<>();
        @Nullable
        private MessageBiConsumer<S, Object> orElseConsumer;
        @Nullable
        private MessageBiConsumer<S, Object> preReceiveConsumer;
        @Nullable
        private MessageBiConsumer<S, Object> postReceiveConsumer;
        private boolean built = false;

        @Override
        public Receive<S> build() {
            if (this.built) {
                throw new IllegalStateException("Can only call the build method once");
            }
            this.built = true;
            return new Receive<>(
                    Collections.unmodifiableMap(new LinkedHashMap<>(onReceiveConsumers)),
                    orElseConsumer,
                    preReceiveConsumer,
                    postReceiveConsumer);
        }

        @Override
        public MessageHandlingStep<S> preReceive(MessageBiConsumer<S, Object> consumer) {
            Objects.requireNonNull(consumer);
            if (this.preReceiveConsumer != null) {
                throw new IllegalStateException("preReceive is already set");
            }
            this.preReceiveConsumer = consumer;
            return this;
        }

        @Override
        public MessageHandlingStep<S> preReceive(MessageConsumer<Object> consumer) {
            Objects.requireNonNull(consumer);
            return this.preReceive((c, m) -> consumer.accept(m));
        }

        @Override
        public MessageHandlingStep<S> preReceive(MessageRunnable runnable) {
            Objects.requireNonNull(runnable);
            return preReceive((a, m) -> runnable.run());
        }

        @Override
        public <M> MessageHandlingStep<S> onReceive(
                Class<M> tClass,
                MessageBiConsumer<S, M> consumer) {
            Objects.requireNonNull(tClass);
            Objects.requireNonNull(consumer);
            if (onReceiveConsumers.containsKey(tClass)) {
                throw new IllegalStateException(String.format(
                        "onReceive for class '%s' already set",
                        tClass.getName()));
            }
            onReceiveConsumers.put(tClass, consumer);
            return this;
        }

        @Override
        public <M> MessageHandlingStep<S> onReceive(Class<M> tClass, MessageConsumer<M> consumer) {
            Objects.requireNonNull(tClass);
            Objects.requireNonNull(consumer);
            return onReceive(tClass, (a, m) -> consumer.accept(m));
        }

        @Override
        public <M> MessageHandlingStep<S> onReceive(Class<M> tClass, MessageRunnable runnable) {
            Objects.requireNonNull(tClass);
            Objects.requireNonNull(runnable);
            return onReceive(tClass, (a, m) -> runnable.run());
        }

        @Override
        public PostReceiveStep<S> orElse(MessageBiConsumer<S, Object> consumer) {
            Objects.requireNonNull(consumer);
            if (this.orElseConsumer != null) {
                throw new IllegalStateException("orElse is already set");
            }
            this.orElseConsumer = consumer;
            return this;
        }

        @Override
        public PostReceiveStep<S> orElse(MessageConsumer<Object> consumer) {
            Objects.requireNonNull(consumer);
            return orElse((a, m) -> consumer.accept(m));
        }

        @Override
        public PostReceiveStep<S> orElse(MessageRunnable runnable) {
            Objects.requireNonNull(runnable);
            return orElse((a, m) -> runnable.run());
        }

        @Override
        public BuildStep<S> postReceive(MessageBiConsumer<S, Object> consumer) {
            Objects.requireNonNull(consumer);
            if (this.postReceiveConsumer != null) {
                throw new IllegalStateException("postReceive is already set");
            }
            this.postReceiveConsumer = consumer;
            return this;
        }

        @Override
        public BuildStep<S> postReceive(MessageConsumer<Object> consumer) {
            Objects.requireNonNull(consumer);
            return postReceive((a, m) -> consumer.accept(m));
        }

        @Override
        public BuildStep<S> postReceive(MessageRunnable runnable) {
            Objects.requireNonNull(runnable);
            return preReceive((a, m) -> runnable.run());
        }
    }

    @FunctionalInterface
    public interface MessageBiConsumer<S, M> {

        static <M, S> MessageBiConsumer<S, M> noop() {
            return (c, m) -> {
            };
        }

        void accept(ActorContext<S> actorContext, M message) throws Exception;

        default MessageBiConsumer<S, M> andThen(MessageBiConsumer<S, M> after) {
            Objects.requireNonNull(after);
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
            Objects.requireNonNull(after);
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
            Objects.requireNonNull(after);
            return () -> {
                run();
                after.run();
            };
        }
    }

}

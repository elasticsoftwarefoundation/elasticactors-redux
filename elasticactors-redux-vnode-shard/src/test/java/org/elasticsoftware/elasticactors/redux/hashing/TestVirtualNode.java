package org.elasticsoftware.elasticactors.redux.hashing;

import lombok.Getter;
import org.elasticsoftware.elasticactors.redux.api.actor.ActorRef;
import org.elasticsoftware.elasticactors.redux.vnode.VirtualNode;
import org.elasticsoftware.elasticactors.redux.vnode.VirtualNodeKey;

import java.util.concurrent.atomic.AtomicInteger;

public final class TestVirtualNode implements VirtualNode {

    public final static AtomicInteger COUNTER = new AtomicInteger();

    @Getter
    private final VirtualNodeKey key;

    public TestVirtualNode(int index) {
        this.key = new VirtualNodeKey("test", "test-node-" + index);
    }

    public TestVirtualNode() {
        this(COUNTER.getAndIncrement());
    }

    @Override
    public String getSpec() {
        return key.getSpec();
    }

    @Override
    public void send(ActorRef sender, ActorRef receiver, Object message) {

    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public String toString() {
        return key.toString();
    }
}

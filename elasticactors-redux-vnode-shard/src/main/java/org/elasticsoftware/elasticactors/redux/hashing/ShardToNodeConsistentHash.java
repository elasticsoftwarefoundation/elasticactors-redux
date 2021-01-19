package org.elasticsoftware.elasticactors.redux.hashing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.MurmurHash3;
import org.elasticsoftware.elasticactors.redux.shard.ShardKey;
import org.elasticsoftware.elasticactors.redux.vnode.VirtualNode;
import org.elasticsoftware.elasticactors.redux.vnode.VirtualNodeKey;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
public final class ShardToNodeConsistentHash<N extends VirtualNode> {

    private final static int REPLICATION_FACTOR = 512;

    private final SortedMap<VirtualNodeKey, N> nodes = new TreeMap<>();
    private final SortedMap<VirtualNodeKey, Integer> loads = new TreeMap<>();
    private final SortedMap<ShardKey, N> shards = new TreeMap<>();
    private final SortedMap<Long, N> ring = new TreeMap<>();

    private final List<ShardKey> shardKeys;

    public ShardToNodeConsistentHash(Collection<ShardKey> shardKeys) {
        this.shardKeys = Collections.unmodifiableList(new ArrayList<>(shardKeys));
    }

    public ShardToNodeConsistentHash(String actorSystemName, int numShards) {
        this.shardKeys = IntStream.range(0, numShards)
                .mapToObj(i -> new ShardKey(actorSystemName, i))
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        Collections::unmodifiableList));
    }

    private static long getHash(byte[] bytes) {
        return MurmurHash3.hash128x64(bytes, 0, bytes.length, MurmurHash3.DEFAULT_SEED)[0];
    }

    private static byte[] getBytes(VirtualNodeKey key, int i) {
        return (key.getNodeId() + "-R#" + i).getBytes(StandardCharsets.UTF_8);
    }

    public synchronized void setNodes(Collection<? extends N> nodes) {
        this.nodes.clear();
        this.loads.clear();
        this.shards.clear();
        this.ring.clear();
        add(nodes);
    }

    public synchronized void add(N node) {
        add(Collections.singletonList(node));
    }

    public synchronized void add(Collection<? extends N> nodes) {
        if (nodes.isEmpty()) {
            return;
        }
        for (N node : nodes) {
            if (this.nodes.containsKey(node.getKey())) {
                // We already have this node. Quit immediately.
                continue;
            }
            addInternal(node);
        }
        ditributeShards();
    }

    public synchronized void remove(VirtualNodeKey key) {
        remove(Collections.singletonList(key));
    }

    public synchronized void remove(Collection<VirtualNodeKey> keys) {
        if (keys.isEmpty()) {
            return;
        }
        for (VirtualNodeKey key : keys) {
            if (!nodes.containsKey(key)) {
                // There is no node with that key. Quit immediately.
                return;
            }
            for (int i = 0; i < REPLICATION_FACTOR; i++) {
                byte[] bytes = getBytes(key, i);
                long hash = getHash(bytes);
                ring.remove(hash);
            }
            loads.remove(key);
            nodes.remove(key);
            if (nodes.isEmpty()) {
                // consistent hash ring is empty now. Reset the partition table.
                shards.clear();
                return;
            }
        }
        ditributeShards();
    }

    private void addInternal(N node) {
        for (int i = 0; i < REPLICATION_FACTOR; i++) {
            byte[] bytes = getBytes(node.getKey(), i);
            long hash = getHash(bytes);
            N previous = ring.put(hash, node);
            if (previous != null) {
                log.warn(
                        "Collision detected [hash({})={}]: {} and {}",
                        i,
                        hash,
                        previous,
                        node);
            }
        }
        nodes.put(node.getKey(), node);
        loads.put(node.getKey(), 0);
    }

    private void ditributeShards() {
        shards.clear();
        loads.replaceAll((k, v) -> 0);
        for (ShardKey shardKey : shardKeys) {
            long hash = getHash(shardKey.getSpec().getBytes(StandardCharsets.UTF_8));
            assignShardToNode(shardKey, hash);
        }
    }

    private void assignShardToNode(ShardKey shardKey, long hash) {
        SortedMap<Long, N> tailMap = ring.tailMap(hash);
        if (tailMap.isEmpty()) {
            tailMap = ring;
        }
        Iterator<N> i = tailMap.values().iterator();
        if (!i.hasNext()) {
            throw new IllegalStateException("Not enough room to distribute shards");
        }
        N node = i.next();
        shards.put(shardKey, node);
        loads.merge(node.getKey(), 1, Integer::sum);
    }

    public synchronized List<N> getNodes() {
        return new ArrayList<>(nodes.values());
    }

    public int getNumShards() {
        return shardKeys.size();
    }

    public synchronized int getNumNodes() {
        return nodes.size();
    }

    public synchronized SortedMap<VirtualNodeKey, Integer> getLoads() {
        return new TreeMap<>(loads);
    }

    public synchronized SortedMap<ShardKey, N> getShards() {
        return new TreeMap<>(shards);
    }

    public synchronized double getAverageLoad() {
        return ((double) shardKeys.size()) / nodes.size();
    }

    public synchronized N getShardOwner(ShardKey shard) {
        return shards.get(shard);
    }

    public synchronized N getShardOwner(int shardId) {
        return shards.get(shardKeys.get(shardId));
    }
}

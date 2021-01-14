package org.elasticsoftware.elasticactors.redux.hashing;

import org.elasticsoftware.elasticactors.redux.hashing.api.Config;
import org.elasticsoftware.elasticactors.redux.hashing.api.Hasher;
import org.elasticsoftware.elasticactors.redux.shard.ShardKey;
import org.elasticsoftware.elasticactors.redux.vnode.VirtualNode;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ShardToNodeHasherTest {

    private final Config props = new Config(new ShardToNodeHashAlgorithm(), 256, 128, 1.25, false);
    private final Hasher<VirtualNodeHashKey, VirtualNodeMember<? extends VirtualNode>> hasher =
            new ShardToNodeConsistentHasher(props, null);

    @Test
    void test() {
        System.out.println("Adding 1st node");
        addNMembers(hasher, 1);
        printCurrentStats(hasher);

        System.out.println("Adding 2nd node");
        addNMembers(hasher, 1);
        printCurrentStats(hasher);
        SortedMap<ShardKey, VirtualNodeHashKey> shardsMap = getShards(props, hasher);
        //printShards(shardsMap);

        System.out.println("Adding 3rd node");
        List<VirtualNodeHashKey> currentMembers = currentMembers(hasher);
        addNMembers(hasher, 1);
        printCurrentStats(hasher);
        SortedMap<ShardKey, VirtualNodeHashKey> newShardsMap = getShards(props, hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        System.out.println("Adding 4th and 5th nodes");
        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 2);
        printCurrentStats(hasher);
        newShardsMap = getShards(props, hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        for (int i = 4; i > 0; i--) {
            currentMembers = currentMembers(hasher);
            shardsMap = readdNode(props, hasher, shardsMap, currentMembers, i);
        }

        System.out.println("Adding 6th and 7th nodes");
        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 2);
        printCurrentStats(hasher);
        newShardsMap = getShards(props, hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        System.out.println("Removing last node");
        removeLastMember(hasher, currentMembers);
        currentMembers = currentMembers(hasher);
        printCurrentStats(hasher);
        newShardsMap = getShards(props, hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        System.out.println("Adding 7th node");
        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 1);
        printCurrentStats(hasher);
        newShardsMap = getShards(props, hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        System.out.println("Removing last node");
        removeLastMember(hasher, currentMembers);
        currentMembers = currentMembers(hasher);
        printCurrentStats(hasher);
        newShardsMap = getShards(props, hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        System.out.println("Adding 7th node");
        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 1);
        printCurrentStats(hasher);
        newShardsMap = getShards(props, hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        System.out.println("Removing last node");
        removeLastMember(hasher, currentMembers);
        currentMembers = currentMembers(hasher);
        printCurrentStats(hasher);
        newShardsMap = getShards(props, hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        System.out.println("Adding 10 nodes");
        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 10);
        printCurrentStats(hasher);
        newShardsMap = getShards(props, hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        System.out.println("Removing last node");
        removeLastMember(hasher, currentMembers);
        currentMembers = currentMembers(hasher);
        printCurrentStats(hasher);
        newShardsMap = getShards(props, hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        System.out.println("Adding 10 nodes");
        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 10);
        printCurrentStats(hasher);
        newShardsMap = getShards(props, hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        System.out.println("Adding 100 nodes");
        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 100);
        printCurrentStats(hasher);
        newShardsMap = getShards(props, hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        System.out.println("Topping nodes so nodes == shards");
        currentMembers = currentMembers(hasher);
        addNMembers(
                hasher,
                props.getPartitionCount() - hasher.getMembers().size());
        printCurrentStats(hasher);
        newShardsMap = getShards(props, hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        System.out.println("Removing last node");
        removeLastMember(hasher, currentMembers);
        currentMembers = currentMembers(hasher);
        printCurrentStats(hasher);
        newShardsMap = getShards(props, hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        System.out.println("Removing last node");
        removeLastMember(hasher, currentMembers);
        currentMembers = currentMembers(hasher);
        printCurrentStats(hasher);
        newShardsMap = getShards(props, hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        System.out.println("Adding 10 nodes");
        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 10);
        printCurrentStats(hasher);
        newShardsMap = getShards(props, hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        System.out.println("Adding 20 nodes");
        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 20);
        printCurrentStats(hasher);
        newShardsMap = getShards(props, hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;
    }

    private SortedMap<ShardKey, VirtualNodeHashKey> readdNode(
            Config props,
            Hasher<VirtualNodeHashKey, VirtualNodeMember<? extends VirtualNode>> hasher,
            SortedMap<ShardKey, VirtualNodeHashKey> shardsMap,
            List<VirtualNodeHashKey> currentMembers,
            int i) {
        SortedMap<ShardKey, VirtualNodeHashKey> newShardsMap;
        System.out.println("Removing node " + i);
        hasher.remove(currentMembers.get(i));
        currentMembers = currentMembers(hasher);
        printCurrentStats(hasher);
        newShardsMap = getShards(props, hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        System.out.println("Adding node " + i);
        currentMembers = currentMembers(hasher);
        hasher.add(new VirtualNodeMember<>(new TestVirtualNode(i)));
        printCurrentStats(hasher);
        newShardsMap = getShards(props, hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;
        return shardsMap;
    }

    private void removeLastMember(
            Hasher<VirtualNodeHashKey, VirtualNodeMember<? extends VirtualNode>> hasher,
            List<VirtualNodeHashKey> currentMembers) {
        hasher.remove(currentMembers.get(currentMembers.size() - 1));
        TestVirtualNode.COUNTER.decrementAndGet();
    }

    private List<VirtualNodeHashKey> currentMembers(Hasher<VirtualNodeHashKey, VirtualNodeMember<
            ? extends VirtualNode>> hasher) {
        return hasher.getMembers()
                .stream()
                .map(VirtualNodeMember::getKey)
                .collect(Collectors.toList());
    }

    private void printShards(SortedMap<ShardKey, VirtualNodeHashKey> shardsMap) {
        System.out.println("Shards summary:");
        shardsMap.forEach((shardKey, key) ->
                System.out.println("\tShard " + shardKey + " will be handled by " + key));
    }

    private void checkDivergence(
            List<VirtualNodeHashKey> currentMembers,
            SortedMap<ShardKey, VirtualNodeHashKey> shardsMap,
            SortedMap<ShardKey, VirtualNodeHashKey> newShardsMap) {
        newShardsMap.forEach((shardKey, key) -> {
            VirtualNodeHashKey oldKey = shardsMap.get(shardKey);
            if (!key.equals(oldKey) && currentMembers.contains(key) && currentMembers.contains(
                    oldKey)) {
                System.out.println("WARNING: Shard "
                        + shardKey
                        + " moved from "
                        + oldKey
                        + " to "
                        + key);
            }
        });
    }

    private SortedMap<ShardKey, VirtualNodeHashKey> getShards(
            Config props,
            Hasher<VirtualNodeHashKey, VirtualNodeMember<? extends VirtualNode>> hasher) {
        SortedMap<ShardKey, VirtualNodeHashKey> shardsMap = new TreeMap<>();
        for (int i = 0; i < props.getPartitionCount(); i++) {
            ShardKey shardKey = new ShardKey("test", i);
            VirtualNodeHashKey key = hasher.locateKey(shardKey.getSpec()).getKey();
            shardsMap.put(shardKey, key);
        }
        return shardsMap;
    }

    private void addNMembers(
            Hasher<VirtualNodeHashKey, VirtualNodeMember<? extends VirtualNode>> hasher,
            long n) {
        hasher.add(Stream.generate(TestVirtualNode::new)
                .limit(n)
                .map(VirtualNodeMember::new)
                .collect(Collectors.toList()));
    }

    private void printCurrentStats(Hasher<VirtualNodeHashKey, VirtualNodeMember<?
            extends VirtualNode>> hasher) {
        System.out.printf(
                "Current balance: (average load=%.3f)%n",
                hasher.averageLoad());
        hasher.getMembers()
                .stream()
                .map(VirtualNodeMember::getKey)
                .forEach(k -> System.out.println("\t"
                        + k
                        + ": "
                        + hasher.loadDistribution().getOrDefault(k, 0L)));
    }

}
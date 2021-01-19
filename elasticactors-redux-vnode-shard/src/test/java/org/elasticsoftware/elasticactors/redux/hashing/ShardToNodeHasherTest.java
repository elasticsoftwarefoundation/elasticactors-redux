package org.elasticsoftware.elasticactors.redux.hashing;

import lombok.extern.slf4j.Slf4j;
import org.elasticsoftware.elasticactors.redux.shard.ShardKey;
import org.elasticsoftware.elasticactors.redux.vnode.VirtualNode;
import org.elasticsoftware.elasticactors.redux.vnode.VirtualNodeKey;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
class ShardToNodeHasherTest {

    private final List<String> uuids = Stream.generate(UUID::randomUUID)
            .limit(1_000_000)
            .map(UUID::toString)
            .collect(Collectors.toList());

    private final ShardToNodeConsistentHash<TestVirtualNode> hasher =
            new ShardToNodeConsistentHash<>("test", 256);

    // This is not actually a test
    // @Test
    void test() {
        System.out.println(
                "count\tavg\trealavg\tmedian\tstddev\tperdev\tmin\tmax\ta_realavg\ta_median"
                        + "\ta_stddev\ta_perdev\ta_min\ta_max\tshuffled");
        //System.out.println("Adding 1st node");
        List<VirtualNodeKey> currentMembers = currentMembers(hasher);
        addNMembers(hasher, 1);
        printCurrentStats(hasher);
        SortedMap<ShardKey, VirtualNodeKey> shardsMap = getShards(hasher);
        SortedMap<ShardKey, VirtualNodeKey> newShardsMap = getShards(hasher);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        //System.out.println("Adding 2nd node");
        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 1);
        printCurrentStats(hasher);
        newShardsMap = getShards(hasher);
        //printShards(shardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        //System.out.println("Adding 3rd node");
        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 1);
        printCurrentStats(hasher);
        newShardsMap = getShards(hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        //System.out.println("Adding 4th and 5th nodes");
        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 2);
        printCurrentStats(hasher);
        newShardsMap = getShards(hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        //System.out.println("Adding 6th and 7th nodes");
        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 2);
        printCurrentStats(hasher);
        newShardsMap = getShards(hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        //System.out.println("Adding 10 nodes");
        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 10);
        printCurrentStats(hasher);
        newShardsMap = getShards(hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        //System.out.println("Adding 10 nodes");
        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 10);
        printCurrentStats(hasher);
        newShardsMap = getShards(hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        //System.out.println("Adding 100 nodes");
        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 10);
        printCurrentStats(hasher);
        newShardsMap = getShards(hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 10);
        printCurrentStats(hasher);
        newShardsMap = getShards(hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 10);
        printCurrentStats(hasher);
        newShardsMap = getShards(hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 10);
        printCurrentStats(hasher);
        newShardsMap = getShards(hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 10);
        printCurrentStats(hasher);
        newShardsMap = getShards(hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 10);
        printCurrentStats(hasher);
        newShardsMap = getShards(hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 10);
        printCurrentStats(hasher);
        newShardsMap = getShards(hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 10);
        printCurrentStats(hasher);
        newShardsMap = getShards(hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 10);
        printCurrentStats(hasher);
        newShardsMap = getShards(hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 10);
        printCurrentStats(hasher);
        newShardsMap = getShards(hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 10);
        printCurrentStats(hasher);
        newShardsMap = getShards(hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 10);
        printCurrentStats(hasher);
        newShardsMap = getShards(hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 10);
        printCurrentStats(hasher);
        newShardsMap = getShards(hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 10);
        printCurrentStats(hasher);
        newShardsMap = getShards(hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 10);
        printCurrentStats(hasher);
        newShardsMap = getShards(hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 10);
        printCurrentStats(hasher);
        newShardsMap = getShards(hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 10);
        printCurrentStats(hasher);
        newShardsMap = getShards(hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 10);
        printCurrentStats(hasher);
        newShardsMap = getShards(hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 10);
        printCurrentStats(hasher);
        newShardsMap = getShards(hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 10);
        printCurrentStats(hasher);
        newShardsMap = getShards(hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 10);
        printCurrentStats(hasher);
        newShardsMap = getShards(hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        currentMembers = currentMembers(hasher);
        addNMembers(hasher, 10);
        printCurrentStats(hasher);
        newShardsMap = getShards(hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        //System.out.println("Topping nodes so nodes == shards");
        currentMembers = currentMembers(hasher);
        addNMembers(hasher, hasher.getNumShards() - hasher.getNumNodes());
        printCurrentStats(hasher);
        newShardsMap = getShards(hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

//        //System.out.println("Removing last node");
//        removeLastMember(hasher);
//        currentMembers = currentMembers(hasher);
//        printCurrentStats(hasher);
//        newShardsMap = getShards(hasher);
//        //printShards(newShardsMap);
//        checkDivergence(currentMembers, shardsMap, newShardsMap);
//        shardsMap = newShardsMap;
//
//        //System.out.println("Removing last node");
//        removeLastMember(hasher);
//        currentMembers = currentMembers(hasher);
//        printCurrentStats(hasher);
//        newShardsMap = getShards(hasher);
//        //printShards(newShardsMap);
//        checkDivergence(currentMembers, shardsMap, newShardsMap);
//        shardsMap = newShardsMap;
//
//        //System.out.println("Adding 10 nodes");
//        currentMembers = currentMembers(hasher);
//        addNMembers(hasher, 10);
//        printCurrentStats(hasher);
//        newShardsMap = getShards(hasher);
//        //printShards(newShardsMap);
//        checkDivergence(currentMembers, shardsMap, newShardsMap);
//        shardsMap = newShardsMap;
//
//        //System.out.println("Adding 20 nodes");
//        currentMembers = currentMembers(hasher);
//        addNMembers(hasher, 20);
//        printCurrentStats(hasher);
//        newShardsMap = getShards(hasher);
//        //printShards(newShardsMap);
//        checkDivergence(currentMembers, shardsMap, newShardsMap);
//        shardsMap = newShardsMap;
    }

    private SortedMap<ShardKey, VirtualNodeKey> readdNode(
            ShardToNodeConsistentHash<TestVirtualNode> hasher,
            SortedMap<ShardKey, VirtualNodeKey> shardsMap,
            int i) {
        List<VirtualNodeKey> currentMembers = currentMembers(hasher);
        SortedMap<ShardKey, VirtualNodeKey> newShardsMap;
        //System.out.println("Removing node " + i);
        hasher.remove(currentMembers.get(i));
        currentMembers = currentMembers(hasher);
        printCurrentStats(hasher);
        newShardsMap = getShards(hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;

        //System.out.println("Adding node " + i);
        currentMembers = currentMembers(hasher);
        hasher.add(new TestVirtualNode(i));
        printCurrentStats(hasher);
        newShardsMap = getShards(hasher);
        //printShards(newShardsMap);
        checkDivergence(currentMembers, shardsMap, newShardsMap);
        shardsMap = newShardsMap;
        return shardsMap;
    }

    private void removeLastMember(ShardToNodeConsistentHash<TestVirtualNode> hasher) {
        List<VirtualNodeKey> currentMembers = currentMembers(hasher);
        hasher.remove(currentMembers.get(TestVirtualNode.COUNTER.decrementAndGet()));
    }

    private List<VirtualNodeKey> currentMembers(ShardToNodeConsistentHash<TestVirtualNode> hasher) {
        return hasher.getNodes()
                .stream()
                .map(VirtualNode::getKey)
                .collect(Collectors.toList());
    }

    private void printShards(SortedMap<ShardKey, VirtualNodeKey> shardsMap) {
        System.out.println("Shards summary:");
        shardsMap.forEach((shardKey, key) ->
                System.out.println("\tShard " + shardKey + " will be handled by " + key));
    }

    private void checkDivergence(
            List<VirtualNodeKey> currentMembers,
            SortedMap<ShardKey, VirtualNodeKey> shardsMap,
            SortedMap<ShardKey, VirtualNodeKey> newShardsMap) {
        long shuffledShards = newShardsMap.entrySet().stream().filter(e -> {
            ShardKey shardKey = e.getKey();
            VirtualNodeKey key = e.getValue();
            VirtualNodeKey oldKey = shardsMap.get(shardKey);
            return !key.equals(oldKey)
                    && currentMembers.contains(key)
                    && currentMembers.contains(oldKey);
        }).count();
        System.out.printf("\t%d%n", shuffledShards);
    }

    private SortedMap<ShardKey, VirtualNodeKey> getShards(
            ShardToNodeConsistentHash<TestVirtualNode> hasher) {
        return hasher.getShards()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().getKey(),
                        (a, b) -> a,
                        TreeMap::new));
    }

    private void addNMembers(ShardToNodeConsistentHash<TestVirtualNode> hasher, long n) {
        hasher.add(Stream.generate(TestVirtualNode::new)
                .limit(n)
                .collect(Collectors.toList()));
    }

    private final ActorToShardConsistentHash shardHasher =
            new ActorToShardConsistentHash(hasher.getNumShards());

    private void printActorStats(ShardToNodeConsistentHash<TestVirtualNode> hasher) {
        SortedMap<VirtualNodeKey, Long> loads = new TreeMap<>();
        hasher.getNodes().forEach(m -> loads.put(m.getKey(), 0L));
        for (String id : uuids) {
            int shard = shardHasher.getShard(id);
            loads.merge(hasher.getShardOwner(shard).getKey(), 1L, Long::sum);
        }
        DoubleStatistics stats = loads.values().stream()
                .map(e -> (double) e)
                .collect(Collector.of(
                        DoubleStatistics::new,
                        DoubleStatistics::accept,
                        DoubleStatistics::combine));
        double median = ((stats.getMax() - stats.getMin()) / 2.0d) + stats.getMin();
        System.out.printf(
                "\t%.3f\t%.3f\t%.3f\t%.3f\t%d\t%d",
                stats.getAverage(),
                median,
                stats.getStandardDeviation(),
                stats.getStandardDeviation() / stats.getAverage() * 100,
                (long) stats.getMin(),
                (long) stats.getMax());
    }

    private void printCurrentStats(ShardToNodeConsistentHash<TestVirtualNode> hasher) {
        SortedMap<VirtualNodeKey, Integer> loads = hasher.getLoads();
        DoubleStatistics stats = loads.values().stream()
                .map(e -> (double) e)
                .collect(Collector.of(
                        DoubleStatistics::new,
                        DoubleStatistics::accept,
                        DoubleStatistics::combine));
        double median = ((stats.getMax() - stats.getMin()) / 2.0d) + stats.getMin();
        System.out.printf(
                "%d\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%d\t%d",
                stats.getCount(),
                hasher.getAverageLoad(),
                stats.getAverage(),
                median,
                stats.getStandardDeviation(),
                stats.getStandardDeviation() / stats.getAverage() * 100,
                (long) stats.getMin(),
                (long) stats.getMax());
        printActorStats(hasher);
//        hasher.getMembers()
//                .stream()
//                .map(VirtualNodeMember::getKey)
//                .forEach(k -> System.out.println("\t"
//                        + k
//                        + ": "
//                        + loads.getOrDefault(k, 0L)));
    }

    private final static class DoubleStatistics extends DoubleSummaryStatistics {

        private double sumOfSquare = 0.0d;
        private double sumOfSquareCompensation; // Low order bits of sum
        private double simpleSumOfSquare; // Used to compute right sum for non-finite inputs

        @Override
        public void accept(double value) {
            super.accept(value);
            double squareValue = value * value;
            simpleSumOfSquare += squareValue;
            sumOfSquareWithCompensation(squareValue);
        }

        public DoubleStatistics combine(DoubleStatistics other) {
            super.combine(other);
            simpleSumOfSquare += other.simpleSumOfSquare;
            sumOfSquareWithCompensation(other.sumOfSquare);
            sumOfSquareWithCompensation(other.sumOfSquareCompensation);
            return this;
        }

        private void sumOfSquareWithCompensation(double value) {
            double tmp = value - sumOfSquareCompensation;
            double velvel = sumOfSquare + tmp; // Little wolf of rounding error
            sumOfSquareCompensation = (velvel - sumOfSquare) - tmp;
            sumOfSquare = velvel;
        }

        public double getSumOfSquare() {
            double tmp = sumOfSquare + sumOfSquareCompensation;
            if (Double.isNaN(tmp) && Double.isInfinite(simpleSumOfSquare)) {
                return simpleSumOfSquare;
            }
            return tmp;
        }

        public final double getStandardDeviation() {
            return getCount() > 0 ? Math.sqrt((getSumOfSquare() / getCount())
                    - Math.pow(getAverage(), 2)) : 0.0d;
        }

    }

}
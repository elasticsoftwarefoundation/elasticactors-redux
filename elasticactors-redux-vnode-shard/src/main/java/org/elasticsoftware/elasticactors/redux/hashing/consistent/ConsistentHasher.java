package org.elasticsoftware.elasticactors.redux.hashing.consistent;

import org.elasticsoftware.elasticactors.redux.hashing.api.Config;
import org.elasticsoftware.elasticactors.redux.hashing.api.HashKey;
import org.elasticsoftware.elasticactors.redux.hashing.api.Hasher;
import org.elasticsoftware.elasticactors.redux.hashing.api.Member;
import org.springframework.lang.Nullable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Simple consistent hasher
 */
public class ConsistentHasher<T extends HashKey<T>, M extends Member<T>> implements Hasher<T, M> {

    protected final Config config;

    protected final SortedMap<T, Long> loads = new TreeMap<>();
    protected final SortedMap<T, M> members = new TreeMap<>();
    protected final SortedMap<Long, M> partitions = new TreeMap<>();
    protected final SortedMap<Long, M> ring = new TreeMap<>();
    protected final SortedSet<T> unoccupiedMembers = new TreeSet<>();

    public ConsistentHasher(Config config, @Nullable List<? extends M> members) {
        this.config = config;
        if (members != null) {
            for (M member : members) {
                addInternal(member);
            }
            distributePartitions();
        }
    }

    private void addInternal(M member) {
        for (long i = 0; i < config.getReplicationFactor(); i++) {
            byte[] key = member.getKey().toByteArray(i);
            long hash = config.getHashAlgorithm().sum64(key);
            ring.put(hash, member);
        }
        members.put(member.getKey(), member);
    }

    private void distributePartitions() {
        loads.clear();
        partitions.clear();
        unoccupiedMembers.clear();
        unoccupiedMembers.addAll(members.keySet());
        ByteBuffer bs = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        for (long partID = 0L; partID < config.getPartitionCount(); partID++) {
            bs.putLong(0, partID);
            long hash = config.getHashAlgorithm().sum64(bs.array());
            distributeWithLoads(partID, hash);
        }
    }

    private void distributeWithLoads(long partID, long hash) {
        long averageLoad = (long) Math.ceil(averageLoad());
        int count = 0;
        SortedMap<Long, M> tailMap = ring.tailMap(hash);
        if (tailMap.isEmpty()) {
            tailMap = ring;
        }
        Iterator<M> i = tailMap.values().iterator();
        while (true) {
            count += 1;
            if (count >= ring.size()) {
                // User needs to decrease partition count, increase member count or increase load
                throw new IllegalStateException("Not enough room to distribute partitions");
            }
            M member = i.next();
            T key = member.getKey();
            if (select(key, averageLoad)) {
                partitions.put(partID, member);
                loads.merge(key, 1L, Long::sum);
                unoccupiedMembers.remove(key);
                return;
            }
            if (!i.hasNext()) {
                i = ring.values().iterator();
            }
        }
    }

    protected boolean select(T key, long averageLoad) {
        long load = loads.getOrDefault(key, 0L);
        if (load == 0L) {
            return true;
        }
        if (config.isForceMinimumLoad()) {
            return unoccupiedMembers.isEmpty();
        }
        return true;
    }

    /**
     * Exposes the current average load.
     */
    @Override
    public double averageLoad() {
        double partitionsPerMember = ((double) config.getPartitionCount()) / members.size();
        return partitionsPerMember * config.getLoad();
    }

    /**
     * Returns a thread-safe copy of members.
     */
    @Override
    public synchronized List<M> getMembers() {
        return new ArrayList<>(members.values());
    }

    @Override
    public synchronized void add(Collection<M> members) {
        if (members.isEmpty()) {
            return;
        }
        for (M member : members) {
            if (this.members.containsKey(member.getKey())) {
                // We already have this member. Quit immediately.
                continue;
            }
            addInternal(member);
        }
        distributePartitions();
    }

    @Override
    public synchronized void remove(Collection<T> keys) {
        if (keys.isEmpty()) {
            return;
        }
        for (T key : keys) {
            if (!members.containsKey(key)) {
                // There is no member with that name. Quit immediately.
                return;
            }
            for (int i = 0; i < config.getReplicationFactor(); i++) {
                byte[] bs = key.toByteArray(i);
                long hash = config.getHashAlgorithm().sum64(bs);
                ring.remove(hash);
            }
            members.remove(key);
            if (members.isEmpty()) {
                // consistent hash ring is empty now. Reset the partition table.
                loads.clear();
                partitions.clear();
                unoccupiedMembers.clear();
                return;
            }
        }
        distributePartitions();
    }

    /**
     * Exposes load distribution of members.
     */
    @Override
    public synchronized SortedMap<T, Long> loadDistribution() {
        return new TreeMap<>(loads);
    }

    /**
     * Returns partition ID for given key.
     */
    public long findPartitionID(byte[] key) {
        long hash = config.getHashAlgorithm().sum64(key);
        return Long.remainderUnsigned(hash, config.getPartitionCount());
    }

    /**
     * Returns the owner of the given partition.
     */
    public synchronized M getPartitionOwner(long partID) {
        return partitions.get(partID);
    }

    /**
     * Finds a home for given key
     */
    @Override
    public M locateKey(byte[] key) {
        long partID = findPartitionID(key);
        return getPartitionOwner(partID);
    }

    private synchronized List<M> getClosestN(long partID, int count) {
        if (count > members.size()) {
            throw new InsufficientMemberCountException();
        }
        List<M> res = new ArrayList<>();
        long ownerKey = 0;
        M owner = getPartitionOwner(partID);
        // Hash and sort all the names.
        List<Long> hashes = new ArrayList<>();
        Map<Long, M> kmems = new HashMap<>();
        for (Map.Entry<T, M> entry : members.entrySet()) {
            T key = entry.getKey();
            M member = entry.getValue();
            long hash = config.getHashAlgorithm().sum64(key.toByteArray());
            if (key.equals(owner.getKey())) {
                ownerKey = hash;
            }
            hashes.add(hash);
            kmems.put(hash, member);
        }
        hashes.sort(Comparator.naturalOrder());

        // Find the key owner
        int idx = 0;
        while (idx < hashes.size()) {
            long hash = hashes.get(idx);
            if (hash == ownerKey) {
                res.add(kmems.get(hash));
                break;
            }
            idx += 1;
        }

        // Find the closest (replica owners) members.
        while (res.size() <= count) {
            idx += 1;
            if (idx >= hashes.size()) {
                idx = 0;
            }
            long hash = hashes.get(idx);
            res.add(kmems.get(hash));
        }

        return res;
    }


    /**
     * Returns the closest N member to a key in the hash ring.
     * <br>
     * This may be useful to find members for replication.
     */
    public List<M> getClosestN(byte[] key, int count) {
        long partID = findPartitionID(key);
        return getClosestN(partID, count);
    }

    /**
     * Returns the closest N member for given partition.
     * <br>
     * This may be useful to find members for replication.
     */
    public List<M> getClosestNForPartition(long partID, int count) {
        return getClosestN(partID, count);
    }
}

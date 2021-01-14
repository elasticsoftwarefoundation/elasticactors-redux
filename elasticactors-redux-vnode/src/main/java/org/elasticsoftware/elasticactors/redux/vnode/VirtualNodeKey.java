package org.elasticsoftware.elasticactors.redux.vnode;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.elasticsoftware.elasticactors.redux.serialization.VirtualNodeKeyDeserializer;
import org.springframework.lang.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Value
@JsonSerialize(using = ToStringSerializer.class)
@JsonDeserialize(using = VirtualNodeKeyDeserializer.class)
@EqualsAndHashCode(cacheStrategy = EqualsAndHashCode.CacheStrategy.LAZY)
public class VirtualNodeKey implements Comparable<VirtualNodeKey> {

    private final static Pattern VIRTUAL_NODES_REGEX = Pattern.compile("^([^/]+)/nodes/([^/]+)$");

    @Value
    public static class IndexSpec implements Comparable<IndexSpec> {

        private final static Pattern INDEXED_NODE_REGEX = Pattern.compile("^([^/])+-([0-9]+)$");

        @Nullable
        private static IndexSpec parse(String nodeId) {
            Matcher matcher = INDEXED_NODE_REGEX.matcher(nodeId);
            if (matcher.matches()) {
                return new IndexSpec(matcher.group(1), Integer.parseInt(matcher.group(2)));
            }
            return null;
        }

        String root;
        int index;

        @Override
        public int compareTo(IndexSpec o) {
            int compare = root.compareTo(o.root);
            if (compare == 0) {
                compare = Integer.compare(index, o.index);
            }
            return compare;
        }
    }

    String actorSystemName;
    String nodeId;

    @EqualsAndHashCode.Exclude
    String spec;

    @Nullable
    @EqualsAndHashCode.Exclude
    IndexSpec indexSpec;

    public VirtualNodeKey(String actorSystemName, String nodeId) {
        this.actorSystemName = actorSystemName;
        this.nodeId = nodeId;
        this.spec = actorSystemName + "/nodes/" + nodeId;
        this.indexSpec = IndexSpec.parse(nodeId);
    }

    public VirtualNodeKey(String spec) {
        Matcher matcher = VIRTUAL_NODES_REGEX.matcher(spec);
        if (matcher.matches()) {
            this.actorSystemName = matcher.group(1);
            this.nodeId = matcher.group(2);
            this.spec = spec;
            this.indexSpec = IndexSpec.parse(nodeId);
        } else {
            throw new IllegalArgumentException(String.format(
                    "'%s' does not conform to the virtual node spec format",
                    spec));
        }
    }

    @Override
    public int compareTo(VirtualNodeKey o) {
        if (actorSystemName.equals(o.actorSystemName) && indexSpec != null && o.indexSpec != null) {
            return indexSpec.compareTo(o.indexSpec);
        }
        return spec.compareTo(o.spec);
    }

    @Override
    public String toString() {
        return spec;
    }
}

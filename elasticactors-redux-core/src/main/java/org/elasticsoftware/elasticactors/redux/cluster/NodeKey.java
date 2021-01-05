package org.elasticsoftware.elasticactors.redux.cluster;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.elasticsoftware.elasticactors.redux.serialization.NodeKeyDeserializer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Value
@JsonSerialize(using = ToStringSerializer.class)
@JsonDeserialize(using = NodeKeyDeserializer.class)
@EqualsAndHashCode(cacheStrategy = EqualsAndHashCode.CacheStrategy.LAZY)
public class NodeKey implements Comparable<NodeKey> {

    private final static Pattern NODES_REGEX = Pattern.compile("^([^/]+)/nodes/([^/]+)$");

    String actorSystemName;
    String nodeId;

    @EqualsAndHashCode.Exclude
    String spec;

    public NodeKey(String actorSystemName, String nodeId) {
        this.actorSystemName = actorSystemName;
        this.nodeId = nodeId;
        this.spec = actorSystemName + "/nodes/" + nodeId;
    }

    public NodeKey(String spec) {
        Matcher matcher = NODES_REGEX.matcher(spec);
        if (matcher.matches()) {
            this.actorSystemName = matcher.group(1);
            this.nodeId = matcher.group(2);
            this.spec = spec;
        } else {
            throw new IllegalArgumentException(String.format(
                    "'%s' does not conform to the node spec format",
                    spec));
        }
    }

    @Override
    public int compareTo(NodeKey o) {
        int result = actorSystemName.compareTo(o.actorSystemName);
        if (result == 0) {
            result = nodeId.compareTo(o.nodeId);
        }
        return result;
    }

    @Override
    public String toString() {
        return spec;
    }
}

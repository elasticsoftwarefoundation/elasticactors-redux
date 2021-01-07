package org.elasticsoftware.elasticactors.redux.vnode;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.elasticsoftware.elasticactors.redux.serialization.VirtualNodeKeyDeserializer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Value
@JsonSerialize(using = ToStringSerializer.class)
@JsonDeserialize(using = VirtualNodeKeyDeserializer.class)
@EqualsAndHashCode(cacheStrategy = EqualsAndHashCode.CacheStrategy.LAZY)
public class VirtualNodeKey implements Comparable<VirtualNodeKey> {

    private final static Pattern VIRTUAL_NODES_REGEX = Pattern.compile("^([^/]+)/nodes/([^/]+)$");

    String actorSystemName;
    String nodeId;

    @EqualsAndHashCode.Exclude
    String spec;

    public VirtualNodeKey(String actorSystemName, String nodeId) {
        this.actorSystemName = actorSystemName;
        this.nodeId = nodeId;
        this.spec = actorSystemName + "/nodes/" + nodeId;
    }

    public VirtualNodeKey(String spec) {
        Matcher matcher = VIRTUAL_NODES_REGEX.matcher(spec);
        if (matcher.matches()) {
            this.actorSystemName = matcher.group(1);
            this.nodeId = matcher.group(2);
            this.spec = spec;
        } else {
            throw new IllegalArgumentException(String.format(
                    "'%s' does not conform to the virtual node spec format",
                    spec));
        }
    }

    @Override
    public int compareTo(VirtualNodeKey o) {
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

package org.elasticsoftware.elasticactors.redux.cluster;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.elasticsoftware.elasticactors.redux.serialization.ShardKeyDeserializer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Value
@JsonSerialize(using = ToStringSerializer.class)
@JsonDeserialize(using = ShardKeyDeserializer.class)
@EqualsAndHashCode(cacheStrategy = EqualsAndHashCode.CacheStrategy.LAZY)
public class ShardKey implements Comparable<ShardKey> {

    private final static Pattern SHARDS_REGEX = Pattern.compile("^([^/]+)/shards/([0-9]+)$");

    String actorSystemName;
    int shardId;

    @EqualsAndHashCode.Exclude
    String spec;

    public ShardKey(String actorSystemName, int shardId) {
        this.actorSystemName = actorSystemName;
        this.shardId = shardId;
        this.spec = actorSystemName + "/shards/" + shardId;
    }

    public ShardKey(String spec) {
        Matcher matcher = SHARDS_REGEX.matcher(spec);
        if (matcher.matches()) {
            this.actorSystemName = matcher.group(1);
            this.shardId = Integer.parseInt(matcher.group(2));
            this.spec = spec;
        } else {
            throw new IllegalArgumentException(String.format(
                    "'%s' does not conform to the shard spec format",
                    spec));
        }
    }

    @Override
    public int compareTo(ShardKey o) {
        int result = actorSystemName.compareTo(o.actorSystemName);
        if (result == 0) {
            result = Integer.compare(shardId, o.shardId);
        }
        return result;
    }

    @Override
    public String toString() {
        return spec;
    }
}

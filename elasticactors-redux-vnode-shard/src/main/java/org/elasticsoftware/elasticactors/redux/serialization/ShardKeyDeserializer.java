package org.elasticsoftware.elasticactors.redux.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.elasticsoftware.elasticactors.redux.shard.ShardKey;

import java.io.IOException;

public final class ShardKeyDeserializer extends JsonDeserializer<ShardKey> {

    @Override
    public ShardKey deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        try {
            return new ShardKey(value);
        } catch (IllegalArgumentException e) {
            throw new InvalidFormatException(p, e.getMessage(), value, ShardKey.class);
        }
    }
}

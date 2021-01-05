package org.elasticsoftware.elasticactors.redux.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.elasticsoftware.elasticactors.redux.cluster.NodeKey;

import java.io.IOException;

public final class NodeKeyDeserializer extends JsonDeserializer<NodeKey> {

    @Override
    public NodeKey deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        try {
            return new NodeKey(value);
        } catch (IllegalArgumentException e) {
            throw new InvalidFormatException(p, e.getMessage(), value, NodeKey.class);
        }
    }
}

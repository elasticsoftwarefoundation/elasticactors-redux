package org.elasticsoftware.elasticactors.redux.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.elasticsoftware.elasticactors.redux.vnode.VirtualNodeKey;

import java.io.IOException;

public final class VirtualNodeKeyDeserializer extends JsonDeserializer<VirtualNodeKey> {

    @Override
    public VirtualNodeKey deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        String value = p.getValueAsString();
        try {
            return new VirtualNodeKey(value);
        } catch (IllegalArgumentException e) {
            throw new InvalidFormatException(p, e.getMessage(), value, VirtualNodeKey.class);
        }
    }
}

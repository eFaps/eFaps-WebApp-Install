package org.efaps.esjp.ui.rest.dto;

import java.io.IOException;
import java.util.LinkedHashMap;

import org.efaps.admin.datamodel.Type;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

@EFapsUUID("a0fd34e3-89bf-4bc6-a8c9-49b7b9a8eecd")
@EFapsApplication("eFaps-WebApp")
public class ValueSerializer
    extends JsonSerializer<LinkedHashMap<String, Object>>
{

    @Override
    public void serialize(final LinkedHashMap<String, Object> _value, final JsonGenerator _gen,
                          final SerializerProvider _serializers)
        throws IOException, JsonProcessingException
    {
        _gen.writeStartObject(_value);
        for (final var entry : _value.entrySet()) {
            if (entry.getValue() instanceof Type) {
                final var objValue = ((Type) entry.getValue()).getName();
                _gen.writeObjectField(entry.getKey(), objValue);
            } else {
                _gen.writeObjectField(entry.getKey(), entry.getValue());
            }
        }
        _gen.writeEndObject();
    }
}

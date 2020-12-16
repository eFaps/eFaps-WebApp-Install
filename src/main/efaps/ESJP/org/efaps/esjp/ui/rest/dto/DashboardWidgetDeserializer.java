/*
 * Copyright 2003 - 2020 The eFaps Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.efaps.esjp.ui.rest.dto;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.EnumUtils;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@EFapsUUID("149f8a15-c42b-4e4d-8748-bc6465c006a0")
@EFapsApplication("eFaps-WebApp")
public class DashboardWidgetDeserializer
    extends JsonDeserializer<DashboardWidgetDto>
{

    protected ObjectMapper getObjectMapper()
    {
        final var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return mapper;
    }

    @Override
    public DashboardWidgetDto deserialize(final JsonParser _jp, final DeserializationContext _ctxt)
        throws IOException, JsonProcessingException
    {
        DashboardWidgetDto ret = null;
        final JsonNode node = _jp.getCodec().readTree(_jp);
        final var typeVal = node.get("type").asText();
        final var identifier = node.get("identifier").asText();
        final var eql = node.get("eql").asText();
        final var type = EnumUtils.getEnum(DashboardWidgetType.class, typeVal);
        switch (type) {
            case TABLE:
                final var arrayNode = node.get("columns");
                final List<ColumnDto> columns;
                if (arrayNode == null) {
                    columns = Collections.emptyList();
                } else {
                    columns = getObjectMapper().readValue(arrayNode.toString(),
                                    new TypeReference<List<ColumnDto>>()
                                    {
                                    });
                }
                ret = DashboardWidgetTableDto.builder()
                                .withIdentifier(identifier)
                                .withType(type)
                                .withEql(eql)
                                .withColumns(columns)
                                .build();
                break;
            default:
                break;
        }
        return ret;
    }

}

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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.EnumUtils;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    public DashboardWidgetDto deserialize(final JsonParser jsonParser,
                                          final DeserializationContext ctxt)
        throws IOException, JsonProcessingException
    {
        DashboardWidgetDto ret = null;
        final JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        final var typeVal = node.get("type").asText();
        final var identifier = node.has("identifier") ? node.get("identifier").asText() : "UNKNOWN";
        final var eql = node.has("eql") ? node.get("eql").asText() : "UNKNOWN";
        final var title = node.has("title") ? node.get("title").asText() : "";
        final var type = EnumUtils.getEnum(DashboardWidgetType.class, typeVal);
        switch (type) {
            case TABLE:
                final var links = node.has("links") ? StreamSupport
                                .stream(node.get("links").spliterator(), false)
                                .map(JsonNode::asText)
                                .collect(Collectors.toList()) : null;
                ret = DashboardWidgetTableDto.builder()
                                .withIdentifier(identifier)
                                .withTitle(title)
                                .withEql(eql)
                                .withLinks(links)
                                .build();
                break;
            case CHART:
                final var groupBy = node.has("groupBy") ? StreamSupport
                                .stream(node.get("groupBy").spliterator(), false)
                                .map(JsonNode::asText)
                                .collect(Collectors.toList()) : null;
                final List<DashboardWidgetChartMetricDto> metric = new ArrayList<>();
                if (node.has("metrics")) {
                    final var metricsNode = node.get("metrics");
                    for (final var metricNode : metricsNode) {
                        final var function = metricNode.has("function")
                                        ? EnumUtils.getEnum(MetricFunction.class,
                                                        metricNode.get("function").textValue())
                                        : MetricFunction.SUM;
                        final var key = metricNode.has("key") ? metricNode.get("key").textValue() : "NOKEY";
                        metric.add(DashboardWidgetChartMetricDto.builder()
                                        .withKey(key)
                                        .withType(function)
                                        .build());
                    }
                }
                ret = DashboardWidgetChartDto.builder()
                                .withIdentifier(identifier)
                                .withTitle(title)
                                .withEql(eql)
                                .withGroupBy(groupBy)
                                .withMetrics(metric)
                                .withChartType(node.has("chartType") ? node.get("chartType").textValue() : "bar")
                                .build();
                break;
            case PLACEHOLDER:
                ret = DashboardWidgetPlaceholderDto.builder()
                                .withIdentifier(identifier)
                                .withTitle(title)
                                .build();
            case TEMPLATE:
                ret = DashboardWidgetTemplateDto.builder()
                                .withIdentifier(identifier)
                                .withTitle(title)
                                .withEql(eql)
                                .build();
            default:
                break;
        }
        return ret;
    }

}

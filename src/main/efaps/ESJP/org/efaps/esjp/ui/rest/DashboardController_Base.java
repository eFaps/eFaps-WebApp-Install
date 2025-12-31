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
package org.efaps.esjp.ui.rest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.db.Context;
import org.efaps.db.stmt.PrintStmt;
import org.efaps.eql.EQL;
import org.efaps.esjp.ci.CICommon;
import org.efaps.esjp.ui.rest.dto.DashboardDto;
import org.efaps.esjp.ui.rest.dto.DashboardTemplateDto;
import org.efaps.esjp.ui.rest.dto.DashboardWidgetChartDto;
import org.efaps.esjp.ui.rest.dto.DashboardWidgetChartMetricDto;
import org.efaps.esjp.ui.rest.dto.DashboardWidgetDataDto;
import org.efaps.esjp.ui.rest.dto.DashboardWidgetDto;
import org.efaps.esjp.ui.rest.dto.DashboardWidgetTableDto;
import org.efaps.esjp.ui.rest.dto.DashboardWidgetType;
import org.efaps.esjp.ui.util.ValueUtils;
import org.efaps.esjp.ui.util.WebApp;
import org.efaps.json.data.DataList;
import org.efaps.util.EFapsException;
import org.efaps.util.OIDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.ws.rs.core.Response;

@EFapsUUID("6b0be50f-17b7-47a4-b6cd-f0ca3a932a7e")
@EFapsApplication("eFaps-WebApp")
public abstract class DashboardController_Base
{

    public final String KEY = "org.efaps.esjp.ui.rest.Dashboard";
    private static final Logger LOG = LoggerFactory.getLogger(DashboardController.class);

    public Response getDashboard()
        throws EFapsException
    {
        Response response;
        if (WebApp.DASHBOARD_ACTIVE.get()) {
            String dashboardStr = null;
            final var eval = EQL.builder().print()
                            .query(CICommon.UserDashboard)
                            .where()
                            .attribute(CICommon.UserDashboard.Person).eq(Context.getThreadContext().getPersonId())
                            .select()
                            .attribute(CICommon.UserDashboard.Config)
                            .evaluate();
            if (eval.next()) {
                dashboardStr = eval.get(CICommon.UserDashboard.Config);
            }
            DashboardDto dto = null;
            if (dashboardStr == null) {
                dto = DashboardDto.builder().build();
            } else {
                final var mapper = ValueUtils.getObjectMapper();
                try {
                    dto = mapper.readValue(dashboardStr, DashboardDto.class);
                } catch (final JsonProcessingException e) {
                    LOG.error("Catched: ", e);
                }
            }
            response = Response.ok()
                            .entity(dto)
                            .build();
        } else {
            response = Response.ok().build();
        }
        return response;
    }

    public Response updateDashboard(final DashboardDto dashboardDto)
        throws EFapsException
    {
        dashboardDto.getPages().stream()
                        .flatMap(page -> page.getItems().stream())
                        .forEach(item -> persistWidget(item.getWidget()));

        final var mapper = ValueUtils.getObjectMapper();
        try {
            final var dashboardStr = mapper.writeValueAsString(dashboardDto);

            final var eval = EQL.builder().print().query(CICommon.UserDashboard).where()
                            .attribute(CICommon.UserDashboard.Person).eq(Context.getThreadContext().getPersonId())
                            .select()
                            .oid()
                            .evaluate();
            if (eval.next()) {
                EQL.builder().update(eval.inst())
                                .set(CICommon.UserDashboard.Config, dashboardStr)
                                .execute();
            } else {
                EQL.builder().insert(CICommon.UserDashboard)
                                .set(CICommon.UserDashboard.Person, Context.getThreadContext().getPersonId())
                                .set(CICommon.UserDashboard.Config, dashboardStr)
                                .execute();
            }
        } catch (final JsonProcessingException e) {
            LOG.error("Catched: ", e);
        }
        return Response.ok().build();
    }

    protected void persistWidget(final DashboardWidgetDto dashboardWidgetDto)
    {
        if (dashboardWidgetDto != null) {
            try {
                final var mapper = ValueUtils.getObjectMapper();
                final var config = mapper.writeValueAsString(dashboardWidgetDto);
                final var eval = EQL.builder().print()
                                .query(CICommon.DashboardWidget).where()
                                .attribute(CICommon.DashboardWidget.Identifier)
                                .eq(dashboardWidgetDto.getIdentifier())
                                .select().oid()
                                .evaluate();
                if (eval.next()) {
                    if (DashboardWidgetType.CHART.equals(dashboardWidgetDto.getType())
                                    || DashboardWidgetType.TABLE.equals(dashboardWidgetDto.getType())) {
                        EQL.builder().update(eval.inst())
                                        .set(CICommon.DashboardWidget.Config, config)
                                        .stmt().execute();
                    } else {
                        EQL.builder().delete(eval.inst()).stmt().execute();
                    }
                } else if (DashboardWidgetType.CHART.equals(dashboardWidgetDto.getType())
                                || DashboardWidgetType.TABLE.equals(dashboardWidgetDto.getType())) {
                    EQL.builder().insert(CICommon.DashboardWidget)
                                    .set(CICommon.DashboardWidget.Identifier, dashboardWidgetDto.getIdentifier())
                                    .set(CICommon.DashboardWidget.Config, config)
                                    .stmt().execute();
                }
            } catch (final EFapsException | JsonProcessingException e) {
                LOG.error("Catched: ", e);
            }
        }
    }

    public Response getWidget(final String widgetId)
        throws EFapsException
    {
        Object entity = null;
        String configStr = null;
        if (OIDUtil.isOID(widgetId)) {
            final var eval = EQL.builder().print(widgetId).attribute(CICommon.DashboardTemplate.Config).evaluate();
            if (eval.next()) {
                configStr = eval.<String>get(CICommon.DashboardTemplate.Config);
            }
        } else {
            final var eval = EQL.builder().print()
                            .query(CICommon.DashboardWidget).where()
                            .attribute(CICommon.DashboardWidget.Identifier)
                            .eq(widgetId)
                            .select().attribute(CICommon.DashboardWidget.Config)
                            .evaluate();
            if (eval.next()) {
                configStr = eval.<String>get(CICommon.DashboardWidget.Config);
            }
        }
        if (configStr != null) {
            final var mapper = ValueUtils.getObjectMapper();
            try {
                final var dto = mapper.readValue(configStr, DashboardWidgetDto.class);
                switch (dto.getType()) {
                    case TABLE:
                        entity = getTable((DashboardWidgetTableDto) dto);
                        break;
                    case CHART:
                        entity = getChart((DashboardWidgetChartDto) dto);
                        break;
                    default:
                        break;
                }
            } catch (JsonProcessingException | EFapsException e) {
                LOG.error("Catched", e);
            }
        }
        return Response.ok()
                        .entity(entity)
                        .build();
    }

    protected DashboardWidgetDataDto getTable(final DashboardWidgetTableDto widgetDto)
        throws EFapsException
    {
        final var stmt = EQL.getStatement(widgetDto.getEql());
        DataList data = null;
        if (stmt != null) {
            final var eval = ((PrintStmt) stmt).evaluate();
            data = eval.getDataList();
        }
        return DashboardWidgetDataDto.builder()
                        .withWidget(widgetDto)
                        .withData(data)
                        .build();
    }

    protected DashboardWidgetDataDto getChart(final DashboardWidgetChartDto widgetDto)
        throws EFapsException
    {
        final Map<Object, Map<String, BigDecimal>> groupedValues = new HashMap<>();
        final var stmt = EQL.getStatement(widgetDto.getEql());
        if (stmt != null) {
            final var eval = ((PrintStmt) stmt).evaluate();

            final var groupDefs = widgetDto.getGroupBy();
            final var metrics = widgetDto.getMetrics().stream().map(DashboardWidgetChartMetricDto::getKey)
                            .collect(Collectors.toList());
            while (eval.next()) {
                for (final var groupDef : groupDefs) {
                    final var groupBy = eval.get(groupDef);
                    if (!groupedValues.containsKey(groupBy)) {
                        groupedValues.put(groupBy, new HashMap<>());
                    }

                    for (final var metric : metrics) {
                        var metricValue = eval.get(metric);
                        if (metricValue == null) {
                            metricValue = BigDecimal.ZERO;
                        }
                        final var values = groupedValues.get(groupBy);
                        if (!values.containsKey(metric)) {
                            values.put(metric, BigDecimal.ZERO);
                        }
                        final var currentValue = values.get(metric);
                        values.put(metric, currentValue.add((BigDecimal) metricValue));
                    }
                }
            }
        }
        return DashboardWidgetDataDto.builder()
                        .withWidget(widgetDto)
                        .withData(groupedValues)
                        .build();
    }

    public Response getTemplates()
        throws EFapsException
    {
        final var eval = EQL.builder().print()
                        .query(CICommon.DashboardTemplate)
                        .select()
                        .attribute(CICommon.DashboardTemplate.Name, CICommon.DashboardTemplate.Description)
                        .evaluate();
        final List<DashboardTemplateDto> dtos = new ArrayList<>();
        while (eval.next()) {
            dtos.add(DashboardTemplateDto.builder()
                            .withOid(eval.inst().getOid())
                            .withName(eval.get(CICommon.DashboardTemplate.Name))
                            .withDescription(eval.get(CICommon.DashboardTemplate.Description))
                            .build());
        }
        return Response.ok()
                        .entity(dtos)
                        .build();
    }
}

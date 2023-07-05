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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.user.UserAttributesSet;
import org.efaps.db.Context;
import org.efaps.db.stmt.PrintStmt;
import org.efaps.eql.EQL;
import org.efaps.esjp.ci.CICommon;
import org.efaps.esjp.ui.rest.dto.DashboardDto;
import org.efaps.esjp.ui.rest.dto.DashboardItemDto;
import org.efaps.esjp.ui.rest.dto.DashboardPageDto;
import org.efaps.esjp.ui.rest.dto.DashboardWidgetChartDto;
import org.efaps.esjp.ui.rest.dto.DashboardWidgetDto;
import org.efaps.esjp.ui.rest.dto.DashboardWidgetTableDto;
import org.efaps.util.EFapsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@EFapsUUID("6b0be50f-17b7-47a4-b6cd-f0ca3a932a7e")
@EFapsApplication("eFaps-WebApp")
public abstract class DashboardController_Base
{

    public final String KEY = "org.efaps.esjp.ui.rest.Dashboard";
    private static final Logger LOG = LoggerFactory.getLogger(DashboardController.class);

    public Response getDashboard()
        throws EFapsException
    {
        final var userAttributesSet = new UserAttributesSet(Context.getThreadContext().getPersonId());
        final var dashboardStr = userAttributesSet.getString(KEY);
        DashboardDto dto = null;
        if (dashboardStr == null) {
            dto = getDefaultDashboard();
        } else {
            final var mapper = getObjectMapper();
            try {
                dto = mapper.readValue(dashboardStr, DashboardDto.class);
            } catch (final JsonProcessingException e) {
                LOG.error("Catched: ", e);
            }
        }
        return Response.ok()
                        .entity(dto)
                        .build();
    }

    protected DashboardDto getDefaultDashboard()
    {
        final var tableDto = DashboardWidgetTableDto.builder()
                        .withTitle("TableTest")
                        .withIdentifier("e543f8d7-1f26-4bc2-bd20-0ce00b6078ed")
                        .build();
        final var chartDto = DashboardWidgetChartDto.builder()
                        .withTitle("Chart Test")
                        .withIdentifier("eb70f1aa-1b0a-4739-9ea6-b1d0bb54436d")
                        .build();
        return DashboardDto.builder()
                        .withPages(Arrays.asList(DashboardPageDto
                                        .builder()
                                        .withLabel("Page 1")
                                        .withItems(Arrays.asList(
                                                        DashboardItemDto.builder().withCols(1).withRows(1).withx(0)
                                                                        .withy(0)
                                                                        .withWidget(tableDto).build(),
                                                        DashboardItemDto.builder().withCols(1).withRows(1).withx(1)
                                                                        .withy(0)
                                                                        .withWidget(chartDto).build(),
                                                        DashboardItemDto.builder().withCols(1).withRows(1).withx(0)
                                                                        .withy(1).build()))
                                        .build(),
                                        DashboardPageDto
                                                        .builder()
                                                        .withLabel("Page 2")
                                                        .build()))
                        .build();
    }

    public Response updateDashboard(final DashboardDto _dashboardDto)
        throws EFapsException
    {
        System.out.println(_dashboardDto);
        // _dashboardDto.getTabs().stream().flatMap(tab ->
        // tab.getLayout().stream())
        // .forEach(layout -> persistWidget(layout.getWidget()));

        final var mapper = getObjectMapper();
        try {
            final var dashboardStr = mapper.writeValueAsString(_dashboardDto);
            final var userAttributesSet = new UserAttributesSet(Context.getThreadContext().getPersonId());
            userAttributesSet.set(KEY, dashboardStr);
            userAttributesSet.storeInDb();
        } catch (final JsonProcessingException e) {
            LOG.error("Catched: ", e);
        }
        return Response.ok().build();
    }

    protected ObjectMapper getObjectMapper()
    {
        final var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return mapper;
    }

    protected void persistWidget(final DashboardWidgetDto _dashboardWidgetDto)
    {
        if (_dashboardWidgetDto != null) {
            try {
                final var mapper = getObjectMapper();
                final var config = mapper.writeValueAsString(_dashboardWidgetDto);
                final var eval = EQL.builder().print()
                                .query(CICommon.DashboardWidget).where()
                                .attribute(CICommon.DashboardWidget.Identifier)
                                .eq(_dashboardWidgetDto.getIdentifier())
                                .select().oid()
                                .evaluate();
                if (eval.next()) {
                    EQL.builder().update(eval.inst())
                                    .set(CICommon.DashboardWidget.Config, config)
                                    .stmt().execute();
                } else {
                    EQL.builder().insert(CICommon.DashboardWidget)
                                    .set(CICommon.DashboardWidget.Identifier, _dashboardWidgetDto.getIdentifier())
                                    .set(CICommon.DashboardWidget.Config, config)
                                    .stmt().execute();
                }
            } catch (final EFapsException | JsonProcessingException e) {
                LOG.error("Catched: ", e);
            }
        }
    }

    public Response getWidget(final String _widgetId)
        throws EFapsException
    {
        final var eval = EQL.builder().print()
                        .query(CICommon.DashboardWidget).where()
                        .attribute(CICommon.DashboardWidget.Identifier)
                        .eq(_widgetId)
                        .select().attribute(CICommon.DashboardWidget.Config)
                        .evaluate();
        var entity = new Object();
        if (eval.next()) {
            final var mapper = getObjectMapper();
            try {
                final var dto = mapper.readValue(eval.<String>get(CICommon.DashboardWidget.Config),
                                DashboardWidgetDto.class);
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
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return Response.ok()
                        .entity(entity)
                        .build();
    }

    protected Object getTable(final DashboardWidgetTableDto _widgetDto)
        throws EFapsException
    {
        final var stmt = EQL.getStatement(_widgetDto.getEql());
        final var eval = ((PrintStmt) stmt).evaluate();
        return eval.getDataList();
    }

    protected Object getChart(final DashboardWidgetChartDto _widgetDto)
        throws EFapsException
    {
        final var stmt = EQL.getStatement(_widgetDto.getEql());
        final var eval = ((PrintStmt) stmt).evaluate();

        final var groupDefs = Arrays.asList("gb");
        final var metrics = Arrays.asList("cross");
        final Map<Object, Map<String, BigDecimal>> groupedValues = new HashMap<>();
        while (eval.next()) {
            for (final var groupDef: groupDefs) {
               final var groupBy = eval.get(groupDef);
               if (!groupedValues.containsKey(groupBy)) {
                   groupedValues.put(groupBy, new HashMap<>());
               }

               for (final var metric: metrics) {
                   final var metricValue = eval.get(metric);
                   final var values = groupedValues.get(groupBy);
                   if (!values.containsKey(metric)) {
                       values.put(metric, BigDecimal.ZERO);
                   }
                   final var currentValue = values.get(metric);
                   values.put(metric, currentValue.add((BigDecimal) metricValue));
               }
            }
        }
        return groupedValues;
    }


}

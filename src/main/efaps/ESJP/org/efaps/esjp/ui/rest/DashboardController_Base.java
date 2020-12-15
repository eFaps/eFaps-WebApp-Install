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

import java.util.ArrayList;
import java.util.Collections;

import javax.ws.rs.core.Response;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.user.UserAttributesSet;
import org.efaps.db.Context;
import org.efaps.eql.EQL;
import org.efaps.esjp.ci.CICommon;
import org.efaps.esjp.ui.rest.dto.DashboardDto;
import org.efaps.esjp.ui.rest.dto.DashboardItemDto;
import org.efaps.esjp.ui.rest.dto.DashboardTabDto;
import org.efaps.esjp.ui.rest.dto.DashboardWidgetDto;
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
        final var layout = new ArrayList<DashboardItemDto>();
        layout.add(DashboardItemDto.builder()
                        .withx(0)
                        .withy(0)
                        .withCols(1)
                        .withRows(13)
                        .build());
        layout.add(DashboardItemDto.builder()
                        .withx(1)
                        .withy(0)
                        .withCols(4)
                        .withRows(13)
                        .build());
        return DashboardDto.builder()
                        .withTabs(Collections.singletonList(DashboardTabDto.builder()
                                        .withLayout(layout)
                                        .build()))
                        .build();
    }

    public Response updateDashboard(final DashboardDto _dashboardDto)
        throws EFapsException
    {
        System.out.println(_dashboardDto);
        _dashboardDto.getTabs().stream().flatMap(tab -> tab.getLayout().stream())
                        .forEach(layout -> persistWidget(layout.getWidget()));

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
}

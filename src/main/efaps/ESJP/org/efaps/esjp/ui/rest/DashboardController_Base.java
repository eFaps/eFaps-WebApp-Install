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
import org.efaps.esjp.ui.rest.dto.DashboardDto;
import org.efaps.esjp.ui.rest.dto.DashboardItemDto;
import org.efaps.esjp.ui.rest.dto.DashboardTabDto;
import org.efaps.util.EFapsException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@EFapsUUID("6b0be50f-17b7-47a4-b6cd-f0ca3a932a7e")
@EFapsApplication("eFaps-WebApp")
public abstract class DashboardController_Base
{

    public final String KEY = "org.efaps.esjp.ui.rest.Dashboard";

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
                // TODO Auto-generated catch block
                e.printStackTrace();
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
        final var mapper = getObjectMapper();
        try {
            final var dashboardStr = mapper.writeValueAsString(_dashboardDto);
            final var userAttributesSet = new UserAttributesSet(Context.getThreadContext().getPersonId());
            userAttributesSet.set(KEY, dashboardStr);
            userAttributesSet.storeInDb();
        } catch (final JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
}

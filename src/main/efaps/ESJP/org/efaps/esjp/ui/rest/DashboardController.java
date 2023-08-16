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

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.esjp.ui.rest.dto.DashboardDto;
import org.efaps.util.EFapsException;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@EFapsUUID("85b9a699-a2ff-4ad9-8945-0323f003bfdd")
@EFapsApplication("eFaps-WebApp")
@Path("/ui/dashboard")
public class DashboardController
    extends DashboardController_Base
{

    @Override
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getDashboard()
        throws EFapsException
    {
        return super.getDashboard();
    }

    @Override
    @POST
    @Produces({ MediaType.APPLICATION_JSON })
    public Response updateDashboard(final DashboardDto _dashboardDto)
        throws EFapsException
    {
        return super.updateDashboard(_dashboardDto);
    }

    @Override
    @GET
    @Path("/widgets/{widgetId}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getWidget(@PathParam("widgetId") final String _widgetId)
        throws EFapsException
    {
        return super.getWidget(_widgetId);
    }
}

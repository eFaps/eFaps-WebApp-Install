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

import java.util.List;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.esjp.ui.rest.dto.FilterDto;
import org.efaps.util.EFapsException;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@EFapsUUID("eaa16cf5-09d5-43aa-8c15-c8e758cfd9c2")
@EFapsApplication("eFaps-WebApp")
@Path("/ui/table")
public class TableController
    extends TableController_Base
{
    @Override
    @Path("/{cmdId}")
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getTable(@PathParam("cmdId") final String cmdId, @QueryParam("oid") final String oid)
        throws EFapsException
    {
        return super.getTable(cmdId, oid);
    }

    @Override
    @Path("/{cmdId}/filters")
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getTableFilters(@PathParam("cmdId") final String cmdId)
        throws EFapsException
    {
        return super.getTableFilters(cmdId);
    }

    @Override
    @Path("/{cmdId}/filters")
    @PUT
    @Produces({ MediaType.APPLICATION_JSON })
    public Response updateTableFilters(@PathParam("cmdId") final String cmdId,
                                      final List<FilterDto> filters)
        throws EFapsException
    {
        return super.updateTableFilters(cmdId, filters);
    }
}

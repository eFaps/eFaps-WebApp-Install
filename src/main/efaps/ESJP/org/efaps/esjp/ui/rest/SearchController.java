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

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.util.EFapsException;

@EFapsUUID("359aa048-d150-46de-a022-75716d1f3fb6")
@EFapsApplication("eFaps-WebApp")
@Path("/ui/search")
public class SearchController
    extends SearchController_Base
{
    @Override
    @GET
    @Path("/{cmdId}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getSearch(@PathParam("cmdId") final String _cmdId)
        throws EFapsException
    {
        return super.getSearch(_cmdId);
    }

    @Override
    @GET
    @Path("/{cmdId}/query")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response search(@PathParam("cmdId") final String _cmdId, @Context final UriInfo _uriInfo)
        throws EFapsException
    {
        return super.search(_cmdId, _uriInfo);
    }
}

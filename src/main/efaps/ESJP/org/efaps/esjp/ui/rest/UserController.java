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
import org.efaps.util.EFapsException;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@EFapsUUID("0f520538-106b-4b94-95be-c4ab619474f7")
@EFapsApplication("eFaps-WebApp")
@Path("/ui/user")
public class UserController
    extends UserController_Base
{

    @Override
    @GET
    @Path("/current")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getCurrent(@Context final Application app,
                               @Context final HttpHeaders headers,
                               @QueryParam("sync") final Boolean sync)
        throws EFapsException
    {
        return super.getCurrent(app, headers, sync);
    }

    @Override
    @GET
    @Path("/companies")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getCompanies()
        throws EFapsException
    {
        return super.getCompanies();
    }
}

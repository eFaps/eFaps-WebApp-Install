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

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.esjp.ui.rest.dto.PayloadDto;
import org.efaps.util.EFapsException;

@EFapsUUID("e768e37f-4723-4dc5-9ca6-971d7e3a3d7e")
@EFapsApplication("eFaps-WebApp")
@Path("/ui/autocomplete")
public class AutocompleteController
    extends AutocompleteController_Base
{
    @Override
    @Path("/{fieldId}")
    @POST
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response search(@PathParam("fieldId") final String fieldId, final PayloadDto dto)
        throws EFapsException
    {
        return super.search(fieldId, dto);
    }
}

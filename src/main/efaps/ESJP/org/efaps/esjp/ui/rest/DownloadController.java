/*
 * Copyright © 2003 - 2024 The eFaps Team (-)
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
 */
package org.efaps.esjp.ui.rest;

import java.io.IOException;

import org.apache.tika.Tika;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.esjp.ui.util.FileUtil;
import org.efaps.util.EFapsException;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;

@EFapsUUID("4dd8f541-1f5b-4c87-b38b-819a96513888")
@EFapsApplication("eFaps-WebApp")
@Path("/ui/download")
public class DownloadController
{

    @GET
    @Path("/{downloadKey}")
    @Produces({ MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON })
    public Response download(@PathParam("downloadKey") final String downloadKey)
        throws EFapsException, IOException
    {
        final ResponseBuilder response;
        final var file = FileUtil.get(downloadKey);
        if (file != null && file.exists()) {
            response = Response.ok(file);
            final Tika tika = new Tika();
            final String mimeType = tika.detect(file);
            response.header("Access-Control-Expose-Headers", "*");
            response.header("Content-Type", mimeType);
            response.header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
            response.header("Content-Length", file.length());
        } else {
            response = Response.status(Response.Status.NOT_FOUND);
        }
        return response.build();

    }
}

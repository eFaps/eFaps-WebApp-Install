/*
 * Copyright 2003 - 2024 The eFaps Team
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
package org.efaps.esjp.ui.rest.modules;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.efaps.admin.event.EventDefinition;
import org.efaps.admin.event.EventType;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsClassLoader;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.Command;
import org.efaps.esjp.ui.rest.modules.dto.CSVImportRequestDto;
import org.efaps.esjp.ui.rest.provider.ICSVImportProvider;
import org.efaps.util.EFapsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@EFapsUUID("e4407966-fe05-41f8-a6b1-a152c7a71e34")
@EFapsApplication("eFaps-WebApp")
@Path("/ui/modules/csvimport")
public class CSVImportController
{

    private static final Logger LOG = LoggerFactory.getLogger(CSVImportController.class);

    @Path("/{commandUUID}/validate")
    @POST
    @Produces({ MediaType.APPLICATION_JSON })
    public Response validate(@PathParam("commandUUID") final String commandUUID,
                             final CSVImportRequestDto requestDto)
        throws EFapsException
    {
        final List<String> messages = new ArrayList<>();
        final var command = Command.get(UUID.fromString(commandUUID));
        command.getEvents(EventType.UI_COMMAND_EXECUTE).forEach(event -> {
            final var provider = getProvider(event);
            messages.addAll(provider.validate(requestDto));
        });
        return Response.ok(messages).build();
    }

    @Path("/{commandUUID}/execute")
    @POST
    @Produces({ MediaType.APPLICATION_JSON })
    public Response execute(@PathParam("commandUUID") final String commandUUID,
                            final CSVImportRequestDto requestDto)
        throws EFapsException
    {
        try {
            final var command = Command.get(UUID.fromString(commandUUID));
            for (final var event : command.getEvents(EventType.UI_COMMAND_EXECUTE)) {
                final var provider = getProvider(event);
                provider.execute(requestDto);
            }
        } catch (final Exception e) {
            LOG.error("Catched", e);
            return Response.ok("{\"message\":\"Algo inesperado paso durante la importacion\"}").build();
        }
        return Response.ok().build();
    }

    protected ICSVImportProvider getProvider(EventDefinition event)
    {
        ICSVImportProvider ret = null;
        try {
            final Class<?> cls = Class.forName(event.getResourceName(), true, EFapsClassLoader.getInstance());
            LOG.info("ICSVImportProvider className {} ", cls);
            ret = (ICSVImportProvider) cls.getConstructor().newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            LOG.error("Catched", e);
        }
        return ret;
    }
}

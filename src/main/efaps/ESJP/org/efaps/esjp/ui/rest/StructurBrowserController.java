/*
 * Copyright 2003 - 2023 The eFaps Team
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
import java.util.UUID;

import org.efaps.admin.event.EventType;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.AbstractCommand;
import org.efaps.admin.ui.AbstractUserInterfaceObject.TargetMode;
import org.efaps.admin.ui.Command;
import org.efaps.admin.ui.Menu;
import org.efaps.esjp.ui.rest.dto.NavItemDto;
import org.efaps.esjp.ui.rest.dto.StructurBrowserDto;
import org.efaps.esjp.ui.rest.dto.StructurBrowserEntryDto;
import org.efaps.util.EFapsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@EFapsUUID("86f2aa96-dd98-4dc4-b915-280911b99ec3")
@EFapsApplication("eFaps-WebApp")
@Path("/ui/strctbrws")
public class StructurBrowserController
    extends AbstractTableController
{

    private static final Logger LOG = LoggerFactory.getLogger(StructurBrowserController.class);

    @Path("/{id}")
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getStructurBrowser(@PathParam("id") final String id,
                                       @QueryParam("oid") final String oid)
        throws EFapsException
    {
        AbstractCommand cmd = Command.get(UUID.fromString(id));
        if (cmd == null) {
            cmd = Menu.get(UUID.fromString(id));
        }
        final var table = cmd.getTargetTable();
        LOG.info("Get TABLE {} ", table);

        final var returned = cmd.executeEvents(EventType.UI_STRCTBRWS_EVALUATE);

        @SuppressWarnings("unchecked")
        final var values = (List<StructurBrowserEntryDto>) returned.get(0)
                        .get(ReturnValues.VALUES);

        final var menu = cmd.getTargetMenu();

        final List<NavItemDto> menus = menu == null ? null : new NavItemEvaluator().getMenu(menu, oid);

        final String selectionMode = cmd.isTargetShowCheckBoxes()
                        ? cmd.getSubmitSelectedRows() == 1 ? "single" : "multiple"
                        : null;

        final var dto = StructurBrowserDto.builder()
                        .withMenu(menus)
                        .withHeader(getHeader(cmd, oid))
                        .withColumns(getColumns(table, TargetMode.VIEW, null))
                        .withValues(values)
                        .withSelectionMode(selectionMode)
                        .withToggleColumn(cmd.getTargetStructurBrowserField())
                        .build();

        return Response.ok(dto)
                        .build();
    }
}

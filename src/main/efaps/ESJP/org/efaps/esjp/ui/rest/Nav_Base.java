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
import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.Response;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.AbstractCommand;
import org.efaps.admin.ui.AbstractMenu;
import org.efaps.admin.ui.Menu;
import org.efaps.esjp.ui.rest.dto.ActionDto;
import org.efaps.esjp.ui.rest.dto.ActionType;
import org.efaps.esjp.ui.rest.dto.NavItemDTO;
import org.efaps.ui.wicket.util.Configuration;
import org.efaps.util.EFapsException;

@EFapsUUID("addb3347-919d-455a-8a82-39e0180471d8")
@EFapsApplication("eFaps-WebApp")
public abstract class Nav_Base
{

    public Response getMyDesk()
        throws EFapsException
    {
        final var toolbarUUID =  Configuration.getSysConfig().getAttributeValue("org.efaps.webapp.MainToolBar");
        final var menu = Menu.get(UUID.fromString(toolbarUUID));
        final List<NavItemDTO> navItems = new ArrayList<>();
        for (final var command : menu.getCommands()) {
            ActionType actionType = null;
            if (command.getTargetTable() != null) {
                actionType = ActionType.GRID;
            }
            navItems.add(NavItemDTO.builder()
                            .withId(command.getUUID().toString())
                            .withLabel(command.getLabelProperty())
                            .withChildren(addChildren(command))
                            .withAction(ActionDto.builder()
                                            .withType(actionType)
                                            .build())
                            .build());
        }
        final Response ret = Response.ok()
                        .entity(navItems)
                        .build();
        return ret;
    }

    protected List<NavItemDTO> addChildren(final AbstractCommand _command)
    {
        final List<NavItemDTO> ret = new ArrayList<>();
        if (_command instanceof AbstractMenu) {
            for (final var command : ((AbstractMenu ) _command).getCommands()) {
                ActionType actionType = null;
                if (command.getTargetTable() != null) {
                    actionType = ActionType.GRID;
                }
                ret.add(NavItemDTO.builder()
                            .withId(command.getUUID().toString())
                            .withLabel(command.getLabelProperty())
                            .withChildren(addChildren(command))
                            .withAction(ActionDto.builder()
                                            .withType(actionType)
                                            .build())
                            .build());
            }
        }
        return ret;
    }
}

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

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.AbstractCommand;
import org.efaps.admin.ui.AbstractCommand.Target;
import org.efaps.admin.ui.AbstractMenu;
import org.efaps.admin.ui.AbstractUserInterfaceObject.TargetMode;
import org.efaps.admin.ui.Menu;
import org.efaps.esjp.ui.rest.dto.ActionDto;
import org.efaps.esjp.ui.rest.dto.ActionType;
import org.efaps.esjp.ui.rest.dto.NavItemDto;
import org.efaps.esjp.ui.util.WebApp;
import org.efaps.util.EFapsException;

import jakarta.ws.rs.core.Response;

@EFapsUUID("addb3347-919d-455a-8a82-39e0180471d8")
@EFapsApplication("eFaps-WebApp")
public abstract class NavController_Base
{

    public Response getMyDesk()
        throws EFapsException
    {
        final var toolbarUUID = WebApp.MAINTOOLBAR.get();
        final var menu = Menu.get(UUID.fromString(toolbarUUID));
        final List<NavItemDto> navItems = new ArrayList<>();
        for (final var command : menu.getCommands()) {
            if (command.hasAccess(TargetMode.VIEW, null)) {
                ActionType actionType = null;
                if (command.getTargetSearch() != null) {
                    actionType = ActionType.SEARCH;
                } else if (command.getTargetTable() != null) {
                    actionType = command.getTargetStructurBrowserField() == null ? ActionType.GRID
                                    : ActionType.STRBRWSR;
                } else if (command.getTarget() == Target.HIDDEN) {
                    actionType = ActionType.EXEC;
                }
                navItems.add(NavItemDto.builder()
                                .withId(command.getUUID().toString())
                                .withLabel(command.getLabelProperty())
                                .withChildren(addChildren(command))
                                .withAction(ActionDto.builder()
                                                .withType(actionType)
                                                .build())
                                .build());
            }
        }
        final Response ret = Response.ok()
                        .entity(navItems)
                        .build();
        return ret;
    }

    protected List<NavItemDto> addChildren(final AbstractCommand _command)
        throws EFapsException
    {
        final List<NavItemDto> ret = new ArrayList<>();
        if (_command instanceof AbstractMenu) {
            for (final var command : ((AbstractMenu) _command).getCommands()) {
                if (command.hasAccess(TargetMode.VIEW, null)) {
                    ActionType actionType = null;
                    if (command.getTargetTable() != null) {
                        actionType = command.getTargetStructurBrowserField() == null ? ActionType.GRID
                                        : ActionType.STRBRWSR;
                    } else if (command.getTargetForm() != null || command.getTargetModule() != null) {
                        actionType = ActionType.FORM;
                    } else if (command.getTargetSearch() != null) {
                        actionType = ActionType.SEARCH;
                    } else if (command.getTarget() == Target.HIDDEN) {
                        actionType = ActionType.EXEC;
                    }
                    ret.add(NavItemDto.builder()
                                    .withId(command.getUUID().toString())
                                    .withLabel(command.getLabelProperty())
                                    .withChildren(addChildren(command))
                                    .withAction(ActionDto.builder()
                                                    .withModal(command.getTarget() == Target.MODAL)
                                                    .withType(actionType)
                                                    .build())
                                    .build());
                }
            }
        }
        return ret;
    }
}

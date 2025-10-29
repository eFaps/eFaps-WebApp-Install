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

import org.apache.commons.collections4.CollectionUtils;
import org.efaps.admin.dbproperty.DBProperties;
import org.efaps.admin.event.EventType;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.AbstractCommand.Target;
import org.efaps.admin.ui.AbstractMenu;
import org.efaps.admin.ui.AbstractUserInterfaceObject.TargetMode;
import org.efaps.db.Instance;
import org.efaps.esjp.ui.rest.dto.ActionDto;
import org.efaps.esjp.ui.rest.dto.ActionType;
import org.efaps.esjp.ui.rest.dto.NavItemDto;
import org.efaps.esjp.ui.rest.dto.VerifyDto;
import org.efaps.util.EFapsException;

@EFapsUUID("ab1cb434-e7b5-424f-9917-0d84ea2dc9c7")
@EFapsApplication("eFaps-WebApp")
public abstract class NavItemEvaluator_Base
{

    public List<NavItemDto> getMenu(final AbstractMenu menu,
                                    final String oid)
        throws EFapsException
    {
        final var ret = new ArrayList<NavItemDto>();
        for (final var command : menu.getCommands()) {
            if (command.hasAccess(TargetMode.VIEW, Instance.get(oid))) {
                final var actionBldr = ActionDto.builder()
                                .withModal(command.getTarget() == Target.MODAL);
                if (command.getTargetForm() != null || command.getTargetModule() != null) {
                    actionBldr.withType(ActionType.FORM);
                    if (command.containsProperty("SubmitSelectedRows")) {
                        actionBldr.withVerify(VerifyDto.builder()
                                        .withQuestion(DBProperties
                                                        .getProperty(command.getName() + ".Selection"))
                                        .withSelectedRows(command.getSubmitSelectedRows())
                                        .build());
                    }
                } else if (command.getTargetSearch() != null) {
                    actionBldr.withType(ActionType.SEARCH);
                } else if (command.getTargetTable() != null) {
                    actionBldr.withType(ActionType.GRID);
                } else if (command.getTarget() == Target.HIDDEN) {
                    actionBldr.withType(ActionType.EXEC);
                } else if (command.getTarget() == Target.UNKNOWN && command.isSubmit() && command.isAskUser()) {
                    actionBldr.withType(ActionType.EXEC)
                                    .withVerify(VerifyDto.builder()
                                                    .withQuestion(DBProperties
                                                                    .getProperty(command.getName() + ".Question"))
                                                    .withSelectedRows(command.getSubmitSelectedRows())
                                                    .build());
                } else if (command.getTarget() == Target.UNKNOWN
                                && CollectionUtils.isNotEmpty(command.getEvents(EventType.UI_COMMAND_EXECUTE))) {
                    actionBldr.withType(ActionType.EXEC);
                }

                List<NavItemDto> children = null;
                if (command instanceof AbstractMenu) {
                    children = getMenu((AbstractMenu) command, oid);
                }
                ret.add(NavItemDto.builder()
                                .withId(command.getUUID().toString())
                                .withLabel(command.getLabelProperty())
                                .withChildren(children)
                                .withAction(actionBldr.build())
                                .build());
            }
        }
        return ret;
    }
}

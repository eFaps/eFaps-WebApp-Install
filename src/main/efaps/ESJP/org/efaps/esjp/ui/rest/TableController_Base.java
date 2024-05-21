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

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.efaps.admin.event.EventType;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsClassLoader;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.AbstractCommand;
import org.efaps.admin.ui.AbstractUserInterfaceObject;
import org.efaps.admin.ui.AbstractUserInterfaceObject.TargetMode;
import org.efaps.admin.ui.Command;
import org.efaps.admin.ui.Menu;
import org.efaps.api.ui.FilterBase;
import org.efaps.db.Context;
import org.efaps.esjp.ui.rest.dto.FilterDto;
import org.efaps.esjp.ui.rest.dto.NavItemDto;
import org.efaps.esjp.ui.rest.dto.TableDto;
import org.efaps.esjp.ui.rest.provider.ITableProvider;
import org.efaps.util.EFapsException;
import org.efaps.util.cache.CacheReloadException;
import org.efaps.util.cache.InfinispanCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.core.Response;

@EFapsUUID("708d3d0d-e230-44e1-99f4-aadb45f70be9")
@EFapsApplication("eFaps-WebApp")
public abstract class TableController_Base
    extends AbstractTableController
{

    /**
     * Logging instance used in this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(TableController.class);

    public static final String CACHENAME = TableController.class.getName() + ".Cache";

    public TableController_Base()
    {
        if (!InfinispanCache.get().exists(TableController_Base.CACHENAME)) {
            InfinispanCache.get().initCache(TableController_Base.CACHENAME);
        }
    }

    public Response getTable(final String cmdId,
                             final String oid)
        throws EFapsException
    {
        AbstractCommand cmd = Command.get(UUID.fromString(cmdId));
        if (cmd == null) {
            cmd = Menu.get(UUID.fromString(cmdId));
        }

        final var event = cmd.getEvents(EventType.UI_TABLE_EVALUATE).get(0);
        String className;
        if ("org.efaps.esjp.common.uitable.MultiPrint".equals(event.getResourceName())) {
            className = "org.efaps.esjp.ui.rest.provider.StandardTableProvider";
        } else {
            className = event.getResourceName();
        }

        ITableProvider provider = null;
        try {
            final Class<?> cls = Class.forName(className, true, EFapsClassLoader.getInstance());
            LOG.info("TableProvider className {} ", className);
            provider = (ITableProvider) cls.getConstructor().newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            LOG.error("Could not instantiate TableProvider", e);
        }
        if (provider == null) {
            throw new EFapsException(this.getClass(), "No TableProvider");
        }

        final var table = cmd.getTargetTable();
        LOG.info("Get TABLE {} ", table);
        final var properties = cmd.getEvents(EventType.UI_TABLE_EVALUATE).get(0).getPropertyMap();
        final var fields = getFields(table);
        final var values = provider.getValues(cmd, fields, properties, oid);

        final var menu = cmd.getTargetMenu();

        final List<NavItemDto> menus = menu == null ? null : new NavItemEvaluator().getMenu(menu);

        final String selectionMode = cmd.isTargetShowCheckBoxes()
                        ? cmd.getSubmitSelectedRows() == 1 ? "single" : "multiple"
                        : null;

        final var dto = TableDto.builder()
                        .withMenu(menus)
                        .withHeader(getHeader(cmd, oid))
                        .withColumns(getColumns(table, TargetMode.VIEW, null))
                        .withValues(values)
                        .withSelectionMode(selectionMode)
                        .withFiltered(fields.stream()
                                        .anyMatch(field -> (field.getFilter() != null
                                                        && FilterBase.DATABASE.equals(field.getFilter().getBase()))))
                        .build();

        final Response ret = Response.ok()
                        .entity(dto)
                        .build();
        return ret;
    }

    public Response getTableFilters(final String cmdId)
        throws EFapsException
    {
        AbstractCommand cmd = Command.get(UUID.fromString(cmdId));
        if (cmd == null) {
            cmd = Menu.get(UUID.fromString(cmdId));
        }
        final var key = getFilterKey(cmd);
        final var filterCache = InfinispanCache.get().<String, List<FilterDto>>getCache(TableController.CACHENAME);
        final Response ret;
        if (filterCache.containsKey(key)) {
            ret = Response.ok()
                            .entity(filterCache.get(key))
                            .build();
        } else {
            ret = Response.ok()
                            .entity(Collections.emptyList())
                            .build();
        }
        return ret;
    }

    public Response updateTableFilters(final String cmdId,
                                       final List<FilterDto> filters)
        throws EFapsException
    {
        AbstractCommand cmd = Command.get(UUID.fromString(cmdId));
        if (cmd == null) {
            cmd = Menu.get(UUID.fromString(cmdId));
        }
        final var key = getFilterKey(cmd);
        final var filterCache = InfinispanCache.get().<String, List<FilterDto>>getCache(TableController.CACHENAME);
        filterCache.put(key, filters);
        return Response.ok().build();
    }

    public static String getFilterKey(final AbstractUserInterfaceObject uiObject)
        throws CacheReloadException, EFapsException
    {
        final var key = uiObject.getUUID() != null ? uiObject.getUUID().toString()
                        : uiObject.getName() + uiObject.getId();
        return Context.getThreadContext().getPersonId() + "-" + Context.getThreadContext().getCompany().getId()
                        + "-" + key;
    }
}

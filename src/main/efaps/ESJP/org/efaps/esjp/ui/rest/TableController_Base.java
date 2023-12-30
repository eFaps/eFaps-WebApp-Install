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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.efaps.admin.datamodel.Attribute;
import org.efaps.admin.datamodel.Type;
import org.efaps.admin.datamodel.attributetype.DateType;
import org.efaps.admin.datamodel.ui.IUIValue;
import org.efaps.admin.event.EventType;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.AbstractCommand;
import org.efaps.admin.ui.AbstractUserInterfaceObject.TargetMode;
import org.efaps.admin.ui.Command;
import org.efaps.admin.ui.Menu;
import org.efaps.admin.ui.field.Field;
import org.efaps.admin.ui.field.Field.Display;
import org.efaps.api.ui.FilterBase;
import org.efaps.api.ui.FilterType;
import org.efaps.db.Context;
import org.efaps.db.Instance;
import org.efaps.eql.EQL;
import org.efaps.eql.builder.Query;
import org.efaps.eql.builder.Where;
import org.efaps.esjp.ui.rest.dto.FilterDto;
import org.efaps.esjp.ui.rest.dto.FilterKind;
import org.efaps.esjp.ui.rest.dto.NavItemDto;
import org.efaps.esjp.ui.rest.dto.TableDto;
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

    protected static final String CACHENAME = TableController.class.getName() + ".Cache";

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
        final var table = cmd.getTargetTable();
        LOG.info("Get TABLE {} ", table);
        final var properties = cmd.getEvents(EventType.UI_TABLE_EVALUATE).get(0).getPropertyMap();
        final var fields = getFields(table);
        final var values = getValues(cmd, fields, properties, oid);

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

    @SuppressWarnings("unchecked")
    public Collection<Map<String, ?>> getValues(final AbstractCommand cmd,
                                                final List<Field> fields,
                                                final Map<String, String> properties,
                                                final String oid)
        throws EFapsException
    {
        final var types = evalTypes(properties);
        final var typeNames = types.stream().map(Type::getName).toArray(String[]::new);
        final var query = EQL.builder()
                        .print()
                        .query(typeNames);

        Where where = null;
        if (properties.containsKey("LinkFrom") && Instance.get(oid).isValid()) {
            where = query.where().attribute(properties.get("LinkFrom")).eq(Instance.get(oid));
        }

        if (properties.containsKey("InstanceSelect")) {
            LOG.error("Cmd uses unsuported InstanceSelect: {}", cmd);
            return Collections.emptyList();
        }

        addFilter(cmd, query, where, types, fields);

        final var print = query.select();

        if (cmd.isTargetShowCheckBoxes() || fields.stream().anyMatch(field -> field.getReference() != null)) {
            print.oid().as("OID");
        }
        for (final var field : fields) {
            if (field.getAttribute() != null) {
                add2Select4Attribute(print, field, types);
            } else if (field.getSelect() != null) {
                var select = field.getSelect();
                if (field.getSelect().endsWith("attribute[Status]") && field.getUIProvider() == null) {
                    select = field.getSelect().replace("attribute[Status]", "status.label");
                }
                print.select(select).as(field.getName());
            } else if (field.getMsgPhrase() != null) {
                print.msgPhrase(getBaseSelect4MsgPhrase(field), field.getMsgPhrase()).as(field.getName());
            } else if (field.getPhrase() != null) {
                print.phrase(field.getPhrase()).as(field.getName());
            }
            if (field.getSelectAlternateOID() != null) {
                print.select(field.getSelectAlternateOID()).as(field.getName() + "_AOID");
            }
        }
        final var data = print.evaluate().getData();
        final var fieldsWithFormat = fields.stream().filter(field -> field.hasEvents(EventType.UI_FIELD_FORMAT))
                        .collect(Collectors.toList());

        for (final var fieldWithFormat : fieldsWithFormat) {
            for (final var map : data) {
                final var obj = map.get(fieldWithFormat.getName());
                final var value = new IUIValue()
                {

                    @Override
                    public Display getDisplay()
                    {
                        return null;
                    }

                    @Override
                    public Field getField()
                    {
                        return null;
                    }

                    @Override
                    public Instance getInstance()
                    {
                        return obj instanceof Instance ? (Instance) obj : null;
                    }

                    @Override
                    public Instance getCallInstance()
                    {
                        return null;
                    }

                    @Override
                    public Object getObject()
                    {
                        return obj;
                    }

                    @Override
                    public Attribute getAttribute()
                        throws EFapsException
                    {
                        return null;
                    }
                };
                final var returns = fieldWithFormat.executeEvents(EventType.UI_FIELD_FORMAT, ParameterValues.UIOBJECT,
                                value);
                for (final var ret : returns) {
                    ((Map<String, Object>) map).put(fieldWithFormat.getName(), ret.get(ReturnValues.VALUES));
                }
            }
        }
        return data;
    }

    public void addFilter(final AbstractCommand cmd,
                          final Query query,
                          final Where where,
                          final List<Type> types,
                          final List<Field> fields)
        throws EFapsException
    {
        final var key = getFilterKey(cmd);
        final var filterCache = InfinispanCache.get().<String, List<FilterDto>>getCache(TableController_Base.CACHENAME);
        List<FilterDto> filters = new ArrayList<>();
        if (filterCache.containsKey(key)) {
            filters = filterCache.get(key);
        } else if (fields.stream().anyMatch(field -> (field.getFilter() != null
                        && FilterBase.DATABASE.equals(field.getFilter().getBase())))) {
            filters = evalDefaultFilter(types, fields);

        }
        if (CollectionUtils.isNotEmpty(filters)) {
            LOG.info("applying filter");
            filterCache.put(key, filters);
            Where wherePart;
            if (where == null) {
                wherePart = query.where();
            } else {
                wherePart = where;
            }
            for (final var filter : filters) {
                switch (filter.getKind()) {
                    case DATE: {
                        wherePart.attribute(filter.getAttribute()).greaterOrEq(filter.getValue1().toString())
                                        .and().attribute(filter.getAttribute()).lessOrEq(filter.getValue2().toString());
                    }
                }
            }
        }
    }

    protected List<FilterDto> evalDefaultFilter(final List<Type> types,
                                                final List<Field> fields)
    {
        final List<FilterDto> ret = new ArrayList<>();
        for (final var field : fields) {
            if (field.getFilter() != null && FilterBase.DATABASE.equals(field.getFilter().getBase())) {
                if (FilterType.FREETEXT == field.getFilter().getType()) {
                    if (field.getAttribute() != null) {
                        Attribute attr = null;
                        for (final var type : types) {
                            attr = type.getAttribute(field.getAttribute());
                            if (attr != null) {
                                break;
                            }
                        }
                        if (attr != null) {
                            if (attr.getAttributeType().getDbAttrType() instanceof DateType) {
                                final var filterBuilder = FilterDto.builder()
                                                .withKind(FilterKind.DATE)
                                                .withField(field.getName())
                                                .withAttribute(attr.getName());

                                final var parts = field.getFilter().getDefaultValue().split(":");
                                final var range = parts[0];
                                final var fromSub = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
                                final var rangeCount = parts.length > 2 ? Integer.parseInt(parts[2]) : 1;
                                switch (range) {
                                    case "WEEK": {
                                        filterBuilder.withValue1(LocalDate.now()
                                                        .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                                                        .minusWeeks(fromSub));
                                        filterBuilder.withValue2(LocalDate.now().plusWeeks(rangeCount));
                                    }
                                }
                                ret.add(filterBuilder.build());
                            }
                        }
                    }
                }
            }
        }
        return ret;
    }

    protected String getFilterKey(AbstractCommand cmd)
        throws CacheReloadException, EFapsException
    {
        return Context.getThreadContext().getPersonId() + "-" + Context.getThreadContext().getCompany().getId()
                        + "-" + cmd.getUUID();

    }

    public Response getTableFilters(final String cmdId)
        throws EFapsException
    {
        AbstractCommand cmd = Command.get(UUID.fromString(cmdId));
        if (cmd == null) {
            cmd = Menu.get(UUID.fromString(cmdId));
        }
        final var key = getFilterKey(cmd);
        final var filterCache = InfinispanCache.get().<String, List<FilterDto>>getCache(TableController_Base.CACHENAME);
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

}

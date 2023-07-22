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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response;

import org.efaps.admin.datamodel.Attribute;
import org.efaps.admin.datamodel.Type;
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
import org.efaps.db.Instance;
import org.efaps.eql.EQL;
import org.efaps.esjp.common.properties.PropertiesUtil;
import org.efaps.esjp.ui.rest.dto.NavItemDto;
import org.efaps.esjp.ui.rest.dto.TableDto;
import org.efaps.util.EFapsException;
import org.efaps.util.UUIDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EFapsUUID("708d3d0d-e230-44e1-99f4-aadb45f70be9")
@EFapsApplication("eFaps-WebApp")
public abstract class TableController_Base
    extends AbstractController
{

    /**
     * Logging instance used in this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(TableController.class);

    public Response getTable(final String _id, final String oid)
        throws EFapsException
    {
        AbstractCommand cmd = Command.get(UUID.fromString(_id));
        if (cmd == null) {
            cmd = Menu.get(UUID.fromString(_id));
        }
        final var table = cmd.getTargetTable();
        LOG.info("Get TABLE {} ", table);
        final var properties = cmd.getEvents(EventType.UI_TABLE_EVALUATE).get(0).getPropertyMap();

        final var values = getValues(cmd, table, properties, oid);

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
                        .build();

        final Response ret = Response.ok()
                        .entity(dto)
                        .build();
        return ret;
    }

    protected List<Type> evalTypes(final Map<String, String> _propertiesMap)
        throws EFapsException
    {
        final var typeList = new ArrayList<Type>();
        final var properties = new Properties();
        properties.putAll(_propertiesMap);
        final var types = PropertiesUtil.analyseProperty(properties, "Type", 0);
        final var expandChildTypes = PropertiesUtil.analyseProperty(properties, "ExpandChildTypes", 0);

        for (final var typeEntry : types.entrySet()) {
            Type type;
            if (UUIDUtil.isUUID(typeEntry.getValue())) {
                type = Type.get(UUID.fromString(typeEntry.getValue()));
            } else {
                type = Type.get(typeEntry.getValue());
            }
            typeList.add(type);
            // default expand for children, if not deactiavted
            if (types.size() == 1
                            && (expandChildTypes.size() == 0
                            || expandChildTypes.size() == 1 && !"false".equalsIgnoreCase(expandChildTypes.get(0)))) {
                type.getChildTypes().forEach(at -> typeList.add(at));
            }
            // if we have more specific ones evaluate for each type
            if (expandChildTypes.size() > 1 && Boolean.parseBoolean(expandChildTypes.get(typeEntry.getKey()))) {
                type.getChildTypes().forEach(at -> typeList.add(at));
            }
        }
        return typeList;
    }

    @SuppressWarnings("unchecked")
    public Collection<Map<String, ?>> getValues(final AbstractCommand _cmd,
                                                final org.efaps.admin.ui.Table _table,
                                                final Map<String, String> properties,
                                                final String oid)
        throws EFapsException
    {
        final var types = evalTypes(properties);
        final var fields = getFields(_table);
        final var typeNames = types.stream().map(Type::getName).toArray(String[]::new);
        final var query = EQL.builder()
                        .print()
                        .query(typeNames);

        if (properties.containsKey("LinkFrom") && Instance.get(oid).isValid()) {
            query.where().attribute(properties.get("LinkFrom")).eq(Instance.get(oid));
        }

        if (properties.containsKey("InstanceSelect")) {
            LOG.error("Cmd uses unsuported InstanceSelect: {}", _cmd);
            return Collections.emptyList();
        }

        final var print = query.select();

        if (_cmd.isTargetShowCheckBoxes() || fields.stream().anyMatch(field -> field.getReference() != null)) {
            print.oid().as("OID");
        }
        for (final var field : fields) {
            if (field.getAttribute() != null) {
                add2Select4Attribute(print, field, types);
            } else if (field.getSelect() != null) {
                var select = field.getSelect();
                if (field.getSelect().equals("attribute[Status]")) {
                    select = "status.label";
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

}

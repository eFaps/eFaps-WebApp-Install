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
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.efaps.admin.datamodel.Attribute;
import org.efaps.admin.datamodel.Type;
import org.efaps.admin.datamodel.attributetype.StatusType;
import org.efaps.admin.dbproperty.DBProperties;
import org.efaps.admin.event.EventType;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.AbstractCommand;
import org.efaps.admin.ui.AbstractUserInterfaceObject.TargetMode;
import org.efaps.admin.ui.Command;
import org.efaps.admin.ui.Menu;
import org.efaps.admin.ui.field.Field;
import org.efaps.eql.EQL;
import org.efaps.eql.builder.Print;
import org.efaps.esjp.common.properties.PropertiesUtil;
import org.efaps.esjp.ui.rest.dto.ColumnDto;
import org.efaps.esjp.ui.rest.dto.TableDto;
import org.efaps.util.EFapsException;
import org.efaps.util.UUIDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EFapsUUID("708d3d0d-e230-44e1-99f4-aadb45f70be9")
@EFapsApplication("eFaps-WebApp")
public abstract class TableController_Base
{

    /**
     * Logging instance used in this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(TableController.class);

    public Response getTable(final String _id)
        throws EFapsException
    {
        AbstractCommand cmd = Command.get(UUID.fromString(_id));
        if (cmd == null) {
            cmd = Menu.get(UUID.fromString(_id));
        }
        final var table = cmd.getTargetTable();
        LOG.info("Get TABLE {} ", table);
        cmd.executeEvents(EventType.UI_TABLE_EVALUATE,
                        ParameterValues.OTHERS, null);

        final var properties = cmd.getEvents(EventType.UI_TABLE_EVALUATE).get(0).getPropertyMap();
        final var typeList = evalTypes(properties);
        final var types = typeList.stream().map(Type::getName).toArray(String[]::new);

        final var fields = getFields(table);

        final var print = EQL.builder()
                        .print()
                        .query(types)
                        .select();
        for (final var field : fields) {
            if (field.getAttribute() != null) {
                add2Select4Attribute(print, field, typeList);
            } else if (field.getSelect() != null) {
                print.select(field.getSelect()).as(field.getName());
            } else if (field.getMsgPhrase() != null) {
                print.msgPhrase(getBaseSelect4MsgPhrase(field), field.getMsgPhrase()).as(field.getName());
            }
        }
        final var values = print.evaluate().getData();

        final var dto = TableDto.builder()
                        .withHeader(getHeader(cmd))
                        .withColumns(getColumns(fields))
                        .withValues(values)
                        .build();

        final Response ret = Response.ok()
                        .entity(dto)
                        .build();
        return ret;
    }

    protected String getBaseSelect4MsgPhrase(final Field _field)
    {
        String ret = "";
        if (_field.getSelectAlternateOID() != null) {
            ret = StringUtils.removeEnd(_field.getSelectAlternateOID(), ".oid");
        }
        return ret;
    }

    protected List<Field> getFields(final org.efaps.admin.ui.Table _table) {
        return _table.getFields().stream().filter(field ->  {
            try {
                return field.hasAccess(TargetMode.VIEW, null) && !field.isNoneDisplay(TargetMode.VIEW);
            } catch (final EFapsException e) {
                LOG.error("Catched error while evaluation access for Field: {}", field);
            }
            return false;
        }).collect(Collectors.toList());
    }

    protected void add2Select4Attribute(final Print _print, final Field _field, final List<Type> _types)
    {
        Attribute attr = null;
        for (final var type : _types) {
            attr = type.getAttribute(_field.getAttribute());
            if (attr != null) {
                break;
            }
        }
        if (attr == null) {
            _print.attribute(_field.getAttribute()).as(_field.getName());
        } else if (attr.getAttributeType().getDbAttrType() instanceof StatusType) {
            _print.select("status.label").as(_field.getName());
        } else {
            _print.attribute(_field.getAttribute()).as(_field.getName());
        }
    }

    protected String getHeader(final AbstractCommand _cmd)
    {
        final var key = _cmd.getTargetTitle() == null
                        ? _cmd.getName() + ".Title"
                        : _cmd.getTargetTitle();
        return DBProperties.getProperty(key);
    }

    protected List<ColumnDto> getColumns(final List<Field> _fields)
    {
        final var ret = new ArrayList<ColumnDto>();
        for (final var field : _fields) {
            ret.add(ColumnDto.builder()
                            .withField(field.getName())
                            .withHeader(field.getLabel() == null ? "" : DBProperties.getProperty(field.getLabel()))
                            .build());
        }
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
            if (expandChildTypes.containsKey(0) && Boolean.parseBoolean(expandChildTypes.get(0))
                            || expandChildTypes.containsKey(typeEntry.getKey())
                                            && Boolean.parseBoolean(expandChildTypes.get(typeEntry.getKey()))

            ) {
                type.getChildTypes().forEach(at -> typeList.add(at));
            }
        }
        return typeList;
    }
}

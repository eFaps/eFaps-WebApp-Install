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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.efaps.admin.datamodel.Type;
import org.efaps.admin.dbproperty.DBProperties;
import org.efaps.admin.event.EventType;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.AbstractCommand;
import org.efaps.admin.ui.AbstractUserInterfaceObject;
import org.efaps.admin.ui.AbstractUserInterfaceObject.TargetMode;
import org.efaps.admin.ui.Command;
import org.efaps.admin.ui.Menu;
import org.efaps.admin.ui.field.Field;
import org.efaps.admin.ui.field.FieldGroup;
import org.efaps.eql.EQL;
import org.efaps.esjp.common.properties.PropertiesUtil;
import org.efaps.esjp.ui.rest.dto.FormSectionDto;
import org.efaps.esjp.ui.rest.dto.SearchDto;
import org.efaps.esjp.ui.rest.dto.TableDto;
import org.efaps.esjp.ui.rest.dto.ValueDto;
import org.efaps.esjp.ui.rest.dto.ValueType;
import org.efaps.util.EFapsException;
import org.efaps.util.UUIDUtil;
import org.efaps.util.cache.CacheReloadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EFapsUUID("b8efbce5-c5cf-495d-855d-aff75f171f9b")
@EFapsApplication("eFaps-WebApp")
public abstract class SearchController_Base
    extends AbstractController
{
    private static final Logger LOG = LoggerFactory.getLogger(SearchController.class);

    public Response getSearch(final String _cmdId)
        throws EFapsException
    {

        final var dtos = new ArrayList<SearchDto>();

        final var cmd = Command.get(UUID.fromString(_cmdId));
        final var search = cmd.getTargetSearch();
        final var defaultCmd = search.getDefaultCommand();
        for (final var child : search.getCommands()) {
            final var children = new ArrayList<SearchDto>();
            FormSectionDto section;
            if (child instanceof Menu) {
                section = null;
                for (final var ele : ((Menu) child).getCommands()) {
                    children.add(SearchDto.builder()
                                    .withId(ele.getUUID().toString())
                                    .withLabel(ele.getLabelProperty())
                                    .withSelected(ele.equals(defaultCmd))
                                    .withFormSection(evalSection(ele))
                                    .build());
                }
            } else {
                section = evalSection(child);
            }
            dtos.add(SearchDto.builder()
                            .withId(child.getUUID().toString())
                            .withLabel(child.getLabelProperty())
                            .withChildren(children)
                            .withSelected(child.equals(defaultCmd))
                            .withFormSection(section)
                            .build());
        }
        return Response.ok()
                        .entity(dtos)
                        .build();
    }

    protected FormSectionDto evalSection(final AbstractCommand _cmd)
        throws EFapsException
    {
        final var form = _cmd.getTargetForm();
        var groupCount = 0;
        final var items = new ArrayList<Object>();
        var currentValues = new ArrayList<ValueDto>();
        for (final var field : form.getFields()) {
            if (field.hasAccess(TargetMode.SEARCH, null, _cmd, null)
                            && !field.isNoneDisplay(TargetMode.SEARCH)) {
                if (field instanceof FieldGroup) {
                    final FieldGroup group = (FieldGroup) field;
                    groupCount = group.getGroupCount();
                } else {
                    if (groupCount > 0) {
                        groupCount--;
                    }
                    currentValues.add(ValueDto.builder()
                                    .withName(field.getName())
                                    .withLabel(getLabel(_cmd, field))
                                    .withType(ValueType.INPUT)
                                    .build());
                    if (groupCount < 1) {
                        items.add(currentValues.size() == 1 ?  currentValues.get(0) : currentValues);
                        currentValues = new ArrayList<ValueDto>();
                    }
                }
            }
        }
        return FormSectionDto.builder()
                        .withItems(items)
                        .build();
    }

    private String getLabel(final AbstractCommand _cmd, final Field _field)
        throws CacheReloadException
    {
        String ret = null;
        if (_field.getLabel() != null) {
            ret = DBProperties.getProperty(_field.getLabel());
        } else if (_field.getAttribute() != null) {
            if (_cmd.hasEvents(EventType.UI_TABLE_EVALUATE)) {
                final var typeOpt = _cmd.getEvents(EventType.UI_TABLE_EVALUATE).stream().map(eventDef -> {
                    return eventDef.getProperty("Type");
                }).filter(Objects::nonNull).findFirst();
                if (typeOpt.isPresent()) {
                    final var attr = Type.get(typeOpt.get()).getAttribute(_field.getAttribute());
                    if (attr != null) {
                        ret = DBProperties.getProperty(attr.getLabelKey());
                    }
                }
            }
        }
        return ret;
    }

    public Response search(final String _cmdId, final UriInfo _uriInfo)
        throws EFapsException
    {
        final var cmd = Command.get(UUID.fromString(_cmdId));
        final var table = cmd.getTargetTable();

        final MultivaluedMap<String, String> queryParameters = _uriInfo.getQueryParameters();
        LOG.debug("", queryParameters);
        final var dto = TableDto.builder()
                        .withHeader(getHeader(cmd))
                        .withColumns(getColumns(table, TargetMode.SEARCH, null))
                        .withValues(getValues(cmd, table))
                        .build();

        final Response ret = Response.ok()
                        .entity(dto)
                        .build();
        return ret;
    }


    public Collection<Map<String, ?>> getValues(final AbstractUserInterfaceObject _cmd, final org.efaps.admin.ui.Table _table)
        throws EFapsException
    {
        final var fields = getFields(_table);
        final var typeList = evalTypes(_cmd);
        final var types = typeList.stream().map(Type::getName).toArray(String[]::new);
        final var query = EQL.builder()
                        .print()
                        .query(types);

        final var print = query.select();

        if (fields.stream().anyMatch(field -> field.getReference() != null)) {
            print.oid().as("OID");
        }
        for (final var field : fields) {
            if (field.getAttribute() != null) {
                add2Select4Attribute(print, field, typeList);
            } else if (field.getSelect() != null) {
                print.select(field.getSelect()).as(field.getName());
            } else if (field.getMsgPhrase() != null) {
                print.msgPhrase(getBaseSelect4MsgPhrase(field), field.getMsgPhrase()).as(field.getName());
            }
            if (field.getSelectAlternateOID() != null) {
                print.select(field.getSelectAlternateOID()).as(field.getName() + "_AOID");
            }
        }
        return print.evaluate().getData();
    }

    protected List<Type> evalTypes(final AbstractUserInterfaceObject _cmd)
        throws EFapsException
    {
        final var propertiesMap = _cmd.getEvents(EventType.UI_TABLE_EVALUATE).get(0).getPropertyMap();
        final var typeList = new ArrayList<Type>();
        final var properties = new Properties();
        properties.putAll(propertiesMap);
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
                                            && Boolean.parseBoolean(expandChildTypes.get(typeEntry.getKey()))) {
                type.getChildTypes().forEach(at -> typeList.add(at));
            }
        }
        return typeList;
    }

}

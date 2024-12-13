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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.efaps.admin.datamodel.Classification;
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
import org.efaps.eql2.impl.AttributeSelectElement;
import org.efaps.eql2.impl.ClassSelectElement;
import org.efaps.esjp.common.properties.PropertiesUtil;
import org.efaps.esjp.ui.rest.dto.FormSectionDto;
import org.efaps.esjp.ui.rest.dto.PageDto;
import org.efaps.esjp.ui.rest.dto.SearchDto;
import org.efaps.esjp.ui.rest.dto.TableDto;
import org.efaps.esjp.ui.rest.dto.ValueDto;
import org.efaps.esjp.ui.rest.dto.ValueType;
import org.efaps.esjp.ui.util.LabelUtils;
import org.efaps.esjp.ui.util.WebApp;
import org.efaps.util.EFapsException;
import org.efaps.util.UUIDUtil;
import org.efaps.util.cache.CacheReloadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

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
        final var items = new ArrayList<>();
        var currentValues = new ArrayList<ValueDto>();
        for (final var field : form.getFields()) {
            if (field.hasAccess(TargetMode.SEARCH, null, _cmd, null)
                            && !field.isNoneDisplay(TargetMode.SEARCH)) {
                if (field instanceof final FieldGroup group) {
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
                        items.add(currentValues.size() == 1 ? currentValues.get(0) : currentValues);
                        currentValues = new ArrayList<>();
                    }
                }
            }
        }
        return FormSectionDto.builder()
                        .withItems(items)
                        .build();
    }

    private String getLabel(final AbstractCommand _cmd,
                            final Field _field)
        throws CacheReloadException
    {
        String ret = null;
        if (_field.getLabel() != null) {
            ret = DBProperties.getProperty(_field.getLabel());
        } else if (_field.getAttribute() != null) {
            if (_cmd.hasEvents(EventType.UI_TABLE_EVALUATE)) {
                final var typeOpt = _cmd.getEvents(EventType.UI_TABLE_EVALUATE).stream()
                                .map(eventDef -> eventDef.getProperty("Type")).filter(Objects::nonNull).findFirst();
                if (typeOpt.isPresent()) {
                    final var labelOpt = LabelUtils.evalForTypeAndAttribute(Type.get(typeOpt.get()), _field.getAttribute());
                    if (labelOpt.isPresent()) {
                        ret = labelOpt.get();
                    }
                }
            }
        }
        if (ret == null && _field.getSelect() != null) {
            final var select = EQL.parseSelect(_field.getSelect());
            if (select.getElements(0) instanceof ClassSelectElement) {
                final var classificationName = ((ClassSelectElement) select.getElements(0)).getName();
                final var classification = Classification.get(classificationName);
                if (classification != null) {
                    final var attrSelectEle = select.getElementsList().stream()
                                    .filter(element -> (element instanceof AttributeSelectElement)).findFirst();
                    if (attrSelectEle.isPresent()) {
                        final var attrName = ((AttributeSelectElement) attrSelectEle.get()).getName();
                        ret = LabelUtils.evalForTypeAndAttribute(classification, attrName).orElse("");
                    }
                }
            }
            LOG.warn("Some other label might be evaluated");
        }
        return ret;
    }

    public Response search(final String _cmdId,
                           final UriInfo _uriInfo)
        throws EFapsException
    {
        final var cmd = Command.get(UUID.fromString(_cmdId));
        final var table = cmd.getTargetTable();

        final MultivaluedMap<String, String> queryParameters = _uriInfo.getQueryParameters();
        LOG.debug("", queryParameters);

        final String selectionMode = cmd.isTargetShowCheckBoxes()
                        ? cmd.getSubmitSelectedRows() == 1 ? "single" : "multiple"
                        : null;

        final var searchConfig = WebApp.SEARCH.get();
        final var cmds = PropertiesUtil.analyseProperty(searchConfig, "cmd", 0).values();
        final var key = cmds.stream().filter(
                        cmdkey -> (cmdkey.equals(cmd.getName()) || cmd.getUUID() != null
                                        && cmdkey.equals(cmd.getUUID().toString())))
                        .findFirst();
        String prefix;
        if (key.isPresent()) {
            prefix = key.get();
        } else {
            prefix = "default";
        }
        final var searchProperties = PropertiesUtil.getProperties4Prefix(searchConfig, prefix, true);
        final var limit = Integer.valueOf(searchProperties.getProperty("limit", "1000"));

        final var dto = TableDto.builder()
                        .withHeader(getHeader(cmd, null))
                        .withColumns(getColumns(table, TargetMode.SEARCH, null))
                        .withValues(getValues(cmd, table, queryParameters, limit))
                        .withSelectionMode(selectionMode)
                        .withPage(PageDto.builder()
                                        .withPageSize(limit).build())
                        .build();

        final Response ret = Response.ok()
                        .entity(dto)
                        .build();
        return ret;
    }

    public Collection<Map<String, ?>> getValues(final AbstractUserInterfaceObject uiObj,
                                                final org.efaps.admin.ui.Table _table,
                                                final MultivaluedMap<String, String> queryParameters,
                                                final int limit)
        throws EFapsException
    {
        final var fields = getFields(_table);
        final var typeList = evalTypes(uiObj);
        final var types = typeList.stream().map(Type::getName).toArray(String[]::new);
        final var query = EQL.builder()
                        .print()
                        .query(types);

        for (final var entry : queryParameters.entrySet()) {
            final var field = fields.stream().filter(f -> f.getName().equals(entry.getKey())).findFirst();
            if (field.isPresent()) {
                if (field.get().getAttribute() != null) {
                    query.where().attribute(field.get().getAttribute()).ilike("%" + entry.getValue().get(0) + "%");
                }
            }
        }



        final var print = query.select();
        print.limit(limit);
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

    protected Set<Type> evalTypes(final AbstractUserInterfaceObject _cmd)
        throws EFapsException
    {
        final var propertiesMap = _cmd.getEvents(EventType.UI_TABLE_EVALUATE).get(0).getPropertyMap();
        final var typeList = new HashSet<Type>();
        final var excluded = new HashSet<Type>();
        final var properties = new Properties();
        properties.putAll(propertiesMap);
        final var types = PropertiesUtil.analyseProperty(properties, "Type", 0);
        final var expandChildTypes = PropertiesUtil.analyseProperty(properties, "ExpandChildTypes", 0);

        for (final var typeEntry : types.entrySet()) {
            var typeStr = typeEntry.getValue();
            boolean exclude = false;
            if (typeStr.startsWith("!")) {
                typeStr = typeStr.substring(1);
                exclude = true;
            }
            Type type;
            if (UUIDUtil.isUUID(typeStr)) {
                type = Type.get(UUID.fromString(typeEntry.getValue()));
            } else {
                type = Type.get(typeStr);
            }
            if (exclude) {
                excluded.add(type);
            } else {
                typeList.add(type);
            }
            if (expandChildTypes.containsKey(0) && Boolean.parseBoolean(expandChildTypes.get(0))
                            || expandChildTypes.containsKey(typeEntry.getKey())
                                            && Boolean.parseBoolean(expandChildTypes.get(typeEntry.getKey()))) {
                for (final var childType : type.getChildTypes()) {
                    if (exclude) {
                        excluded.add(childType);
                    } else {
                        typeList.add(childType);
                    }
                }

            }
        }
        typeList.removeAll(excluded);
        return typeList;
    }

}

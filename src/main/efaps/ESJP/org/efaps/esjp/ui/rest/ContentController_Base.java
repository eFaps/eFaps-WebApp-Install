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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response;

import org.apache.wicket.RestartResponseException;
import org.efaps.admin.datamodel.IEnum;
import org.efaps.admin.datamodel.Status;
import org.efaps.admin.datamodel.Type;
import org.efaps.admin.dbproperty.DBProperties;
import org.efaps.admin.event.EventType;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsClassLoader;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.AbstractCommand;
import org.efaps.admin.ui.AbstractUserInterfaceObject;
import org.efaps.admin.ui.AbstractUserInterfaceObject.TargetMode;
import org.efaps.admin.ui.Command;
import org.efaps.admin.ui.Menu;
import org.efaps.admin.ui.field.Field;
import org.efaps.admin.ui.field.FieldClassification;
import org.efaps.admin.ui.field.FieldGroup;
import org.efaps.admin.ui.field.FieldHeading;
import org.efaps.admin.ui.field.FieldSet;
import org.efaps.admin.ui.field.FieldTable;
import org.efaps.api.ui.UIType;
import org.efaps.beans.ValueList;
import org.efaps.beans.valueparser.ParseException;
import org.efaps.beans.valueparser.ValueParser;
import org.efaps.db.Instance;
import org.efaps.db.PrintQuery;
import org.efaps.eql.EQL;
import org.efaps.esjp.common.properties.PropertiesUtil;
import org.efaps.esjp.db.InstanceUtils;
import org.efaps.esjp.ui.rest.dto.ActionDto;
import org.efaps.esjp.ui.rest.dto.ActionType;
import org.efaps.esjp.ui.rest.dto.ContentDto;
import org.efaps.esjp.ui.rest.dto.FormSectionDto;
import org.efaps.esjp.ui.rest.dto.HeaderSectionDto;
import org.efaps.esjp.ui.rest.dto.ISection;
import org.efaps.esjp.ui.rest.dto.NavItemDto;
import org.efaps.esjp.ui.rest.dto.OptionDto;
import org.efaps.esjp.ui.rest.dto.OutlineDto;
import org.efaps.esjp.ui.rest.dto.SectionType;
import org.efaps.esjp.ui.rest.dto.TableSectionDto;
import org.efaps.esjp.ui.rest.dto.ValueDto;
import org.efaps.esjp.ui.rest.dto.ValueType;
import org.efaps.ui.wicket.pages.error.ErrorPage;
import org.efaps.util.EFapsException;
import org.efaps.util.UUIDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EFapsUUID("da1c680f-8219-4a93-ab64-d6dbd261dc56")
@EFapsApplication("eFaps-WebApp")
public abstract class ContentController_Base
    extends AbstractController
{
    private static final Logger LOG = LoggerFactory.getLogger(ContentController.class);

    public Response getContent(final String _oid)
        throws EFapsException
    {
        ContentDto dto = null;
        final var instance = Instance.get(_oid);
        if (instance.isValid()) {

            final var typeMenu = instance.getType().getTypeMenu();
            final var defaultSelected = typeMenu.getCommands().stream().filter(cmd -> cmd.isDefaultSelected()).findFirst();
            final var currentCmd = defaultSelected.isPresent() ? defaultSelected.get() : typeMenu;

            final var targetMenu = currentCmd.getTargetMenu();
            final List<NavItemDto> menus = targetMenu == null ? null : new NavItemEvaluator().getMenu(targetMenu);
            final var header = getLabel(instance, currentCmd.getTargetTitle());

            final List<NavItemDto> navItems = new ArrayList<>();
            navItems.add(NavItemDto.builder()
                            .withId(typeMenu.getUUID().toString())
                            .withLabel(getLabel(instance, typeMenu.getLabel()))
                            .withAction(ActionDto.builder()
                                            .withType(ActionType.GRID)
                                            .build())
                            .build());
            for (final var command : typeMenu.getCommands()) {
                ActionType actionType = null;
                if (command.getTargetTable() != null) {
                    actionType = ActionType.GRID;
                }
                navItems.add(NavItemDto.builder()
                                .withId(command.getUUID().toString())
                                .withLabel(command.getLabelProperty())
                                .withAction(ActionDto.builder()
                                                .withType(actionType)
                                                .build())
                                .build());
            }
            final var outline = OutlineDto.builder()
                            .withOid(_oid)
                            .withMenu(menus)
                            .withHeader(header)
                            .withSections(evalSections(instance, currentCmd))
                            .build();
            dto = ContentDto.builder()
                            .withOutline(outline)
                            .withNav(navItems)
                            .withSelected(currentCmd.getUUID().toString())
                            .build();
        }
        return Response.ok()
                        .entity(dto)
                        .build();
    }

    @SuppressWarnings("unchecked")
    public List<ISection> evalSections(final Instance _instance, final AbstractCommand _cmd)
        throws EFapsException
    {
        final var ret = new ArrayList<ISection>();
        final var sections = new ArrayList<Object>();
        final var targetMode = TargetMode.UNKNOWN.equals(_cmd.getTargetMode()) ? TargetMode.VIEW : _cmd.getTargetMode();

        final Instance sectionInstance;
        if (TargetMode.CREATE.equals(targetMode) && _cmd.getTargetCreateType() != null) {
            sectionInstance = Instance.get(_cmd.getTargetCreateType(), null);
        } else {
            sectionInstance = _instance;
        }

        final var form = _cmd.getTargetForm();
        final var table = _cmd.getTargetTable();
        if (form != null) {
            boolean executable = false;
            final var print = EQL.builder().print(sectionInstance);
            for (final Field field : form.getFields()) {
                if (sectionInstance.isValid() && !field.isNoneDisplay(targetMode)
                                && field.hasAccess(targetMode, sectionInstance, _cmd, sectionInstance)) {
                    if (field instanceof FieldSet) {
                        LOG.debug("FieldSet {}", field);
                    } else {
                        if (field.getSelect() != null) {
                            print.select(field.getSelect()).as(field.getName());
                            executable = true;
                        } else if (field.getAttribute() != null) {
                            if (TargetMode.VIEW.equals(targetMode)) {
                                add2Select4Attribute(print, field, Collections.singletonList(_instance.getType()));
                            } else {
                                print.attribute(field.getAttribute()).as(field.getName());
                            }
                            executable = true;
                        } else if (field.getPhrase() != null) {
                            // print.addPhrase(field.getName(),
                            // field.getPhrase());
                        } else if (field.getMsgPhrase() != null) {
                            print.msgPhrase(getBaseSelect4MsgPhrase(field), field.getMsgPhrase()).as(field.getName())
                                            .as(field.getName());
                            executable = true;
                        }
                    }
                    if (field.getSelectAlternateOID() != null) {
                        print.select(field.getSelectAlternateOID()).as(field.getName() + "_AOID");
                        executable = true;
                    }
                }
            }
            final var eval = executable ? print.execute().evaluate() : null;

            FormSection currentSection = null;
            var groupCount = 0;
            var currentValues = new ArrayList<ValueDto>();
            for (final Field field : form.getFields()) {
                if (!field.isNoneDisplay(targetMode)
                                && field.hasAccess(targetMode, sectionInstance, _cmd, sectionInstance)) {
                    if (field instanceof FieldGroup) {
                        final FieldGroup group = (FieldGroup) field;
                        groupCount = group.getGroupCount();
                    } else if (field instanceof FieldTable) {
                        currentSection = null;
                        final var fieldTable = ((FieldTable) field).getTargetTable();
                        final var columns = getColumns(fieldTable);
                        sections.add(TableSectionDto.builder()
                                        .withColumns(columns)
                                        .withValues(getValues(field, fieldTable, sectionInstance))
                                        .build());
                    } else if (field instanceof FieldHeading) {
                        sections.add(HeaderSectionDto.builder()
                                        .withHeader(DBProperties.getProperty(field.getLabel()))
                                        .withLevel(((FieldHeading) field).getLevel())
                                        .build());
                    } else if (field instanceof FieldClassification) {
                        LOG.info("FieldClassification {}", field);
                    } else {
                        if (currentSection == null) {
                            currentSection = new FormSection();
                            currentSection.type = SectionType.FORM;
                            sections.add(currentSection);
                        }
                        if (groupCount > 0) {
                            groupCount--;
                        }
                        final var valueBldr = ValueDto.builder();

                        var valueType = ValueType.READ_ONLY;
                        var fieldValue = executable ? eval.get(field.getName()) : null;
                        final UIType uiType = getUIType(field);
                        if (UIType.SNIPPLET.equals(uiType)) {
                            fieldValue = getSnipplet(sectionInstance, field);
                            valueType = ValueType.SNIPPLET;
                        } else if (UIType.UPLOAD.equals(uiType))  {
                            valueType = ValueType.UPLOAD;
                        } else if (UIType.UPLOADMULTIPLE.equals(uiType))  {
                            valueType = ValueType.UPLOADMULTIPLE;
                        } else if ((TargetMode.CREATE.equals(targetMode) || TargetMode.EDIT.equals(targetMode))
                                        && field.isEditableDisplay(targetMode)) {
                            final var attr = sectionInstance.getType().getAttribute(field.getAttribute());
                            if (attr != null) {
                                final var attrType = attr.getAttributeType();
                                switch (attrType.getName()) {
                                    case "Enum":
                                        valueType = ValueType.ENUM;
                                        try {
                                            final Class<?> clazz = Class.forName(attr.getClassName(), false,
                                                            EFapsClassLoader.getInstance());
                                            valueBldr.withOptions(Arrays.asList(clazz.getEnumConstants()).stream()
                                                .map(ienum -> {
                                                    return OptionDto.builder()
                                                                .withValue(((IEnum) ienum).getInt())
                                                                .withLabel(getEnumLabel((IEnum) ienum))
                                                                .build();
                                                }).collect(Collectors.toList()));
                                        } catch (final ClassNotFoundException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                        if (TargetMode.EDIT.equals(targetMode) && fieldValue instanceof IEnum) {
                                            fieldValue = ((IEnum) fieldValue).getInt();
                                        }
                                        break;
                                    case "Status":
                                        valueType = ValueType.STATUS;
                                        final var statusType = attr.getLink();
                                        valueBldr.withOptions(Status.get(statusType.getUUID()).values().stream()
                                            .map(status -> {
                                                return OptionDto.builder()
                                                            .withValue(status.getId())
                                                            .withLabel(status.getLabel())
                                                            .build();
                                            }).collect(Collectors.toList()));
                                        break;
                                    default:
                                        valueType = ValueType.INPUT;
                                        break;
                                }
                            } else {
                                valueType = ValueType.INPUT;
                            }
                        }
                        final var value = valueBldr.withLabel(getLabel(sectionInstance, field))
                                        .withName(field.getName())
                                        .withValue(fieldValue)
                                        .withType(valueType)
                                        .build();
                        currentValues.add(value);
                        if (groupCount < 1) {
                            currentSection.addValue(currentValues);
                            currentValues = new ArrayList<ValueDto>();
                        }
                    }
                }

            }
        }
        sections.stream().forEach(section -> {
            if (section instanceof FormSection) {
                ret.add(FormSectionDto.builder()
                                .withItems(((FormSection) section).values)
                                .build());
            } else {
                ret.add((ISection) section);
            }
        });

        if (table != null) {
            final var columns = getColumns(table);
            ret.add(TableSectionDto.builder()
                            .withColumns(columns)
                            .withValues(getValues(_cmd, table, _instance))
                            .build());
        }
        return ret;
    }

    private String getLabel(final Instance _instance, final Field _field)
    {
        String ret = null;
        if (_field.getLabel() != null) {
            ret = DBProperties.getProperty(_field.getLabel());
        } else if (_field.getAttribute() != null) {
            final var attr = _instance.getType().getAttribute(_field.getAttribute());
            if (attr != null) {
                ret = DBProperties.getProperty(attr.getLabelKey());
            }
        }
        return ret;
    }

    private class FormSection
    {

        SectionType type;
        List<Object> values = new ArrayList<>();

        public void addValue(final List<ValueDto> _value)
        {
            if (_value.size() == 1) {
                values.add(_value.get(0));
            } else {
                values.add(_value);
            }
        }
    }

    public String getLabel(final Instance _instance, final String _propertyKey)
    {
        String ret = "";
        try {
            ret = DBProperties.getProperty(_propertyKey);
            final ValueParser parser = new ValueParser(new StringReader(ret));
            final ValueList list = parser.ExpressionString();
            if (InstanceUtils.isValid(_instance) && list.getExpressions().size() > 0) {
                final PrintQuery print = new PrintQuery(_instance);
                list.makeSelect(print);
                if (print.execute()) {
                    ret = list.makeString(_instance, print, TargetMode.VIEW);
                }
            }
        } catch (final EFapsException e) {
            throw new RestartResponseException(new ErrorPage(e));
        } catch (final ParseException e) {
            throw new RestartResponseException(new ErrorPage(e));
        }
        return ret;
    }

    public Response getContent(final String _oid, final String _cmdId)
        throws EFapsException
    {
        OutlineDto dto = null;
        final var instance = Instance.get(_oid);
        AbstractCommand cmd = Command.get(UUID.fromString(_cmdId));
        if (cmd == null) {
            cmd = Menu.get(UUID.fromString(_cmdId));
        }
        if (instance.isValid() || cmd.getTargetMode().equals(TargetMode.CREATE)) {
            final var targetMenu = cmd.getTargetMenu();
            final List<NavItemDto> menus = targetMenu == null ? null : new NavItemEvaluator().getMenu(targetMenu);
            final var header = getLabel(instance, cmd.getTargetTitle());
            ActionDto action = null;
            if (cmd.getTargetMode().equals(TargetMode.CREATE) && cmd.hasEvents(EventType.UI_COMMAND_EXECUTE)) {
                action = ActionDto.builder()
                                .withLabel(DBProperties.getProperty("default.Button.Create"))
                                .build();
            } else if (cmd.getTargetMode().equals(TargetMode.EDIT) && cmd.hasEvents(EventType.UI_COMMAND_EXECUTE)) {
                action = ActionDto.builder()
                                .withLabel(DBProperties.getProperty("default.Button.Edit"))
                                .build();
            }

            dto = OutlineDto.builder()
                            .withOid(_oid)
                            .withMenu(menus)
                            .withHeader(header)
                            .withSections(evalSections(instance, cmd))
                            .withAction(action)
                            .build();
        }
        return Response.ok()
                        .entity(dto)
                        .build();
    }

    public Collection<Map<String, ?>> getValues(final AbstractUserInterfaceObject _cmd, final org.efaps.admin.ui.Table _table,
                                                final Instance _instance)
        throws EFapsException
    {
        final var fields = getFields(_table);
        final var typeList = evalTypes(_cmd);
        final var types = typeList.stream().map(Type::getName).toArray(String[]::new);

        final var propertiesMap = _cmd.getEvents(EventType.UI_TABLE_EVALUATE).get(0).getPropertyMap();
        if (propertiesMap.containsKey("InstanceSelect")) {
            LOG.warn("doe not work with InstanceSelect yet..");
        }

        final var query = EQL.builder()
                        .print()
                        .query(types);

        if (propertiesMap.containsKey("LinkFrom")) {
            final var linkfromAttr = propertiesMap.get("LinkFrom");
            query.where().attr(linkfromAttr).eq(_instance);
        }
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

    private CharSequence getSnipplet(final Instance _instance, final Field _field)
        throws EFapsException
    {
        CharSequence ret = null;
        if (_field.hasEvents(EventType.UI_FIELD_VALUE)) {
            final var values = _field.executeEvents(EventType.UI_FIELD_VALUE, ParameterValues.INSTANCE, _instance);
            for (final var entry : values) {
                ret = (CharSequence) entry.get(ReturnValues.SNIPLETT);
            }
        }
        return ret;
    }

    public static String getEnumLabel(final IEnum _enum)
    {
        String ret = null;
        if (_enum != null) {
            final String key;
            if (_enum.getClass().isEnum()) {
                key = _enum.getClass().getName() + "." + _enum.toString();
            } else {
                key = _enum.getClass().getName();
            }
            ret = DBProperties.getProperty(key, false);
            if (ret == null) {
                ret = _enum.toString();
            }
        }
        return ret;
    }

}

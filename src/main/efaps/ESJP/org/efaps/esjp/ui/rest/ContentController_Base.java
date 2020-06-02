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
import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.Response;

import org.apache.wicket.RestartResponseException;
import org.efaps.admin.dbproperty.DBProperties;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.AbstractCommand;
import org.efaps.admin.ui.AbstractUserInterfaceObject.TargetMode;
import org.efaps.admin.ui.Command;
import org.efaps.admin.ui.field.Field;
import org.efaps.admin.ui.field.FieldClassification;
import org.efaps.admin.ui.field.FieldGroup;
import org.efaps.admin.ui.field.FieldHeading;
import org.efaps.admin.ui.field.FieldTable;
import org.efaps.beans.ValueList;
import org.efaps.beans.valueparser.ParseException;
import org.efaps.beans.valueparser.ValueParser;
import org.efaps.db.Instance;
import org.efaps.db.PrintQuery;
import org.efaps.eql.EQL;
import org.efaps.esjp.ui.rest.dto.ActionDto;
import org.efaps.esjp.ui.rest.dto.ActionType;
import org.efaps.esjp.ui.rest.dto.ContentDto;
import org.efaps.esjp.ui.rest.dto.NavItemDto;
import org.efaps.esjp.ui.rest.dto.OutlineDto;
import org.efaps.esjp.ui.rest.dto.SectionDto;
import org.efaps.esjp.ui.rest.dto.SectionType;
import org.efaps.esjp.ui.rest.dto.ValueDto;
import org.efaps.esjp.ui.rest.dto.ValueType;
import org.efaps.ui.wicket.pages.error.ErrorPage;
import org.efaps.util.EFapsException;

@EFapsUUID("da1c680f-8219-4a93-ab64-d6dbd261dc56")
@EFapsApplication("eFaps-WebApp")
public abstract class ContentController_Base
    extends AbstractController
{

    public Response getContent(final String _oid)
        throws EFapsException
    {
        ContentDto dto = null;
        final var instance = Instance.get(_oid);
        if (instance.isValid()) {

            final var menu = instance.getType().getTypeMenu();
            final var defaultSelected = menu.getCommands().stream().filter(cmd -> cmd.isDefaultSelected()).findFirst();
            final var currentCmd = defaultSelected.isPresent() ? defaultSelected.get() : menu;

            final var header = getLabel(instance, currentCmd.getTargetTitle());

            final List<NavItemDto> navItems = new ArrayList<>();
            navItems.add(NavItemDto.builder()
                            .withId(menu.getUUID().toString())
                            .withLabel(getLabel(instance, menu.getLabel()))
                            .withAction(ActionDto.builder()
                                            .withType(ActionType.GRID)
                                            .build())
                            .build());
            for (final var command : menu.getCommands()) {
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

    public List<SectionDto> evalSections(final Instance _instance, final AbstractCommand _cmd)
        throws EFapsException
    {
        final var ret = new ArrayList<SectionDto>();
        final var sections = new ArrayList<Section>();
        final var form = _cmd.getTargetForm();
        if (form != null) {
            final var print = EQL.builder().print(_instance);
            for (final Field field : form.getFields()) {
                if (field.hasAccess(TargetMode.VIEW, _instance, _cmd, _instance)
                                && !field.isNoneDisplay(TargetMode.VIEW)) {
                    if (field.getSelect() != null) {
                        print.select(field.getSelect()).as(field.getName());
                    } else if (field.getAttribute() != null) {
                        print.attribute(field.getAttribute()).as(field.getName());
                    } else if (field.getPhrase() != null) {
                        // print.addPhrase(field.getName(), field.getPhrase());
                    } else if (field.getMsgPhrase() != null) {
                        print.msgPhrase(getBaseSelect4MsgPhrase(field), field.getMsgPhrase()).as(field.getName())
                                        .as(field.getName());
                    }
                    if (field.getSelectAlternateOID() != null) {
                        print.select(field.getSelectAlternateOID()).as(field.getName() + "_AOID");
                    }
                }
            }
            final var eval = print.execute().evaluate();

            Section currentSection = null;
            var groupCount = 0;
            var currentValues = new ArrayList<ValueDto>();
            for (final Field field : form.getFields()) {
                if (field.hasAccess(TargetMode.VIEW, _instance, _cmd, _instance)
                                && !field.isNoneDisplay(TargetMode.VIEW)) {
                    if (field instanceof FieldGroup) {
                        final FieldGroup group = (FieldGroup) field;
                        groupCount = group.getGroupCount();
                    } else if (field instanceof FieldTable) {

                    } else if (field instanceof FieldHeading) {

                    } else if (field instanceof FieldClassification) {

                    } else {
                        if (currentSection == null) {
                            currentSection = new Section();
                            currentSection.type = SectionType.FORM;
                            sections.add(currentSection);
                        }
                        if (groupCount > 0) {
                            groupCount--;
                        }
                        final var fieldValue = eval.get(field.getName());

                        final var value = ValueDto.builder()
                                        .withLabel(getLabel(_instance, field))
                                        .withValue(fieldValue)
                                        .withType(ValueType.READ_ONLY)
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
            ret.add(SectionDto.builder()
                            .withType(section.type)
                            .withItems(section.values)
                            .build());
        });
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

    private class Section {
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
            if (list.getExpressions().size() > 0) {
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
        if (instance.isValid()) {
            final var cmd = Command.get(UUID.fromString(_cmdId));

            final var header = getLabel(instance, cmd.getLabel());
            dto = OutlineDto.builder()
                            .withOid(_oid)
                            .withHeader(header)
                            .build();
        }
        return Response.ok()
                        .entity(dto)
                        .build();
    }

}

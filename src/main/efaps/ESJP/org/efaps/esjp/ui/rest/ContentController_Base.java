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

import java.io.StringReader;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.wicket.RestartResponseException;
import org.efaps.admin.datamodel.Attribute;
import org.efaps.admin.datamodel.AttributeSet;
import org.efaps.admin.datamodel.Classification;
import org.efaps.admin.datamodel.IBitEnum;
import org.efaps.admin.datamodel.IEnum;
import org.efaps.admin.datamodel.Status;
import org.efaps.admin.datamodel.Type;
import org.efaps.admin.datamodel.attributetype.StatusType;
import org.efaps.admin.datamodel.ui.IUIValue;
import org.efaps.admin.dbproperty.DBProperties;
import org.efaps.admin.event.EventType;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.event.Return;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsClassLoader;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.AbstractCommand;
import org.efaps.admin.ui.AbstractUserInterfaceObject;
import org.efaps.admin.ui.AbstractUserInterfaceObject.TargetMode;
import org.efaps.admin.ui.Command;
import org.efaps.admin.ui.Form;
import org.efaps.admin.ui.Menu;
import org.efaps.admin.ui.field.Field;
import org.efaps.admin.ui.field.Field.Display;
import org.efaps.admin.ui.field.FieldClassification;
import org.efaps.admin.ui.field.FieldCommand;
import org.efaps.admin.ui.field.FieldGroup;
import org.efaps.admin.ui.field.FieldHeading;
import org.efaps.admin.ui.field.FieldSet;
import org.efaps.admin.ui.field.FieldTable;
import org.efaps.api.ui.IOption;
import org.efaps.api.ui.UIType;
import org.efaps.beans.ValueList;
import org.efaps.beans.ValueList.Token;
import org.efaps.beans.valueparser.ParseException;
import org.efaps.beans.valueparser.ValueParser;
import org.efaps.db.Context;
import org.efaps.db.Instance;
import org.efaps.db.PrintQuery;
import org.efaps.db.stmt.selection.Evaluator;
import org.efaps.eql.EQL;
import org.efaps.eql.builder.Print;
import org.efaps.eql2.impl.AttributeSelectElement;
import org.efaps.eql2.impl.LinktoSelectElement;
import org.efaps.esjp.common.properties.PropertiesUtil;
import org.efaps.esjp.db.InstanceUtils;
import org.efaps.esjp.ui.rest.dto.ActionDto;
import org.efaps.esjp.ui.rest.dto.ActionType;
import org.efaps.esjp.ui.rest.dto.AttributeSetDto;
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
import org.efaps.util.cache.CacheReloadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@EFapsUUID("da1c680f-8219-4a93-ab64-d6dbd261dc56")
@EFapsApplication("eFaps-WebApp")
public abstract class ContentController_Base
    extends AbstractController
{

    private static final Logger LOG = LoggerFactory.getLogger(ContentController.class);

    protected Instance mainInstance;
    protected AbstractCommand callCmd;
    protected TargetMode currentTargetMode;
    protected List<Classification> classifications;


    public List<ISection> evalSections(final Instance instance,
                                       final AbstractCommand cmd)
        throws EFapsException
    {
        LOG.info("Evaluation sections for  instance: {} and cmd : {}", instance, cmd);

        List<ISection> ret = new ArrayList<>();
        final var targetMode = TargetMode.UNKNOWN.equals(cmd.getTargetMode()) ? TargetMode.VIEW : cmd.getTargetMode();
        currentTargetMode = targetMode;
        mainInstance = instance;
        final Instance sectionInstance;
        if (TargetMode.CREATE.equals(targetMode) && cmd.getTargetCreateType() != null) {
            sectionInstance = Instance.get(cmd.getTargetCreateType(), null);
        } else {
            sectionInstance = instance;
        }

        final var form = cmd.getTargetForm();
        final var table = cmd.getTargetTable();
        if (form != null) {
            final var print = EQL.builder().print(sectionInstance);
            final var executable = evalSelects4Form(cmd, form, print, sectionInstance, null);

            final var eval = executable ? print.evaluate() : null;
            ret = evalSections4Form(form, sectionInstance.getType(), sectionInstance, eval);
            // check for classifications
            final var fieldClassifications = form.getFields().stream()
                            .filter(field -> (field instanceof FieldClassification)).collect(Collectors.toList());
            for (final var fieldClassification : fieldClassifications) {
                ret.addAll(evalSections4Class((FieldClassification) fieldClassification, sectionInstance, eval));
            }
        }
        if (table != null) {
            final var columns = getColumns(table, currentTargetMode, null);
            ret.add(TableSectionDto.builder()
                            .withColumns(columns)
                            .withValues(getValues(cmd, table, instance))
                            .build());
        }
        return ret;
    }

    protected void evalClassifications()
        throws EFapsException
    {
        classifications = new ArrayList<>();
        final var clazzes = mainInstance.getType().getClassifiedByTypes();
        for (final var clazz : clazzes) {
            if (clazz.isRoot()) {
                classifications.addAll(evalClassificationsForInstance(clazz, mainInstance));
            }
        }
    }

    protected List<Classification> getClassifications()
        throws EFapsException
    {
        if (classifications == null) {
            evalClassifications();
        }
        return this.classifications;
    }

    protected List<ISection> evalSections4Class(final FieldClassification field,
                                                final Instance instance,
                                                final Evaluator eval)
        throws EFapsException
    {
        final var ret = new ArrayList<ISection>();
        for (final var classification : getClassifications()) {
            final var form = classification.getTypeForm();
            ret.addAll(evalSections4Form(form, classification, instance, eval));
        }
        ret.sort((arg0,
         arg1) -> {
            if (SectionType.FORM.equals(arg0.getType()) && SectionType.HEADING.equals(arg1.getType())) {
                return -1;
            }
            if (SectionType.HEADING.equals(arg0.getType()) && SectionType.HEADING.equals(arg1.getType())) {
                return ((HeaderSectionDto) arg0).getHeader().compareTo(((HeaderSectionDto) arg1).getHeader());
            }
            return 0;
        });
        return ret;
    }

    protected List<ISection> evalSections4Form(final Form form,
                                               final Type type,
                                               final Instance instance,
                                               final Evaluator eval)
        throws CacheReloadException, EFapsException
    {
        LOG.info("Evaluation sections for form:\n {}, type:\n {}, instance:\n{}, eval:\n {}", form, type, instance, eval);
        final var ret = new ArrayList<ISection>();
        FormSectionDto.Builder currentFormSectionBldr = null;
        var groupCount = 0;
        var currentValues = new ArrayList<>();
        HeaderSectionDto.Builder currentHeaderSectionBldr = null;
        for (final Field field : form.getFields()) {
            if (field.isHiddenDisplay(currentTargetMode)) {
                LOG.warn("Skipped Hidden field {} in form {}", field.getName(), form.getName());
            } else if (!field.isNoneDisplay(currentTargetMode)
                            && field.hasAccess(currentTargetMode, instance, callCmd, instance)) {
                if (field instanceof FieldGroup) {
                    final var group = (FieldGroup) field;
                    groupCount = group.getGroupCount();
                } else if (field instanceof FieldTable) {
                    if (currentFormSectionBldr != null) {
                        ret.add(currentFormSectionBldr.build());
                    }
                    currentFormSectionBldr = null;
                    final var fieldTable = ((FieldTable) field).getTargetTable();
                    final var columns = getColumns(fieldTable, currentTargetMode, evalTypes(field));
                    final var tableSection = TableSectionDto.builder()
                                    .withEditable(field.isEditableDisplay(currentTargetMode))
                                    .withColumns(columns)
                                    .withValues(getValues(field, fieldTable, instance))
                                    .build();
                    if (currentHeaderSectionBldr != null) {
                        currentHeaderSectionBldr.addSection(tableSection);
                    } else {
                        ret.add(tableSection);
                    }
                } else if (field instanceof FieldHeading) {
                    if (currentHeaderSectionBldr != null) {
                        ret.add(currentHeaderSectionBldr.build());
                    } else if (currentFormSectionBldr != null) {
                        ret.add(currentFormSectionBldr.build());
                    }
                    currentFormSectionBldr = null;
                    currentHeaderSectionBldr = HeaderSectionDto.builder()
                                    .withHeader(DBProperties.getProperty(field.getLabel()))
                                    .withLevel(((FieldHeading) field).getLevel());
                } else {
                    if (currentFormSectionBldr == null) {
                        currentFormSectionBldr = FormSectionDto.builder();
                        if (currentHeaderSectionBldr != null) {
                            currentHeaderSectionBldr.addSection(currentFormSectionBldr);
                        }
                    }
                    if (groupCount > 0) {
                        groupCount--;
                    }
                    if (field instanceof FieldSet) {
                        if (eval != null) {
                            // what happens if that is in a group?
                            final var attributeSet = AttributeSet.find(type.getName(), field.getAttribute());
                            final var attrList = ((FieldSet) field).getOrder().isEmpty()
                                            ? attributeSet.getSetAttributes()
                                            : ((FieldSet) field).getOrder();
                            if (attributeSet != null) {
                                final var attrSetDto = AttributeSetDto.builder()
                                                .withName(field.getName())
                                                .withLabel(DBProperties.getProperty(field.getLabel()));
                                final List<ValueDto> values = new ArrayList<>();
                                for (final var attr : attrList) {
                                    final var fieldValue = eval.get(field.getName() + "-" + attr);
                                    final var valueBldr = ValueDto.builder()
                                                    .withName(attr)
                                                    .withValue(fieldValue)
                                                    .withLabel(DBProperties.getProperty(field.getLabel() + "/" + attr));
                                    values.add(valueBldr.build());
                                }
                                currentValues.add(attrSetDto.withValue(values).build());
                            }
                        }
                    } else {
                        currentValues.add(evalValue(eval, field, type, instance));
                    }

                    if (groupCount < 1) {
                        currentFormSectionBldr.addItem(currentValues);
                        currentValues = new ArrayList<>();
                    }
                }
            }
        }
        if (currentHeaderSectionBldr != null) {
            ret.add(currentHeaderSectionBldr.build());
        } else if (currentFormSectionBldr != null) {
            ret.add(currentFormSectionBldr.build());
        }
        return ret;
    }

    protected void evalSelects4Class(final AbstractCommand callCmd,
                                     final FieldClassification field,
                                     final Print print,
                                     final Instance instance)
        throws EFapsException
    {
        for (final var classification : getClassifications()) {
            final var form = classification.getTypeForm();
            evalSelects4Form(callCmd, form, print, instance, classification);
        }
    }

    protected boolean evalSelects4Form(final AbstractCommand callCmd,
                                       final Form form,
                                       final Print print,
                                       final Instance instance,
                                       final Classification clazz)
        throws CacheReloadException, EFapsException
    {
        boolean executable = false;
        for (final Field field : form.getFields()) {
            if (instance.isValid() && !field.isNoneDisplay(currentTargetMode)
                            && field.hasAccess(currentTargetMode, instance, callCmd, instance)) {
                if (field instanceof FieldClassification) {
                    evalSelects4Class(callCmd, (FieldClassification) field, print, instance);
                } else if (field instanceof FieldSet) {
                    final var typeName = clazz == null ?  instance.getType().getName() : clazz.getName();
                    final var attributeSet = AttributeSet.find(typeName, field.getAttribute());
                    if (attributeSet != null) {
                        add2Selects4AttributeSetInViewMode(print, (FieldSet) field, attributeSet, clazz);
                    }
                } else if (field.getSelect() != null) {
                    if (clazz != null) {
                        print.clazz(clazz.getName()).select(field.getSelect()).as(field.getName());
                    } else {
                        print.select(field.getSelect()).as(field.getName());
                    }
                    executable = true;
                } else if (field.getAttribute() != null) {
                    if (TargetMode.VIEW.equals(currentTargetMode)) {
                        add2Selects4AttributeInViewMode(print, field, clazz == null ? instance.getType() : clazz);
                    } else {
                        add2Selects4AttributeInEditMode(print, field, clazz == null ? instance.getType() : clazz);
                    }
                    executable = true;
                } else if (field.getPhrase() != null) {
                    if (clazz != null) {
                        print.clazz(clazz.getName()).phrase(field.getPhrase()).as(field.getName());
                    } else {
                        print.phrase(field.getPhrase()).as(field.getName());
                    }
                } else if (field.getMsgPhrase() != null) {
                    print.msgPhrase(getBaseSelect4MsgPhrase(field), field.getMsgPhrase()).as(field.getName());
                    executable = true;
                }
                if (field.getSelectAlternateOID() != null) {
                    print.select(field.getSelectAlternateOID()).as(field.getName() + "_AOID");
                    executable = true;
                }
            }
        }
        return executable;
    }

    private void add2Selects4AttributeSetInViewMode(final Print print,
                                                    final FieldSet fieldSet,
                                                    final AttributeSet attributeSet,
                                                    final Classification clazz)
        throws EFapsException
    {
        final var attrList = fieldSet.getOrder().isEmpty()
                        ? attributeSet.getSetAttributes()
                        : fieldSet.getOrder();
        for (final var attrName : attrList) {
            final var attr = attributeSet.getAttribute(attrName);
            if (attr.hasEvents(EventType.RANGE_VALUE)) {
                var baseSelect = clazz != null ? "class[" + clazz.getName() + "]." : "";
                baseSelect = baseSelect + "attributeset[" + fieldSet.getAttribute() + "]";
                add2Select4RangeValue(print, fieldSet.getName() + "-" + attrName, attr, baseSelect);
            } else if (clazz != null) {
                print.clazz(clazz.getName()).attributeSet(fieldSet.getAttribute()).attribute(attrName)
                                .as(fieldSet.getName() + "-" + attrName);
            } else {
                print.attributeSet(fieldSet.getAttribute()).attribute(attrName)
                                .as(fieldSet.getName() + "-" + attrName);
            }
        }
    }

    protected void add2Selects4AttributeInViewMode(final Print print,
                                                   final Field field,
                                                   final Type type)
        throws EFapsException
    {
        final var attr = type.getAttribute(field.getAttribute());
        if (attr != null) {
            if (attr.getAttributeType().getDbAttrType() instanceof StatusType) {
                print.status().as(field.getName());
            } else if (attr.hasEvents(EventType.RANGE_VALUE)) {
                final var baseSelect = type instanceof Classification ? "class["+ type.getName() + "]" : "";
                add2Select4RangeValue(print, field.getName(), attr, baseSelect);
            } else if (type instanceof Classification) {
                print.clazz(type.getName()).attribute(field.getAttribute()).as(field.getName());
            } else {
                print.attribute(field.getAttribute()).as(field.getName());
            }
        }
    }

    protected void add2Selects4AttributeInEditMode(final Print print,
                                                   final Field field,
                                                   final Type type)
        throws EFapsException
    {
        final var attr = type.getAttribute(field.getAttribute());
        if (attr != null) {
            if (attr.getAttributeType().getDbAttrType() instanceof StatusType) {
                print.attribute(attr.getName()).as(field.getName());
            } else if (attr.hasEvents(EventType.RANGE_VALUE)) {
                final var baseSelect = type instanceof Classification ? "class["+ type.getName() + "]" : "";
                add2Select4RangeValue(print, field.getName(), attr, baseSelect);
            } else if (type instanceof Classification) {
                print.clazz(type.getName()).attribute(field.getAttribute()).as(field.getName());
            } else {
                print.attribute(field.getAttribute()).as(field.getName());
            }
        }
    }


    protected void add2Select4Attribute(final Print _print,
                                        final Field _field,
                                        final List<Type> _types,
                                        final Classification clazz)
        throws EFapsException
    {
        Attribute attr = null;
        for (final var type : _types) {
            attr = type.getAttribute(_field.getAttribute());
            if (attr != null) {
                break;
            }
        }
        if (attr == null) {
            if (clazz != null) {
                _print.clazz(clazz.getName()).attribute(_field.getAttribute()).as(_field.getName());
            } else {
                _print.attribute(_field.getAttribute()).as(_field.getName());
            }
        } else if (attr.getAttributeType().getDbAttrType() instanceof StatusType) {
            if (clazz != null) {

            } else {

                _print.select("status.label").as(_field.getName());
            }
        } else if (attr.hasEvents(EventType.RANGE_VALUE)) {
            add2Select4RangeValue(_print, _field.getName(), attr, "");
        } else if (clazz != null) {

        } else {
            _print.attribute(_field.getAttribute()).as(_field.getName());
        }
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

    protected ValueDto evalValue(final Evaluator eval,
                                 final Field field,
                                 final Type type,
                                 final Instance inst)
        throws EFapsException
    {
        final var valueBldr = ValueDto.builder()
                        .withRequired(field.isRequired());
        Object fieldValue = null;
        boolean rangeValue = false;
        // range value
        if (TargetMode.VIEW.equals(currentTargetMode) && field.getAttribute() != null) {
            final var attr = type.getAttribute(field.getAttribute());
            if (attr != null && attr.hasEvents(EventType.RANGE_VALUE)
                            && !"Status".equals(attr.getAttributeType().getName())) {
                rangeValue = true;
                final var event = attr.getEvents(EventType.RANGE_VALUE).get(0);
                final var valueStr = event.getProperty("Value");
                if (valueStr.contains("$<")) {
                    try {
                        final var valueList = new ValueParser(new StringReader(valueStr)).ExpressionString();
                        final int i = 0;
                        final var strBldr = new StringBuilder();
                        for (final Token token : valueList.getTokens()) {
                            switch (token.getType()) {
                                case EXPRESSION:
                                    final var expValue = eval.get(field.getName() + "_ex" + i);
                                    if (expValue != null) {
                                        strBldr.append(String.valueOf(expValue));
                                    }
                                    break;
                                case TEXT:
                                    strBldr.append(token.getValue());
                                    break;
                                default:
                                    break;
                            }
                        }
                        fieldValue = strBldr.toString();
                    } catch (final ParseException e) {
                        throw new EFapsException("Catched", e);
                    }
                } else {
                    fieldValue = eval.get(field.getName());
                }
            }
        }
        if (!rangeValue) {
            fieldValue = eval == null ? null : eval.get(field.getName());
        }

        final UIType uiType = getUIType(field);
        if (UIType.SNIPPLET.equals(uiType)) {
            fieldValue = getSnipplet(inst, field);
            valueBldr.withType(ValueType.SNIPPLET);
        } else if (UIType.UPLOAD.equals(uiType)) {
            valueBldr.withType(ValueType.UPLOAD);
        } else if (UIType.UPLOADMULTIPLE.equals(uiType)) {
            valueBldr.withType(ValueType.UPLOADMULTIPLE);
        } else if (UIType.BUTTON.equals(uiType) || field instanceof FieldCommand) {
            valueBldr.withType(ValueType.BUTTON).withRef(String.valueOf(field.getId()));
        } else if (field.hasEvents(EventType.UI_FIELD_FORMAT)) {
            fieldValue = evalFieldFormatEvent(inst, field, valueBldr, fieldValue, currentTargetMode);
        } else if ((TargetMode.CREATE.equals(currentTargetMode) || TargetMode.EDIT.equals(currentTargetMode))
                        && field.isEditableDisplay(currentTargetMode)) {
            if (field.hasEvents(EventType.UI_FIELD_AUTOCOMPLETE)) {
                valueBldr.withType(ValueType.AUTOCOMPLETE)
                                .withRef(String.valueOf(field.getId()));
                final var alterOid = eval == null ? null : eval.get(field.getName() + "_AOID");
                if (alterOid != null) {
                    valueBldr.withOptions(List.of(OptionDto.builder()
                                    .withLabel(String.valueOf(fieldValue))
                                    .withValue(alterOid)
                                    .build()));
                    fieldValue = alterOid;
                }
            } else if (field.hasEvents(EventType.UI_FIELD_VALUE)) {
                Instance callInstance;
                if (type instanceof Classification) {
                    callInstance = eval.inst(type.getName());
                } else {
                    callInstance = inst;
                }
                fieldValue = evalFieldValueEvent(callInstance, field, valueBldr, fieldValue, currentTargetMode);
            } else {
                final var attr = type== null ? null : type.getAttribute(field.getAttribute());
                if (attr != null) {
                    if (attr.hasEvents(EventType.RANGE_VALUE) && !"Status".equals(attr.getAttributeType().getName())) {
                        final var options = getRangeValue(attr, fieldValue, currentTargetMode);
                        valueBldr
                                        .withType(ValueType.DROPDOWN)
                                        .withOptions(options.stream()
                                                        .map(opt -> OptionDto.builder()
                                                                        .withLabel(opt.getLabel())
                                                                        .withValue(opt.getValue())
                                                                        .build())
                                                        .collect(Collectors.toList()));
                        final var selectedOpt = options.stream().filter(IOption::isSelected).findFirst();
                        if (selectedOpt.isPresent()) {
                            fieldValue = selectedOpt.get().getValue();
                        }
                    } else {
                        final var attrType = attr.getAttributeType();
                        switch (attrType.getName()) {
                            case "Enum":
                                try {
                                    final Class<?> clazz = Class.forName(attr.getClassName(), false,
                                                    EFapsClassLoader.getInstance());
                                    valueBldr.withType(ValueType.RADIO)
                                                    .withOptions(Arrays.asList(clazz.getEnumConstants()).stream()
                                                                    .map(ienum -> OptionDto.builder()
                                                                                    .withValue(((IEnum) ienum).getInt())
                                                                                    .withLabel(getEnumLabel(
                                                                                                    (IEnum) ienum))
                                                                                    .build())
                                                                    .collect(Collectors.toList()));
                                } catch (final ClassNotFoundException e) {
                                    LOG.error("Catched", e);
                                }
                                if (TargetMode.EDIT.equals(currentTargetMode) && fieldValue instanceof IEnum) {
                                    fieldValue = ((IEnum) fieldValue).getInt();
                                }
                                break;
                            case "BitEnum":
                                try {
                                    final Class<?> clazz = Class.forName(attr.getClassName(), false,
                                                    EFapsClassLoader.getInstance());
                                    valueBldr.withType(ValueType.BITENUM)
                                                    .withOptions(Arrays.asList(clazz.getEnumConstants()).stream()
                                                                    .map(ienum -> OptionDto.builder()
                                                                                    .withValue(((IBitEnum) ienum)
                                                                                                    .getInt())
                                                                                    .withLabel(getEnumLabel(
                                                                                                    (IEnum) ienum))
                                                                                    .build())
                                                                    .collect(Collectors.toList()));
                                } catch (final ClassNotFoundException e) {
                                    LOG.error("Catched", e);
                                }
                                if (TargetMode.EDIT.equals(currentTargetMode) && fieldValue instanceof Collection) {
                                    fieldValue = ((Collection<?>) fieldValue).stream()
                                                    .map(enumVal -> ((IBitEnum) enumVal).getInt()).toList();
                                }
                                break;
                            case "Status":
                                final var statusType = attr.getLink();
                                valueBldr.withType(ValueType.DROPDOWN)
                                                .withOptions(Status.get(statusType.getUUID()).values().stream()
                                                                .map(status -> OptionDto.builder()
                                                                                .withValue(status.getId())
                                                                                .withLabel(status.getLabel())
                                                                                .build())
                                                                .collect(Collectors.toList()));
                                break;
                            case "Boolean":
                                valueBldr.withType(ValueType.RADIO);
                                valueBldr.withOptions(Arrays.asList(OptionDto.builder()
                                                .withValue(true)
                                                .withLabel(getBooleanLabel(attr, field, true))
                                                .build(),
                                                OptionDto.builder()
                                                                .withValue(false)
                                                                .withLabel(getBooleanLabel(attr, field, false))
                                                                .build()));
                                break;
                            case "Date":
                                valueBldr.withType(ValueType.DATE);
                                if (TargetMode.CREATE.equals(currentTargetMode) && fieldValue == null) {
                                    fieldValue = LocalDate.now(Context.getThreadContext().getZoneId()).toString();
                                }
                                break;
                            default:
                                final var valueType =  field.getRows() > 1 ? ValueType.TEXTAREA  :ValueType.INPUT;
                                valueBldr.withType(valueType);
                                break;
                        }
                    }
                } else {
                    final var valueType =  field.getRows() > 1 ? ValueType.TEXTAREA  :ValueType.INPUT;
                    valueBldr.withType(valueType);
                }
            }
        }
        if (fieldValue != null && valueBldr.getType() == null) {
            if (fieldValue instanceof OffsetDateTime) {
                valueBldr.withType(ValueType.DATETIME);
            }
        }
        if (field.hasEvents(EventType.UI_FIELD_UPDATE)) {
            valueBldr.withUpdateRef(String.valueOf(field.getId()));
        }
        return valueBldr.withLabel(getLabel(type, field))
                        .withName(field.getName())
                        .withValue(fieldValue)
                        .build();
    }

    public Response getContent(final String _oid)
        throws EFapsException
    {
        LOG.info("Get content for oid: {}", _oid);

        ContentDto dto = null;
        final var instance = Instance.get(_oid);
        if (instance.isValid()) {

            final var typeMenu = instance.getType().getTypeMenu();
            final var defaultSelected = typeMenu.getCommands().stream().filter(AbstractCommand::isDefaultSelected)
                            .findFirst();

            final var targetMenu = typeMenu.getTargetMenu();
            final List<NavItemDto> menus = targetMenu == null ? null : new NavItemEvaluator().getMenu(targetMenu);
            final var header = getLabel(instance, typeMenu.getTargetTitle());

            final List<NavItemDto> navItems = new ArrayList<>();
            navItems.add(NavItemDto.builder()
                            .withId(typeMenu.getUUID().toString())
                            .withLabel(getLabel(instance, typeMenu.getLabel()))
                            .withAction(ActionDto.builder()
                                            .withType(ActionType.FORM)
                                            .build())
                            .build());
            for (final var command : typeMenu.getCommands()) {
                ActionType actionType = null;
                if (command.getTargetTable() != null) {
                    actionType = ActionType.GRID;
                } else if (command.getTargetForm() != null) {
                    actionType = ActionType.FORM;
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
                            .withSections(evalSections(instance, typeMenu))
                            .build();
            dto = ContentDto.builder()
                            .withOutline(outline)
                            .withNav(navItems)
                            .withSelected(defaultSelected.isPresent() ? defaultSelected.get().getUUID().toString()
                                            : typeMenu.getUUID().toString())
                            .build();
        }
        return Response.ok()
                        .entity(dto)
                        .build();
    }

    public Response getContent(final String oid,
                               final String cmdId)
        throws EFapsException
    {
        LOG.info("Get content for oid: {} and cmdId: {}", oid, cmdId);
        OutlineDto dto = null;
        final var instance = Instance.get(oid);
        AbstractCommand cmd = Command.get(UUID.fromString(cmdId));
        if (cmd == null) {
            cmd = Menu.get(UUID.fromString(cmdId));
        }
        if (instance.isValid() || cmd.getTargetMode().equals(TargetMode.CREATE)
                        || cmd.getTargetMode().equals(TargetMode.UNKNOWN)) {
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
                            .withOid(oid)
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

    private String getLabel(final Type type,
                            final Field field) throws CacheReloadException
    {
        String ret = null;
        if (field.getLabel() != null) {
            ret = DBProperties.getProperty(field.getLabel());
        } else if (field.getAttribute() != null) {
            final var attr = type.getAttribute(field.getAttribute());
            if (attr != null) {
                ret = DBProperties.getProperty(attr.getLabelKey());
            }
        }
        if (ret == null && field.getSelect() != null) {
            final var select = EQL.parseSelect(field.getSelect());
            if (select.getElements(0) instanceof LinktoSelectElement) {
                final var linkAttrName = ((LinktoSelectElement) select.getElements(0)).getName();
                final var linkAttr = type.getAttribute(linkAttrName);
                if (linkAttr != null && linkAttr.getLink() != null) {
                    final var attrSelectEle = select.getElementsList().stream()
                                    .filter(element -> (element instanceof AttributeSelectElement)).findFirst();
                    if (attrSelectEle.isPresent()) {
                        final var attrName = ((AttributeSelectElement) attrSelectEle.get()).getName();
                        final var attr = linkAttr.getLink().getAttribute(attrName);
                        if (attr != null) {
                            ret = DBProperties.getProperty(attr.getLabelKey());
                        }
                    }
                }
            }
        }
        return ret;
    }

    public String getLabel(final Instance _instance,
                           final String _propertyKey)
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

    @SuppressWarnings("unchecked")
    private List<IOption> getRangeValue(final Attribute _attr,
                                        final Object fieldValue,
                                        final TargetMode targetMode)
        throws EFapsException
    {
        final List<IOption> ret = new ArrayList<>();
        for (final Return values : _attr.executeEvents(EventType.RANGE_VALUE,
                        ParameterValues.UIOBJECT, _attr,
                        ParameterValues.ACCESSMODE, targetMode,
                        ParameterValues.OTHERS, fieldValue)) {
            ret.addAll((List<IOption>) values.get(ReturnValues.VALUES));
        }
        return ret;
    }

    private CharSequence getSnipplet(final Instance _instance,
                                     final Field _field)
        throws EFapsException
    {
        CharSequence ret = null;
        if (_field.hasEvents(EventType.UI_FIELD_VALUE)) {
            final var uiValue = RestUIValue.builder()
                            .withInstance(_instance)
                            .withField(_field)
                            .build();
            final var values = _field.executeEvents(EventType.UI_FIELD_VALUE, ParameterValues.INSTANCE, _instance,
                            ParameterValues.UIOBJECT, uiValue);
            for (final var entry : values) {
                ret = (CharSequence) entry.get(ReturnValues.SNIPLETT);
            }
        }
        return ret;
    }

    public Collection<Map<String, ?>> getValues(final AbstractUserInterfaceObject _cmd,
                                                final org.efaps.admin.ui.Table _table,
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
                add2Select4Attribute(print, field, typeList, null);
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


    public List<Classification> evalClassificationsForInstance(final Classification classification,
                                                               final Instance instance)
        throws EFapsException
    {
        final List<Classification> ret = new ArrayList<>();
        if (classification.isRoot()) {
            final var eval = EQL.builder().print(instance).clazz(classification.getName()).instance().evaluate();
            if (eval.next() && eval.get(1) != null) {
                ret.add(classification);
            }
        }
        for (final var childClassification : classification.getChildClassifications()) {
            final var eval = EQL.builder().print(instance).clazz(childClassification.getName()).instance().evaluate();
            if (eval.next() && eval.get(1) != null) {
                ret.add(childClassification);
                ret.addAll(evalClassificationsForInstance(childClassification, instance));
            }
        }
        return ret;
    }

    public static String getBooleanLabel(final Attribute attr,
                                         final Field field,
                                         final Boolean bool)
    {
        {
            String ret = BooleanUtils.toStringTrueFalse(bool);
            if (attr != null
                            && DBProperties.hasProperty(attr.getKey() + "." + BooleanUtils.toStringTrueFalse(bool))) {
                ret = DBProperties.getProperty(attr.getKey() + "."
                                + BooleanUtils.toStringTrueFalse(bool));
            } else if (DBProperties
                            .hasProperty(field.getLabel() + "." + BooleanUtils.toStringTrueFalse(bool))) {
                ret = DBProperties.getProperty(field.getLabel() + "." + BooleanUtils.toStringTrueFalse(bool));
            }
            return ret;
        }
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

    @JsonDeserialize(builder = RestUIValue.Builder.class)
    public static class RestUIValue
        implements IUIValue
    {

        private final Attribute attribute;
        private final Field field;
        private final Instance instance;
        private final Object object;

        private RestUIValue(final Builder builder)
        {
            attribute = builder.attribute;
            field = builder.field;
            instance = builder.instance;
            object = builder.object;
        }

        @Override
        public Attribute getAttribute()
            throws EFapsException
        {
            return attribute;
        }

        @Override
        public Instance getCallInstance()
        {
            LOG.warn("getCallInstance Not implemented");
            return null;
        }

        @Override
        public Display getDisplay()
        {
            LOG.warn("getDisplay Not implemented");
            return null;
        }

        @Override
        public Field getField()
        {
            return field;
        }

        @Override
        public Instance getInstance()
        {
            return instance;
        }

        @Override
        public Object getObject()
        {
            return object;
        }

        /**
         * Creates builder to build {@link RestUIValue}.
         *
         * @return created builder
         */
        public static Builder builder()
        {
            return new Builder();
        }

        /**
         * Builder to build {@link RestUIValue}.
         */
        public static final class Builder
        {

            private Attribute attribute;
            private Field field;
            private Instance instance;
            private Object object;

            private Builder()
            {
            }

            public RestUIValue build()
            {
                return new RestUIValue(this);
            }

            public Builder withAttribute(final Attribute attribute)
            {
                this.attribute = attribute;
                return this;
            }

            public Builder withField(final Field field)
            {
                this.field = field;
                return this;
            }

            public Builder withInstance(final Instance instance)
            {
                this.instance = instance;
                return this;
            }

            public Builder withObject(final Object object)
            {
                this.object = object;
                return this;
            }
        }
    }
}

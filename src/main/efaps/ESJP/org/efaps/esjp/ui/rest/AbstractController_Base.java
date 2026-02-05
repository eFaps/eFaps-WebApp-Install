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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.Strings;
import org.efaps.admin.datamodel.Attribute;
import org.efaps.admin.datamodel.Type;
import org.efaps.admin.datamodel.attributetype.StatusType;
import org.efaps.admin.dbproperty.DBProperties;
import org.efaps.admin.event.EventType;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.event.Return;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.AbstractCommand;
import org.efaps.admin.ui.AbstractUserInterfaceObject.TargetMode;
import org.efaps.admin.ui.field.Field;
import org.efaps.api.ci.UIFormFieldProperty;
import org.efaps.api.ui.IOption;
import org.efaps.api.ui.UIType;
import org.efaps.beans.valueparser.ParseException;
import org.efaps.beans.valueparser.ValueParser;
import org.efaps.db.Checkout;
import org.efaps.db.Instance;
import org.efaps.db.PrintQuery;
import org.efaps.eql.builder.Print;
import org.efaps.esjp.ui.rest.ContentController_Base.RestUIValue;
import org.efaps.esjp.ui.rest.dto.ColumnDto;
import org.efaps.esjp.ui.rest.dto.IFieldBuilder;
import org.efaps.esjp.ui.rest.dto.OptionDto;
import org.efaps.esjp.ui.rest.dto.PayloadDto;
import org.efaps.esjp.ui.rest.dto.ValueType;
import org.efaps.esjp.ui.util.ImageUtil;
import org.efaps.util.DateTimeUtil;
import org.efaps.util.EFapsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EFapsUUID("b2fdecb4-5bce-4692-b260-14ff75281241")
@EFapsApplication("eFaps-WebApp")
public abstract class AbstractController_Base
{

    private static final Logger LOG = LoggerFactory.getLogger(AbstractController.class);

    protected void add2Select4Attribute(final Print _print,
                                        final Field _field,
                                        final Collection<Type> _types)
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
            _print.attribute(_field.getAttribute()).as(_field.getName());
        } else if (attr.getAttributeType().getDbAttrType() instanceof StatusType) {
            _print.select("status.label").as(_field.getName());
        } else if (attr.hasEvents(EventType.RANGE_VALUE)) {
            add2Select4RangeValue(_print, _field.getName(), attr, "");
        } else {
            _print.attribute(_field.getAttribute()).as(_field.getName());
        }
    }

    protected void add2Select4RangeValue(final Print print,
                                         final String key,
                                         final Attribute attr,
                                         final String baseSelect)
        throws EFapsException
    {
        var baseSel = "";
        if (baseSelect != null) {
            baseSel = baseSelect.endsWith(".") ? baseSelect : baseSelect + ".";
        }
        final var event = attr.getEvents(EventType.RANGE_VALUE).get(0);
        final var valueStr = event.getProperty("Value");
        if (valueStr.contains("$<")) {
            try {
                final var valueList = new ValueParser(new StringReader(valueStr)).ExpressionString();
                int i = 0;
                for (final var expression : valueList.getExpressions()) {
                    print.select(baseSel + "linkto[" + attr.getName() + "]." + expression)
                                    .as(key + "_ex" + i);
                    i++;
                }
            } catch (final ParseException e) {
                throw new EFapsException("Catched", e);
            }
        } else {
            print.select(baseSel + "linkto[" + attr.getName() + "].attribute[" + valueStr + "]").as(key);
        }
    }

    protected Map<String, String[]> convertToMap(final PayloadDto dto)
    {
        final var ret = new HashMap<String, String[]>();
        ret.put("eFaps-REST", new String[] { "true" });
        if (dto != null && dto.getValues() != null) {
            for (final var entry : dto.getValues().entrySet()) {
                if (entry.getValue() instanceof Collection) {
                    ret.put(entry.getKey(), ((Collection<?>) entry.getValue()).stream()
                                    .map(this::toString)
                                    .toArray(String[]::new));
                } else {
                    ret.put(entry.getKey(), new String[] { toString(entry.getValue()) });
                }
            }
        }
        return ret;
    }

    protected String toString(Object object) {
        String ret = null;
        if (object != null) {
            ret = String.valueOf(object);
        }
        return ret;
    }


    protected Object evalFieldFormatEvent(final Instance instance,
                                          final Field field,
                                          final IFieldBuilder bldr,
                                          final Object fieldValue,
                                          final TargetMode targetMode)
        throws EFapsException
    {
        Object ret = fieldValue;
        final var uiValue = RestUIValue.builder()
                        .withInstance(instance)
                        .withField(field)
                        .withObject(fieldValue)
                        .withDisplay(field.getDisplay(targetMode))
                        .build();
        for (final Return aReturn : field.executeEvents(
                        EventType.UI_FIELD_FORMAT,
                        ParameterValues.INSTANCE, instance,
                        ParameterValues.ACCESSMODE, targetMode,
                        ParameterValues.UIOBJECT, uiValue,
                        ParameterValues.OTHERS, fieldValue)) {
            ret = aReturn.get(ReturnValues.VALUES);
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    protected Object evalFieldValueEvent(final Instance instance,
                                         final Field field,
                                         final IFieldBuilder bldr,
                                         final Object fieldValue,
                                         final TargetMode targetMode)
        throws EFapsException
    {
        Object ret = fieldValue;
        final Attribute attr = instance == null ? null : instance.getType() == null ? null
                        : instance.getType().getAttribute(field.getAttribute());
        final var uiValue = RestUIValue.builder()
                        .withInstance(instance == null ? null : instance.isValid() ? instance : null)
                        .withField(field)
                        .withAttribute(attr)
                        .withDisplay(field.getDisplay(targetMode))
                        .withObject(fieldValue)
                        .build();
        for (final Return aReturn : field.executeEvents(EventType.UI_FIELD_VALUE,
                        ParameterValues.INSTANCE, instance,
                        ParameterValues.ACCESSMODE, targetMode,
                        ParameterValues.UIOBJECT, uiValue,
                        ParameterValues.OTHERS, fieldValue,
                        ParameterValues.CALL_INSTANCE, instance)) {
            final var values = aReturn.get(ReturnValues.VALUES);
            if (values instanceof List && !((List<?>) values).isEmpty()) {
                if (((List<?>) values).get(0) instanceof IOption) {
                    final var valueType = getUIType(field).equals(UIType.CHECKBOX) ? ValueType.CHECKBOX
                                    : ValueType.DROPDOWN;
                    bldr.withType(valueType)
                                    .withOptions(((List<IOption>) values).stream().map(option -> OptionDto.builder()
                                                    .withValue(option.getValue())
                                                    .withLabel(option.getLabel())
                                                    .build()).collect(Collectors.toList()));
                    final var selectedOpt = ((List<IOption>) values).stream().filter(IOption::isSelected).findFirst();
                    if (selectedOpt.isPresent()) {
                        ret = selectedOpt.get().getValue();
                    }
                }
            } else if (values instanceof org.joda.time.DateTime) {
                ret = DateTimeUtil.toDateTime(values);
            } else if (values instanceof final String valueStr) {
                ret = valueStr;
            }
        }
        return ret;
    }

    protected String getBaseSelect4MsgPhrase(final Field _field)
    {
        String ret = "";
        if (_field.getSelectAlternateOID() != null) {
            ret = Strings.CS.removeEnd(_field.getSelectAlternateOID(), ".oid");
        }
        return ret;
    }

    protected List<ColumnDto> getColumns(final org.efaps.admin.ui.Table table,
                                         final TargetMode targetMode,
                                         final Collection<Type> types)
        throws EFapsException
    {
        final var ret = new ArrayList<ColumnDto>();
        LOG.debug("evaluating Colums for {}", table.getName());
        for (final var field : table.getFields()) {
            LOG.debug("  field {}", field.getName());
            if (!field.isNoneDisplay(targetMode) && field.hasAccess(targetMode, null, null, null)) {
                final var columBldr = ColumnDto.builder()
                                .withField(field.getName())
                                .withHeader(field.getLabel() == null ? "" : DBProperties.getProperty(field.getLabel()))
                                .withRef(field.getReference() != null ? "true" : null);
                if ((TargetMode.CREATE.equals(targetMode) || TargetMode.EDIT.equals(targetMode) && !types.isEmpty())
                                && field.isEditableDisplay(targetMode)) {
                    if (field.getAttribute() != null) {
                        final var typeOpt = types.stream()
                                        .filter(type -> (type.getAttribute(field.getAttribute()) != null)).findFirst();
                        if (typeOpt.isPresent()) {
                            final var uiType = getUIType(field);
                            if (UIType.DEFAULT.equals(uiType)) {
                                columBldr.withType(ValueType.INPUT);
                            } else {
                                switch (uiType) {
                                    case DROPDOWN -> columBldr.withType(ValueType.DROPDOWN);
                                    default -> columBldr.withType(ValueType.INPUT);
                                }

                            }
                        }
                    } else if (field.hasEvents(EventType.UI_FIELD_AUTOCOMPLETE)) {
                        columBldr.withType(ValueType.AUTOCOMPLETE);
                        columBldr.withRef(String.valueOf(field.getId()));
                    } else if (field.hasEvents(EventType.UI_FIELD_VALUE)) {
                        evalFieldValueEvent(null, field, columBldr, null, targetMode);
                    }
                    if (field.hasEvents(EventType.UI_FIELD_UPDATE)) {
                        columBldr.withUpdateRef(String.valueOf(field.getId()));
                    }
                }
                final var column = columBldr.build();
                LOG.debug("  column: {}", column);
                ret.add(column);
            }
        }
        return ret;
    }

    protected List<Field> getFields(final org.efaps.admin.ui.Table table, TargetMode targetMode)
    {
        return table.getFields().stream().filter(field -> {
            try {
                return field.hasAccess( targetMode, null) && !field.isNoneDisplay(targetMode);
            } catch (final EFapsException e) {
                LOG.error("Catched error while evaluation access for Field: {}", field);
            }
            return false;
        }).collect(Collectors.toList());
    }

    protected String getHeader(final AbstractCommand _cmd,
                               final String oid)
        throws EFapsException
    {
        final var key = _cmd.getTargetTitle() == null
                        ? _cmd.getName() + ".Title"
                        : _cmd.getTargetTitle();
        var header = DBProperties.getProperty(key);
        final var instance = Instance.get(oid);
        if (instance.isValid()) {
            final PrintQuery print = new PrintQuery(instance);
            final ValueParser parser = new ValueParser(new StringReader(header));
            try {
                final var list = parser.ExpressionString();
                if (!list.getExpressions().isEmpty()) {
                    list.makeSelect(print);
                    if (print.execute()) {
                        header = list.makeString(instance, print, TargetMode.VIEW);
                    }
                }
            } catch (final ParseException e) {
                LOG.error("Catched error while evaluation header for oid: {}", oid);
            }
        }
        return header;
    }

    protected UIType getUIType(final Field _field)
    {
        final UIType ret;
        final String uiTypeStr = _field.getProperty(UIFormFieldProperty.UI_TYPE.value());
        if (EnumUtils.isValidEnum(UIType.class, uiTypeStr)) {
            ret = UIType.valueOf(uiTypeStr);
        } else {
            // as a second priority evaluate the provider
            final var uiProvider = _field.getProperty(UIFormFieldProperty.UI_PROVIDER.value());
            if (uiProvider != null) {
                final var key = uiProvider.substring(uiProvider.lastIndexOf(".") + 1);
                ret = switch (key) {
                    case "DateUI" -> UIType.DATE;
                    case "DateTimeUI" -> UIType.DATETIME;
                    default -> UIType.DEFAULT;
                };
            } else {
                ret = UIType.DEFAULT;
            }
        }
        return ret;
    }

    protected ValueType getValueType(final Field field)
    {
        ValueType ret = null;
        final String valueTypeStr = field.getProperty("ValueType");
        if (EnumUtils.isValidEnum(ValueType.class, valueTypeStr)) {
            ret = ValueType.valueOf(valueTypeStr);
        }
        return ret;
    }

    protected String prepareImage(final String oid)
        throws EFapsException
    {
        String url = null;
        if (oid != null) {
            try {
                final var checkout = new Checkout(oid);
                final InputStream input = checkout.execute();
                if (input != null) {
                    final var temp = new org.efaps.esjp.common.file.FileUtil().getFile(checkout.getFileName());

                    final OutputStream out = new FileOutputStream(temp);
                    IOUtils.copy(input, out);
                    input.close();
                    out.close();

                    final var imageKey = ImageUtil.put(temp);
                    url = "/image/" + imageKey;
                }
            } catch (EFapsException | IOException e) {
                LOG.error("Catched error while preparing image for oid: {}", oid);
            }
        }
        return url;
    }
}

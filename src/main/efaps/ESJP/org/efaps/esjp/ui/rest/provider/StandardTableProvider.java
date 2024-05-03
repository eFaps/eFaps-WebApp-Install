/*
 * Copyright 2003 - 2024 The eFaps Team
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
package org.efaps.esjp.ui.rest.provider;

import java.io.StringReader;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.efaps.admin.datamodel.Attribute;
import org.efaps.admin.datamodel.Type;
import org.efaps.admin.datamodel.attributetype.DateType;
import org.efaps.admin.datamodel.attributetype.StatusType;
import org.efaps.admin.datamodel.ui.IUIValue;
import org.efaps.admin.event.EventType;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.AbstractCommand;
import org.efaps.admin.ui.field.Field;
import org.efaps.admin.ui.field.Field.Display;
import org.efaps.api.ui.FilterBase;
import org.efaps.api.ui.FilterType;
import org.efaps.beans.valueparser.ParseException;
import org.efaps.beans.valueparser.ValueParser;
import org.efaps.db.Instance;
import org.efaps.eql.EQL;
import org.efaps.eql.builder.Print;
import org.efaps.eql.builder.Query;
import org.efaps.eql.builder.Where;
import org.efaps.esjp.common.properties.PropertiesUtil;
import org.efaps.esjp.ui.rest.TableController;
import org.efaps.esjp.ui.rest.TableController_Base;
import org.efaps.esjp.ui.rest.dto.FilterDto;
import org.efaps.esjp.ui.rest.dto.FilterKind;
import org.efaps.util.EFapsException;
import org.efaps.util.UUIDUtil;
import org.efaps.util.cache.InfinispanCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EFapsUUID("d6494966-e942-4ca5-8c93-bb794ff70c2d")
@EFapsApplication("eFaps-WebApp")
public class StandardTableProvider
    implements ITableProvider
{

    private static final Logger LOG = LoggerFactory.getLogger(TableController.class);

    @Override
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
                                            || expandChildTypes.size() == 1
                                                            && !"false".equalsIgnoreCase(expandChildTypes.get(0)))) {
                type.getChildTypes().forEach(at -> typeList.add(at));
            }
            // if we have more specific ones evaluate for each type
            if (expandChildTypes.size() > 1 && Boolean.parseBoolean(expandChildTypes.get(typeEntry.getKey()))) {
                type.getChildTypes().forEach(at -> typeList.add(at));
            }
        }
        return typeList;
    }

    public void addFilter(final AbstractCommand cmd,
                          final Query query,
                          final Where where,
                          final List<Type> types,
                          final List<Field> fields)
        throws EFapsException
    {
        final var key = TableController.getFilterKey(cmd.getUUID().toString());
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

    protected String getBaseSelect4MsgPhrase(final Field _field)
    {
        String ret = "";
        if (_field.getSelectAlternateOID() != null) {
            ret = StringUtils.removeEnd(_field.getSelectAlternateOID(), ".oid");
        }
        return ret;
    }

}

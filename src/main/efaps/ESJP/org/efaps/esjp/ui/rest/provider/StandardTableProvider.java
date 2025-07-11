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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.efaps.admin.datamodel.Attribute;
import org.efaps.admin.datamodel.Status;
import org.efaps.admin.datamodel.Status.StatusGroup;
import org.efaps.admin.datamodel.Type;
import org.efaps.admin.datamodel.attributetype.CreatedType;
import org.efaps.admin.datamodel.attributetype.DateType;
import org.efaps.admin.datamodel.attributetype.RateType;
import org.efaps.admin.datamodel.attributetype.StatusType;
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
import org.efaps.eql.builder.Count;
import org.efaps.eql.builder.Print;
import org.efaps.eql.builder.Query;
import org.efaps.eql.builder.Where;
import org.efaps.esjp.common.properties.PropertiesUtil;
import org.efaps.esjp.ui.rest.ContentController_Base.RestUIValue;
import org.efaps.esjp.ui.rest.TableController;
import org.efaps.esjp.ui.rest.dto.FilterDto;
import org.efaps.esjp.ui.rest.dto.FilterKind;
import org.efaps.esjp.ui.rest.dto.OptionDto;
import org.efaps.esjp.ui.rest.dto.PageDto;
import org.efaps.esjp.ui.util.WebApp;
import org.efaps.util.EFapsException;
import org.efaps.util.UUIDUtil;
import org.efaps.util.cache.CacheReloadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EFapsUUID("d6494966-e942-4ca5-8c93-bb794ff70c2d")
@EFapsApplication("eFaps-WebApp")
public class StandardTableProvider
    extends AbstractTableProvider
    implements ITableProvider
{

    private static final Logger LOG = LoggerFactory.getLogger(StandardTableProvider.class);

    private int pageSize;

    private int pageNo;

    private int[] pageOptions;

    private String sortBy;

    protected int getPageSize()
    {
        return pageSize;
    }

    protected int getPageNo()
    {
        return pageNo;
    }

    protected String getSortBy()
    {
        return sortBy;
    }

    @Override
    public Collection<Map<String, ?>> getValues()
        throws EFapsException
    {
        final var types = evalTypes();
        final var query = evalQuery(types);

        final var print = query.select();

        if (isPaginated()) {
            String orderBy;
            if (getSortBy() == null) {
                orderBy = getFields().get(0).getName();
            } else {
                orderBy = getSortBy();
            }
            print.limit(pageSize).offset(pageNo * pageSize).orderBy(orderBy);
        }

        if (getCmd() instanceof AbstractCommand && ((AbstractCommand) getCmd()).isTargetShowCheckBoxes()
                        || getFields().stream().anyMatch(field -> field.getReference() != null)) {
            print.oid().as("OID");
        }
        for (final var field : getFields()) {
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

        for (final var field : getFields()) {
            if (field.hasEvents(EventType.UI_FIELD_VALUE) || field.hasEvents(EventType.UI_FIELD_FORMAT)) {
                for (final var map : data) {
                    applyEvent(map, field, EventType.UI_FIELD_VALUE);
                    applyEvent(map, field, EventType.UI_FIELD_FORMAT);
                }
            }
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    protected void applyEvent(final Map<String, ?> map,
                              final Field field,
                              final EventType eventType)
        throws EFapsException
    {
        if (field.hasEvents(eventType)) {
            final var obj = map.get(field.getName());
            Instance instance = null;
            if (obj instanceof final Instance inst) {
                instance = inst;
            } else if (map.containsKey(field.getName() + "_AOID")) {
                instance = Instance.get((String) map.get(field.getName() + "_AOID"));
            } else if (map.containsKey("OID")) {
                instance = Instance.get((String) map.get("OID"));
            }
            final var value = RestUIValue.builder()
                            .withObject(obj)
                            .withInstance(instance)
                            .withField(field)
                            .withDisplay(field.isEditableDisplay(getTargetMode()) ? Display.EDITABLE : Display.READONLY)
                            .build();

            final var returns = field.executeEvents(eventType,
                            ParameterValues.UIOBJECT, value,
                            ParameterValues.ACCESSMODE, getTargetMode());
            for (final var ret : returns) {
                ((Map<String, Object>) map).put(field.getName(), ret.get(ReturnValues.VALUES));
            }
        }
    }

    protected Query evalQuery(final List<Type> types)
        throws EFapsException
    {

        final var typeNames = types.stream().map(Type::getName).toArray(String[]::new);
        LOG.debug("typeNames: {}", Arrays.toString(typeNames));
        final var query = EQL.builder()
                        .print()
                        .query(typeNames);

        Where where = null;
        final var properties = new Properties();
        properties.putAll(getPropertiesMap());
        final var linkFroms = PropertiesUtil.analyseProperty(properties, "LinkFrom", 0);
        boolean first = true;
        for (final var linkfrom : linkFroms.entrySet()) {
            if (first) {
                first = false;
                where = query.where().attribute(linkfrom.getValue()).eq(Instance.get(getOid()));
            } else {
                where.or().attribute(linkfrom.getValue()).eq(Instance.get(getOid()));
            }
        }

        addFilter(query, where, types);
        return query;
    }

    protected boolean isPaginated()
        throws EFapsException
    {
        if (this.pageSize == 0) {
            final var paginationConfig = WebApp.PAGINATION.get();
            final var cmds = PropertiesUtil.analyseProperty(paginationConfig, "cmd", 0).values();
            final var key = cmds.stream().filter(
                            cmd -> (cmd.equals(getCmd().getName()) || getCmd().getUUID() != null
                                            && cmd.equals(getCmd().getUUID().toString())))
                            .findFirst();
            if (key.isPresent()) {
                final var pagConf = PropertiesUtil.getProperties4Prefix(paginationConfig, key.get(), true);
                this.pageSize = Integer.valueOf(pagConf.getProperty("pageSize", "10"));
                final var optionStr = pagConf.getProperty("pageOptions", "10,20,30");
                this.pageOptions = Arrays.stream(optionStr.split(",")).mapToInt(Integer::parseInt).toArray();
            } else {
                this.pageSize = -1;
            }
        }
        return this.pageSize > 0;
    }

    @Override
    public ITableProvider withPageRequest(final int pageSize,
                                          final int pageNo,
                                          final String sortBy)
    {
        this.pageSize = pageSize;
        this.pageNo = pageNo;
        this.sortBy = sortBy;
        return this;
    }

    @Override
    public PageDto getPage()
        throws EFapsException
    {
        PageDto ret = null;
        if (isPaginated()) {
            final var types = evalTypes();
            final var eval = evalCount(types).stmt().evaluate();

            ret = PageDto.builder()
                            .withPageSize(pageSize)
                            .withPageOptions(pageOptions)
                            .withTotalItems(eval.count())
                            .build();
        }
        return ret;
    }

    protected Count evalCount(final List<Type> types)
        throws EFapsException
    {

        final var typeNames = types.stream().map(Type::getName).toArray(String[]::new);
        LOG.debug("typeNames: {}", Arrays.toString(typeNames));
        final var count = EQL.builder()
                        .count(typeNames);

        Where where = null;
        final var properties = new Properties();
        properties.putAll(getPropertiesMap());
        final var linkFroms = PropertiesUtil.analyseProperty(properties, "LinkFrom", 0);
        boolean first = true;
        for (final var linkfrom : linkFroms.entrySet()) {
            if (first) {
                first = false;
                where = count.where().attribute(linkfrom.getValue()).eq(Instance.get(getOid()));
            } else {
                where.or().attribute(linkfrom.getValue()).eq(Instance.get(getOid()));
            }
        }

        // addFilter(query, where, types);
        return count;
    }

    protected List<Type> evalTypes()
        throws EFapsException
    {
        final var typeList = new ArrayList<Type>();
        final var properties = new Properties();
        properties.putAll(getPropertiesMap());
        final var types = PropertiesUtil.analyseProperty(properties, "Type", 0);
        final var expandChildTypes = PropertiesUtil.analyseProperty(properties, "ExpandChildTypes", 0);
        LOG.debug("evaluating types: {}, expandChildTypes: {}", types, expandChildTypes);
        for (final var typeEntry : types.entrySet()) {
            Type type;
            if (UUIDUtil.isUUID(typeEntry.getValue())) {
                type = Type.get(UUID.fromString(typeEntry.getValue()));
            } else {
                type = Type.get(typeEntry.getValue());
            }
            typeList.add(type);
            // default expand for children, if not deactivated
            if (expandChildTypes.size() == 0 || expandChildTypes.size() == 1
                            && !"false".equalsIgnoreCase(expandChildTypes.get(0))) {
                type.getChildTypes().forEach(at -> typeList.add(at));
            }
            // if we have more specific ones evaluate for each type
            if (expandChildTypes.size() > 1 && Boolean.parseBoolean(expandChildTypes.get(typeEntry.getKey()))) {
                type.getChildTypes().forEach(at -> typeList.add(at));
            }
        }
        return typeList;
    }

    public void addFilter(final Query query,
                          final Where where,
                          final List<Type> types)
        throws EFapsException
    {
        final var key = TableController.getFilterKey(getCmd());
        var filters = TableController.getFilters(key);
        if (filters.isEmpty() && getFields().stream().anyMatch(field -> (field.getFilter() != null
                        && FilterBase.DATABASE.equals(field.getFilter().getBase())))) {
            filters = evalDefaultFilter(types);
        }
        if (CollectionUtils.isNotEmpty(filters)) {
            LOG.info("applying filter");
            TableController.cacheFilters(key, filters);
            Where wherePart;
            boolean connect;
            if (where == null) {
                wherePart = query.where();
                connect = false;
            } else {
                wherePart = where;
                connect = true;
            }
            for (final var filter : filters) {
                if (connect) {
                    wherePart.and();
                } else {
                    connect = true;
                }
                switch (filter.getKind()) {
                    case DATE:
                        wherePart.attribute(filter.getAttribute()).greaterOrEq(filter.getValue1().toString())
                                        .and().attribute(filter.getAttribute()).lessOrEq(filter.getValue2().toString());
                        break;
                    case STATUS:
                        final var type = findCommonAncestor(types);
                        final Set<Status> stati = new HashSet<>();
                        for (final var oneType : types) {
                            final Attribute attr = oneType.getStatusAttribute();
                            stati.addAll(getStatus4Type(attr.getLink()));
                        }
                        @SuppressWarnings("unchecked") final Collection<String> selected = (Collection<String>) filter
                                        .getValue2();
                        final var selectedIds = stati.stream()
                                        .filter(status -> selected.contains(status.getKey()))
                                        .map(Status::getId).toArray(Long[]::new);
                        if (selectedIds.length > 0) {
                            wherePart.attribute(type.getStatusAttribute().getName()).in(selectedIds);
                        }
                        break;
                }
            }
        }
    }

    protected List<FilterDto> evalDefaultFilter(final List<Type> types)
        throws CacheReloadException
    {
        final List<FilterDto> ret = new ArrayList<>();
        for (final var field : getFields()) {
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
                            if (attr.getAttributeType().getDbAttrType() instanceof DateType
                                            || attr.getAttributeType().getDbAttrType() instanceof CreatedType) {
                                final var filterBuilder = FilterDto.builder()
                                                .withKind(FilterKind.DATE)
                                                .withField(field.getName())
                                                .withAttribute(attr.getName());

                                final var parts = field.getFilter().getDefaultValue().split(":");
                                final var range = parts[0];
                                final var fromSub = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
                                final var rangeCount = parts.length > 2 ? Integer.parseInt(parts[2]) : 1;
                                switch (range) {
                                    case "TODAY":
                                        filterBuilder.withValue1(LocalDate.now());
                                        filterBuilder.withValue2(LocalDate.now().plusDays(rangeCount));
                                        break;
                                    case "WEEK":
                                        filterBuilder.withValue1(LocalDate.now()
                                                        .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                                                        .minusWeeks(fromSub));
                                        filterBuilder.withValue2(LocalDate.now().plusWeeks(rangeCount));
                                        break;
                                    case "MONTH":
                                        filterBuilder.withValue1(LocalDate.now()
                                                        .minusMonths(fromSub));
                                        filterBuilder.withValue2(LocalDate.now().plusMonths(rangeCount));
                                        break;
                                    default:
                                        LOG.warn("Missing default date filter: {}", range);
                                }
                                ret.add(filterBuilder.build());
                            }
                        }
                    }
                } else if (FilterType.STATUS == field.getFilter().getType()) {
                    final var filterBuilder = FilterDto.builder()
                                    .withKind(FilterKind.STATUS)
                                    .withField(field.getName());
                    final var defaultValues = field.getFilter().getDefaultValue().split(";");

                    final Set<Status> stati = new HashSet<>();
                    for (final var type : types) {
                        final Attribute attr = type.getStatusAttribute();
                        stati.addAll(getStatus4Type(attr.getLink()));
                    }
                    final Map<String, OptionDto> options = new HashMap<>();
                    final Set<String> selected = new HashSet<>();
                    for (final Status statusTmp : stati) {
                        for (final String defaultv : defaultValues) {
                            if (defaultv.equals(statusTmp.getKey())) {
                                selected.add(statusTmp.getKey());
                            }
                        }
                        final OptionDto option;
                        if (options.containsKey(statusTmp.getKey())) {
                            option = options.get(statusTmp.getKey());
                        } else {
                            option = OptionDto.builder()
                                            .withLabel(statusTmp.getLabel())
                                            .withValue(statusTmp.getKey())
                                            .build();
                            options.put(statusTmp.getKey(), option);
                        }
                    }
                    filterBuilder.withValue1(options.values()).withValue2(selected);
                    if (!selected.isEmpty()) {
                        ret.add(filterBuilder.build());
                    }
                }
            }
        }
        return ret;
    }

    protected Set<Status> getStatus4Type(final Type type)
        throws CacheReloadException
    {
        final Set<Status> ret = new HashSet<>();
        final StatusGroup grp = Status.get(type.getUUID());
        if (grp != null) {
            ret.addAll(grp.values());
        } else {
            for (final Type childType : type.getChildTypes()) {
                ret.addAll(getStatus4Type(childType));
            }
        }
        return ret;
    }

    protected void add2Select4Attribute(final Print print,
                                        final Field field,
                                        final Collection<Type> types)
        throws EFapsException
    {
        Attribute attr = null;
        for (final var type : types) {
            attr = type.getAttribute(field.getAttribute());
            if (attr != null) {
                break;
            }
        }
        if (attr == null) {
            print.attribute(field.getAttribute()).as(field.getName());
        } else if (attr.getAttributeType().getDbAttrType() instanceof StatusType) {
            print.select("status.label").as(field.getName());
        } else if (attr.getAttributeType().getDbAttrType() instanceof RateType) {
            print.attribute(field.getAttribute()).value().as(field.getName());
        } else if (attr.hasEvents(EventType.RANGE_VALUE)) {
            add2Select4RangeValue(print, field.getName(), attr, "");
        } else {
            print.attribute(field.getAttribute()).as(field.getName());
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

    protected Type findCommonAncestor(final List<Type> types)
    {
        if (types.size() == 1) {
            return types.get(0);
        }
        final List<List<Type>> typeLists = new ArrayList<>();
        for (final Type type : types) {
            final List<Type> ancestors = new ArrayList<>();
            Type currentType = type;
            ancestors.add(currentType);
            while (currentType.getParentType() != null) {
                currentType = currentType.getParentType();
                ancestors.add(currentType);
            }
            typeLists.add(ancestors);
        }
        Type tempType = null;
        final List<Type> compList = typeLists.get(0);
        typeLists.remove(0);
        for (final Type comp : compList) {
            boolean found = true;
            for (final List<Type> typeList : typeLists) {
                if (!typeList.contains(comp)) {
                    found = false;
                    break;
                }
            }
            if (found) {
                tempType = comp;
                break;
            }
        }
        return tempType;
    }
}

/*
 * Copyright 2003 - 2013 The eFaps Team
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
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
 */

package org.efaps.esjp.common.uitable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.efaps.admin.datamodel.Attribute;
import org.efaps.admin.datamodel.Classification;
import org.efaps.admin.datamodel.Status;
import org.efaps.admin.datamodel.Type;
import org.efaps.admin.event.Parameter;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.event.Return;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.program.esjp.EFapsRevision;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.AbstractCommand;
import org.efaps.admin.ui.AbstractUserInterfaceObject;
import org.efaps.admin.ui.field.Field;
import org.efaps.admin.ui.field.Filter;
import org.efaps.db.AttributeQuery;
import org.efaps.db.Instance;
import org.efaps.db.InstanceQuery;
import org.efaps.db.MultiPrintQuery;
import org.efaps.db.QueryBuilder;
import org.efaps.esjp.common.AbstractCommon;
import org.efaps.ui.wicket.util.FilterDefault;
import org.efaps.util.DateTimeUtil;
import org.efaps.util.EFapsException;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic class to get the Instances for a UI-table.
 *
 * <table>
 * <tr>
 * <th>Property</th>
 * <th>Value</th>
 * <th>Mandatory</th>
 * <th>Default</th>
 * <th>Description</th>
 * </tr>
 *
 * <tr>
 * <td>TypeNN</td>
 * <td>String</td>
 * <td>true</td>
 * <td>-</td>
 * <td>Name of a Type that will be used as the base for a QueryBuilder.
 * If only one type or abstract type the numeral are not necessary.</td>
 * </tr>
 * <tr>
 * <td>LinkFromNN</td>
 * <td>String</td>
 * <td>false</td>
 * <td>-</td>
 * <td>Name of an attribute used to link it with a given instance. Is applied on the TypeNN. (Abstract and same NN)</td>
 * </tr>
 * <tr>
 * <td>StatusGrpNN</td>
 * <td>String</td>
 * <td>false</td>
 * <td>-</td>
 * <td>The name of a StatusGroup. Is applied together with StatusNN on the TypeNN. (Abstract and same NN)</td>
 * </tr>
 * <tr>
 * <td>StatusNN</td>
 * <td>String</td>
 * <td>false</td>
 * <td>-</td>
 * <td>A list of Status, seperated with <code>;</code>. Is applied together with StatusGroupNN on the TypeNN. (Abstract and same NN)</td>
 * </tr>
 * <tr>
 * <td>ExpandChildTypesNN</td>
 * <td>true/false</td>
 * <td>false</td>
 * <td>true</td>
 * <td>Must the ChildTypes be expanded.Is applied on the TypeNN. (Abstract and same NN)</td>
 * </tr>
 * </table>
 * <br>
 * <i>NN > 00</i></br>
 * <b>Examples:</b><br>
 * <ul>
 * <li>
 * Abstract Type only:<br>
 * <code>
 *  &lt;property name=&quot;Type&quot;&gt;ERP_DocumentAbstract&lt;/property&gt;<br>
 * </code>
 * </li>
 * <li>
 * Type only:<br>
 * <code>
 *  &lt;property name=&quot;Type&quot;&gt;Sales_Invoice&lt;/property&gt;<br>
 * </code>
 * </li>
 * <li>
 * Abstract Type with Link to a parent instance:<br>
 * <code>
 *  &lt;property name=&quot;Type&quot;&gt;Sales_PositionAbstract&lt;/property&gt;<br>
 *  &lt;property name=&quot;LinkFrom&quot;&gt;DocumentAbstractLink&lt;/property&gt;
 * </code>
 * </li>
 * <li>
 * Type with Link to a parent instance:<br>
 * <code>
 *  &lt;property name=&quot;Type&quot;&gt;Sales_InvoicePosition&lt;/property&gt;<br>
 *  &lt;property name=&quot;LinkFrom&quot;&gt;InvoiceLink&lt;/property&gt;
 * </code>
 * </li>
 * <li>
 * Type Status filter:<br>
 * <code>
 * &lt;property name=&quot;Type01&quot;&gt;Sales_Quotation&lt;/property&gt;<br>
 * &lt;property name=&quot;StatusGrp01&quot;&gt;Sales_QuotationStatus&lt;/property&gt;<br>
 * &lt;property name=&quot;Status01&quot;&gt;Open;Canceled&lt;/property&gt;
 * </code>
 * </li>
 *  <li>
 * Type Status filter on abstract type:<br>
 * <code>
 * &lt;property name=&quot;Type&quot;&gt;Sales_DocumentAbstract&lt;/property&gt;<br>
 * &lt;property name=&quot;StatusGrp01&quot;&gt;Sales_QuotationStatus&lt;/property&gt;<br>
 * &lt;property name=&quot;Status01&quot;&gt;Open;Canceled&lt;/property&gt;<br>
 * &lt;property name=&quot;StatusGrp02&quot;&gt;Sales_InvoiceStatus&lt;/property&gt;<br>
 * &lt;property name=&quot;Status02&quot;&gt;Open;Canceled&lt;/property&gt;
 * </code>
 * </li>
 * </uL>
 * @author The eFaps Team
 * @version $Id$
 */
@EFapsUUID("49c223e8-e500-4c91-a949-576b63c4fb31")
@EFapsRevision("$Rev$")
public abstract class MultiPrint_Base
    extends AbstractCommon
{
    /**
     * Logger for this class.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(MultiPrint.class);

    /**
     * @param _parameter Parameter as passed by the eFaps API
     * @return Return with List of instances
     * @throws EFapsException on error
     */
    public Return execute(final Parameter _parameter)
        throws EFapsException
    {
        final Return ret = new Return();
        ret.put(ReturnValues.VALUES, getInstances(_parameter));
        return ret;
    }

    /**
     * @param _parameter Parameter as passed by the eFaps API
     * @return Return with List of instances
     * @throws EFapsException on error
     */
    public List<Instance> getInstances(final Parameter _parameter)
        throws EFapsException
    {
        final List<Instance> instances = new ArrayList<Instance>();

        final Map<?, ?> properties = (Map<?, ?>) _parameter.get(ParameterValues.PROPERTIES);
        final boolean hasTable = _parameter.get(ParameterValues.OTHERS) instanceof Map;

        if (properties.containsKey("Types")) {
            instances.addAll(tobeRemoved(_parameter));
        } else {
            final Map<Integer, String> expands = analyseProperty(_parameter, "ExpandChildTypes");
            final int i = expands.containsKey(0) ? 0 : 1;
            for (final QueryBuilder queryBldr : getQueryBuilders(_parameter)) {
                add2QueryBldr(_parameter, queryBldr);
                if (!hasTable || analyzeTable(_parameter,
                                            (Map<?, ?>) _parameter.get(ParameterValues.OTHERS), queryBldr)) {
                    final InstanceQuery query = queryBldr.getQuery();
                    query.setIncludeChildTypes(!"false".equalsIgnoreCase(expands.get(i)));
                    instances.addAll(query.execute());
                }
            }
        }
        return instances;
    }

    /**
     * @param _parameter Parameter as passed by the eFaps API
     * @return Return List with QueryBuilders
     * @throws EFapsException on error
     */
    public List<QueryBuilder> getQueryBuilders(final Parameter _parameter)
        throws EFapsException
    {
        final List<QueryBuilder> ret = new ArrayList<QueryBuilder>();

        MultiPrint_Base.LOG.debug("analysing for QueryBuilders");
        final Map<Integer, String> types = analyseProperty(_parameter, "Type");
        final Map<Integer, String> linkFroms = analyseProperty(_parameter, "LinkFrom");
        final Map<Integer, String> statusGrps = analyseProperty(_parameter, "StatusGrp");
        final Map<Integer, String> status = analyseProperty(_parameter, "Status");

        if (statusGrps.size() != status.size()) {
            final AbstractUserInterfaceObject command = (AbstractUserInterfaceObject) _parameter
                            .get(ParameterValues.UIOBJECT);
            MultiPrint_Base.LOG.error("Map for StatusGrp and Status are of different size. Command: {}",
                            command.getName());
            throw new EFapsException(getClass(), "StatusSizes", statusGrps, status);
        }

        for (final Entry<Integer, String> typeEntry : types.entrySet()) {
            final Type type = Type.get(typeEntry.getValue());
            if (type == null) {
                final AbstractUserInterfaceObject command = (AbstractUserInterfaceObject) _parameter
                                .get(ParameterValues.UIOBJECT);
                MultiPrint_Base.LOG.error("Type cannot be found for name: {}. Command: {}", typeEntry.getValue(),
                                command.getName());
                throw new EFapsException(getClass(), "type", typeEntry);
            }
            final QueryBuilder queryBldr = new QueryBuilder(type);
            ret.add(queryBldr);

            if (linkFroms.containsKey(typeEntry.getKey())) {
                queryBldr.addWhereAttrEqValue(linkFroms.get(typeEntry.getKey()),
                                getInstance4LinkFrom(_parameter).getId());
            }
            final List<Long> statusIds = new ArrayList<Long>();
            for (int i = 0; i < 100; i++) {
                if (statusGrps.containsKey(i)) {
                    final Status stat = Status.find(statusGrps.get(i), status.get(i));
                    if (stat == null) {
                        final AbstractUserInterfaceObject command = (AbstractUserInterfaceObject) _parameter
                                        .get(ParameterValues.UIOBJECT);
                        MultiPrint_Base.LOG.error("Status Definition invalid. Command: {}, Index: {}",
                                        command.getName(), typeEntry.getKey());
                        throw new EFapsException(getClass(), "Status", typeEntry);
                    } else {
                        statusIds.add(stat.getId());
                    }
                } else if (i > 0) {
                    break;
                }
            }
            if (!statusIds.isEmpty()) {
                queryBldr.addWhereAttrEqValue(getStatusAttribute4Type(_parameter, type), statusIds.toArray());
            }
        }
        return ret;
    }

    /**
     * @param _parameter Parameter as passed by the eFaps API
     * @return Instance use for the whre on Linkfroms
     * @throws EFapsException on error
     */
    protected Instance getInstance4LinkFrom(final Parameter _parameter)
        throws EFapsException
    {
        return _parameter.getInstance();
    }

    /**
     * Get the Status attribute name of a type by searching in the parent types.
     * @param _parameter Parameter as passed by the eFaps API
     * @param _type Type to use
     * @return name of a StatusAttribute
     * @throws EFapsException on error
     */
    protected String getStatusAttribute4Type(final Parameter _parameter,
                                             final Type _type)
        throws EFapsException
    {
        Type type = _type;
        Attribute attr = type.getStatusAttribute();
        while (attr == null) {
            type = type.getParentType();
            attr = type.getStatusAttribute();
        }
        return attr.getName();
    }

    /**
     * @param _parameter    Parameter as passes by the eFaps API
     * @param _queryBldr    QueryBuilder to add to
     * @throws EFapsException on error
     */
    protected void add2QueryBldr(final Parameter _parameter,
                                 final QueryBuilder _queryBldr)
        throws EFapsException
    {

    }

    /**
     * Method to get the List of instances.
     *
     * @param _parameter parameter from the eFaps API
     * @param _filter map of filters
     * @param _queryBldr QueryBuilder used to get the instances
     * @return List of instance
     * @throws EFapsException on error
     */
    protected boolean analyzeTable(final Parameter _parameter,
                                   final Map<?, ?> _filter,
                                   final QueryBuilder _queryBldr)
        throws EFapsException
    {
        return analyzeTable(_parameter, _filter, _queryBldr, Type.get(_queryBldr.getTypeUUID()));
    }

    /**
     * Method to get the List of instances.
     *
     * @param _parameter parameter from the eFaps API
     * @param _filter map of filters
     * @param _queryBldr QueryBuilder used to get the instances
     * @param _type type the query is based on
     * @return List of instance
     * @throws EFapsException on error
     */
    protected boolean analyzeTable(final Parameter _parameter,
                                   final Map<?, ?> _filter,
                                   final QueryBuilder _queryBldr,
                                   final Type _type)
        throws EFapsException
    {
        boolean exec = true;
        if (_filter != null && _filter.size() > 0) {
            final AbstractCommand command = (AbstractCommand) _parameter.get(ParameterValues.UIOBJECT);
            // filtering
            for (final Entry<?, ?> entry : _filter.entrySet()) {
                final String fieldName = (String) entry.getKey();
                final Field field = command.getTargetTable().getField(fieldName);
                if (field.getFilter().getType().equals(Filter.Type.FREETEXT)) {
                    String attrName = field.getAttribute();
                    String[] attrNames = null;
                    if (field.getFilter().getAttributes() != null) {
                        if (field.getFilter().getAttributes().contains(",")) {
                            attrNames = field.getFilter().getAttributes().split(",");
                        } else {
                            attrName = field.getFilter().getAttributes();
                        }
                    }
                    if (attrNames != null) {
                        exec = addInsideRangeFilter(entry, _queryBldr, _type, attrNames, field);
                    } else {
                        if (attrName != null && _type.getAttribute(attrName) != null) {
                            exec = addFilter(entry, _queryBldr, _type, attrName, field);
                        } else {
                            exec = addFilter4Select(entry, _queryBldr, _type, field.getSelect(), field);
                        }
                    }
                    if (!exec) {
                        break;
                    }
                } else if (field.getFilter().getType().equals(Filter.Type.CLASSIFICATION)) {
                    exec = addClassFilter(entry, _queryBldr, _type, field);
                }
            }
        }
        return exec;
    }

    /**
     * Method to add a Filter for a inside Range of two attributes.
     *
     * @param _entry entry to be evaluated
     * @param _queryBldr QueryBuilder used to get the instances
     * @param _type type for the query
     * @param _field field the filter belongs to
     * @return true if the query must be executed, else false
     * @throws EFapsException on error
     */
    protected boolean addClassFilter(final Entry<?, ?> _entry,
                                     final QueryBuilder _queryBldr,
                                     final Type _type,
                                     final Field _field)
        throws EFapsException
    {
        final Map<?, ?> inner = (Map<?, ?>) _entry.getValue();
        boolean ret = false;
        if (inner != null) {
            if (inner.containsKey("list")) {
                final Set<?> list = (Set<?>) inner.get("list");
                final Set<Classification> filters = new HashSet<Classification>();
                final Set<Classification> remove = new HashSet<Classification>();
                for (final Object obj : list) {
                    if (obj != null) {
                        filters.add((Classification) Type.get((UUID) obj));
                    }
                }

                for (final Classification clazz : filters) {
                    for (final Classification child : clazz.getChildClassifications()) {
                        if (filters.contains(child)) {
                            remove.add(clazz);
                            break;
                        }
                    }
                }
                filters.removeAll(remove);
                if (!filters.isEmpty()) {
                    _queryBldr.addWhereClassification(filters.toArray(new Classification[filters.size()]));
                    ret = true;
                }
            }
        }
        return ret;
    }

    /**
     * Method to add a Filter for a inside Range of two attributes.
     *
     * @param _entry entry to be evaluated
     * @param _queryBldr QueryBuilder used to get the instances
     * @param _type type for the query
     * @param _attrNames names of the attributes
     * @param _field field the filter belongs to
     * @return true if the query must be executed, else false
     * @throws EFapsException on error
     */
    protected boolean addInsideRangeFilter(final Entry<?, ?> _entry,
                                           final QueryBuilder _queryBldr,
                                           final Type _type,
                                           final String[] _attrNames,
                                           final Field _field)
        throws EFapsException
    {
        boolean ret = true;
        final Attribute attr1;
        if (_attrNames[0].contains("/")) {
            attr1 = Attribute.get(_attrNames[0]);
        } else {
            attr1 = _type.getAttribute(_attrNames[0]);
        }
        final Attribute attr2;
        if (_attrNames[0].contains("/")) {
            attr2 = Attribute.get(_attrNames[1]);
        } else {
            attr2 = _type.getAttribute(_attrNames[1]);
        }
        final UUID attrTypeUUId = attr1.getAttributeType().getUUID();
        final Map<?, ?> inner = (Map<?, ?>) _entry.getValue();
        final String from = (String) inner.get("from");
        final String to = (String) inner.get("to");
        if ((from == null || to == null) && _field.getFilter().getDefaultValue() == null) {
            if (from != null && to == null && !from.isEmpty()) {
                _queryBldr.addWhereAttrMatchValue(attr1, from).setIgnoreCase(true);
            } else {
                ret = false;
            }
        } else {
            // Date or DateTime
            if (UUID.fromString("68ce3aa6-e3e8-40bb-b48f-2a67948c2e7e").equals(attrTypeUUId)
                            || UUID.fromString("e764db0f-70f2-4cd4-b2fe-d23d3da72f78").equals(attrTypeUUId)) {
                DateTime dateFrom = null;
                DateTime dateTo = null;
                if ((from == null) || (to == null)) {
                    final DateTime[] dates = (DateTime[]) getFilter(_field);
                    dateTo = dates[0];
                    dateFrom = dateTo.plusMinutes(2);
                } else {
                    dateFrom = DateTimeUtil.translateFromUI(from).minusMinutes(1);
                    dateTo = DateTimeUtil.translateFromUI(from).plusMinutes(1);
                }
                _queryBldr.addWhereAttrLessValue(attr1, dateFrom);
                _queryBldr.addWhereAttrGreaterValue(attr2, dateTo);
            }
        }
        return ret;
    }

    /**
     * Method to add a Filter for one attribute.
     *
     * @param _entry entry to be evaluated
     *@param _queryBldr QueryBuilder used to get the instances
     * @param _type type for the query
     * @param _attrName name of the attribute
     * @param _field field the filter belongs to
     * @return true if the query must be executed, else false
     * @throws EFapsException on error
     */
    protected boolean addFilter(final Entry<?, ?> _entry,
                                final QueryBuilder _queryBldr,
                                final Type _type,
                                final String _attrName,
                                final Field _field)
        throws EFapsException
    {
        boolean ret = true;
        final Attribute attr = _type.getAttribute(_attrName);
        final UUID attrTypeUUId = attr.getAttributeType().getUUID();
        final Map<?, ?> inner = (Map<?, ?>) _entry.getValue();
        final String from = (String) inner.get("from");
        final String to = (String) inner.get("to");
        // Date or DateTime
        if (UUID.fromString("68ce3aa6-e3e8-40bb-b48f-2a67948c2e7e").equals(attrTypeUUId)
                        || UUID.fromString("e764db0f-70f2-4cd4-b2fe-d23d3da72f78").equals(attrTypeUUId)) {
            DateTime dateFrom = null;
            DateTime dateTo = null;
            if ((from == null || to == null) ) {
                final DateTime[] dates = (DateTime[]) getFilter(_field);
                dateFrom = dates[0];
                dateTo = dates[1];
            } else {
                dateFrom = DateTimeUtil.translateFromUI(from).minusSeconds(1);
                dateTo = DateTimeUtil.translateFromUI(to).plusDays(1);
            }
            _queryBldr.addWhereAttrGreaterValue(_attrName, dateFrom);
            _queryBldr.addWhereAttrLessValue(_attrName, dateTo);
        } else {
            if (from != null && !from.isEmpty()) {
                _queryBldr.addWhereAttrMatchValue(_attrName, from).setIgnoreCase(true);
            } else {
                final String[] filter = (String[]) getFilter(_field);
                if (filter != null) {
                    _queryBldr.addWhereAttrMatchValue(_attrName, filter[0]).setIgnoreCase(true);
                } else {
                    ret = false;
                }
            }
        }
        return ret;
    }

    /**
     * Method to add a Filter for one select.
     *
     * @param _entry entry to be evaluated
     *@param _queryBldr QueryBuilder used to get the instances
     * @param _type type for the query
     * @param _select of the field
     * @param _field field the filter belongs to
     * @return true if the query must be executed, else false
     * @throws EFapsException on error
     */
    protected boolean addFilter4Select(final Entry<?, ?> _entry,
                                       final QueryBuilder _queryBldr,
                                       final Type _type,
                                       final String _select,
                                       final Field _field)
        throws EFapsException
    {
        boolean ret = true;
        final Map<?, ?> inner = (Map<?, ?>) _entry.getValue();
        final String from = (String) inner.get("from");
        final boolean validFrom = from != null && !from.isEmpty() ? true : false;
        final String to = (String) inner.get("to");
        final boolean validTo = to != null && !to.isEmpty() ? true : false;
        final Object[] filter = getFilter(_field);

        if ((validFrom || (!validFrom && filter != null))
                        || ((validFrom && validTo) || (!validFrom && !validTo && filter != null))) {
            final String[] parts = _select.split("\\.");
            final List<String> lstParts = new ArrayList<String>();
            for (final String part : parts) {
                lstParts.add(part);
            }
            if (_select.startsWith("class")) {
                final String typeStr = lstParts.get(0).substring(6, lstParts.get(0).length() - 1);
                final Classification classification = (Classification) Type.get(typeStr);
                final AttributeQuery attrQuery = evaluateFilterSelect(lstParts, 1, classification, from, to, filter);

                _queryBldr.addWhereAttrInQuery("ID", attrQuery);
            } else if (_select.startsWith("linkto")) {
                final String attrName = lstParts.get(0).substring(7, lstParts.get(0).length() - 1);
                final Attribute attr = _type.getAttribute(attrName);
                final AttributeQuery attrQuery = evaluateFilterSelect(lstParts, 1, attr.getLink(), from, to, filter);

                _queryBldr.addWhereAttrInQuery(attrName, attrQuery);
            } else if (_select.startsWith("linkfrom")) {
                final String[] parts2 = lstParts.get(0).split("#");
                final String typeStr = parts2[0].substring(9);
                final Type type = Type.get(typeStr);
                final String attrName = parts2[1].substring(0, parts2[1].length() - 1);
                final AttributeQuery attrQuery2 = evaluateFilterSelect(lstParts, 1, type, from, to, filter);

                final QueryBuilder attrQueryBldr = new QueryBuilder(type);
                attrQueryBldr.addWhereAttrInQuery("ID", attrQuery2);
                final AttributeQuery attrQuery = attrQueryBldr.getAttributeQuery(attrName);

                _queryBldr.addWhereAttrInQuery("ID", attrQuery);
            }
        } else {
            ret = false;
        }

        return ret;
    }

    protected AttributeQuery evaluateFilterSelect(final List<String> _lstParts,
                                              final Integer _dir,
                                              final Type _type,
                                              final String _from,
                                              final String _to,
                                              final Object[] _filter)
        throws EFapsException
    {
        AttributeQuery attrQuery = null;
        if (_lstParts.get(_dir).startsWith("linkto")) {
            final String attrName = _lstParts.get(_dir).substring(7, _lstParts.get(_dir).length() - 1);
            final Attribute attr = _type.getAttribute(attrName);

            final AttributeQuery attrQueryAux = evaluateFilterSelect(_lstParts, _dir + 1, attr.getLink(), _from, _to, _filter);
            final QueryBuilder attrQueryBldr = new QueryBuilder(_type);
            attrQueryBldr.addWhereAttrInQuery(attrName, attrQueryAux);

            attrQuery = attrQueryBldr.getAttributeQuery("ID");
        } else if (_lstParts.get(_dir).startsWith("linkfrom")) {

        } else if (_lstParts.get(_dir).startsWith("attribute")) {
            final String attrName = _lstParts.get(_dir).substring(10, _lstParts.get(_dir).length() - 1);
            final QueryBuilder attrQueryBldr = new QueryBuilder(_type);
            final Attribute attr = _type.getAttribute(attrName);
            final UUID attrTypeUUId = attr.getAttributeType().getUUID();
            // Date or DateTime
            if (UUID.fromString("68ce3aa6-e3e8-40bb-b48f-2a67948c2e7e").equals(attrTypeUUId)
                            || UUID.fromString("e764db0f-70f2-4cd4-b2fe-d23d3da72f78").equals(attrTypeUUId)) {
                DateTime dateFrom = null;
                DateTime dateTo = null;
                if (_from == null || _to == null ) {
                    final DateTime[] dates = (DateTime[]) _filter;
                    dateFrom = dates[0];
                    dateTo = dates[1];
                } else {
                    dateFrom = DateTimeUtil.translateFromUI(_from).minusSeconds(1);
                    dateTo = DateTimeUtil.translateFromUI(_to).plusDays(1);
                }
                attrQueryBldr.addWhereAttrGreaterValue(attrName, dateFrom);
                attrQueryBldr.addWhereAttrLessValue(attrName, dateTo);
            } else {
                if (_from != null && !_from.isEmpty()) {
                    attrQueryBldr.addWhereAttrMatchValue(attrName, _from).setIgnoreCase(true);
                } else {
                    final String[] filter = (String[]) _filter;
                    attrQueryBldr.addWhereAttrMatchValue(attrName, filter[0]).setIgnoreCase(true);
                }
            }

            if (_type instanceof Classification) {
                attrQuery = attrQueryBldr.getAttributeQuery(((Classification)_type).getLinkAttributeName());
            } else {
                attrQuery = attrQueryBldr.getAttributeQuery("ID");
            }
        } else if (_lstParts.get(_dir).startsWith("class")) {
            final String typeStr = _lstParts.get(_dir).substring(6, _lstParts.get(_dir).length() - 1);
            final Classification classification = (Classification) Type.get(typeStr);

            final AttributeQuery attrQueryAux = evaluateFilterSelect(_lstParts, _dir + 1, classification, _from, _to, _filter);

            final QueryBuilder attrQueryBldr = new QueryBuilder(_type);
            attrQueryBldr.addWhereAttrInQuery("ID", attrQueryAux);
            attrQuery = attrQueryBldr.getAttributeQuery("ID");
        }
        return attrQuery;
    }

    /**
     * @param _field Field the date is wanted for
     * @return datetime array
     * @throws EFapsException on error
     */
    protected Object[] getFilter(final Field _field)
        throws EFapsException
    {
        Object[] ret = null;
        final String filter = _field.getFilter().getDefaultValue();
        if (filter != null) {
            final String[] parts = filter.split(":");
            final String range = parts[0];
            final int fromSub = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
            final int rangeCount = parts.length > 2 ? Integer.parseInt(parts[2]) : 1;
            DateTime dateFrom = new DateTime();
            DateTime dateTo = new DateTime();
            if (range != null) {
                final FilterDefault def = FilterDefault.valueOf(range.toUpperCase());
                DateMidnight tmp = DateTimeUtil.translateFromUI(new DateTime()).toDateMidnight();
                switch (def) {
                    case TODAY:
                        dateFrom = tmp.toDateTime().minusDays(fromSub).minusMinutes(1);
                        dateTo = dateFrom.plusDays(rangeCount).plusSeconds(1);
                        ret = new DateTime[] { dateFrom, dateTo };
                        break;
                    case WEEK:
                        tmp = tmp.minusDays(tmp.getDayOfWeek() - 1);
                        dateFrom = tmp.toDateTime().minusWeeks(fromSub).minusMinutes(1);
                        dateTo = dateFrom.toDateTime().plusWeeks(rangeCount);
                        ret = new DateTime[] { dateFrom, dateTo };
                        break;
                    case MONTH:
                        tmp = tmp.minusDays(tmp.getDayOfMonth() - 1);
                        dateFrom = tmp.toDateTime().minusMonths(fromSub).minusMinutes(1);
                        dateTo = dateFrom.toDateTime().plusMonths(rangeCount);
                        ret = new DateTime[] { dateFrom, dateTo };
                        break;
                    case YEAR:
                        tmp = tmp.minusDays(tmp.getDayOfYear() - 1);
                        dateFrom = tmp.toDateTime().minusYears(fromSub).minusMinutes(1);
                        dateTo = dateFrom.toDateTime().plusYears(rangeCount);
                        ret = new DateTime[] { dateFrom, dateTo };
                        break;
                    case ALL:
                        ret = new String[] {"*"};
                        break;
                    case NONE:
                        break;
                    default:
                        ret = new String[] {range + "*"};
                        break;
                }
            }
        }
        return ret;
    }

    /**
     * WILL BE REMOVED!!
     *
     * @param _parameter Parameter as passed by the eFasp API
     * @return Return with List of instances
     * @throws EFapsException on error
     */
    private List<Instance> tobeRemoved(final Parameter _parameter)
        throws EFapsException
    {
        final AbstractUserInterfaceObject command = (AbstractUserInterfaceObject) _parameter
                        .get(ParameterValues.UIOBJECT);
        MultiPrint_Base.LOG.warn("Command: '{}' uses deprecated API defintion for MultiPrint.", command.getName());

        final List<Instance> instances = new ArrayList<Instance>();
        final Map<?, ?> properties = (Map<?, ?>) _parameter.get(ParameterValues.PROPERTIES);
        final Instance instance = _parameter.getInstance();

        final Map<?, ?> filter = (Map<?, ?>) _parameter.get(ParameterValues.OTHERS);
        final String typesStr = (String) properties.get("Types");
        final String linkFromsStr = (String) properties.get("LinkFroms");
        final String selectStr = (String) properties.get("Selects");

        final boolean includeChildTypes = !"false".equalsIgnoreCase((String) properties.get("ExpandChildTypes"));

        if (MultiPrint_Base.LOG.isDebugEnabled()) {
            MultiPrint_Base.LOG.debug("Types: {}\n LinkFroms: {}\n Selects: {}\n ExpandChildTypes: {}",
                            new Object[] { typesStr, linkFromsStr, selectStr, includeChildTypes });
        }

        final Map<QueryBuilder, Boolean> queryBldrs2exec = new LinkedHashMap<QueryBuilder, Boolean>();

        final String[] typesArray = typesStr.split(";");
        for (int x = 0; x < typesArray.length; x++) {
            final Type type = Type.get(typesArray[x]);
            final QueryBuilder queryBldr = new QueryBuilder(type);
            if (linkFromsStr != null) {
                final String[] linkFroms = linkFromsStr.split(";");
                queryBldr.addWhereAttrEqValue(linkFroms[x], instance.getId());
            }
            add2QueryBldr(_parameter, queryBldr);
            queryBldrs2exec.put(queryBldr, analyzeTable(_parameter, filter, queryBldr, type));
        }
        int count = 0;
        for (final Entry<QueryBuilder, Boolean> entry : queryBldrs2exec.entrySet()) {
            if (entry.getValue()) {
                if (selectStr != null) {
                    final String[] selects = selectStr.split(":");
                    final MultiPrintQuery multi = entry.getKey().getPrint();
                    if (count < selects.length) {
                        multi.addSelect(selects[count]);
                    }
                    multi.executeWithoutAccessCheck();
                    instances.addAll(count < selects.length ? multi.getInstances4Select(selects[count])
                                    : multi.getInstanceList());
                } else {
                    final InstanceQuery query = entry.getKey().getQuery();
                    query.setIncludeChildTypes(includeChildTypes);
                    instances.addAll(query.execute());
                }
            }
            count++;
        }
        return instances;
    }
}

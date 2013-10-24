/*
 * Copyright 2003 - 2012 The eFaps Team
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

package org.efaps.esjp.ui.structurbrowser;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.efaps.admin.datamodel.Type;
import org.efaps.admin.event.EventExecution;
import org.efaps.admin.event.Parameter;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.event.Return;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.program.esjp.EFapsRevision;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.AbstractUserInterfaceObject;
import org.efaps.db.AttributeQuery;
import org.efaps.db.Instance;
import org.efaps.db.InstanceQuery;
import org.efaps.db.QueryBuilder;
import org.efaps.esjp.common.AbstractCommon;
import org.efaps.ui.wicket.models.cell.UIStructurBrowserTableCell;
import org.efaps.ui.wicket.models.objects.UIStructurBrowser;
import org.efaps.ui.wicket.models.objects.UIStructurBrowser.ExecutionStatus;
import org.efaps.util.EFapsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO description!
 *
 * @author The eFasp Team
 * @version $Id$
 */
@EFapsUUID("d6548826-830b-4540-a46d-d861c3f21f15")
@EFapsRevision("$Rev$")
public abstract class StandartStructurBrowser_Base
extends AbstractCommon
implements EventExecution
{
    /**
     * Logger for this class.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(StandartStructurBrowser_Base.class);

    /**
     * @param _parameter Parameter
     * @throws EFapsException on error
     * @return Return
     */
    @Override
    public Return execute(final Parameter _parameter)
        throws EFapsException
    {
        Return ret = null;

        final UIStructurBrowser strBro = (UIStructurBrowser) _parameter.get(ParameterValues.CLASS);
        if (strBro != null) {
            final ExecutionStatus status = strBro.getExecutionStatus();
            if (status.equals(ExecutionStatus.EXECUTE)) {
                ret = internalExecute(_parameter);
            } else if (status.equals(ExecutionStatus.ALLOWSCHILDREN)) {
                ret = allowChildren(_parameter);
            } else if (status.equals(ExecutionStatus.ALLOWSITEM)) {
                ret = allowItem(_parameter);
            } else if (status.equals(ExecutionStatus.CHECKFORCHILDREN)) {
                ret = checkForChildren(_parameter);
            } else if (status.equals(ExecutionStatus.ADDCHILDREN)) {
                ret = addChildren(_parameter);
            } else if (status.equals(ExecutionStatus.SORT)) {
                ret = sort(_parameter);
            } else if (status.equals(ExecutionStatus.CHECKHIDECOLUMN4ROW)) {
                ret = checkHideColumn4Row(_parameter);
            } else if (status.equals(ExecutionStatus.NODE_REMOVE)) {
                ret = onNodeRemove(_parameter);
            } else if (status.equals(ExecutionStatus.NODE_INSERTCHILDITEM)) {
                ret = onNodeInsertChildItem(_parameter);
            } else if (status.equals(ExecutionStatus.NODE_INSERTITEM)) {
                ret = onNodeInsertItem(_parameter);
            } else if (status.equals(ExecutionStatus.NODE_INSERTCHILDFOLDER)) {
                ret = onNodeInsertChildFolder(_parameter);
            } else if (status.equals(ExecutionStatus.GETJAVASCRIPT4TARGET)) {
                ret = getJavaScript4Target(_parameter);
            }
        }
        return ret;
    }

    /**
     * Method is called after the insert etc. of a new node in edit mode to
     * get a JavaScript that will be appended to the AjaxTarget.
     * In this Step the values for the StructurBrowsers also can be altered.
     *
     * @param _parameter as passed from eFaps API.
     * @return Return with SNIPPLET
     * @throws EFapsException on error
     */
    protected Return getJavaScript4Target(final Parameter _parameter)
        throws EFapsException
    {
        final Return ret = new Return();
        // EXAMPLE to be done by implementation
        final StringBuilder js = new StringBuilder();
        // js.append("document.getElementById..");
        ret.put(ReturnValues.SNIPLETT, js.toString());
        return ret;
    }

    /**
     * Method to get a list of instances the StructurBrowser will be filled
     * with.
     * @param _parameter as passed from eFaps API.
     * @return Return with instances
     * @throws EFapsException on error
     */
    protected Return internalExecute(final Parameter _parameter)
        throws EFapsException
    {
        final Return ret = new Return();
        final Instance instance = _parameter.getInstance();
        final Map<Instance, Boolean> tree = new LinkedHashMap<Instance, Boolean>();
        final Map<?, ?> properties = (Map<?, ?>) _parameter.get(ParameterValues.PROPERTIES);

        final String typesStr = (String) properties.get("Types");
        if (typesStr != null) {
            final AbstractUserInterfaceObject command = (AbstractUserInterfaceObject) _parameter
                            .get(ParameterValues.UIOBJECT);
            StandartStructurBrowser_Base.LOG.error("Command: '{}' uses deprecated API defintion for StructurBrowser.",
                            command.getName());
        }

        final Map<Integer, String> types = analyseProperty(_parameter, "Type");
        final Map<Integer, String> linkFroms = analyseProperty(_parameter, "LinkFrom");
        final Map<Integer, String> expandChildTypes = analyseProperty(_parameter, "ExpandChildType");
        final Map<Integer, String> excludeConnectedTypes = analyseProperty(_parameter, "ExcludeConnectedType");
        final Map<Integer, String> excludeConnectedLinkFroms = analyseProperty(_parameter, "ExcludeConnectedLinkFrom");

        StandartStructurBrowser_Base.LOG.debug("Types: {}, LinkFroms: {}, ExpandChildTypes: {}",
                        new Object[] { types, linkFroms, expandChildTypes });
        if (!types.isEmpty()) {
            for (final Entry<Integer, String> entry : types.entrySet()) {
                final Type type = Type.get(entry.getValue());
                final QueryBuilder queryBldr = new QueryBuilder(type);
                if (!linkFroms.isEmpty() && instance != null && instance.isValid()) {
                    String linkfrom = linkFroms.get(entry.getKey());
                    if (linkfrom == null && entry.getKey() > 0) {
                        linkfrom = linkFroms.get(0);
                    }
                    queryBldr.addWhereAttrEqValue(linkfrom, instance.getId());
                }
                addCriteria(_parameter, queryBldr);

                if (!excludeConnectedTypes.isEmpty() && !excludeConnectedLinkFroms.isEmpty()) {
                    final String excludeConnTypesStr = excludeConnectedTypes.containsKey(entry.getKey())
                                    ? excludeConnectedTypes.get(entry.getKey()) : excludeConnectedTypes.get(0);
                    final String excludeConnLinkFromsStr = excludeConnectedLinkFroms.containsKey(entry.getKey())
                                    ? excludeConnectedLinkFroms.get(entry.getKey()) : excludeConnectedLinkFroms.get(0);
                    final String[] excludeConnTypesArr = excludeConnTypesStr.split(";");
                    final String[] excludeConnLinkFromsArr = excludeConnLinkFromsStr.split(";");
                    int cont = 0;
                    for (final String excludeConnType : excludeConnTypesArr) {
                        final QueryBuilder attrQueryBldr = new QueryBuilder(Type.get(excludeConnType));
                        final AttributeQuery attrQuery = attrQueryBldr.getAttributeQuery(excludeConnLinkFromsArr[cont]);
                        queryBldr.addWhereAttrNotInQuery("ID", attrQuery);
                        cont++;
                    }
                }

                final InstanceQuery query = queryBldr.getQuery();
                final boolean includeChildTypes = expandChildTypes.isEmpty() ? true :
                                !"false".equalsIgnoreCase(expandChildTypes.containsKey(entry.getKey())
                                                ? expandChildTypes.get(entry.getKey()) : expandChildTypes.get(0));
                query.setIncludeChildTypes(includeChildTypes);

                query.execute();
                while (query.next()) {
                    tree.put(query.getCurrentValue(), null);
                }
            }
        }
        ret.put(ReturnValues.VALUES, tree);
        return ret;
    }



    /**
     * Add additional Criteria to the QueryBuilder.
     * To be used by implementation.
     *
     * @param _parameter Parameter as passed from the eFaps API
     * @param _queryBldr QueryBuilder the criteria will be added to
     * @throws EFapsException on error
     */
    protected void addCriteria(final Parameter _parameter,
                               final QueryBuilder _queryBldr)
        throws EFapsException
    {
        // used by implementation
    }

    /**
     * Add additional Criteria to the QueryBuilder.
     * To be used by implementation.
     *
     * @param _parameter Parameter as passed from the eFaps API
     * @param _queryBldr QueryBuilder the criteria will be added to
     * @throws EFapsException on error
     */
    protected void addCriteria4Children(final Parameter _parameter,
                                        final QueryBuilder _queryBldr)
        throws EFapsException
    {
        // used by implementation
    }

    /**
     * Method to check if an instance allows children. It is used in the tree to
     * determine "folder" or an "item" must be rendered and if the checkForChildren
     * method must be executed.
     *
     * @param _parameter Parameter as passed from the eFaps API
     * @return Return with true or false
     * @throws EFapsException on error
     */
    protected Return allowChildren(final Parameter _parameter)
        throws EFapsException
    {
        final Return ret = new Return();
        ret.put(ReturnValues.TRUE, true);
        return ret;
    }

    /**
     * Method to check if an instance allows children of type item. It is used in the
     * tree to determine if "item" can be rendered. It will only be executed if
     * the {@link #allowChildren(Parameter)} method returned true.
     *
     * @param _parameter Parameter as passed from the eFaps API
     * @return Return with true or false
     * @throws EFapsException on error
     */
    protected Return allowItem(final Parameter _parameter)
        throws EFapsException
    {
        final Return ret = new Return();
        ret.put(ReturnValues.TRUE, true);
        return ret;
    }

    /**
     * Method to check if an instance has children. It is used in the tree to
     * determine if a "plus" to open the children must be rendered.
     *
     * @param _parameter Parameter as passed from the eFaps API
     * @return Return with true or false
     * @throws EFapsException on error
     */
    protected Return checkForChildren(final Parameter _parameter)
        throws EFapsException
    {
        final Return ret = new Return();
        final Map<Instance, Boolean> map = getChildren(_parameter, true);
        if (!map.isEmpty()) {
            ret.put(ReturnValues.TRUE, true);
        }
        return ret;
    }

    /**
     * @param _parameter    Parameter as passed from the eFaps API
     * @param _check        check only?
     * @return map with instances
     * @throws EFapsException on error
     */
    protected Map<Instance, Boolean> getChildren(final Parameter _parameter,
                                                 final boolean _check)
        throws EFapsException
    {
        final Map<Instance, Boolean> ret = new LinkedHashMap<Instance, Boolean>();

        final Instance instance = _parameter.getInstance();
        final Map<?, ?> properties = (Map<?, ?>) _parameter.get(ParameterValues.PROPERTIES);

        final String typesStr = (String) properties.get("Child_Types");
        if (typesStr != null) {
            final AbstractUserInterfaceObject command = (AbstractUserInterfaceObject) _parameter
                            .get(ParameterValues.UIOBJECT);
            StandartStructurBrowser_Base.LOG.error("Command: '{}' uses deprecated API defintion for StructurBrowser.",
                            command.getName());
        }

        final Map<Integer, String> types = analyseProperty(_parameter, "Child_Type");
        final Map<Integer, String> linkFroms = analyseProperty(_parameter, "Child_LinkFrom");
        final Map<Integer, String> expandChildTypes = analyseProperty(_parameter, "Child_ExpandChildType");

        if (StandartStructurBrowser_Base.LOG.isDebugEnabled()) {
            StandartStructurBrowser_Base.LOG.debug("Child_Types: {}, Child_LinkFroms: {}, Child_ExpandChildTypes: {}",
                            new Object[] { types, linkFroms, expandChildTypes });
        }

        if (!types.isEmpty()) {
            for (final Entry<Integer, String> entry : types.entrySet()) {
                final Type type = Type.get(entry.getValue());
                final QueryBuilder queryBldr = new QueryBuilder(type);
                if (!linkFroms.isEmpty() && instance != null && instance.isValid()) {
                    String linkfrom = linkFroms.get(entry.getKey());
                    if (linkfrom == null && entry.getKey() > 0) {
                        linkfrom = linkFroms.get(0);
                    }
                    queryBldr.addWhereAttrEqValue(linkfrom, instance.getId());
                }
                addCriteria4Children(_parameter, queryBldr);
                final InstanceQuery query = queryBldr.getQuery();
                final boolean includeChildTypes = expandChildTypes.isEmpty() ? true :
                                !"false".equalsIgnoreCase(expandChildTypes.containsKey(entry.getKey())
                                                ? expandChildTypes.get(entry.getKey()) : expandChildTypes.get(0));
                query.setIncludeChildTypes(includeChildTypes);
                if (_check) {
                    query.setLimit(1);
                }
                query.execute();
                while (query.next()) {
                    ret.put(query.getCurrentValue(), null);
                }
            }
        }
        return ret;
    }

    /**
     * Method to add the children to an instance. It is used to expand the
     * children of a node in the tree.
     *
     * @param _parameter Paraemter as passed from the eFasp API
     * @return Return with instances
     * @throws EFapsException on error
     */
    protected Return addChildren(final Parameter _parameter)
        throws EFapsException
    {
        final Return ret = new Return();
        final Map<Instance, Boolean> map = getChildren(_parameter, false);
        ret.put(ReturnValues.VALUES, map);
        return ret;
    }

    /**
     * Method is called from the StructurBrowser in edit mode before rendering
     * the columns for row to be able to hide the columns for different rows by
     * setting the cell model to hide.
     * In this implementation all columns except the StructurBrowser column is
     * hidden for the folders.
     * @param _parameter Paraemter as passed from the eFasp API
     * @return empty Return;
     * @throws EFapsException on error
     */
    protected Return checkHideColumn4Row(final Parameter _parameter)
        throws EFapsException
    {
        final UIStructurBrowser strBrws = (UIStructurBrowser) _parameter.get(ParameterValues.CLASS);
        for (final UIStructurBrowserTableCell cell : strBrws.getColumns()) {
            if (strBrws.isAllowChildren() && !cell.isBrowserField()) {
                cell.setHide(true);
            }
        }
        return new Return();
    }

    /**
     * Executed on insert of a folder node as an listener and does not
     * effect directly the tree, but allows to manipulate it.
     *
     * @param _parameter    _parameter as passed from eFaps API.
     * @return empty Return
     * @throws EFapsException on error
     */
    protected Return onNodeInsertChildFolder(final Parameter _parameter)
        throws EFapsException
    {
        return new Return();
    }

    /**
     * Executed on insert of an new item node as an listener and does not
     * effect directly the tree, but allows to manipulate it.
     *
     * @param _parameter    _parameter as passed from eFaps API.
     * @return empty Return
     * @throws EFapsException on error
     */
    protected Return onNodeInsertItem(final Parameter _parameter)
        throws EFapsException
    {
        return new Return();
    }

    /**
     * Executed on insert of an item as child as an listener and does not
     * effect directly the tree, but allows to manipulate it.
     *
     * @param _parameter    _parameter as passed from eFaps API.
     * @return empty Return
     * @throws EFapsException on error
     */
    protected Return onNodeInsertChildItem(final Parameter _parameter)
        throws EFapsException
    {
        return new Return();
    }

    /**
     * Executed on removal of a node as an listener and does not
     * effect directly the tree, but allows to manipulate it.
     *
     * @param _parameter    _parameter as passed from eFaps API.
     * @return empty Return
     * @throws EFapsException on error
     */
    protected Return onNodeRemove(final Parameter _parameter)
        throws EFapsException
    {
        return new Return();
    }

    /**
     * Method to sort the values of the StructurBrowser.
     *
     * @param _parameter Paraemter as passed from the eFasp API
     * @return empty Return
     * @throws EFapsException on error
     */
    protected Return sort(final Parameter _parameter)
        throws EFapsException
    {
        final UIStructurBrowser strBro = (UIStructurBrowser) _parameter.get(ParameterValues.CLASS);

        Collections.sort(strBro.getChildren(), new Comparator<UIStructurBrowser>()
        {
            @Override
            @SuppressWarnings({ "rawtypes", "unchecked" })
            public int compare(final UIStructurBrowser _structurBrowser1,
                               final UIStructurBrowser _structurBrowser2)
            {
                final Comparable value1 = getComparable(_parameter, _structurBrowser1);
                final Comparable value2 = getComparable(_parameter, _structurBrowser2);
                return value1.compareTo(value2);
            }
        });
        return new Return();
    }

    /**
     * @param _parameter Paraemter as passed from the eFasp API
     * @return Comparable value
     */
    @SuppressWarnings("rawtypes")
    protected Comparable getComparable(final Parameter _parameter,
                                       final UIStructurBrowser _structurBrowser)
    {
        final StringBuilder ret = new StringBuilder();
        ret.append(_structurBrowser.getLabel());
        return ret.toString();
    }
}

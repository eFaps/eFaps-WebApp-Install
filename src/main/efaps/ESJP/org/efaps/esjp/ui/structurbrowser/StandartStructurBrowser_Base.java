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

package org.efaps.esjp.ui.structurbrowser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.efaps.admin.datamodel.Type;
import org.efaps.admin.event.EventExecution;
import org.efaps.admin.event.Parameter;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.event.Return;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.AbstractCommand;
import org.efaps.admin.ui.AbstractUserInterfaceObject;
import org.efaps.admin.ui.Command;
import org.efaps.admin.ui.Menu;
import org.efaps.api.ui.ITree;
import org.efaps.db.AttributeQuery;
import org.efaps.db.Context;
import org.efaps.db.Instance;
import org.efaps.db.InstanceQuery;
import org.efaps.db.QueryBuilder;
import org.efaps.eql.EQL;
import org.efaps.eql.builder.Print;
import org.efaps.eql.builder.Query;
import org.efaps.eql.builder.Where;
import org.efaps.esjp.common.AbstractCommon;
import org.efaps.esjp.db.InstanceUtils;
import org.efaps.ui.wicket.models.field.AbstractUIField;
import org.efaps.ui.wicket.models.objects.UIStructurBrowser;
import org.efaps.ui.wicket.models.objects.UIStructurBrowser.ExecutionStatus;
import org.efaps.ui.wicket.models.objects.grid.UIGrid;
import org.efaps.util.EFapsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author The eFasp Team
 */
@EFapsUUID("d6548826-830b-4540-a46d-d861c3f21f15")
@EFapsApplication("eFaps-WebApp")
public abstract class StandartStructurBrowser_Base
    extends AbstractCommon
    implements EventExecution
{
    /**
     * Logger for this class.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(StandartStructurBrowser.class);

    /**
     * @param _parameter Parameter
     * @throws EFapsException on error
     * @return Return
     */
    @SuppressWarnings("unchecked")
    @Override
    public Return execute(final Parameter _parameter)
        throws EFapsException
    {
        Return ret = new Return();
        final Object object = _parameter.get(ParameterValues.CLASS);
        if (object instanceof UIStructurBrowser) {
            final UIStructurBrowser strBro = (UIStructurBrowser) object;
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
        } else if (object instanceof UIGrid) {
            if (_parameter.get(ParameterValues.REQUEST_INSTANCES) != null) {
                ret = evalChildrenQuery(_parameter,
                                (Collection<Instance>) _parameter.get(ParameterValues.REQUEST_INSTANCES));
            } else {
                ret = evalMainQuery(_parameter);
            }
        } else {
            ret = ret.put(ReturnValues.INSTANCE, _parameter.getInstance());
        }
        return ret;
    }

    protected Return evalMainQuery(final Parameter _parameter)
        throws EFapsException
    {
        final Return ret = new Return();
        final var instance = _parameter.getInstance();
        final Map<Integer, String> types = analyseProperty(_parameter, "Type");
        final Map<Integer, String> linkFroms = analyseProperty(_parameter, "LinkFrom");
        final Query query = EQL.builder()
                        .print()
                        .query(types.values().stream().toArray(String[]::new));
        final Where where = query.where();
        if (!linkFroms.isEmpty() && InstanceUtils.isValid(instance)) {
            where.attribute(linkFroms.get(0)).eq(instance);
        }
        add2MainQuery(_parameter, where);
        ret.put(ReturnValues.VALUES, query.select().build());
        return ret;
    }

    protected void add2MainQuery(final Parameter _parameter, final Where _where)
        throws EFapsException
    {

    }

    protected Return evalChildrenQuery(final Parameter _parameter, final Collection<Instance> _instances)
        throws EFapsException
    {
        final Return ret = new Return();
        final Map<Integer, String> types = analyseProperty(_parameter, "Child_Type");
        final Map<Integer, String> linkFroms = analyseProperty(_parameter, "Child_LinkFrom");

        final Print print = EQL.builder()
                        .print()
                        .query(types.values().stream().toArray(String[]::new))
                        .where()
                        .attribute(linkFroms.get(0)).in(_instances)
                        .select()
                        .linkto(linkFroms.get(0)).instance().as("ParentInstance");

        add2ChildrenQuery(_parameter, print);
        ret.put(ReturnValues.VALUES, print.build());
        return ret;
    }

    protected void add2ChildrenQuery(final Parameter _parameter, final Print _print)
    {
        // to be used by implementations
    }

    /**
    protected Return executeForGrid(final Parameter _parameter)
        throws EFapsException
    {
        final Return ret = new Return();

        final StructureTree tree = new StructureTree();

        final Map<Integer, String> types = analyseProperty(_parameter, "Type");
        final Evaluator eval = EQL.printQuery(types.values().toArray(new String[types.size()]))
            .instance()
            .stmt()
            .evaluate();
        while(eval.next()) {
            tree.addChild(StructureTree.of(eval.inst()));
        }
        loadChildren(_parameter, tree.getChildren());
        ret.put(ReturnValues.VALUES, tree);
        return ret;
    }

    protected void loadChildren(final Parameter _parameter, final Collection<ITree<Instance>> _collection)
        throws EFapsException
    {
        final Map<Integer, String> types = analyseProperty(_parameter, "Child_Type");
        final Map<Integer, String> linkFroms = analyseProperty(_parameter, "Child_LinkFrom");
        final Map<Instance, ITree<Instance>> instances = _collection.stream()
                        .collect(Collectors.toMap(ITree::getNode, tree -> tree));
        for (final Entry<Integer, String> entry : types.entrySet()) {
            final Query query = EQL.query(types.values().toArray(new String[types.size()]))
                .where(EQL.where()
                            .attribute(linkFroms.get(entry.getKey())).in(instances.keySet()));

            final Evaluator eval = EQL.print(query)
                        .linkto(linkFroms.get(entry.getKey())).instance().as("1")
                        .stmt()
                        .evaluate();
            while(eval.next()) {
                final Instance childInstance = eval.inst();
                final Instance parentInstance = eval.get("1");
                instances.get(parentInstance).getChildren().add(StructureTree.of(childInstance));
            }
        }
    }
**/
    public static class StructureTree
        implements ITree<Instance>
    {

        private static final long serialVersionUID = 1L;

        private Instance node;

        private final Collection<ITree<Instance>> children = new ArrayList<>();

        @Override
        public Instance getNode()
        {
            return node;
        }

        public void addChild(final StructureTree _child)
        {
            children.add(_child);
        }

        @Override
        public Collection<ITree<Instance>> getChildren()
        {
            return children;
        }

        public static StructureTree of(final Instance _instance) {
            final StructureTree ret = new StructureTree();
            ret.node = _instance;
            return ret;
        }
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
        final Map<Instance, Boolean> tree = new LinkedHashMap<>();
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
        final Map<Instance, Boolean> ret = new LinkedHashMap<>();

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
        for (final AbstractUIField uiField : strBrws.getColumns()) {
            if (strBrws.isAllowChildren() && !strBrws.isBrowserField(uiField)) {
                uiField.setHide(true);
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

    /**
     * @param _parameter Parameter as passed by the efaps API
     * @return new Return
     * @throws EFapsException on error
     */
    @SuppressWarnings("unchecked")
    public Return expand(final Parameter _parameter)
        throws EFapsException
    {
        final String[] oids = (String[]) _parameter.get(ParameterValues.OTHERS);
        final String cmdStr = getProperty(_parameter, "StrBrwsCmd");
        AbstractCommand cmd;
        if (isUUID(cmdStr)) {
            cmd = Command.get(UUID.fromString(cmdStr));
            if (cmd == null) {
                cmd = Menu.get(UUID.fromString(cmdStr));
            }
        } else {
            cmd = Command.get(cmdStr);
            if (cmd == null) {
                cmd = Menu.get(cmdStr);
            }
        }
        final String key = cmd.getUUID() + "-" + UIStructurBrowser.USERSESSIONKEY;
        final Set<Instance> instances = new HashSet<>();
        for (final String oid :oids) {
            final Instance inst = Instance.get(oid);
            instances.add(inst);
            _parameter.put(ParameterValues.INSTANCE, inst);
            instances.addAll(getChildren4Expand(_parameter));
        }
        final Map<String, Boolean> sessMap;
        if (Context.getThreadContext().containsSessionAttribute(key)) {
            sessMap = (Map<String, Boolean>) Context.getThreadContext().getSessionAttribute(key);
        } else {
            sessMap = new HashMap<>();
        }

        for (final Instance inst : instances) {
            sessMap.put(inst.getKey(), true);
        }
        Context.getThreadContext().setSessionAttribute(key, sessMap);
        return new Return();
    }

    /**
     * Recursive method to load all children.
     * @param _parameter    Parameter as passed by the eFaps API
     * @return set of instances to be expanded
     * @throws EFapsException on errro
     */
    protected Set<Instance> getChildren4Expand(final Parameter _parameter)
        throws EFapsException
    {
        final Set<Instance> ret = new HashSet<>();
        final Map<Instance, Boolean> tmp = getChildren(_parameter, false);
        for (final Instance inst : tmp.keySet()) {
            ret.add(inst);
            _parameter.put(ParameterValues.INSTANCE, inst);
            ret.addAll(getChildren4Expand(_parameter));
        }
        return ret;
    }
}

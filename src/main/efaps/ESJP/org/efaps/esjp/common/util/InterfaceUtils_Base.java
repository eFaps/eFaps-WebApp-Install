/*
 * Copyright 2003 - 2016 The eFaps Team
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


package org.efaps.esjp.common.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.comparators.ComparatorChain;
import org.apache.commons.lang3.ArrayUtils;
import org.efaps.admin.event.Parameter;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.db.QueryBuilder;


/**
 * Interface Utilities.
 *
 * @author The eFaps Team
 */
@EFapsUUID("f5464f0e-c51c-4076-99fa-9992c1e3bdd0")
@EFapsApplication("eFaps-WebApp")
public abstract class InterfaceUtils_Base
{

    /**
     * Gets the row keys.
     *
     * @param _parameter Parameter as passed by the eFaps API
     * @param _currentkey the currentkey
     * @param _keySequence the key sequence
     * @return the row keys
     */
    protected static String[] getRowKeys(final Parameter _parameter,
                                         final String _currentkey,
                                         final String... _keySequence)
    {
        final String[] rowKeys = _parameter.getParameterValues("eFapsTRID");
        final Map<String, String[]> result = new HashMap<>();
        int idx = 0;
        for (final String key : _keySequence) {
            final String[] keyArr = _parameter.getParameterValues(key);
            result.put(key, ArrayUtils.subarray(rowKeys, idx, keyArr.length + idx));
            idx = keyArr.length;
        }
        return result.get(_currentkey);
    }

    /**
     * Append script for field update.
     *
     * @param _map the map
     * @param _script the script
     */
    protected static void appendScript4FieldUpdate(final Map<String, Object> _map,
                                                   final CharSequence _script)
    {
        InterfaceUtils_Base.add2Script("eFapsFieldUpdateJS", _map, _script, true);
    }

    /**
     * Prepend script for field update.
     *
     * @param _map the map
     * @param _script the script
     */
    protected static void prependScript4FieldUpdate(final Map<String, Object> _map,
                                                    final CharSequence _script)
    {
        InterfaceUtils_Base.add2Script("eFapsFieldUpdateJS", _map, _script, false);
    }

    /**
     * @param _key  key to add to
     * @param _map  map
     * @param _script   script to add
     * @param _append append or prepend
     */
    protected static void add2Script(final String _key,
                                     final Map<String, Object> _map,
                                     final CharSequence _script,
                                     final boolean _append)
    {
        if (_map != null) {
            final StringBuilder js = new StringBuilder();
            if (!_append) {
                js.append(_script);
            }
            if (_map.containsKey(_key)) {
                js.append(_map.get(_key));
            }
            if (_append) {
                js.append(_script);
            }
            _map.put(_key, js.toString());
        }
    }

    /**
     * @param _parameter Parameter as passed by the eFaps API
     * @param _queryBldr queryBuilder the MaxResult will be added to
     */
    protected static void addMaxResult2QueryBuilder4AutoComplete(final Parameter _parameter,
                                                                 final QueryBuilder _queryBldr)
    {
        final Map<?, ?> properties = (Map<?, ?>) _parameter.get(ParameterValues.PROPERTIES);
        final int maxResult;
        if (properties.containsKey("MaxResult")) {
            maxResult = Integer.parseInt((String) properties.get("MaxResult"));
        } else {
            maxResult = 500;
        }
        if (maxResult > 0) {
            _queryBldr.setLimit(maxResult);
        }
    }

    /**
     * Method to evaluate the selected row.
     *
     * @param _parameter paaremter
     * @return number of selected row.
     */
    protected static int getSelectedRow(final Parameter _parameter)
    {
        int ret = 0;
        final String value = _parameter.getParameterValue("eFapsRSR");
        if (value != null && value.length() > 0) {
            ret = Integer.parseInt(value);
        }
        return ret;
    }

    /**
     * Wrapp in script tag.
     *
     * @param _parameter Parameter as passed by the eFaps API
     * @param _script   script to be wrapped
     * @param _jsTag the js tag
     * @param _ready the ready
     * @return wrapped script
     */
    protected static StringBuilder wrappInScriptTag(final Parameter _parameter,
                                                    final CharSequence _script,
                                                    final boolean _jsTag,
                                                    final int _ready)
    {
        final StringBuilder ret = new StringBuilder();
        if (_jsTag) {
            ret.append("<script type=\"text/javascript\">/*<![CDATA[*/\n");
        }
        if (_ready > 0) {
            ret.append("require([\"dojo/ready\"], function(ready){ready(").append(_ready).append(", function(){\n");
        }
        ret.append(_script);
        if (_ready > 0) {
            ret.append("});});");
        }
        if (_jsTag) {
            ret.append("\n/*]]>*/</script>");
        }
        return ret;
    }

    /**
     * @param _parameter Parameter as passed by the eFaps API
     * @param _script   script to be wrapped
     * @param _libraries libraries to be added
     * @return wrapped script
     */
    protected static StringBuilder wrapInDojoRequire(final Parameter _parameter,
                                                     final CharSequence _script,
                                                     final DojoLibs... _libraries)
    {
        final StringBuilder ret = new StringBuilder()
            .append("require([");
        final StringBuilder paras = new StringBuilder();
        boolean first = true;
        final List<DojoLibs> libs = Arrays.asList(_libraries);

        final ComparatorChain<DojoLibs> comparator = new ComparatorChain<>();
        comparator.addComparator((_arg0,
         _arg1) -> _arg0.paraName == null && _arg1.paraName == null
                        ||  _arg0.paraName != null && _arg1.paraName != null
                            ? 0 : _arg0.paraName == null ? 1 : -1);
        comparator.addComparator(Comparator.comparing(_arg0 -> _arg0.libName));
        Collections.sort(libs, comparator);
        for (final DojoLibs dojoLibs : libs) {
            if (first) {
                first = false;
            } else {
                ret.append(",");
                if (dojoLibs.paraName != null) {
                    paras.append(",");
                }
            }
            ret.append("\"").append(dojoLibs.libName).append("\"");
            if (dojoLibs.paraName != null) {
                paras.append(dojoLibs.paraName);
            }
        }
        ret.append("],").append(" function(")
            .append(paras)
            .append(") {\n")
            .append(_script).append("});");
        return ret;
    }

    /**
     * The Enum DojoLibs.
     */
    @SuppressWarnings("checkstyle:javadocvariable")
    public enum DojoLibs
    {
        ARRAY("dojo/_base/array", "array"),
        AUTOCOMP("efaps/AutoComplete", "AutoComplete"),
        AUTOSUGG("efaps/AutoSuggestion", "AutoSuggestion"),
        ASPECT("dojo/aspect", "aspect"),
        CHECKBOX("dijit/form/CheckBox", "CheckBox"),
        CLMLOCK("efaps/ColumnLock", "ColumnLock"),
        DATAGRID("dojox/grid/DataGrid", "DataGrid"),
        DNDSOURCE("dojo/dnd/Source", "Source"),
        DOM("dojo/dom", "dom"),
        DOMATTR("dojo/dom-attr", "domAttr"),
        DOMCLASS("dojo/dom-class", "domClass"),
        DOMCONSTRUCT("dojo/dom-construct", "domConstruct"),
        DOMSTYLE("dojo/dom-style", "domStyle"),
        ENHANCEDGRID("dojox/grid/EnhancedGrid", "EnhancedGrid"),
        FSELECT("dijit/form/FilteringSelect", "FilteringSelect"),
        GXGRID("gridx/Grid", "Grid"),
        GXCACHE("gridx/core/model/cache/Sync", "Cache"),
        GXGRPHEADER("gridx/modules/GroupHeader", "GroupHeader"),
        GXEDIT("gridx/modules/Edit", "Edit"),
        GXCELLWIDGET("gridx/modules/CellWidget", "CellWidget"),
        HTML("dojo/html", "html"),
        IFRSTORE("dojo/data/ItemFileReadStore", "ItemFileReadStore"),
        IFWSTORE("dojo/data/ItemFileWriteStore", "ItemFileWriteStore"),
        LANG("dojo/_base/lang", "lang"),
        MEMORY("dojo/store/Memory", "Memory"),
        NBRTEXTBOX("dijit/form/NumberTextBox", "NumberTextBox"),
        NUMBER("dojo/number", "number"),
        NLDOM("dojo/NodeList-dom", null),
        NLTRAVERSE("dojo/NodeList-traverse", null),
        ON("dojo/on", "on"),
        POPUP("dijit/popup", "popup"),
        QUERY("dojo/query", "query"),
        REGISTRY("dijit/registry", "registry"),
        SELECT("dijit/form/Select", "Select"),
        TOGGLEBUTTON("dijit/form/ToggleButton", "ToggleButton"),
        TOOLTIPDIALOG("dijit/TooltipDialog", "TooltipDialog"),
        TOPIC("dojo/topic", "topic");

        /** The lib name. */
        private final String libName;

        /** The para name. */
        private final String paraName;

        /**
         * Instantiates a new dojo libs.
         *
         * @param _libName the lib name
         * @param _paraName the para name
         */
        DojoLibs(final String _libName,
                 final String _paraName)
        {
            this.libName = _libName;
            this.paraName = _paraName;
        }
    }
}

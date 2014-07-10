/*
 * Copyright 2003 - 2014 The eFaps Team
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


package org.efaps.esjp.common.util;

import java.util.Map;

import org.efaps.admin.event.Parameter;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.program.esjp.EFapsRevision;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.db.QueryBuilder;
import org.efaps.ui.wicket.behaviors.SetSelectedRowBehavior;
import org.efaps.ui.wicket.util.EFapsKey;


/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
@EFapsUUID("f5464f0e-c51c-4076-99fa-9992c1e3bdd0")
@EFapsRevision("$Rev$")
public abstract class InterfaceUtils_Base
{

    /**
     * @param _map
     * @param _script
     */
    protected static void appendScript4FieldUpdate(final Map<String, Object> _map,
                                                   final CharSequence _script)
    {
        InterfaceUtils_Base.add2Script(EFapsKey.FIELDUPDATE_JAVASCRIPT, _map, _script, true);
    }

    /**
     * @param _map
     * @param _script
     */
    public static void prependScript4FieldUpdate(final Map<String, Object> _map,
                                                final CharSequence _script)
    {
        InterfaceUtils_Base.add2Script(EFapsKey.FIELDUPDATE_JAVASCRIPT, _map, _script, false);
    }

    protected static void add2Script(final EFapsKey _key,
                                     final Map<String, Object> _map,
                                     final CharSequence _script,
                                     final boolean _append)
    {
        final StringBuilder js = new StringBuilder();
        if (!_append) {
            js.append(_script);
        }
        if (_map.containsKey(_key.getKey())) {
            js.append(_map.get(_key.getKey()));
        }
        if (_append) {
            js.append(_script);
        }
        _map.put(_key.getKey(), js.toString());
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
        final String value = _parameter.getParameterValue(SetSelectedRowBehavior.INPUT_ROW);
        if (value != null && value.length() > 0) {
            ret = Integer.parseInt(value);
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

        for (int i = 0; i < _libraries.length; i++) {
            final DojoLibs dojoLibs = _libraries[i];
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

    public enum DojoLibs
    {
        QUERY("dojo/query", "query"),
        REGISTRY("dijit/registry", "registry"),
        AUTOCOMP("efaps/AutoComplete", "AutoComplete"),
        AUTOSUGG("efaps/AutoSuggestion", "AutoSuggestion"),
        NLDOM("dojo/NodeList-dom", null),
        DOMCLASS("dojo/dom-class", "domClass");

        private final String libName;
        private final String paraName;

        private DojoLibs(final String _libName,
                         final String _paraName)
        {
            this.libName = _libName;
            this.paraName = _paraName;
        }
    }
}

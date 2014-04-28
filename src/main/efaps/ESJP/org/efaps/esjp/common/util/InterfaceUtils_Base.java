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
    public static void appendScript4FieldUpdate(final Map<String, Object> _map,
                                                final CharSequence _script)
    {
        InterfaceUtils_Base.add2Script(EFapsKey.FIELDUPDATE_JAVASCRIPT, _map, _script, true);
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
    public static void addMaxResult2QueryBuilder4AutoComplete(final Parameter _parameter,
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
}

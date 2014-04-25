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

import org.efaps.ui.wicket.util.EFapsKey;


/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
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

}

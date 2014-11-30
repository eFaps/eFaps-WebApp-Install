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

package org.efaps.esjp.ui.html.dojo.charting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
public abstract class Util_Base
{

    /**
     * @param _map map to be converted
     * @return StringBuilder conting the string elements
     */
    protected static CharSequence mapToObjectList(final Map<String, Object> _map)
    {
        final StringBuilder ret = new StringBuilder();
        if (!_map.isEmpty()) {
            boolean first = true;
            ret.append("{");
            for (final Entry<String, Object> entry : _map.entrySet()) {
                if (first) {
                    first = false;
                } else {
                    ret.append(",");
                }
                ret.append(entry.getKey()).append(":").append(entry.getValue());
            }
            ret.append("}");
        }
        return ret;
    }

    /**
     * @param _list Collection to be converted
     * @return StringBuilder conting the string elements
     */
    protected static CharSequence collectionToObjectArray(final Collection<CharSequence> _list)
    {
        final StringBuilder ret = new StringBuilder();
        if (!_list.isEmpty()) {
            boolean first = true;
            ret.append("[");
            for (final CharSequence entry : _list) {
                if (first) {
                    first = false;
                } else {
                    ret.append(",");
                }
                ret.append(entry);
            }
            ret.append("]");
        }
        return ret;
    }

    /**
     * @param _mapCollection
     * @return
     */
    protected static CharSequence mapCollectionToObjectArray(final Collection<Map<String, Object>> _mapCollection)
    {
        final List<CharSequence> maps = new ArrayList<>();
        for (final Map<String, Object> map : _mapCollection) {
            maps.add(Util.mapToObjectList(map));
        }
        return Util.collectionToObjectArray(maps);
    }

    /**
     * @param _mapCollection
     * @return
     */
    protected static CharSequence wrap4String(final  Object _object)
    {
        return "\"" + _object + "\"";
    }

}

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

package org.efaps.esjp.ui.html.dojo.charting;

import java.util.Collection;
import java.util.Map;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

/**
 * This class must be replaced for customization, therefore it is left empty.
 * Functional description can be found in the related "<code>_base</code>"
 * class.
 *
 * @author The eFaps Team
 */
@EFapsUUID("151ba09b-7384-48d4-a305-824915ade52e")
@EFapsApplication("eFaps-WebApp")
public class Util
    extends Util_Base
{

    /**
     * Map to object list.
     *
     * @param _map the map
     * @return the char sequence
     */
    public static CharSequence mapToObjectList(final Map<String, Object> _map)
    {
        return Util_Base.mapToObjectList(_map);
    }

    /**
     * Collection to object array.
     *
     * @param _list the list
     * @return the char sequence
     */
    public static CharSequence collectionToObjectArray(final Collection<CharSequence> _list)
    {
        return Util_Base.collectionToObjectArray(_list);
    }

    /**
     * Map collection to object array.
     *
     * @param _mapCollection the map collection
     * @return the char sequence
     */
    public static CharSequence mapCollectionToObjectArray(final Collection<Map<String, Object>> _mapCollection)
    {
        return Util_Base.mapCollectionToObjectArray(_mapCollection);
    }

    /**
     * Wrap 4 string.
     *
     * @param _object the object
     * @return the char sequence
     */
    public static CharSequence wrap4String(final Object _object)
    {
        return Util_Base.wrap4String(_object);
    }
}

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

import java.util.Map;

import org.efaps.admin.event.Parameter;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.db.QueryBuilder;


/**
 * This class must be replaced for customization, therefore it is left empty.
 * Functional description can be found in the related "<code>_Base</code>"
 * class.
 *
 * @author The eFaps Team
 */
@EFapsUUID("c9af3b39-477f-4f21-917f-41065389d990")
@EFapsApplication("eFaps-WebApp")
public class InterfaceUtils
    extends InterfaceUtils_Base
{

    /**
     * Gets the row keys.
     *
     * @param _parameter Parameter as passed by the eFaps API
     * @param _currentkey the currentkey
     * @param _keySequence the key sequence
     * @return the row keys
     */
    public static String[] getRowKeys(final Parameter _parameter,
                                         final String _currentkey,
                                         final String... _keySequence)
    {
        return InterfaceUtils_Base.getRowKeys(_parameter, _currentkey, _keySequence);
    }


    /**
     * Append script for field update.
     *
     * @param _map the map
     * @param _script the script
     */
    public static void appendScript4FieldUpdate(final Map<String, Object> _map,
                                                final CharSequence _script)
    {
        InterfaceUtils_Base.appendScript4FieldUpdate(_map, _script);
    }

    /**
     * Prepend script for field update.
     *
     * @param _map the map
     * @param _script the script
     */
    public static void prependScript4FieldUpdate(final Map<String, Object> _map,
                                                final CharSequence _script)
    {
        InterfaceUtils_Base.prependScript4FieldUpdate(_map, _script);
    }

    /**
     * Adds the max result to queryBuilder for AutoComplete.
     *
     * @param _parameter Parameter as passed by the eFaps API
     * @param _queryBldr the query bldr
     */
    public static void addMaxResult2QueryBuilder4AutoComplete(final Parameter _parameter,
                                                              final QueryBuilder _queryBldr)
    {
        InterfaceUtils_Base.addMaxResult2QueryBuilder4AutoComplete(_parameter, _queryBldr);
    }

    /**
     * Method to evaluate the selected row.
     *
     * @param _parameter paaremter
     * @return number of selected row.
     */
    public static int getSelectedRow(final Parameter _parameter)
    {
        return InterfaceUtils_Base.getSelectedRow(_parameter);
    }

    /**
     * @param _parameter Parameter as passed by the eFaps API
     * @param _script   script to be wrapped
     * @param _libraries libraries to be added
     * @return wrapped script
     */
    public static StringBuilder wrapInDojoRequire(final Parameter _parameter,
                                                  final CharSequence _script,
                                                  final DojoLibs... _libraries)
    {
        return InterfaceUtils_Base.wrapInDojoRequire(_parameter, _script, _libraries);
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
    public static StringBuilder wrappInScriptTag(final Parameter _parameter,
                                                 final CharSequence _script,
                                                 final boolean _jsTag,
                                                 final int _ready)
    {
        return InterfaceUtils_Base.wrappInScriptTag(_parameter, _script, _jsTag, _ready);
    }
}

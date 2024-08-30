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
package org.efaps.esjp.ui.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.efaps.admin.datamodel.Type;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.AbstractCommand;
import org.efaps.admin.ui.Command;
import org.efaps.admin.ui.Menu;
import org.efaps.esjp.common.properties.PropertiesUtil;
import org.efaps.util.EFapsException;
import org.efaps.util.UUIDUtil;
import org.efaps.util.cache.CacheReloadException;

@EFapsUUID("192d405d-9819-43d9-9dfd-892b15a2abf9")
@EFapsApplication("eFaps-WebApp")
public class AbstractTableController
    extends AbstractController
{

    protected AbstractCommand evalCmd(final String cmdId)
        throws CacheReloadException
    {
        AbstractCommand cmd = Command.get(UUID.fromString(cmdId));
        if (cmd == null) {
            cmd = Menu.get(UUID.fromString(cmdId));
        }
        return cmd;
    }

    protected List<Type> evalTypes(final Map<String, String> _propertiesMap)
        throws EFapsException
    {
        final var typeList = new ArrayList<Type>();
        final var properties = new Properties();
        properties.putAll(_propertiesMap);
        final var types = PropertiesUtil.analyseProperty(properties, "Type", 0);
        final var expandChildTypes = PropertiesUtil.analyseProperty(properties, "ExpandChildTypes", 0);

        for (final var typeEntry : types.entrySet()) {
            Type type;
            if (UUIDUtil.isUUID(typeEntry.getValue())) {
                type = Type.get(UUID.fromString(typeEntry.getValue()));
            } else {
                type = Type.get(typeEntry.getValue());
            }
            typeList.add(type);
            // default expand for children, if not deactiavted
            if (types.size() == 1
                            && (expandChildTypes.size() == 0
                                            || expandChildTypes.size() == 1
                                                            && !"false".equalsIgnoreCase(expandChildTypes.get(0)))) {
                type.getChildTypes().forEach(at -> typeList.add(at));
            }
            // if we have more specific ones evaluate for each type
            if (expandChildTypes.size() > 1 && Boolean.parseBoolean(expandChildTypes.get(typeEntry.getKey()))) {
                type.getChildTypes().forEach(at -> typeList.add(at));
            }
        }
        return typeList;
    }
}

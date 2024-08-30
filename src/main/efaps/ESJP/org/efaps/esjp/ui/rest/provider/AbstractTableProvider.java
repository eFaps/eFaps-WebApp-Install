/*
 * Copyright 2003 - 2024 The eFaps Team
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
package org.efaps.esjp.ui.rest.provider;

import java.util.List;
import java.util.Map;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.AbstractUserInterfaceObject;
import org.efaps.admin.ui.field.Field;
import org.efaps.util.EFapsException;

@EFapsUUID("f7ae6ba9-9204-42d3-b8b4-2d437b1611fd")
@EFapsApplication("eFaps-WebApp")
public abstract class AbstractTableProvider
    implements ITableProvider
{

    private AbstractUserInterfaceObject cmd;

    private List<Field> fields;

    private Map<String, String> propertiesMap;

    private String oid;

    protected AbstractUserInterfaceObject getCmd()
    {
        return cmd;
    }

    protected List<Field> getFields()
    {
        return fields;
    }

    protected Map<String, String> getPropertiesMap()
    {
        return propertiesMap;
    }

    protected String getOid()
    {
        return oid;
    }

    @Override
    public ITableProvider init(final AbstractUserInterfaceObject cmd,
                               final List<Field> fields,
                               final Map<String, String> propertiesMap,
                               final String oid)
        throws EFapsException
    {
        this.cmd = cmd;
        this.fields = fields;
        this.propertiesMap = propertiesMap;
        this.oid = oid;
        return this;
    }
}

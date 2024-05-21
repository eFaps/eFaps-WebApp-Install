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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.AbstractUserInterfaceObject;
import org.efaps.admin.ui.field.Field;
import org.efaps.util.EFapsException;

@EFapsUUID("d3997c61-839c-435b-a82f-995eb9c4bc52")
@EFapsApplication("eFaps-WebApp")
public interface ITableProvider
{

    Collection<Map<String, ?>> getValues(final AbstractUserInterfaceObject cmd,
                                         final List<Field> fields,
                                         final Map<String, String> properties,
                                         final String oid)
        throws EFapsException;

}

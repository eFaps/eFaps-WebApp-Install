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
package org.efaps.esjp.ui.rest.dto;

import org.efaps.admin.datamodel.Type;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.user.Person;

import com.fasterxml.jackson.databind.JsonSerializer;

@EFapsUUID("6da7182e-208a-4787-aa43-26f2d29ccb1f")
@EFapsApplication("eFaps-WebApp")
public abstract class AbstractSerializer<T>
    extends JsonSerializer<T>
{

    public Object getObjectValue(final Object _object)
    {
        Object ret;
        if (_object instanceof Type) {
            ret = ((Type) _object).getLabel();
        } else if (_object instanceof Person) {
            ret = ((Person) _object).getName();
        } else {
            ret = _object;
        }
        return ret;
    }

}

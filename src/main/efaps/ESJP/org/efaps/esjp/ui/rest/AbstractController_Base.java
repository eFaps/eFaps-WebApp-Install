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

import org.apache.commons.lang3.StringUtils;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.field.Field;

@EFapsUUID("b2fdecb4-5bce-4692-b260-14ff75281241")
@EFapsApplication("eFaps-WebApp")
public abstract class AbstractController_Base
{
    protected String getBaseSelect4MsgPhrase(final Field _field)
    {
        String ret = "";
        if (_field.getSelectAlternateOID() != null) {
            ret = StringUtils.removeEnd(_field.getSelectAlternateOID(), ".oid");
        }
        return ret;
    }
}

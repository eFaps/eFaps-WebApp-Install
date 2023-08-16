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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.text.StringSubstitutor;
import org.efaps.admin.EFapsSystemConfiguration;
import org.efaps.admin.KernelSettings;
import org.efaps.admin.datamodel.Status;
import org.efaps.admin.datamodel.Type;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.user.Person;
import org.efaps.admin.user.Person.AttrName;
import org.efaps.db.Instance;
import org.efaps.util.EFapsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonSerializer;

@EFapsUUID("6da7182e-208a-4787-aa43-26f2d29ccb1f")
@EFapsApplication("eFaps-WebApp")
public abstract class AbstractSerializer<T>
    extends JsonSerializer<T>
{

    private static final Logger LOG = LoggerFactory.getLogger(AbstractSerializer.class);

    public Object getObjectValue(final Object object)
    {
        LOG.debug("Serializing: {}", object);
        Object ret;
        if (object instanceof Type) {
            ret = ((Type) object).getLabel();
        } else if (object instanceof final Person person) {
            String display = null;
            try {
                display = EFapsSystemConfiguration.get().getAttributeValue(KernelSettings.USERUI_DISPLAYPERSON);
            } catch (final EFapsException e) {
                LOG.error("Catched", e);
            }
            if (display == null) {
                display = "${LASTNAME}, ${FIRSTNAME}";
            }
            final Map<String, String> values = new HashMap<>();
            for (final AttrName attr : AttrName.values()) {
                values.put(attr.name(), person.getAttrValue(attr));
            }
            final StringSubstitutor sub = new StringSubstitutor(values);
            ret = sub.replace(display);
        } else if (object instanceof Status) {
            ret = ((Status) object).getLabel();
        } else if (object instanceof Instance) {
            ret = ((Instance) object).getOid();
        } else {
            ret = object;
        }
        return ret;
    }
}

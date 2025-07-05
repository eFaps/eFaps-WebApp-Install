/*
 * Copyright 2003 - 2023 The eFaps Team
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
package org.efaps.esjp.ui.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.efaps.admin.datamodel.Attribute;
import org.efaps.admin.datamodel.Type;
import org.efaps.admin.event.EventType;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.event.Return;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.AbstractCollection;
import org.efaps.admin.ui.AbstractUserInterfaceObject.TargetMode;
import org.efaps.admin.ui.field.Field;
import org.efaps.db.Instance;
import org.efaps.db.stmt.selection.Evaluator;
import org.efaps.esjp.ui.rest.ContentController_Base.RestUIValue;
import org.efaps.util.EFapsException;
import org.efaps.util.UUIDUtil;
import org.efaps.util.cache.CacheReloadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@EFapsUUID("1b168a8f-97ec-4fdf-bfa3-97b4cbd0b239")
@EFapsApplication("eFaps-WebApp")
public class ValueUtils
{

    private static final Logger LOG = LoggerFactory.getLogger(ValueUtils.class);

    private static ObjectMapper OBJECTMAPPER;

    public static List<Field> getFields(final AbstractCollection uiCollection)
    {
        return uiCollection.getFields().stream().filter(field -> {
            try {
                return field.hasAccess(TargetMode.VIEW, null) && !field.isNoneDisplay(TargetMode.VIEW);
            } catch (final EFapsException e) {
                LOG.error("Catched error while evaluation access for Field: {}", field);
            }
            return false;
        }).collect(Collectors.toList());
    }

    public static Type evalType(final String typeStr)
    {
        Type type = null;
        try {
            if (UUIDUtil.isUUID(typeStr)) {
                type = Type.get(UUID.fromString(typeStr));
            } else {
                type = Type.get(typeStr);
            }
        } catch (final CacheReloadException e) {
            LOG.error("Catched error while evaluation type for : {}", typeStr);
        }
        return type;
    }

    public static Map<String, Object> values(final Evaluator eval,
                                             final List<Field> fields)
        throws EFapsException
    {
        final var map = new HashMap<String, Object>();
        map.put("OID", eval.inst().getOid());
        for (final var field : fields) {
            if (field.hasEvents(EventType.UI_FIELD_VALUE)) {
                map.put(field.getName(), evalFieldValueEvent(eval.inst(), field, eval.get(field.getName())));
            } else {
                map.put(field.getName(), eval.get(field.getName()));
            }
            if (field.getSelectAlternateOID() != null) {
                map.put(field.getName() + "_AOID", eval.get(field.getName() + "_AOID"));
            }
        }
        return map;
    }


    static Object evalFieldValueEvent(final Instance instance,
                                         final Field field,
                                         final Object fieldValue)
        throws EFapsException
    {
        Object ret = fieldValue;
        final Attribute attr = instance.getType() == null ? null
                        : instance.getType().getAttribute(field.getAttribute());
        final var uiValue = RestUIValue.builder()
                        .withInstance(instance)
                        .withField(field)
                        .withAttribute(attr)
                        .withDisplay(field.getDisplay(TargetMode.VIEW))
                        .withObject(fieldValue)
                        .build();
        for (final Return aReturn : field.executeEvents(EventType.UI_FIELD_VALUE,
                        ParameterValues.ACCESSMODE, TargetMode.VIEW,
                        ParameterValues.UIOBJECT, uiValue,
                        ParameterValues.OTHERS, fieldValue,
                        ParameterValues.CALL_INSTANCE, instance,
                        ParameterValues.INSTANCE, instance)) {
            ret = aReturn.get(ReturnValues.VALUES);
        }
        return ret;
    }



    public static ObjectMapper getObjectMapper()
    {
        if (OBJECTMAPPER == null) {

            OBJECTMAPPER = new ObjectMapper();
            OBJECTMAPPER.registerModule(new JavaTimeModule());
            OBJECTMAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        }
        return OBJECTMAPPER;
    }
}

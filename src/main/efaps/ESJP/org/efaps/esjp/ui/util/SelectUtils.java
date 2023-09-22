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

import java.io.StringReader;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.efaps.admin.datamodel.Attribute;
import org.efaps.admin.datamodel.Type;
import org.efaps.admin.datamodel.attributetype.StatusType;
import org.efaps.admin.event.EventType;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.field.Field;
import org.efaps.beans.valueparser.ParseException;
import org.efaps.beans.valueparser.ValueParser;
import org.efaps.eql.builder.Print;
import org.efaps.util.EFapsException;

@EFapsUUID("9f542df2-1122-4cd0-bccf-5c09c731b382")
@EFapsApplication("eFaps-WebApp")
public class SelectUtils
{

    public static void add2Select(final Print print,
                                  final List<Type> types,
                                  final List<Field> fields)
        throws EFapsException
    {
        for (final var field : fields) {
            if (field.getAttribute() != null) {
                add2Select4Attribute(print, field, types);
            } else if (field.getSelect() != null) {
                var select = field.getSelect();
                if (field.getSelect().equals("attribute[Status]")) {
                    select = "status.label";
                }
                print.select(select).as(field.getName());
            } else if (field.getMsgPhrase() != null) {
                print.msgPhrase(getBaseSelect4MsgPhrase(field), field.getMsgPhrase()).as(field.getName());
            } else if (field.getPhrase() != null) {
                print.phrase(field.getPhrase()).as(field.getName());
            }
            if (field.getSelectAlternateOID() != null) {
                print.select(field.getSelectAlternateOID()).as(field.getName() + "_AOID");
            }
        }
    }

    public static void add2Select4Attribute(final Print _print,
                                            final Field _field,
                                            final List<Type> _types)
        throws EFapsException
    {
        Attribute attr = null;
        for (final var type : _types) {
            attr = type.getAttribute(_field.getAttribute());
            if (attr != null) {
                break;
            }
        }
        if (attr == null) {
            _print.attribute(_field.getAttribute()).as(_field.getName());
        } else if (attr.getAttributeType().getDbAttrType() instanceof StatusType) {
            _print.select("status.label").as(_field.getName());
        } else if (attr.hasEvents(EventType.RANGE_VALUE)) {
            add2Select4RangeValue(_print, _field.getName(), attr, "");
        } else {
            _print.attribute(_field.getAttribute()).as(_field.getName());
        }
    }

    public static String getBaseSelect4MsgPhrase(final Field _field)
    {
        String ret = "";
        if (_field.getSelectAlternateOID() != null) {
            ret = StringUtils.removeEnd(_field.getSelectAlternateOID(), ".oid");
        }
        return ret;
    }

    public static void add2Select4RangeValue(final Print print,
                                             final String key,
                                             final Attribute attr,
                                             final String baseSelect)
        throws EFapsException
    {
        var baseSel = "";
        if (baseSelect != null) {
            baseSel = baseSelect.endsWith(".") ? baseSelect : baseSelect + ".";
        }
        final var event = attr.getEvents(EventType.RANGE_VALUE).get(0);
        final var valueStr = event.getProperty("Value");
        if (valueStr.contains("$<")) {
            try {
                final var valueList = new ValueParser(new StringReader(valueStr)).ExpressionString();
                int i = 0;
                for (final var expression : valueList.getExpressions()) {
                    print.select(baseSel + "linkto[" + attr.getName() + "]." + expression)
                                    .as(key + "_ex" + i);
                    i++;
                }
            } catch (final ParseException e) {
                throw new EFapsException("Catched", e);
            }
        } else {
            print.select(baseSel + "linkto[" + attr.getName() + "].attribute[" + valueStr + "]").as(key);
        }
    }
}

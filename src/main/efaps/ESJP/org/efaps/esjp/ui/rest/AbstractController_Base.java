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
import java.util.stream.Collectors;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.efaps.admin.datamodel.Attribute;
import org.efaps.admin.datamodel.Type;
import org.efaps.admin.datamodel.attributetype.StatusType;
import org.efaps.admin.dbproperty.DBProperties;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.AbstractCommand;
import org.efaps.admin.ui.AbstractUserInterfaceObject.TargetMode;
import org.efaps.admin.ui.field.Field;
import org.efaps.api.ci.UIFormFieldProperty;
import org.efaps.api.ui.UIType;
import org.efaps.eql.builder.Print;
import org.efaps.esjp.ui.rest.dto.ColumnDto;
import org.efaps.util.EFapsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EFapsUUID("b2fdecb4-5bce-4692-b260-14ff75281241")
@EFapsApplication("eFaps-WebApp")
public abstract class AbstractController_Base
{
    private static final Logger LOG = LoggerFactory.getLogger(AbstractController.class);

    protected String getBaseSelect4MsgPhrase(final Field _field)
    {
        String ret = "";
        if (_field.getSelectAlternateOID() != null) {
            ret = StringUtils.removeEnd(_field.getSelectAlternateOID(), ".oid");
        }
        return ret;
    }

    protected List<Field> getFields(final org.efaps.admin.ui.Table _table) {
        return _table.getFields().stream().filter(field ->  {
            try {
                return field.hasAccess(TargetMode.VIEW, null) && !field.isNoneDisplay(TargetMode.VIEW);
            } catch (final EFapsException e) {
                LOG.error("Catched error while evaluation access for Field: {}", field);
            }
            return false;
        }).collect(Collectors.toList());
    }

    protected List<ColumnDto> getColumns(final org.efaps.admin.ui.Table _table)
    {
        final var fields = getFields(_table);
        final var ret = new ArrayList<ColumnDto>();
        for (final var field : fields) {
            ret.add(ColumnDto.builder()
                            .withField(field.getName())
                            .withHeader(field.getLabel() == null ? "" : DBProperties.getProperty(field.getLabel()))
                            .withRef(field.getReference() != null)
                            .build());
        }
        return ret;
    }

    protected String getHeader(final AbstractCommand _cmd)
    {
        final var key = _cmd.getTargetTitle() == null
                        ? _cmd.getName() + ".Title"
                        : _cmd.getTargetTitle();
        return DBProperties.getProperty(key);
    }

    protected void add2Select4Attribute(final Print _print, final Field _field, final List<Type> _types)
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
        } else {
            _print.attribute(_field.getAttribute()).as(_field.getName());
        }
    }

    protected UIType getUIType(final Field _field)
    {
        final UIType ret;
        final String uiTypeStr = _field.getProperty(UIFormFieldProperty.UI_TYPE);
        if (EnumUtils.isValidEnum(UIType.class, uiTypeStr)) {
            ret = UIType.valueOf(uiTypeStr);
        } else {
            ret = UIType.DEFAULT;
        }
        return ret;
    }
}

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

import java.util.UUID;

import javax.ws.rs.core.Response;

import org.efaps.admin.dbproperty.DBProperties;
import org.efaps.admin.event.EventType;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.AbstractCommand;
import org.efaps.admin.ui.Command;
import org.efaps.eql.EQL;
import org.efaps.esjp.ui.rest.dto.TableDto;
import org.efaps.util.EFapsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EFapsUUID("708d3d0d-e230-44e1-99f4-aadb45f70be9")
@EFapsApplication("eFaps-WebApp")
public abstract class Table_Base
{

    /**
     * Logging instance used in this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(Table.class);

    public Response getTable(final String _id)
        throws EFapsException
    {
        final var cmd = Command.get(UUID.fromString(_id));
        final var table = cmd.getTargetTable();
        LOG.info("Get TABLE {} ", table);
        cmd.executeEvents(EventType.UI_TABLE_EVALUATE,
                        ParameterValues.OTHERS, null);

        final var properties = cmd.getEvents(EventType.UI_TABLE_EVALUATE).get(0).getPropertyMap();
        final var type = properties.get("Type");

        final var select = EQL.builder()
                        .print()
                        .query(type)
                        .select();
        for (final var field : table.getFields()) {
            if (field.getAttribute() != null) {
                select.attribute(field.getAttribute()).as(field.getName());
            } else if (field.getSelect() != null) {
                select.select(field.getSelect()).as(field.getName());
            }
        }
        final var values = select.evaluate().getData();
        LOG.info("values: {} ", values);

        final var dto = TableDto.builder()
                        .withHeader(getHeader(cmd))
                        .build();

        final Response ret = Response.ok()
                        .entity(dto)
                        .build();
        return ret;
    }

    protected String getHeader(final AbstractCommand _cmd)
    {
        final String key = _cmd.getTargetTitle() == null
                        ? _cmd.getName() + ".Title"
                        : _cmd.getTargetTitle();
        return DBProperties.getProperty(key);
    }

}

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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.Response;

import org.efaps.admin.event.EventType;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.AbstractCommand;
import org.efaps.admin.ui.AbstractUserInterfaceObject.TargetMode;
import org.efaps.admin.ui.Command;
import org.efaps.db.Instance;
import org.efaps.esjp.ui.rest.dto.PayloadDto;
import org.efaps.util.EFapsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EFapsUUID("29e9c73d-a4ec-4f20-a8ad-b213b0db1afe")
@EFapsApplication("eFaps-WebApp")
public abstract class ExecController_Base
    extends AbstractController
{

    private static final Logger LOG = LoggerFactory.getLogger(ExecController.class);

    public Response exec(final String _cmdId,
                         final PayloadDto dto)
        throws EFapsException
    {
        final var parameters = convertToMap(dto);
        final AbstractCommand cmd = Command.get(UUID.fromString(_cmdId));

        final var paraValues = new ArrayList<>();
        paraValues.add(ParameterValues.PARAMETERS);
        paraValues.add(parameters);

        if (TargetMode.EDIT.equals(cmd.getTargetMode()) && parameters.containsKey("eFapsOID")) {
            paraValues.add(ParameterValues.INSTANCE);
            paraValues.add(Instance.get(parameters.get("eFapsOID")[0]));
        }
        if (parameters.containsKey("eFapsSelectedOids")) {
            paraValues.add(ParameterValues.OTHERS);
            paraValues.add(parameters.get("eFapsSelectedOids"));
        }
        cmd.executeEvents(EventType.UI_COMMAND_EXECUTE, paraValues.toArray());

        final Response ret = Response.ok()
                        .build();
        return ret;
    }

    protected Map<String, String[]> convertToMap(final PayloadDto dto)
    {
        final var ret = new HashMap<String, String[]>();
        if (dto != null && dto.getValues() != null) {
            for (final var entry : dto.getValues().entrySet()) {
                if (entry.getValue() instanceof Collection) {
                    ret.put(entry.getKey(), ((Collection<?>) entry.getValue()).stream().map(val -> {
                        return String.valueOf(val);
                    }).toArray(String[]::new));
                } else {
                    ret.put(entry.getKey(), new String[] { String.valueOf(entry.getValue()) });
                }
            }
        }
        return ret;
    }
}

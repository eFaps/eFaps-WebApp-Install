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

import java.util.HashMap;
import java.util.UUID;

import javax.ws.rs.core.Response;

import org.efaps.admin.event.EventType;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.AbstractCommand;
import org.efaps.admin.ui.Command;
import org.efaps.util.EFapsException;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

@EFapsUUID("29e9c73d-a4ec-4f20-a8ad-b213b0db1afe")
@EFapsApplication("eFaps-WebApp")
public abstract class ExecController_Base
{

    public Response exec(final String _cmdId, final FormDataMultiPart _formData)
        throws EFapsException
    {
        final var parameters = new HashMap<String, String[]>();
        final var fields = _formData.getFields();
        for (final var entry : fields.entrySet()) {
            final var fieldName = entry.getKey();
            final var values = entry.getValue().stream().map(part -> {
                return part.getValue();
            }).toArray(String[]::new) ;
            parameters.put(fieldName, values);
        }

        final AbstractCommand cmd = Command.get(UUID.fromString(_cmdId));
        cmd.executeEvents(EventType.UI_COMMAND_EXECUTE, ParameterValues.PARAMETERS, parameters);

        final Response ret = Response.ok()
                        .build();
        return ret;
    }
}

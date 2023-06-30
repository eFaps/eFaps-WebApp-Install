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
package org.efaps.esjp.ui.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.efaps.admin.event.EventType;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.field.Field;
import org.efaps.esjp.ui.rest.dto.FieldCommandResponseDto;
import org.efaps.esjp.ui.rest.dto.PayloadDto;
import org.efaps.util.EFapsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EFapsUUID("f8d1f18e-214d-4f7c-aeae-8ab78159e502")
@EFapsApplication("eFaps-WebApp")
@Path("/ui/field-command")
public class FieldCommandController
    extends AbstractController
{
    private static final Logger LOG = LoggerFactory.getLogger(FieldCommandController.class);

    @Path("/{fieldId}")
    @POST
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response execute(@PathParam("fieldId") final String fieldId, final PayloadDto payloadDto)
        throws EFapsException
    {
        LOG.info("field-command : {}", fieldId);

        final var parameters = convertToMap(payloadDto);

        final var paraValues = new ArrayList<>();
        paraValues.add(ParameterValues.PARAMETERS);
        paraValues.add(parameters);

        LOG.info("{}", parameters);
        final var field = Field.get(Long.valueOf(fieldId));
        FieldCommandResponseDto responseDto = null;
        for (final var returns : field.executeEvents(EventType.UI_FIELD_CMD, paraValues.toArray())) {
            if (returns.contains(ReturnValues.SNIPLETT)) {
                LOG.error("SNIPPLET do not work here");
            } else if (returns.contains(ReturnValues.VALUES)) {
                @SuppressWarnings("unchecked")
                final List<Map<String, Object>> values =  (List<Map<String, Object>>) returns.get(ReturnValues.VALUES);
                if (!values.isEmpty()) {
                    responseDto = FieldCommandResponseDto.builder()
                                    .withValues(values.get(0))
                                    .build();
                }
            }
        }
        return Response.ok()
                        .entity(responseDto)
                        .build();
    }
}

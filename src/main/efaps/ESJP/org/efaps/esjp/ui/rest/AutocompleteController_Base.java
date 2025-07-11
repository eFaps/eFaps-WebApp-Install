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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.efaps.admin.event.EventType;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.field.Field;
import org.efaps.esjp.ui.rest.dto.AutocompleteResponseDto;
import org.efaps.esjp.ui.rest.dto.OptionDto;
import org.efaps.esjp.ui.rest.dto.PayloadDto;
import org.efaps.util.EFapsException;

import jakarta.ws.rs.core.Response;

@EFapsUUID("0f3c95a0-abb6-4c70-9869-5cf9142a3dac")
@EFapsApplication("eFaps-WebApp")
public abstract class AutocompleteController_Base
{

    public Response search(final String fieldId, final PayloadDto dto)
        throws EFapsException
    {
        final var field = Field.get(Long.valueOf(fieldId));

        final var parameters = new HashMap<String, String[]>();

        final var term = dto.getValues().get(fieldId + "_query");

        final var paraValues = new ArrayList<>();
        paraValues.add(ParameterValues.OTHERS);
        paraValues.add(term == null ? "" :term );
        paraValues.add(ParameterValues.PARAMETERS);
        paraValues.add(parameters);

        final List<OptionDto> options = new ArrayList<>();
        for (final var returns : field.executeEvents(EventType.UI_FIELD_AUTOCOMPLETE, paraValues.toArray())) {
            final List<?> values = (List<?>) returns.get(ReturnValues.VALUES);
            if (values != null) {
                values.stream().forEach(val -> {
                    @SuppressWarnings("unchecked") final var map = (Map<String, String>) val;
                    final var label = map.containsKey("eFapsAutoCompleteCHOICE")
                                    ? map.get("eFapsAutoCompleteCHOICE")
                                    : map.get("eFapsAutoCompleteVALUE");
                    options.add(OptionDto.builder()
                                    .withLabel(label)
                                    .withValue(map.get("eFapsAutoCompleteKEY"))
                                    .withDisplay(map.get("eFapsAutoCompleteVALUE"))
                                    .build());
                });
            }
        }

        final var ret = AutocompleteResponseDto.builder()
                        .withOptions(options)
                        .build();
        return Response.ok()
                        .entity(ret)
                        .build();
    }
}

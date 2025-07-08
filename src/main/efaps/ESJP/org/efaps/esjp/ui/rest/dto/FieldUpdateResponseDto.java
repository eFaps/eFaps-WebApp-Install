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
package org.efaps.esjp.ui.rest.dto;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = FieldUpdateResponseDto.Builder.class)
@EFapsUUID("7b51f0eb-0cd5-4248-bfdd-3873e587a6d7")
@EFapsApplication("eFaps-WebApp")
public class FieldUpdateResponseDto
{

    private final List<Map<String, ?>> values;

    private FieldUpdateResponseDto(final Builder builder)
    {
        values = builder.values;
    }

    public List<Map<String, ?>> getValues()
    {
        return values;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private List<Map<String, ?>> values = Collections.emptyList();

        private Builder()
        {
        }

        public FieldUpdateResponseDto build()
        {
            return new FieldUpdateResponseDto(this);
        }

        public Builder withValues(final List<Map<String, ?>> values)
        {
            this.values = values;
            return this;
        }
    }
}

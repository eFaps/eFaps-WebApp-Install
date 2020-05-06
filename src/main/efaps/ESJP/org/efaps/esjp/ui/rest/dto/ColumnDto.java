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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@EFapsUUID("035f280d-7634-477c-a7a6-b01d665b3088")
@EFapsApplication("eFaps-WebApp")
@JsonDeserialize(builder = ColumnDto.Builder.class)
public class ColumnDto
{

    private final String header;
    private final String field;

    private ColumnDto(final Builder _builder)
    {
        header = _builder.header;
        field = _builder.field;
    }

    public String getHeader()
    {
        return header;
    }

    public String getField()
    {
        return field;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private String header;
        private String field;

        public Builder withHeader(final String _header)
        {
            header = _header;
            return this;
        }

        public Builder withField(final String _field)
        {
            field = _field;
            return this;
        }

        public ColumnDto build()
        {
            return new ColumnDto(this);
        }
    }
}

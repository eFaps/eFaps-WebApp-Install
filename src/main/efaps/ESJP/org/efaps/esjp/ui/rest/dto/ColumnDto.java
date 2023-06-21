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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ColumnDto.Builder.class)
@EFapsUUID("035f280d-7634-477c-a7a6-b01d665b3088")
@EFapsApplication("eFaps-WebApp")
public class ColumnDto
{

    private final String header;
    private final String field;
    private final String ref;
    private final ValueType type;
    private final List<OptionDto> options;
    private final String updateRef;
    private ColumnDto(final Builder builder)
    {
        header = builder.header;
        field = builder.field;
        ref = builder.ref;
        type = builder.type;
        options = builder.options;
        updateRef = builder.updateRef;
    }

    public String getField()
    {
        return field;
    }

    public String getHeader()
    {
        return header;
    }

    public List<OptionDto> getOptions()
    {
        return options;
    }

    public String getRef()
    {
        return ref;
    }

    public ValueType getType()
    {
        return type;
    }

    public String getUpdateRef()
    {
        return updateRef;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Creates builder to build {@link ColumnDto}.
     *
     * @return created builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to build {@link ColumnDto}.
     */
    public static final class Builder
        implements IFieldBuilder
    {

        private String header;
        private String field;
        private String ref;
        private ValueType type;
        private List<OptionDto> options = Collections.emptyList();
        private String updateRef;

        private Builder()
        {
        }

        public ColumnDto build()
        {
            return new ColumnDto(this);
        }

        public Builder withField(final String field)
        {
            this.field = field;
            return this;
        }

        public Builder withHeader(final String header)
        {
            this.header = header;
            return this;
        }

        @Override
        public Builder withOptions(final List<OptionDto> options)
        {
            this.options = options;
            return this;
        }

        public Builder withRef(final String ref)
        {
            this.ref = ref;
            return this;
        }

        @Override
        public Builder withType(final ValueType type)
        {
            this.type = type;
            return this;
        }

        public Builder withUpdateRef(final String updateRef)
        {
            this.updateRef = updateRef;
            return this;
        }
    }
}

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

import java.util.Collections;
import java.util.List;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(builder = ValueDto.Builder.class)
@EFapsUUID("5b98f5dd-366b-41e3-8402-f26afbbf2240")
@EFapsApplication("eFaps-WebApp")
public class ValueDto
{

    private final ValueType type;
    private final String name;
    private final String label;
    private final Object value;
    private final String ref;
    private final List<OptionDto> options;

    private ValueDto(final Builder builder)
    {
        type = builder.type;
        name = builder.name;
        label = builder.label;
        value = builder.value;
        ref = builder.ref;
        options = builder.options;
    }

    public ValueType getType()
    {
        return type;
    }

    public String getName()
    {
        return name;
    }

    public String getLabel()
    {
        return label;
    }

    @JsonSerialize(using = ValueSerializer.class)
    public Object getValue()
    {
        return value;
    }

    public List<OptionDto> getOptions()
    {
        return options;
    }

    public String getRef()
    {
        return ref;
    }

    /**
     * Creates builder to build {@link ValueDto}.
     * @return created builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to build {@link ValueDto}.
     */
    public static final class Builder
    {

        private ValueType type;
        private String name;
        private String label;
        private Object value;
        private String ref;
        private List<OptionDto> options = Collections.emptyList();

        private Builder()
        {
        }

        public Builder withType(final ValueType type)
        {
            this.type = type;
            return this;
        }

        public Builder withName(final String name)
        {
            this.name = name;
            return this;
        }

        public Builder withLabel(final String label)
        {
            this.label = label;
            return this;
        }

        public Builder withValue(final Object value)
        {
            this.value = value;
            return this;
        }

        public Builder withRef(final String ref)
        {
            this.ref = ref;
            return this;
        }

        public Builder withOptions(final List<OptionDto> options)
        {
            this.options = options;
            return this;
        }

        public ValueDto build()
        {
            return new ValueDto(this);
        }
    }
}

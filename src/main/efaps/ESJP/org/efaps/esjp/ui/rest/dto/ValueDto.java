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
    private final boolean required;
    private final String updateRef;
    private final String navRef;

    private ValueDto(final Builder builder)
    {
        this.type = builder.type;
        this.name = builder.name;
        this.label = builder.label;
        this.value = builder.value;
        this.ref = builder.ref;
        this.options = builder.options;
        this.required = builder.required;
        this.updateRef = builder.updateRef;
        this.navRef = builder.navRef;
    }

    public String getLabel()
    {
        return label;
    }

    public String getName()
    {
        return name;
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

    @JsonSerialize(using = ValueSerializer.class)
    public Object getValue()
    {
        return value;
    }

    public boolean isRequired()
    {
        return required;
    }

    public String getNavRef()
    {
        return navRef;
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
        implements IFieldBuilder
    {

        private ValueType type;
        private String name;
        private String label;
        private Object value;
        private String ref;
        private List<OptionDto> options = Collections.emptyList();
        private boolean required;
        private String updateRef;
        private String navRef;

        private Builder()
        {
        }

        @Override
        public Builder withType(ValueType type)
        {
            this.type = type;
            return this;
        }

        public Builder withName(String name)
        {
            this.name = name;
            return this;
        }

        public Builder withLabel(String label)
        {
            this.label = label;
            return this;
        }

        public Builder withValue(Object value)
        {
            this.value = value;
            return this;
        }

        public Builder withRef(String ref)
        {
            this.ref = ref;
            return this;
        }

        @Override
        public Builder withOptions(List<OptionDto> options)
        {
            this.options = options;
            return this;
        }

        public Builder withRequired(boolean required)
        {
            this.required = required;
            return this;
        }

        public Builder withUpdateRef(String updateRef)
        {
            this.updateRef = updateRef;
            return this;
        }

        public Builder withNavRef(String navRef)
        {
            this.navRef = navRef;
            return this;
        }

        public ValueType getType()
        {
            return type;
        }

        public ValueDto build()
        {
            return new ValueDto(this);
        }
    }
}

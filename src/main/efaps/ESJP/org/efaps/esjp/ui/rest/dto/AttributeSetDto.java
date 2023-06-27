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

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = AttributeSetDto.Builder.class)
@EFapsUUID("5169a25a-e6da-4474-aa86-60ce932610d6")
@EFapsApplication("eFaps-WebApp")
public class AttributeSetDto
{

    private final ValueType type;
    private final String name;
    private final String label;
    private final List<ValueDto> values;

    private AttributeSetDto(Builder builder)
    {
        this.type = builder.type;
        this.name = builder.name;
        this.label = builder.label;
        this.values = builder.values;
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

    public List<ValueDto> getValues()
    {
        return values;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private ValueType type = ValueType.ATTRSET;
        private String name;
        private String label;
        private List<ValueDto> values = Collections.emptyList();

        private Builder()
        {
        }

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

        public Builder withValues(List<ValueDto> values)
        {
            this.values = values;
            return this;
        }

        public AttributeSetDto build()
        {
            return new AttributeSetDto(this);
        }
    }

}

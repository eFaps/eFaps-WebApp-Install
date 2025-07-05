/*
 * Copyright 2003 - 2025 The eFaps Team
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

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = FieldModuleDto.Builder.class)
@EFapsUUID("015be93a-1b1d-4e4a-a157-4243e7bf2178")
@EFapsApplication("eFaps-WebApp")
public class FieldModuleDto
{

    private final ValueType type;
    private final String name;
    private final String label;
    private final ModuleDto value;

    private FieldModuleDto(Builder builder)
    {
        this.type = builder.type;
        this.name = builder.name;
        this.label = builder.label;
        this.value = builder.value;
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

    public ModuleDto getValue()
    {
        return value;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private ValueType type;
        private String name;
        private String label;
        private ModuleDto value;

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

        public Builder withValue(ModuleDto value)
        {
            this.value = value;
            return this;
        }

        public FieldModuleDto build()
        {
            return new FieldModuleDto(this);
        }
    }
}

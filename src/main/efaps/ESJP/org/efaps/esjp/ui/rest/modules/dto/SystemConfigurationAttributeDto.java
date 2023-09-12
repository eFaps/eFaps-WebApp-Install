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
package org.efaps.esjp.ui.rest.modules.dto;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = SystemConfigurationAttributeDto.Builder.class)
@EFapsUUID("600100ac-eb0d-48be-be44-5adf79be5b50")
@EFapsApplication("eFaps-WebApp")
public class SystemConfigurationAttributeDto
{

    private final String key;
    private final SystemConfigurationAttributeType type;
    private final String defaultValue;
    private final String description;

    private SystemConfigurationAttributeDto(Builder builder)
    {
        this.key = builder.key;
        this.type = builder.type;
        this.defaultValue = builder.defaultValue;
        this.description = builder.description;
    }

    public String getKey()
    {
        return key;
    }

    public SystemConfigurationAttributeType getType()
    {
        return type;
    }

    public String getDefaultValue()
    {
        return defaultValue;
    }

    public String getDescription()
    {
        return description;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private String key;
        private SystemConfigurationAttributeType type;
        private String defaultValue;
        private String description;

        private Builder()
        {
        }

        public Builder withKey(String key)
        {
            this.key = key;
            return this;
        }

        public Builder withType(SystemConfigurationAttributeType type)
        {
            this.type = type;
            return this;
        }

        public Builder withDefaultValue(String defaultValue)
        {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder withDescription(String description)
        {
            this.description = description;
            return this;
        }

        public SystemConfigurationAttributeDto build()
        {
            return new SystemConfigurationAttributeDto(this);
        }
    }
}

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
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.AbstractUserInterfaceObject.TargetMode;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ModuleDto.Builder.class)
@EFapsUUID("86cb6dc7-890a-4282-923e-45d6806a11ab")
@EFapsApplication("eFaps-WebApp")
public class ModuleDto
{

    private final String id;
    private final String key;
    private final TargetMode targetMode;
    private final Map<String, String> properties;
    private final String header;

    private ModuleDto(Builder builder)
    {
        this.id = builder.id;
        this.key = builder.key;
        this.targetMode = builder.targetMode;
        this.properties = builder.properties;
        this.header = builder.header;
    }

    public String getId()
    {
        return id;
    }

    public String getKey()
    {
        return key;
    }

    public TargetMode getTargetMode()
    {
        return targetMode;
    }

    public Map<String, String> getProperties()
    {
        return properties;
    }

    public String getHeader()
    {
        return header;
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

        private String id;
        private String key;
        private TargetMode targetMode;
        private Map<String, String> properties = Collections.emptyMap();
        private String header;

        private Builder()
        {
        }

        public Builder withId(String id)
        {
            this.id = id;
            return this;
        }

        public Builder withKey(String key)
        {
            this.key = key;
            return this;
        }

        public Builder withTargetMode(TargetMode targetMode)
        {
            this.targetMode = targetMode;
            return this;
        }

        public Builder withProperties(Map<String, String> properties)
        {
            this.properties = properties;
            return this;
        }

        public Builder withHeader(String header)
        {
            this.header = header;
            return this;
        }

        public ModuleDto build()
        {
            return new ModuleDto(this);
        }
    }
}

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

@JsonDeserialize(builder = SystemConfigurationLinkValueDto.Builder.class)
@EFapsUUID("b9cf0553-e6db-4e5c-aba8-2ccae738cd09")
@EFapsApplication("eFaps-WebApp")
public class SystemConfigurationLinkValueDto
{

    private final String key;
    private final String value;
    private final String description;
    private final Long companyLink;
    private final String appKey;

    private SystemConfigurationLinkValueDto(Builder builder)
    {
        this.key = builder.key;
        this.value = builder.value;
        this.description = builder.description;
        this.companyLink = builder.companyLink;
        this.appKey = builder.appKey;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }

    public String getDescription()
    {
        return description;
    }

    public String getAppKey()
    {
        return appKey;
    }

    public Long getCompanyLink()
    {
        return companyLink;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private String key;
        private String value;
        private String description;
        private Long companyLink;
        private String appKey;

        private Builder()
        {
        }

        public Builder withKey(String key)
        {
            this.key = key;
            return this;
        }

        public Builder withValue(String value)
        {
            this.value = value;
            return this;
        }

        public Builder withDescription(String description)
        {
            this.description = description;
            return this;
        }

        public Builder withCompanyLink(Long companyLink)
        {
            this.companyLink = companyLink;
            return this;
        }

        public Builder withAppKey(String appKey)
        {
            this.appKey = appKey;
            return this;
        }

        public SystemConfigurationLinkValueDto build()
        {
            return new SystemConfigurationLinkValueDto(this);
        }
    }

}

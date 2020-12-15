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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = DashboardWidgetDto.Builder.class)
@EFapsUUID("e0e5217c-f8c3-4b8a-8aac-5b0afacc5449")
@EFapsApplication("eFaps-WebApp")
public class DashboardWidgetDto
{

    private final DashboardWidgetType type;
    private final String identifier;
    private final String eql;

    private DashboardWidgetDto(final Builder builder)
    {
        type = builder.type;
        identifier = builder.identifier;
        eql = builder.eql;
    }

    public DashboardWidgetType getType()
    {
        return type;
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public String getEql()
    {
        return eql;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Creates builder to build {@link DashboardWidgetDto}.
     *
     * @return created builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to build {@link DashboardWidgetDto}.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder
    {

        private DashboardWidgetType type;
        private String identifier;
        private String eql;

        private Builder()
        {
        }

        public Builder withType(final DashboardWidgetType type)
        {
            this.type = type;
            return this;
        }

        public Builder withIdentifier(final String identifier)
        {
            this.identifier = identifier;
            return this;
        }

        public Builder withEql(final String eql)
        {
            this.eql = eql;
            return this;
        }

        public DashboardWidgetDto build()
        {
            return new DashboardWidgetDto(this);
        }
    }
}

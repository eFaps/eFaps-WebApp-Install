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

@EFapsUUID("e0e5217c-f8c3-4b8a-8aac-5b0afacc5449")
@EFapsApplication("eFaps-WebApp")
@JsonDeserialize(using = DashboardWidgetDeserializer.class)
public abstract class DashboardWidgetDto
{

    private final DashboardWidgetType type;
    private final String identifier;
    private final String eql;
    private final String title;

    protected DashboardWidgetDto(final Builder<?> builder)
    {
        type = builder.type;
        identifier = builder.identifier;
        eql = builder.eql;
        title = builder.title;
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

    public String getTitle()
    {
        return title;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Builder to build {@link DashboardWidgetDto}.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Builder<T extends Builder<T>>
    {

        private DashboardWidgetType type;
        private String identifier;
        private String eql;
        private String title;

        protected Builder()
        {
        }

        @SuppressWarnings("unchecked")
        public T withType(final DashboardWidgetType type)
        {
            this.type = type;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T withIdentifier(final String identifier)
        {
            this.identifier = identifier;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T withEql(final String eql)
        {
            this.eql = eql;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T withTitle(final String title)
        {
            this.title = title;
            return (T) this;
        }
    }
}

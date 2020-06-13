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

@JsonDeserialize(builder = OutlineDto.Builder.class)
@EFapsUUID("43ce6e45-afd4-40a8-92db-0944dc859570")
@EFapsApplication("eFaps-WebApp")
public class OutlineDto
{
    private final String oid;
    private final List<NavItemDto> menu;
    private final String header;
    private final List<ISection> sections;

    private OutlineDto(final Builder builder)
    {
        oid = builder.oid;
        menu = builder.menu;
        header = builder.header;
        sections = builder.sections;
    }

    public String getOid()
    {
        return oid;
    }

    public List<NavItemDto> getMenu()
    {
        return menu;
    }

    public String getHeader()
    {
        return header;
    }

    public List<ISection> getSections()
    {
        return sections;
    }

    /**
     * Creates builder to build {@link OutlineDto}.
     * @return created builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to build {@link OutlineDto}.
     */
    public static final class Builder
    {

        private String oid;
        private List<NavItemDto> menu = Collections.emptyList();
        private String header;
        private List<ISection> sections = Collections.emptyList();

        private Builder()
        {
        }

        public Builder withOid(final String oid)
        {
            this.oid = oid;
            return this;
        }

        public Builder withMenu(final List<NavItemDto> menu)
        {
            this.menu = menu;
            return this;
        }

        public Builder withHeader(final String header)
        {
            this.header = header;
            return this;
        }

        public Builder withSections(final List<ISection> sections)
        {
            this.sections = sections;
            return this;
        }

        public OutlineDto build()
        {
            return new OutlineDto(this);
        }
    }
}

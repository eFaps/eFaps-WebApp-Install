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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

@EFapsUUID("5d8211b1-5d2e-40df-8eb6-185542b37724")
@EFapsApplication("eFaps-WebApp")
public class ContentDto
{
    private final List<NavItemDto> nav;
    private final OutlineDto outline;
    private final String selected;

    private ContentDto(final Builder builder)
    {
        nav = builder.nav;
        outline = builder.outline;
        selected = builder.selected;
    }

    public List<NavItemDto> getNav()
    {
        return nav;
    }

    public OutlineDto getOutline()
    {
        return outline;
    }

    public String getSelected()
    {
        return selected;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Creates builder to build {@link ContentDto}.
     * @return created builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to build {@link ContentDto}.
     */
    public static final class Builder
    {

        private List<NavItemDto> nav = Collections.emptyList();
        private OutlineDto outline;
        private String selected;

        private Builder()
        {
        }

        public Builder withNav(final List<NavItemDto> nav)
        {
            this.nav = nav;
            return this;
        }

        public Builder withOutline(final OutlineDto outline)
        {
            this.outline = outline;
            return this;
        }

        public Builder withSelected(final String selected)
        {
            this.selected = selected;
            return this;
        }

        public ContentDto build()
        {
            return new ContentDto(this);
        }
    }
}

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

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

@EFapsUUID("857f2867-f2ca-49e8-97ff-0f7732bc6b01")
@EFapsApplication("eFaps-WebApp")
public class HeaderSectionDto
    implements ISection
{

    private final String header;
    private final int level;

    private HeaderSectionDto(final Builder builder)
    {
        header = builder.header;
        level = builder.level;
    }

    @Override
    public SectionType getType()
    {
        return SectionType.HEADING;
    }

    public String getHeader()
    {
        return header;
    }

    public int getLevel()
    {
        return level;
    }

    /**
     * Creates builder to build {@link HeaderSectionDto}.
     *
     * @return created builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to build {@link HeaderSectionDto}.
     */
    public static final class Builder
    {

        private String header;
        private int level;

        private Builder()
        {
        }

        public Builder withHeader(final String header)
        {
            this.header = header;
            return this;
        }

        public Builder withLevel(final int level)
        {
            this.level = level;
            return this;
        }

        public HeaderSectionDto build()
        {
            return new HeaderSectionDto(this);
        }
    }

}

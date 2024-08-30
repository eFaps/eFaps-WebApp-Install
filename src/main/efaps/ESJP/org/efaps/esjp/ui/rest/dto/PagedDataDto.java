/*
 * Copyright 2003 - 2024 The eFaps Team
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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(builder = PagedDataDto.Builder.class)
@EFapsUUID("df222de2-c37c-4114-8573-5f9f3f1b725e")
@EFapsApplication("eFaps-WebApp")
public class PagedDataDto
{

    private final Collection<Map<String, ?>> values;

    private final PageDto page;

    private PagedDataDto(Builder builder)
    {
        this.values = builder.values;
        this.page = builder.page;
    }

    @JsonSerialize(contentUsing = TableValueSerializer.class)
    public Collection<Map<String, ?>> getValues()
    {
        return values;
    }

    public PageDto getPage()
    {
        return page;
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

        private Collection<Map<String, ?>> values = Collections.emptyList();
        private PageDto page;

        private Builder()
        {
        }

        public Builder withValues(Collection<Map<String, ?>> values)
        {
            this.values = values;
            return this;
        }

        public Builder withPage(PageDto page)
        {
            this.page = page;
            return this;
        }

        public PagedDataDto build()
        {
            return new PagedDataDto(this);
        }
    }
}

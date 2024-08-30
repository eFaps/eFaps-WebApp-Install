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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = PageDto.Builder.class)
@EFapsUUID("9540e3d8-72ac-4d5a-8f12-1be1f1d79560")
@EFapsApplication("eFaps-WebApp")
public class PageDto
{

    private final int pageSize;

    private final long totalItems;

    private final int[] pageOptions;

    private PageDto(Builder builder)
    {
        this.pageSize = builder.pageSize;
        this.totalItems = builder.totalItems;
        this.pageOptions = builder.pageOptions;
    }

    public int getPageSize()
    {
        return pageSize;
    }

    public long getTotalItems()
    {
        return totalItems;
    }

    public int[] getPageOptions()
    {
        return pageOptions;
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

        private int pageSize;
        private long totalItems;
        private int[] pageOptions;

        private Builder()
        {
        }

        public Builder withPageSize(int pageSize)
        {
            this.pageSize = pageSize;
            return this;
        }

        public Builder withTotalItems(long totalItems)
        {
            this.totalItems = totalItems;
            return this;
        }

        public Builder withPageOptions(int[] pageOptions)
        {
            this.pageOptions = pageOptions;
            return this;
        }

        public PageDto build()
        {
            return new PageDto(this);
        }
    }
}

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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = DashboardPageDto.Builder.class)
@EFapsUUID("51206226-3c8f-4bfa-a91f-172f38c3ef1c")
@EFapsApplication("eFaps-WebApp")
public class DashboardPageDto
{

    private final String label;
    private final List<DashboardItemDto> items;

    private DashboardPageDto(Builder builder)
    {
        this.label = builder.label;
        this.items = builder.items;
    }

    public String getLabel()
    {
        return label;
    }

    public List<DashboardItemDto> getItems()
    {
        return items;
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

        private String label;
        private List<DashboardItemDto> items = Collections.emptyList();

        private Builder()
        {
        }

        public Builder withLabel(String label)
        {
            this.label = label;
            return this;
        }

        public Builder withItems(List<DashboardItemDto> items)
        {
            this.items = items;
            return this;
        }

        public DashboardPageDto build()
        {
            return new DashboardPageDto(this);
        }
    }
}

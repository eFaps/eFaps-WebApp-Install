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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = StructurBrowserEntryDto.Builder.class)
@EFapsUUID("dd8820f6-64fd-4ea4-a87d-79d706e18c63")
@EFapsApplication("eFaps-WebApp")
public class StructurBrowserEntryDto
{

    private final List<StructurBrowserEntryDto> children;

    private final Map<String, ?> values;

    private StructurBrowserEntryDto(Builder builder)
    {
        this.children = builder.children;
        this.values = builder.values;
    }

    public List<StructurBrowserEntryDto> getChildren()
    {
        return children;
    }

  //  @JsonSerialize(contentUsing = TableValueSerializer.class)
    public Map<String, ?> getValues()
    {
        return values;
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

        private List<StructurBrowserEntryDto> children = Collections.emptyList();
        private Map<String, ?> values = Collections.emptyMap();

        private Builder()
        {
        }

        public Builder withChildren(List<StructurBrowserEntryDto> children)
        {
            this.children = children;
            return this;
        }

        public Builder withValues(Map<String, ?> values)
        {
            this.values = values;
            return this;
        }

        public StructurBrowserEntryDto build()
        {
            return new StructurBrowserEntryDto(this);
        }
    }
}

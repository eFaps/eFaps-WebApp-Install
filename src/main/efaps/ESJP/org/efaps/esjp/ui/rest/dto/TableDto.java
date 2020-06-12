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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(builder = TableDto.Builder.class)
@EFapsUUID("8999d621-02d9-4ec6-9f3e-7571a485142b")
@EFapsApplication("eFaps-WebApp")
public class TableDto
{

    private final List<NavItemDto> menu;
    private final String header;
    private final List<ColumnDto> columns;
    private final Collection<Map<String, ?>> values;

    private TableDto(final Builder builder)
    {
        menu = builder.menu;
        header = builder.header;
        columns = builder.columns;
        values = builder.values;
    }

    public List<NavItemDto> getMenu()
    {
        return menu;
    }

    public String getHeader()
    {
        return header;
    }

    public List<ColumnDto> getColumns()
    {
        return columns;
    }

    @JsonSerialize(contentUsing = TableValueSerializer.class)
    public Collection<Map<String, ?>> getValues()
    {
        return values;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Creates builder to build {@link TableDto}.
     *
     * @return created builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to build {@link TableDto}.
     */
    public static final class Builder
    {

        private List<NavItemDto> menu = Collections.emptyList();
        private String header;
        private List<ColumnDto> columns = Collections.emptyList();
        private Collection<Map<String, ?>> values = Collections.emptyList();

        private Builder()
        {
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

        public Builder withColumns(final List<ColumnDto> columns)
        {
            this.columns = columns;
            return this;
        }

        public Builder withValues(final Collection<Map<String, ?>> values)
        {
            this.values = values;
            return this;
        }

        public TableDto build()
        {
            return new TableDto(this);
        }
    }
}

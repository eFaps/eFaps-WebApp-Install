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

@JsonDeserialize(builder = StructurBrowserDto.Builder.class)
@EFapsUUID("fffb2ec8-4251-4f83-aaf1-61d00791fdc4")
@EFapsApplication("eFaps-WebApp")
public class StructurBrowserDto
{

    private final List<NavItemDto> menu;
    private final String header;
    private final List<ColumnDto> columns;
    private final List<StructurBrowserEntryDto> values;
    private final String selectionMode;
    private final String toggleColumn;

    private StructurBrowserDto(Builder builder)
    {
        this.menu = builder.menu;
        this.header = builder.header;
        this.columns = builder.columns;
        this.values = builder.values;
        this.selectionMode = builder.selectionMode;
        this.toggleColumn = builder.toggleColumn;
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

    public List<StructurBrowserEntryDto> getValues()
    {
        return values;
    }

    public String getSelectionMode()
    {
        return selectionMode;
    }

    public String getToggleColumn()
    {
        return toggleColumn;
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

        private List<NavItemDto> menu = Collections.emptyList();
        private String header;
        private List<ColumnDto> columns = Collections.emptyList();
        private List<StructurBrowserEntryDto> values = Collections.emptyList();
        private String selectionMode;
        private String toggleColumn;

        private Builder()
        {
        }

        public Builder withMenu(List<NavItemDto> menu)
        {
            this.menu = menu;
            return this;
        }

        public Builder withHeader(String header)
        {
            this.header = header;
            return this;
        }

        public Builder withColumns(List<ColumnDto> columns)
        {
            this.columns = columns;
            return this;
        }

        public Builder withValues(List<StructurBrowserEntryDto> values)
        {
            this.values = values;
            return this;
        }

        public Builder withSelectionMode(String selectionMode)
        {
            this.selectionMode = selectionMode;
            return this;
        }

        public Builder withToggleColumn(String toggleColumn)
        {
            this.toggleColumn = toggleColumn;
            return this;
        }

        public StructurBrowserDto build()
        {
            return new StructurBrowserDto(this);
        }
    }
}

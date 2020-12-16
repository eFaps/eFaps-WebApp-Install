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

@JsonDeserialize(builder = WidgetTableDto.Builder.class)
@EFapsUUID("466c005a-7028-41d2-8906-13f092af901a")
@EFapsApplication("eFaps-WebApp")
public class WidgetTableDto
{

    private final List<ColumnDto> columns;
    private final Collection<Map<String, String>> values;

    private WidgetTableDto(final Builder builder)
    {
        columns = builder.columns;
        values = builder.values;
    }

    public List<ColumnDto> getColumns()
    {
        return columns;
    }

    public Collection<Map<String, String>> getValues()
    {
        return values;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Creates builder to build {@link WidgetTableDto}.
     *
     * @return created builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to build {@link WidgetTableDto}.
     */
    public static final class Builder
    {

        private List<ColumnDto> columns = Collections.emptyList();
        private Collection<Map<String, String>> values = Collections.emptyList();

        private Builder()
        {
        }

        public Builder withColumns(final List<ColumnDto> columns)
        {
            this.columns = columns;
            return this;
        }

        public Builder withValues(final Collection<Map<String, String>> values)
        {
            this.values = values;
            return this;
        }

        public WidgetTableDto build()
        {
            return new WidgetTableDto(this);
        }
    }

}

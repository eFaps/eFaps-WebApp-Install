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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = DashboardItemDto.Builder.class)
@EFapsUUID("ab961c5a-d974-4af0-a233-aeb4e7613b77")
@EFapsApplication("eFaps-WebApp")
public class DashboardItemDto
{

    private final int x;
    private final int y;
    private final int rows;
    private final int cols;

    private DashboardItemDto(final Builder builder)
    {
        x = builder.x;
        y = builder.y;
        rows = builder.rows;
        cols = builder.cols;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getRows()
    {
        return rows;
    }

    public int getCols()
    {
        return cols;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Creates builder to build {@link DashboardItemDto}.
     *
     * @return created builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to build {@link DashboardItemDto}.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder
    {

        private int x;
        private int y;
        private int rows;
        private int cols;

        private Builder()
        {
        }

        public Builder withx(final int x)
        {
            this.x = x;
            return this;
        }

        public Builder withy(final int y)
        {
            this.y = y;
            return this;
        }

        public Builder withRows(final int rows)
        {
            this.rows = rows;
            return this;
        }

        public Builder withCols(final int cols)
        {
            this.cols = cols;
            return this;
        }

        public DashboardItemDto build()
        {
            return new DashboardItemDto(this);
        }
    }
}

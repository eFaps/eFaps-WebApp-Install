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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = DashboardDto.Builder.class)
@EFapsUUID("d7702aed-aed6-46e7-8b80-268afa5fc649")
@EFapsApplication("eFaps-WebApp")
public class DashboardDto
{

    private final List<DashboardTabDto> tabs;

    private DashboardDto(final Builder builder)
    {
        tabs = builder.tabs;
    }

    public List<DashboardTabDto> getTabs()
    {
        return tabs;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Creates builder to build {@link DashboardDto}.
     *
     * @return created builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to build {@link DashboardDto}.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder
    {

        private List<DashboardTabDto> tabs = Collections.emptyList();

        private Builder()
        {
        }

        public Builder withTabs(final List<DashboardTabDto> tabs)
        {
            this.tabs = tabs;
            return this;
        }

        public DashboardDto build()
        {
            return new DashboardDto(this);
        }
    }
}

package org.efaps.esjp.ui.rest.dto;

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
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = DashboardWidgetChartMetricDto.Builder.class)
@EFapsUUID("5a1f30ad-0660-41ee-9d4e-7a263cddabf6")
@EFapsApplication("eFaps-WebApp")
public class DashboardWidgetChartMetricDto
{

    private final MetricFunction function;

    private final String key;

    private DashboardWidgetChartMetricDto(Builder builder)
    {
        this.function = builder.function;
        this.key = builder.key;
    }

    public MetricFunction getFunction()
    {
        return function;
    }

    public String getKey()
    {
        return key;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private MetricFunction function;
        private String key;

        private Builder()
        {
        }

        public Builder withType(MetricFunction function)
        {
            this.function = function;
            return this;
        }

        public Builder withKey(String key)
        {
            this.key = key;
            return this;
        }

        public DashboardWidgetChartMetricDto build()
        {
            return new DashboardWidgetChartMetricDto(this);
        }
    }
}

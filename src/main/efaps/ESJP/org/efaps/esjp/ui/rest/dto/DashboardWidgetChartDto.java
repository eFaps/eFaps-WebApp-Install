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

import java.util.List;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = DashboardWidgetChartDto.Builder.class)
@EFapsUUID("88f48f01-b568-4ad5-9b47-fcea8fe24ccc")
@EFapsApplication("eFaps-WebApp")
public class DashboardWidgetChartDto
    extends DashboardWidgetDto
{

    private final List<String> groupBy;
    private final List<DashboardWidgetChartMetricDto> metrics;
    private final String chartType;

    private DashboardWidgetChartDto(final Builder builder)
    {
        super(builder);
        this.groupBy = builder.groupBy;
        this.metrics = builder.metrics;
        this.chartType = builder.chartType;
    }

    public List<String> getGroupBy()
    {
        return groupBy;
    }

    public List<DashboardWidgetChartMetricDto> getMetrics()
    {
        return metrics;
    }

    public String getChartType()
    {
        return chartType;
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
        extends DashboardWidgetDto.Builder<Builder>
    {

        private List<String> groupBy;
        private List<DashboardWidgetChartMetricDto> metrics;
        private String chartType;

        private Builder()
        {
            withType(DashboardWidgetType.CHART);
        }

        public Builder withGroupBy(List<String> groupBy)
        {
            this.groupBy = groupBy;
            return this;
        }

        public Builder withMetrics(List<DashboardWidgetChartMetricDto> metrics)
        {
            this.metrics = metrics;
            return this;
        }

        public Builder withChartType(String chartType)
        {
            this.chartType = chartType;
            return this;
        }

        public DashboardWidgetChartDto build()
        {
            return new DashboardWidgetChartDto(this);
        }
    }
}

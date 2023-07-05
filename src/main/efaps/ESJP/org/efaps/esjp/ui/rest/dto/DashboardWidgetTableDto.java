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

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = DashboardWidgetTableDto.Builder.class)
@EFapsUUID("a225a3ce-33e1-41b8-80a5-3cc04466bde9")
@EFapsApplication("eFaps-WebApp")
public class DashboardWidgetTableDto
    extends DashboardWidgetDto
{

    private DashboardWidgetTableDto(final Builder builder)
    {
        super(builder);
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

        private Builder()
        {
            withType(DashboardWidgetType.TABLE);
        }

        public DashboardWidgetTableDto build()
        {
            return new DashboardWidgetTableDto(this);
        }
    }
}

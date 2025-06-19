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

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = DashboardWidgetTemplateDto.Builder.class)
@EFapsUUID("3882973e-be90-4fc0-9902-ccb255f078da")
@EFapsApplication("eFaps-WebApp")
public class DashboardWidgetTemplateDto
    extends DashboardWidgetDto
{

    private DashboardWidgetTemplateDto(final Builder builder)
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
            withType(DashboardWidgetType.TEMPLATE);
        }

        public DashboardWidgetTemplateDto build()
        {
            return new DashboardWidgetTemplateDto(this);
        }
    }
}

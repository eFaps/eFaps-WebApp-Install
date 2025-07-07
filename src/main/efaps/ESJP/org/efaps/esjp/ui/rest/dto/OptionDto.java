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
import org.apache.commons.lang3.builder.ToStringStyle;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = OptionDto.Builder.class)
@EFapsUUID("9bd46754-d249-4a4a-ae5f-1de3f370bbbc")
@EFapsApplication("eFaps-WebApp")
public class OptionDto
{

    private final String label;
    private final String display;
    private final Object value;

    private OptionDto(final Builder builder)
    {
        label = builder.label;
        value = builder.value;
        display = builder.display;
    }

    public String getLabel()
    {
        return label;
    }

    public Object getValue()
    {
        return value;
    }

    public String getDisplay()
    {
        return display;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    /**
     * Creates builder to build {@link OptionDto}.
     *
     * @return created builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to build {@link OptionDto}.
     */
    public static final class Builder
    {

        private String label;
        private String display;
        private Object value;

        private Builder()
        {
        }

        public Builder withLabel(final String label)
        {
            this.label = label;
            return this;
        }

        public Builder withDisplay(final String display)
        {
            this.display = display;
            return this;
        }

        public Builder withValue(final Object value)
        {
            this.value = value;
            return this;
        }

        public OptionDto build()
        {
            return new OptionDto(this);
        }
    }

}

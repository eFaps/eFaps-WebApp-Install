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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = FilterDto.Builder.class)
@EFapsUUID("45bf4ab8-fa54-45f8-aa81-0d947f323746")
@EFapsApplication("eFaps-WebApp")
public class FilterDto
{

    private final FilterKind kind;

    private final String attribute;

    private final String field;

    private final Object value1;

    private final Object value2;

    private final boolean required;

    private FilterDto(Builder builder)
    {
        this.kind = builder.kind;
        this.attribute = builder.attribute;
        this.field = builder.field;
        this.value1 = builder.value1;
        this.value2 = builder.value2;
        this.required = builder.required;
    }

    public FilterKind getKind()
    {
        return kind;
    }

    public String getAttribute()
    {
        return attribute;
    }

    public String getField()
    {
        return field;
    }

    public Object getValue1()
    {
        return value1;
    }

    public Object getValue2()
    {
        return value2;
    }

    public boolean isRequired()
    {
        return required;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private FilterKind kind;
        private String attribute;
        private String field;
        private Object value1;
        private Object value2;
        private boolean required;

        private Builder()
        {
        }

        public Builder withKind(FilterKind kind)
        {
            this.kind = kind;
            return this;
        }

        public Builder withAttribute(String attribute)
        {
            this.attribute = attribute;
            return this;
        }

        public Builder withField(String field)
        {
            this.field = field;
            return this;
        }

        public Builder withValue1(Object value1)
        {
            this.value1 = value1;
            return this;
        }

        public Builder withValue2(Object value2)
        {
            this.value2 = value2;
            return this;
        }

        public Builder withRequired(boolean required)
        {
            this.required = required;
            return this;
        }

        public FilterDto build()
        {
            return new FilterDto(this);
        }
    }
}

/*
 * Copyright 2003 - 2024 The eFaps Team
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
package org.efaps.esjp.ui.rest.modules.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = CSVImportRequestDto.Builder.class)
@EFapsUUID("e0203c7c-abde-49da-9d1f-f46008e7efd5")
@EFapsApplication("eFaps-WebApp")
public class CSVImportRequestDto
{

    private final String parentOid;
    private final CSVImportResult result;

    private CSVImportRequestDto(Builder builder)
    {
        this.parentOid = builder.parentOid;
        this.result = builder.result;
    }

    public String getParentOid()
    {
        return parentOid;
    }

    public CSVImportResult getResult()
    {
        return result;
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

        private String parentOid;
        private CSVImportResult result;

        private Builder()
        {
        }

        public Builder withParentOid(String parentOid)
        {
            this.parentOid = parentOid;
            return this;
        }

        public Builder withResult(CSVImportResult result)
        {
            this.result = result;
            return this;
        }

        public CSVImportRequestDto build()
        {
            return new CSVImportRequestDto(this);
        }
    }
}

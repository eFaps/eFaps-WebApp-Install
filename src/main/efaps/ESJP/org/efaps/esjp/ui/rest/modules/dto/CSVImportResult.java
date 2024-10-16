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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = CSVImportResult.Builder.class)
@EFapsUUID("06ccd28b-81f3-4a85-ac5e-74998868c811")
@EFapsApplication("eFaps-WebApp")
public class CSVImportResult
{
    private final List<Map<String, Object>> data;

    private final CSVImportMeta meta;

    private CSVImportResult(Builder builder)
    {
        this.data = builder.data;
        this.meta = builder.meta;
    }

    public List<Map<String, Object>> getData()
    {
        return data;
    }

    public CSVImportMeta getMeta()
    {
        return meta;
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder
    {

        private List<Map<String, Object>> data = Collections.emptyList();
        private CSVImportMeta meta;

        private Builder()
        {
        }

        public Builder withData(List<Map<String, Object>> data)
        {
            this.data = data;
            return this;
        }

        public Builder withMeta(CSVImportMeta meta)
        {
            this.meta = meta;
            return this;
        }

        public CSVImportResult build()
        {
            return new CSVImportResult(this);
        }
    }

}

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

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = CSVImportMeta.Builder.class)
@EFapsUUID("bb2923b3-1223-440d-b803-d379dd8ba04a")
@EFapsApplication("eFaps-WebApp")
public class CSVImportMeta
{

    private final List<String> fields;

    public List<String> getFields()
    {
        return fields;
    }

    private CSVImportMeta(Builder builder)
    {
        this.fields = builder.fields;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder
    {

        private List<String> fields = Collections.emptyList();

        private Builder()
        {
        }

        public Builder withFields(List<String> fields)
        {
            this.fields = fields;
            return this;
        }

        public CSVImportMeta build()
        {
            return new CSVImportMeta(this);
        }
    }
}

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

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = AutocompleteResponseDto.Builder.class)
@EFapsUUID("13994486-fef8-4fbe-8f55-6b627e1822e3")
@EFapsApplication("eFaps-WebApp")
public class AutocompleteResponseDto
{

    private final List<OptionDto> options;

    private AutocompleteResponseDto(final Builder builder)
    {
        options = builder.options;
    }

    public List<OptionDto> getOptions()
    {
        return options;
    }

    /**
     * Creates builder to build {@link AutocompleteResponseDto}.
     *
     * @return created builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to build {@link AutocompleteResponseDto}.
     */
    public static final class Builder
    {

        private List<OptionDto> options = Collections.emptyList();

        private Builder()
        {
        }

        public Builder withOptions(final List<OptionDto> options)
        {
            this.options = options;
            return this;
        }

        public AutocompleteResponseDto build()
        {
            return new AutocompleteResponseDto(this);
        }
    }
}

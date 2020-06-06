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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = SearchDto.Builder.class)
@EFapsUUID("c34bb8a5-7cc3-4b2d-9aa2-45f7e19cbcae")
@EFapsApplication("eFaps-WebApp")
public class SearchDto
{

    private final List<SearchDto> children;
    private final String label;
    private final boolean selected;
    private final FormSectionDto formSection;

    private SearchDto(final Builder builder)
    {
        children = builder.children;
        label = builder.label;
        selected = builder.selected;
        formSection = builder.formSection;
    }

    public List<SearchDto> getChildren()
    {
        return children;
    }
    public String getLabel()
    {
        return label;
    }
    public boolean isSelected()
    {
        return selected;
    }
    public FormSectionDto getFormSection()
    {
        return formSection;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Creates builder to build {@link SearchDto}.
     * @return created builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to build {@link SearchDto}.
     */
    public static final class Builder
    {

        private List<SearchDto> children = Collections.emptyList();
        private String label;
        private boolean selected;
        private FormSectionDto formSection;

        private Builder()
        {
        }

        public Builder withChildren(final List<SearchDto> children)
        {
            this.children = children;
            return this;
        }

        public Builder withLabel(final String label)
        {
            this.label = label;
            return this;
        }

        public Builder withSelected(final boolean selected)
        {
            this.selected = selected;
            return this;
        }

        public Builder withFormSection(final FormSectionDto formSection)
        {
            this.formSection = formSection;
            return this;
        }

        public SearchDto build()
        {
            return new SearchDto(this);
        }
    }

}

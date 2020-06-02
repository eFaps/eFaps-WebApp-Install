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

@EFapsUUID("e9d8679f-d8d1-4340-b04b-0ffc9150779d")
@EFapsApplication("eFaps-WebApp")
public class FormSectionDto
    implements ISection
{

    private final List<Object> items;

    private FormSectionDto(final Builder builder)
    {
        items = builder.items;
    }

    @Override
    public SectionType getType()
    {
        return SectionType.FORM;
    }

    public List<Object> getItems()
    {
        return items;
    }

    /**
     * Creates builder to build {@link FormSectionDto}.
     * @return created builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to build {@link FormSectionDto}.
     */
    public static final class Builder
    {

        private List<Object> items = Collections.emptyList();

        private Builder()
        {
        }

        public Builder withItems(final List<Object> items)
        {
            this.items = items;
            return this;
        }

        public FormSectionDto build()
        {
            return new FormSectionDto(this);
        }
    }
}

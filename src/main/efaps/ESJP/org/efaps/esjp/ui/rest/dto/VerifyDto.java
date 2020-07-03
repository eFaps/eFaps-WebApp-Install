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

@JsonDeserialize(builder = VerifyDto.Builder.class)
@EFapsUUID("52511bd7-97a7-4d5a-a1ed-ccc8fce47bab")
@EFapsApplication("eFaps-WebApp")
public class VerifyDto
{
    private final String question;
    private final int selectedRows;

    private VerifyDto(final Builder builder)
    {
        question = builder.question;
        selectedRows = builder.selectedRows;
    }
    public String getQuestion()
    {
        return question;
    }
    public int getSelectedRows()
    {
        return selectedRows;
    }
    /**
     * Creates builder to build {@link VerifyDto}.
     * @return created builder
     */
    public static Builder builder()
    {
        return new Builder();
    }
    /**
     * Builder to build {@link VerifyDto}.
     */
    public static final class Builder
    {

        private String question;
        private int selectedRows;

        private Builder()
        {
        }

        public Builder withQuestion(final String question)
        {
            this.question = question;
            return this;
        }

        public Builder withSelectedRows(final int selectedRows)
        {
            this.selectedRows = selectedRows;
            return this;
        }

        public VerifyDto build()
        {
            return new VerifyDto(this);
        }
    }
}

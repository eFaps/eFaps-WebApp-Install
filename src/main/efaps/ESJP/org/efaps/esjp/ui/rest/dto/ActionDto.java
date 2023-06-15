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
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ActionDto.Builder.class)
@EFapsUUID("271067ff-2afc-4c25-93b8-372ffb5eddda")
@EFapsApplication("eFaps-WebApp")
public class ActionDto
{

    private final ActionType type;
    private final String label;
    private final VerifyDto verify;
    private final boolean modal;

    private ActionDto(final Builder builder)
    {
        type = builder.type;
        label = builder.label;
        verify = builder.verify;
        this.modal = builder.modal;
    }

    public ActionType getType()
    {
        return type;
    }

    public String getLabel()
    {
        return label;
    }

    public VerifyDto getVerify()
    {
        return verify;
    }

    public boolean isModal()
    {
        return modal;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Creates builder to build {@link ActionDto}.
     * @return created builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to build {@link ActionDto}.
     */
    public static final class Builder
    {

        private ActionType type;
        private String label;
        private VerifyDto verify;
        boolean modal;

        private Builder()
        {
        }

        public Builder withType(final ActionType type)
        {
            this.type = type;
            return this;
        }

        public Builder withLabel(final String label)
        {
            this.label = label;
            return this;
        }

        public Builder withVerify(final VerifyDto verify)
        {
            this.verify = verify;
            return this;
        }

        public Builder withModal(final boolean modal)
        {
            this.modal = modal;
            return this;
        }

        public ActionDto build()
        {
            return new ActionDto(this);
        }
    }
}

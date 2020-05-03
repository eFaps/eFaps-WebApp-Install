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

@EFapsUUID("271067ff-2afc-4c25-93b8-372ffb5eddda")
@EFapsApplication("eFaps-WebApp")
@JsonDeserialize(builder = ActionDto.Builder.class)
public class ActionDto
{

    private final ActionType type;

    private ActionDto(final Builder _builder)
    {
        type = _builder.type;
    }

    public ActionType getType()
    {
        return type;
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

        private ActionType type;

        public Builder withType(final ActionType _type)
        {
            type = _type;
            return this;
        }

        public ActionDto build()
        {
            return new ActionDto(this);
        }
    }
}

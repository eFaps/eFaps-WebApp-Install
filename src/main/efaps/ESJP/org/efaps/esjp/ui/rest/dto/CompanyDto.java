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

import java.util.UUID;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = CompanyDto.Builder.class)
@EFapsUUID("271067ff-2afc-4c25-93b8-372ffb5eddda")
@EFapsApplication("eFaps-WebApp")
public class CompanyDto
{

    private final String name;

    private final UUID uuid;

    private final boolean current;

    private CompanyDto(Builder builder)
    {
        this.name = builder.name;
        this.uuid = builder.uuid;
        this.current = builder.current;
    }

    public boolean isCurrent()
    {
        return current;
    }

    public String getName()
    {
        return name;
    }

    public UUID getUuid()
    {
        return uuid;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private String name;
        private UUID uuid;
        private boolean current;

        private Builder()
        {
        }

        public Builder withName(String name)
        {
            this.name = name;
            return this;
        }

        public Builder withUuid(UUID uuid)
        {
            this.uuid = uuid;
            return this;
        }

        public Builder withCurrent(boolean current)
        {
            this.current = current;
            return this;
        }

        public CompanyDto build()
        {
            return new CompanyDto(this);
        }
    }
}

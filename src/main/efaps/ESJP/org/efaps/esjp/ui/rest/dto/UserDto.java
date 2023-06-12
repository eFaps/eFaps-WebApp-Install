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

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@EFapsUUID("4e510e19-5072-4f9e-b62c-953a3963f831")
@EFapsApplication("eFaps-WebApp")
@JsonDeserialize(builder = UserDto.Builder.class)
public class UserDto
{

    private final String firstName;
    private final String lastName;
    private final List<CompanyDto> companies;

    public UserDto(final Builder _builder)
    {
        firstName = _builder.firstName;
        lastName = _builder.lastName;
        companies = _builder.companies;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public List<CompanyDto> getCompanies()
    {
        return companies;
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

        private String firstName;
        private String lastName;
        private List<CompanyDto> companies;

        public Builder withFirstName(final String _firstName)
        {
            firstName = _firstName;
            return this;
        }

        public Builder withLastName(final String _lastName)
        {
            lastName = _lastName;
            return this;
        }

        public Builder withCompanies(final List<CompanyDto> companies)
        {
            this.companies = companies;
            return this;
        }

        public UserDto build()
        {
            return new UserDto(this);
        }
    }
}

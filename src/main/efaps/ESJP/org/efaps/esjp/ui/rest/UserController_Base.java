/*
 * Copyright 2023 - 2020 The eFaps Team
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
package org.efaps.esjp.ui.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.user.Company;
import org.efaps.ci.CIAdminUser;
import org.efaps.db.Context;
import org.efaps.eql.EQL;
import org.efaps.esjp.ui.rest.dto.CompanyDto;
import org.efaps.esjp.ui.rest.dto.UserDto;
import org.efaps.util.EFapsException;

import jakarta.ws.rs.core.Response;

@EFapsUUID("001b5b92-6389-4a0b-9b57-0ee415f8bd8b")
@EFapsApplication("eFaps-WebApp")
public abstract class UserController_Base
{

    public Response getCurrent()
        throws EFapsException
    {
        final var person = Context.getThreadContext().getPerson();
        final var currentCompanyId = Context.getThreadContext().getCompany().getId();

        final List<CompanyDto> companies = new ArrayList<>();

        for (final var companyId : person.getCompanies()) {
            final var oneCompany = Company.get(companyId);
            companies.add(CompanyDto.builder()
                            .withName(oneCompany.getName())
                            .withUuid(oneCompany.getUUID())
                            .withCurrent(currentCompanyId == companyId)
                            .build());
        }

        final var dto = UserDto.builder()
                        .withFirstName(person.getFirstName())
                        .withLastName(person.getLastName())
                        .withCompanies(companies)
                        .build();

        final Response ret = Response.ok()
                        .entity(dto)
                        .build();
        return ret;
    }

    public Response getCompanies()
        throws EFapsException
    {
        final var eval = EQL.builder().print().query(CIAdminUser.Company).select()
                        .attribute(CIAdminUser.Company.Name, CIAdminUser.Company.UUID)
                        .evaluate();
        final List<CompanyDto> companies = new ArrayList<>();
        while (eval.next()) {
            companies.add(CompanyDto.builder()
                            .withOid(eval.inst().getOid())
                            .withName(eval.get(CIAdminUser.Company.Name))
                            .withUuid(UUID.fromString(eval.get(CIAdminUser.Company.UUID)))
                            .build());
        }
        return Response.ok()
                        .entity(companies)
                        .build();
    }
}

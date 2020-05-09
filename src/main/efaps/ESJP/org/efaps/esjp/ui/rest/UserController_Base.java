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
package org.efaps.esjp.ui.rest;

import javax.ws.rs.core.Response;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.db.Context;
import org.efaps.esjp.ui.rest.dto.UserDto;
import org.efaps.util.EFapsException;

@EFapsUUID("001b5b92-6389-4a0b-9b57-0ee415f8bd8b")
@EFapsApplication("eFaps-WebApp")
public abstract class UserController_Base
{

    public Response getCurrent()
        throws EFapsException
    {
        final var person = Context.getThreadContext().getPerson();
        final var company = Context.getThreadContext().getCompany();
        final var dto = UserDto.builder()
                        .withFirstName(person.getFirstName())
                        .withLastName(person.getLastName())
                        .withCompany(company.getName())
                        .build();

        final Response ret = Response.ok()
                        .entity(dto)
                        .build();
        return ret;
    }
}

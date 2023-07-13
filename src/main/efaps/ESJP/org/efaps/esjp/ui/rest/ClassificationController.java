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
package org.efaps.esjp.ui.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.efaps.admin.datamodel.Classification;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.esjp.ui.rest.dto.ClassificationDto;
import org.efaps.util.EFapsException;
import org.efaps.util.cache.CacheReloadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EFapsUUID("ff450297-ad55-4b04-b215-86153588f144")
@EFapsApplication("eFaps-WebApp")
@Path("/ui/classification")
public class ClassificationController
{

    private static final Logger LOG = LoggerFactory.getLogger(ClassificationController.class);

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getTree(@QueryParam("id") final List<String> uuids)
        throws EFapsException
    {
        final List<Classification> classifications = new ArrayList<>();
        for (final var uuid : uuids) {
            classifications.add(Classification.get(UUID.fromString(uuid)));
        }

        final var entity = classifications.stream().map(ClassificationController::toDto).collect(Collectors.toList());

        return Response.ok()
                        .entity(entity)
                        .build();
    }

    public static ClassificationDto toDto(final Classification clazz)
    {
        List<ClassificationDto> children = new ArrayList<>();
        try {
            children = clazz.getChildClassifications().stream().map(ClassificationController::toDto)
                            .collect(Collectors.toList());
        } catch (final CacheReloadException e) {
            LOG.error("Catched", e);
        }
        return ClassificationDto.builder()
                        .withId(clazz.getUUID().toString())
                        .withLabel(clazz.getLabel())
                        .withChildren(children)
                        .build();
    }
}

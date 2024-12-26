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
package org.efaps.esjp.ui.rest.modules;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.ci.CIAdminCommon;
import org.efaps.db.Instance;
import org.efaps.eql.EQL;
import org.efaps.esjp.admin.common.systemconfiguration.AbstractSysConfAttribute;
import org.efaps.esjp.admin.common.systemconfiguration.BooleanSysConfAttribute;
import org.efaps.esjp.admin.common.systemconfiguration.ISysConfAttribute;
import org.efaps.esjp.admin.common.systemconfiguration.IntegerSysConfAttribute;
import org.efaps.esjp.admin.common.systemconfiguration.ListSysConfAttribute;
import org.efaps.esjp.admin.common.systemconfiguration.PropertiesSysConfAttribute;
import org.efaps.esjp.admin.common.systemconfiguration.StringSysConfAttribute;
import org.efaps.esjp.admin.common.systemconfiguration.SysConfResourceConfig;
import org.efaps.esjp.db.InstanceUtils;
import org.efaps.esjp.ui.rest.modules.dto.SystemConfigurationAttributeDto;
import org.efaps.esjp.ui.rest.modules.dto.SystemConfigurationAttributeType;
import org.efaps.esjp.ui.rest.modules.dto.SystemConfigurationAttributeValueDto;
import org.efaps.esjp.ui.rest.modules.dto.SystemConfigurationLinkDto;
import org.efaps.esjp.ui.rest.modules.dto.SystemConfigurationLinkValueDto;
import org.efaps.esjp.ui.util.ValueUtils;
import org.efaps.util.EFapsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@EFapsUUID("22922ed8-2eb6-445e-8250-3e7714cec55c")
@EFapsApplication("eFaps-WebApp")
@Path("/ui/modules/system-configurations")
public class SystemConfigurationController
{

    private static final Logger LOG = LoggerFactory.getLogger(SystemConfigurationController.class);

    @Path("/{sysConfOid}/attributes")
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public Response geteSystemConfigurationAttributes(@PathParam("sysConfOid") final String oid)
        throws EFapsException
    {
        final var inst = Instance.get(oid);
        List<SystemConfigurationAttributeDto> keys = new ArrayList<>();
        if (InstanceUtils.isType(inst, CIAdminCommon.SystemConfiguration)) {
            final var eval = EQL.builder().print(inst)
                            .attribute(CIAdminCommon.SystemConfiguration.UUID)
                            .evaluate();
            final var sysconfUUID = eval.<String>get(CIAdminCommon.SystemConfiguration.UUID);
            keys = SysConfResourceConfig.getResourceConfig().getAttributes(sysconfUUID).stream()
                            .map(this::evalKey)
                            .sorted(Comparator.comparing(SystemConfigurationAttributeDto::getKey))
                            .collect(Collectors.toList());
        }
        return Response.ok(keys).build();
    }

    @POST
    @Path("/{sysConfOid}/attributes")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response createSystemConfigurationAttribute(@PathParam("sysConfOid") final String oid,
                                                       final SystemConfigurationAttributeValueDto dto)
        throws EFapsException
    {
        final var inst = Instance.get(oid);
        if (InstanceUtils.isType(inst, CIAdminCommon.SystemConfiguration)) {
            EQL.builder().insert(CIAdminCommon.SystemConfigurationAttribute)
                            .set(CIAdminCommon.SystemConfigurationAttribute.AbstractLink, inst)
                            .set(CIAdminCommon.SystemConfigurationAttribute.Key, dto.getKey())
                            .set(CIAdminCommon.SystemConfigurationAttribute.Value, evalValue(dto))
                            .set(CIAdminCommon.SystemConfigurationAttribute.Description, dto.getDescription())
                            .set(CIAdminCommon.SystemConfigurationAttribute.AppKey, dto.getAppKey())
                            .set(CIAdminCommon.SystemConfigurationAttribute.CompanyLink, dto.getCompanyLink())
                            .execute();
        }
        return Response.ok().build();
    }

    @Path("/{sysConfOid}/attributes/{attrOid}")
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getSystemConfigurationAttribute(@PathParam("attrOid") final String oid)
        throws EFapsException
    {
        final var inst = Instance.get(oid);
        SystemConfigurationAttributeValueDto dto = null;
        if (InstanceUtils.isType(inst, CIAdminCommon.SystemConfigurationAttribute)) {
            final var eval = EQL.builder().print(inst)
                            .attribute(CIAdminCommon.SystemConfigurationAttribute.Key,
                                            CIAdminCommon.SystemConfigurationAttribute.Value,
                                            CIAdminCommon.SystemConfigurationAttribute.Description,
                                            CIAdminCommon.SystemConfigurationAttribute.CompanyLink,
                                            CIAdminCommon.SystemConfigurationAttribute.AppKey)
                            .linkto(CIAdminCommon.SystemConfigurationAttribute.AbstractLink)
                            .attribute(CIAdminCommon.SystemConfiguration.UUID)
                            .evaluate();
            final var sysconfUUID = eval.<String>get(CIAdminCommon.SystemConfiguration.UUID);
            final var key = eval.<String>get(CIAdminCommon.SystemConfigurationAttribute.Key);
            final var attr = SysConfResourceConfig.getResourceConfig().getAttribute(sysconfUUID, key);
            dto = SystemConfigurationAttributeValueDto.builder()
                            .withKey(key)
                            .withValue(eval.get(CIAdminCommon.SystemConfigurationAttribute.Value))
                            .withDescription(eval.get(CIAdminCommon.SystemConfigurationAttribute.Description))
                            .withCompanyLink(eval.get(CIAdminCommon.SystemConfigurationAttribute.CompanyLink))
                            .withAppKey(eval.get(CIAdminCommon.SystemConfigurationAttribute.AppKey))
                            .withType(evalType(attr))
                            .build();
        }
        return Response.ok(dto).build();
    }

    @Path("/{sysConfOid}/attributes/{attrOid}")
    @PUT
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response updateSystemConfigurationAttribute(@PathParam("attrOid") final String oid,
                                                       final SystemConfigurationAttributeValueDto dto)
        throws EFapsException
    {
        final var inst = Instance.get(oid);
        if (InstanceUtils.isType(inst, CIAdminCommon.SystemConfigurationAttribute)) {
            EQL.builder().update(inst)
                            .set(CIAdminCommon.SystemConfigurationAttribute.Key, dto.getKey())
                            .set(CIAdminCommon.SystemConfigurationAttribute.Value, evalValue(dto))
                            .set(CIAdminCommon.SystemConfigurationAttribute.Description, dto.getDescription())
                            .set(CIAdminCommon.SystemConfigurationAttribute.AppKey, dto.getAppKey())
                            .set(CIAdminCommon.SystemConfigurationAttribute.CompanyLink, dto.getCompanyLink())
                            .execute();
        }
        return Response.ok().build();
    }

    @Path("/{sysConfOid}/links")
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getSystemConfigurationLinks(@PathParam("sysConfOid") final String oid)
        throws EFapsException
    {
        final var inst = Instance.get(oid);
        List<SystemConfigurationLinkDto> keys = new ArrayList<>();
        if (InstanceUtils.isType(inst, CIAdminCommon.SystemConfiguration)) {
            final var eval = EQL.builder().print(inst)
                            .attribute(CIAdminCommon.SystemConfiguration.UUID)
                            .evaluate();
            final var sysconfUUID = eval.<String>get(CIAdminCommon.SystemConfiguration.UUID);
            keys = SysConfResourceConfig.getResourceConfig().getLinks(sysconfUUID).stream()
                            .map(link -> SystemConfigurationLinkDto.builder()
                                            .withKey(link.getKey())
                                            .withDescription(String.valueOf(
                                                            ((AbstractSysConfAttribute<?, ?>) link).getDescription()))
                                            .build())
                            .sorted(Comparator.comparing(SystemConfigurationLinkDto::getKey))
                            .collect(Collectors.toList());
        }
        return Response.ok(keys).build();
    }

    @POST
    @Path("/{sysConfOid}/links")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response createSystemConfigurationLink(@PathParam("sysConfOid") final String oid,
                                                  final SystemConfigurationLinkValueDto dto)
        throws EFapsException
    {
        final var inst = Instance.get(oid);
        if (InstanceUtils.isType(inst, CIAdminCommon.SystemConfiguration)) {
            EQL.builder().insert(CIAdminCommon.SystemConfigurationLink)
                            .set(CIAdminCommon.SystemConfigurationLink.AbstractLink, inst)
                            .set(CIAdminCommon.SystemConfigurationLink.Key, dto.getKey())
                            .set(CIAdminCommon.SystemConfigurationLink.Value, dto.getValue())
                            .set(CIAdminCommon.SystemConfigurationLink.Description, dto.getDescription())
                            .set(CIAdminCommon.SystemConfigurationLink.AppKey, dto.getAppKey())
                            .set(CIAdminCommon.SystemConfigurationLink.CompanyLink, dto.getCompanyLink())
                            .execute();
        }
        return Response.ok().build();
    }

    @Path("/{sysConfOid}/links/{linkOid}")
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getSystemConfigurationLink(@PathParam("linkOid") final String oid)
        throws EFapsException
    {
        final var inst = Instance.get(oid);
        SystemConfigurationLinkValueDto dto = null;
        if (InstanceUtils.isType(inst, CIAdminCommon.SystemConfigurationLink)) {
            final var eval = EQL.builder().print(inst)
                            .attribute(CIAdminCommon.SystemConfigurationLink.Key,
                                            CIAdminCommon.SystemConfigurationLink.Value,
                                            CIAdminCommon.SystemConfigurationLink.Description,
                                            CIAdminCommon.SystemConfigurationLink.CompanyLink,
                                            CIAdminCommon.SystemConfigurationLink.AppKey)
                            .linkto(CIAdminCommon.SystemConfigurationLink.AbstractLink)
                            .attribute(CIAdminCommon.SystemConfiguration.UUID)
                            .evaluate();
            final var key = eval.<String>get(CIAdminCommon.SystemConfigurationLink.Key);
            dto = SystemConfigurationLinkValueDto.builder()
                            .withKey(key)
                            .withValue(eval.get(CIAdminCommon.SystemConfigurationLink.Value))
                            .withDescription(eval.get(CIAdminCommon.SystemConfigurationLink.Description))
                            .withCompanyLink(eval.get(CIAdminCommon.SystemConfigurationLink.CompanyLink))
                            .withAppKey(eval.get(CIAdminCommon.SystemConfigurationLink.AppKey))
                            .build();
        }
        return Response.ok(dto).build();
    }

    @Path("/{sysConfOid}/links/{linkOid}")
    @PUT
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response updateSystemConfigurationLink(@PathParam("linkOid") final String oid,
                                                  final SystemConfigurationLinkValueDto dto)
        throws EFapsException
    {
        final var inst = Instance.get(oid);
        if (InstanceUtils.isType(inst, CIAdminCommon.SystemConfigurationLink)) {
            EQL.builder().update(inst)
                            .set(CIAdminCommon.SystemConfigurationLink.Key, dto.getKey())
                            .set(CIAdminCommon.SystemConfigurationLink.Value, dto.getValue())
                            .set(CIAdminCommon.SystemConfigurationLink.Description, dto.getDescription())
                            .set(CIAdminCommon.SystemConfigurationLink.AppKey, dto.getAppKey())
                            .set(CIAdminCommon.SystemConfigurationLink.CompanyLink, dto.getCompanyLink())
                            .execute();
        }
        return Response.ok().build();
    }

    protected SystemConfigurationAttributeType evalType(final ISysConfAttribute attr)
    {
        SystemConfigurationAttributeType type;
        if (attr instanceof BooleanSysConfAttribute) {
            type = SystemConfigurationAttributeType.BOOLEAN;
        } else if (attr instanceof PropertiesSysConfAttribute) {
            type = SystemConfigurationAttributeType.PROPERTIES;
        } else if (attr instanceof StringSysConfAttribute) {
            type = SystemConfigurationAttributeType.STRING;
        } else if (attr instanceof ListSysConfAttribute) {
            type = SystemConfigurationAttributeType.LIST;
        } else if (attr instanceof IntegerSysConfAttribute) {
            type = SystemConfigurationAttributeType.INTEGER;
        } else {
            type = SystemConfigurationAttributeType.UNDEFINED;
        }
        return type;
    }

    @SuppressWarnings("rawtypes")
    protected SystemConfigurationAttributeDto evalKey(final ISysConfAttribute attr)
    {
        String defaultValue = null;
        String description = null;
        if (attr instanceof AbstractSysConfAttribute) {
            final var mapper = ValueUtils.getObjectMapper();
            try {
                defaultValue = mapper.writeValueAsString(((AbstractSysConfAttribute) attr).getDefaultValue());
            } catch (final JsonProcessingException e) {
                LOG.error("Catched", e);
            }
            description = String.valueOf(((AbstractSysConfAttribute) attr).getDescription());
        }
        return SystemConfigurationAttributeDto.builder()
                        .withKey(attr.getKey())
                        .withType(evalType(attr))
                        .withDefaultValue(defaultValue)
                        .withDescription(description)
                        .build();
    }

    protected String evalValue(final SystemConfigurationAttributeValueDto dto)
    {
        return switch (dto.getType()) {
            case LIST: {
                String strValue = null;
                try {
                    if (dto.getValue() != null) {
                        final var mapper = ValueUtils.getObjectMapper();
                        strValue = mapper.readValue(dto.getValue(), new TypeReference<List<String>>()
                        {
                        }).stream().collect(Collectors.joining("\n"));
                    }
                } catch (final JsonProcessingException e) {
                    LOG.error("Catched", e);
                }
                yield strValue;
            }
            default:
                yield dto.getValue();
        };
    }

}

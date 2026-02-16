/*
 * Copyright © 2003 - 2024 The eFaps Team (-)
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
 */
package org.efaps.esjp.ui.rest.modules;

import java.io.File;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.efaps.admin.event.Parameter;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.ci.CIAdminProgram;
import org.efaps.db.Instance;
import org.efaps.eql.EQL;
import org.efaps.esjp.common.jasperreport.StandartReport;
import org.efaps.esjp.common.parameter.ParameterUtil;
import org.efaps.esjp.ui.util.FileUtil;
import org.efaps.util.EFapsException;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@EFapsUUID("b288b652-30e7-4eaa-b51e-3ffbbf93281f")
@EFapsApplication("eFaps-WebApp")
@Path("/ui/modules/jasper-report")
public class JasperReportController
{

    @POST
    @Path("/export/{oid}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response export(@PathParam("oid") final String reportOid,
                           final Map<String, String> values)
        throws EFapsException
    {
        final var parameter = ParameterUtil.instance();
        ParameterUtil.setParameterValues(parameter, "mime", values.get("mime"));
        parameter.put(ParameterValues.INSTANCE, Instance.get(reportOid));
        final var standartReport = new StandartReport()
        {

            @Override
            protected Instance getJasperReportInstance(final Parameter parameter)
                throws EFapsException
            {
                final var reportInst = Instance.get(reportOid);
                final var eval = EQL.builder().print().query(CIAdminProgram.JasperReportCompiled).where()
                                .attribute(CIAdminProgram.JasperReportCompiled.ProgramLink).eq(reportInst)
                                .select()
                                .instance().evaluate();

                Instance compiledInst = null;
                if (eval.next()) {
                    compiledInst = eval.inst();
                }
                return compiledInst;
            }
        };

        final var jrParameters = standartReport.loadJRParameters(parameter);
        for (final var jrParameter : jrParameters) {
            final String value = values.get(jrParameter.getName());
            Object obj = null;
            obj = switch (jrParameter.getValueClassName()) {
                case "java.lang.Integer" -> Integer.valueOf(value);
                case "java.lang.Long" -> Long.valueOf(value);
                case "java.lang.Boolean" -> BooleanUtils.toBoolean(value);
                default -> value;
            };
            standartReport.getJrParameters().put(jrParameter.getName(), obj);
        }

        final var result = standartReport.execute(parameter);
        final var retVal = result.get(ReturnValues.VALUES);
        String downloadKey = null;
        if (retVal instanceof File) {
            downloadKey = FileUtil.put((File) retVal);
        }
        return Response.ok()
                        .entity("{ \"downloadKey\": \"" + downloadKey + "\"}")
                        .build();
    }

    @GET
    @Path("/{oid}/parameters")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response getParameters(@PathParam("oid") final String reportOid)
        throws EFapsException
    {
        final var parameter = ParameterUtil.instance();
        final var reportInst = Instance.get(reportOid);
        parameter.put(ParameterValues.INSTANCE, reportInst);
        final var jrParameters = new StandartReport().loadJRParameters(parameter);
        return Response.ok(jrParameters).build();
    }

}

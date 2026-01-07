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
package org.efaps.esjp.ui.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.efaps.admin.datamodel.Classification;
import org.efaps.admin.event.EventType;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.AbstractCommand;
import org.efaps.admin.ui.AbstractUserInterfaceObject.TargetMode;
import org.efaps.admin.ui.Command;
import org.efaps.db.Context;
import org.efaps.db.Instance;
import org.efaps.esjp.db.InstanceUtils;
import org.efaps.esjp.ui.rest.dto.ExecResponseDto;
import org.efaps.esjp.ui.rest.dto.PayloadDto;
import org.efaps.esjp.ui.util.FileUtil;
import org.efaps.util.EFapsException;
import org.efaps.util.cache.CacheReloadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.core.Response;

@EFapsUUID("29e9c73d-a4ec-4f20-a8ad-b213b0db1afe")
@EFapsApplication("eFaps-WebApp")
public abstract class ExecController_Base
    extends AbstractController
{

    private static final Logger LOG = LoggerFactory.getLogger(ExecController.class);

    protected void evalUpload(final PayloadDto dto)
        throws EFapsException
    {
        if (dto.getValues().containsKey("eFapsUpload")) {
            final String uploadFieldName = (String) dto.getValues().get("eFapsUpload");
            final var uploads = dto.getValues().get(uploadFieldName);
            final List<String> keys = new ArrayList<>();
            if (uploads instanceof Collection) {
                keys.addAll(((Collection<?>) uploads).stream().map(obj -> ((String) obj)).toList());
            } else {
                keys.add((String) uploads);
            }
            int i = 0;
            for (final var key : keys) {
                final var file = UploadController.FILEMAP.get(key);
                if (file != null) {
                    final var parameterName = keys.size() > 1 ? uploadFieldName + "_" + i : uploadFieldName;
                    Context.getThreadContext().getFileParameters().put(parameterName, new Context.FileParameter()
                    {

                        @Override
                        public void close()
                            throws IOException
                        {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public String getContentType()
                        {
                            return "";
                        }

                        @Override
                        public InputStream getInputStream()
                            throws IOException
                        {
                            return new FileInputStream(file);
                        }

                        @Override
                        public String getName()
                        {
                            return file.getName();
                        }

                        @Override
                        public String getParameterName()
                        {
                            return uploadFieldName;
                        }

                        @Override
                        public long getSize()
                        {
                            return file.length();
                        }
                    });
                }
                i++;
            }
        }
    }

    public Response exec(final String cmdId,
                         final PayloadDto dto)
        throws EFapsException
    {
        LOG.info("exec : {}, payload: {}", cmdId, dto);
        final var parameters = convertToMap(dto);
        evalUpload(dto);
        final AbstractCommand cmd = Command.get(UUID.fromString(cmdId));

        final var paraValues = new ArrayList<>();
        paraValues.add(ParameterValues.PAYLOAD);
        paraValues.add(dto.getValues());

        paraValues.add(ParameterValues.PARAMETERS);
        paraValues.add(parameters);

        if ((TargetMode.EDIT.equals(cmd.getTargetMode()) || TargetMode.UNKNOWN.equals(cmd.getTargetMode()))
                        && parameters.containsKey("eFapsOID")) {
            paraValues.add(ParameterValues.INSTANCE);
            paraValues.add(Instance.get(parameters.get("eFapsOID")[0]));
        }
        if (TargetMode.CREATE.equals(cmd.getTargetMode())
                        && parameters.containsKey("eFapsParentOID")) {
            paraValues.add(ParameterValues.INSTANCE);
            paraValues.add(Instance.get(parameters.get("eFapsParentOID")[0]));
        }
        if (parameters.containsKey("eFapsSelectedOids")) {
            paraValues.add(ParameterValues.OTHERS);
            paraValues.add(parameters.get("eFapsSelectedOids"));
        }
        evalClassifications(dto, paraValues);

        final var result = cmd.executeEvents(EventType.UI_COMMAND_EXECUTE, paraValues.toArray());
        String downloadKey = null;
        if (cmd.isTargetShowFile()) {
            final var retVal = result.get(0).get(ReturnValues.VALUES);
            if (retVal instanceof File) {
                downloadKey = FileUtil.put((File) retVal);
            }
        }
        String targetOid = null;
        if (TargetMode.CREATE.equals(cmd.getTargetMode()) && result.get(0).get(ReturnValues.INSTANCE) != null) {
            final var inst =  (Instance) result.get(0).get(ReturnValues.INSTANCE);
            if (InstanceUtils.isValid(inst)) {
                targetOid = inst.getOid();
            }
        }

        final var response = ExecResponseDto.builder()
                        .withReload(!cmd.isNoUpdateAfterCmd())
                        .withDownloadKey(downloadKey)
                        .withTargetOid(targetOid)
                        .build();

        final Response ret = Response.ok()
                        .entity(response)
                        .build();
        return ret;
    }

    protected void evalClassifications(final PayloadDto dto,
                                       final ArrayList<Object> paraValues)
    {
        if (dto.getValues().containsKey("eFapsClassifications")) {
            paraValues.add(ParameterValues.CLASSIFICATIONS);
            @SuppressWarnings("unchecked")
            final List<String> uuids = (List<String>) dto.getValues().get("eFapsClassifications");
            paraValues.add(uuids.stream().map(uuid -> {
                try {
                    return Classification.get(UUID.fromString(uuid));
                } catch (final CacheReloadException e) {
                    LOG.error("Catched", e);
                }
                return null;
            }).toList());
        }
    }
}

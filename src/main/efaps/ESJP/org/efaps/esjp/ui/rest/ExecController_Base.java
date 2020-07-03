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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.efaps.admin.event.EventType;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.AbstractCommand;
import org.efaps.admin.ui.AbstractUserInterfaceObject.TargetMode;
import org.efaps.admin.ui.Command;
import org.efaps.admin.ui.field.Field;
import org.efaps.api.ui.UIType;
import org.efaps.db.Context;
import org.efaps.db.Instance;
import org.efaps.util.EFapsException;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EFapsUUID("29e9c73d-a4ec-4f20-a8ad-b213b0db1afe")
@EFapsApplication("eFaps-WebApp")
public abstract class ExecController_Base
    extends AbstractController
{

    private static final Logger LOG = LoggerFactory.getLogger(ExecController.class);

    public Response exec(final String _cmdId, final FormDataMultiPart _formData)
        throws EFapsException
    {
        final var parameters = new HashMap<String, String[]>();
        final var fields = _formData.getFields();
        for (final var entry : fields.entrySet()) {
            final var fieldName = entry.getKey();
            final var values = entry.getValue().stream().map(part -> {
                return part.getValue();
            }).toArray(String[]::new);
            parameters.put(fieldName, values);
        }

        final AbstractCommand cmd = Command.get(UUID.fromString(_cmdId));
        evalUpload(cmd, parameters);

        final var paraValues = new ArrayList<Object>();
        paraValues.add(ParameterValues.PARAMETERS);
        paraValues.add(parameters);

        if (TargetMode.EDIT.equals(cmd.getTargetMode()) && parameters.containsKey("eFapsOID")) {
            paraValues.add(ParameterValues.INSTANCE);
            paraValues.add(Instance.get(parameters.get("eFapsOID")[0]));
        }
        if (parameters.containsKey("eFapsSelectedOids")) {
            paraValues.add(ParameterValues.OTHERS);
            paraValues.add(parameters.get("eFapsSelectedOids"));
        }
        cmd.executeEvents(EventType.UI_COMMAND_EXECUTE, paraValues.toArray());

        final Response ret = Response.ok()
                        .build();
        return ret;
    }

    protected void evalUpload(final AbstractCommand _cmd, final Map<String, String[]> _parameters)
    {
        final var form = _cmd.getTargetForm();
        if (form != null) {
            form.getFields().stream().filter(field -> {
                final var uiType = getUIType(field);
                return UIType.UPLOAD.equals(uiType) || UIType.UPLOADMULTIPLE.equals(uiType);
            }).forEach(field -> {
                registerFileParameter(field, _parameters);
            });
        }
    }

    protected void registerFileParameter(final Field _field, final Map<String, String[]> _parameters)
    {
        final var keys = _parameters.get(_field.getName());
        int idx = -1;
        for (final String key : keys) {
            final var file = UploadController_Base.MAP.get(key);
            if (file != null && file.exists()) {
                try {
                    final var filePara = new FilePara()
                                    .setParameterName(_field.getName())
                                    .setFile(file);
                    final var fieldKey = idx > -1 ? _field.getName() + "_" + idx : _field.getName();
                    Context.getThreadContext().getFileParameters().put(fieldKey, filePara);
                    idx++;
                } catch (final EFapsException e) {
                    LOG.error("Catched", e);
                }
            }
        }
    }

    public static class FilePara
        implements Context.FileParameter
    {

        private String parameterName;
        private File file;
        private InputStream inputStream;

        @Override
        public void close()
            throws IOException
        {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        @Override
        public String getContentType()
        {
            return null;
        }

        @Override
        public InputStream getInputStream()
            throws IOException
        {
            if (inputStream == null) {
                inputStream = FileUtils.openInputStream(file);
            }
            return inputStream;
        }

        @Override
        public String getName()
        {
            return file.getName();
        }

        @Override
        public String getParameterName()
        {
            return parameterName;
        }

        @Override
        public long getSize()
        {
            return file.length();
        }

        public FilePara setParameterName(final String _parameterName)
        {
            parameterName = _parameterName;
            return this;
        }

        public FilePara setFile(final File _file)
        {
            file = _file;
            return this;
        }
    }
}

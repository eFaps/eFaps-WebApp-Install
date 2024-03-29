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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.efaps.admin.AppConfigHandler;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.db.Context;
import org.efaps.esjp.ui.rest.dto.UploadResponseDto;
import org.efaps.util.EFapsException;
import org.efaps.util.RandomUtil;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EFapsUUID("9b89aec9-f0cc-4a4e-9b67-06aee26963f6")
@EFapsApplication("eFaps-WebApp")
public abstract class UploadController_Base
{

    private static final Logger LOG = LoggerFactory.getLogger(UploadController.class);

    public static Map<String, File> FILEMAP = new HashMap<>();

    public Response upload(final FormDataMultiPart _formData)
        throws EFapsException
    {
        final List<String> ret = new ArrayList<>();
        try {
            for (final var bodyPart : _formData.getBodyParts()) {
                final var fileName = bodyPart.getContentDisposition().getFileName();
                final var file = getFile(fileName);
                final InputStream is = bodyPart.getEntityAs(InputStream.class);
                FileUtils.copyInputStreamToFile(is, file);
                final var key = RandomUtil.randomAlphanumeric(12);
                FILEMAP.put(key, file);
                ret.add(key);
            }
        } catch (final IOException e) {
            LOG.error("Catched", e);
        }
        return Response.ok()
                        .entity(UploadResponseDto.builder()
                                        .withKeys(ret)
                                        .build())
                        .build();
    }

    public File getFile(final String _name)
        throws EFapsException, IOException
    {
        File tmpfld = AppConfigHandler.get().getTempFolder();
        if (tmpfld == null) {
            final File temp = File.createTempFile("eFaps", ".tmp");
            tmpfld = temp.getParentFile();
            temp.delete();
        }
        final File storeFolder = new File(tmpfld, "eFapsUserDepUploads");
        final NumberFormat formater = NumberFormat.getInstance();
        formater.setMinimumIntegerDigits(8);
        formater.setGroupingUsed(false);
        final File userFolder = new File(storeFolder, formater.format(Context.getThreadContext().getPersonId()));
        if (!userFolder.exists()) {
            userFolder.mkdirs();
        }
        final String name = StringUtils.stripAccents(_name);
        return new File(userFolder, name.replaceAll("[^a-zA-Z0-9.-]", "_"));
    }
}

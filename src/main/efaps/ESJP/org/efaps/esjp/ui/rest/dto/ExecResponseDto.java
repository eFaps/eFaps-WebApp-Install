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
package org.efaps.esjp.ui.rest.dto;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ExecResponseDto.Builder.class)
@EFapsUUID("9ae6757b-e420-4e32-bcf0-679cfec80982")
@EFapsApplication("eFaps-WebApp")
public class ExecResponseDto
{

    private final boolean reload;
    private final String downloadKey;

    private ExecResponseDto(Builder builder)
    {
        this.reload = builder.reload;
        this.downloadKey = builder.downloadKey;
    }

    public boolean isReload()
    {
        return reload;
    }

    public String getDownloadKey()
    {
        return downloadKey;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private boolean reload;
        private String downloadKey;

        private Builder()
        {
        }

        public Builder withReload(boolean reload)
        {
            this.reload = reload;
            return this;
        }

        public Builder withDownloadKey(String downloadKey)
        {
            this.downloadKey = downloadKey;
            return this;
        }

        public ExecResponseDto build()
        {
            return new ExecResponseDto(this);
        }
    }
}

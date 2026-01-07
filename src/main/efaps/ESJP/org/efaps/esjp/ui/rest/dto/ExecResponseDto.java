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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.annotation.Generated;

@JsonDeserialize(builder = ExecResponseDto.Builder.class)
@EFapsUUID("9ae6757b-e420-4e32-bcf0-679cfec80982")
@EFapsApplication("eFaps-WebApp")
public class ExecResponseDto
{

    private final boolean reload;
    private final String downloadKey;
    private final String targetOid;

    @Generated("SparkTools")
    private ExecResponseDto(Builder builder)
    {
        this.reload = builder.reload;
        this.downloadKey = builder.downloadKey;
        this.targetOid = builder.targetOid;
    }

    public boolean isReload()
    {
        return reload;
    }

    public String getDownloadKey()
    {
        return downloadKey;
    }

    public String getTargetOid()
    {
        return targetOid;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    @Generated("SparkTools")
    public static Builder builder()
    {
        return new Builder();
    }

    @Generated("SparkTools")
    public static final class Builder
    {

        private boolean reload;
        private String downloadKey;
        private String targetOid;

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

        public Builder withTargetOid(String targetOid)
        {
            this.targetOid = targetOid;
            return this;
        }

        public ExecResponseDto build()
        {
            return new ExecResponseDto(this);
        }
    }
}

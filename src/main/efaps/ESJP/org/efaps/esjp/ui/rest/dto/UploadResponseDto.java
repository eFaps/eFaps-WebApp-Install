package org.efaps.esjp.ui.rest.dto;

import java.util.List;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = UploadResponseDto.Builder.class)
@EFapsUUID("033e6ff6-82f9-4940-abec-6b235a60f6e1")
@EFapsApplication("eFaps-WebApp")
public class UploadResponseDto
{

    private final List<String> keys;

    private UploadResponseDto(final Builder builder)
    {
        keys = builder.keys;
    }

    public List<String> getKeys()
    {
        return keys;
    }

    /**
     * Creates builder to build {@link UploadResponseDto}.
     *
     * @return created builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to build {@link UploadResponseDto}.
     */
    public static final class Builder
    {

        private List<String> keys;

        private Builder()
        {
        }

        public Builder withKeys(final List<String> keys)
        {
            this.keys = keys;
            return this;
        }

        public UploadResponseDto build()
        {
            return new UploadResponseDto(this);
        }
    }

}

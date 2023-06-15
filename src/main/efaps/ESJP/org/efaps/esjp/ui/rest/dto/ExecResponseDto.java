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

    private ExecResponseDto(Builder builder)
    {
        this.reload = builder.reload;
    }

    public boolean isReload()
    {
        return reload;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private boolean reload;

        private Builder()
        {
        }

        public Builder withReload(boolean reload)
        {
            this.reload = reload;
            return this;
        }

        public ExecResponseDto build()
        {
            return new ExecResponseDto(this);
        }
    }
}

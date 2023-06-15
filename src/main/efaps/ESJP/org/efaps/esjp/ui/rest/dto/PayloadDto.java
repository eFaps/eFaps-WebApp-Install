package org.efaps.esjp.ui.rest.dto;

import java.util.Collections;
import java.util.Map;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = PayloadDto.Builder.class)
@EFapsUUID("2005361c-f55a-4b71-a496-fd98166cb750")
@EFapsApplication("eFaps-WebApp")
public class PayloadDto
{

    private final Map<String, ?> values;

    private PayloadDto(Builder builder)
    {
        this.values = builder.values;
    }

    public Map<String, ?> getValues()
    {
        return values;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder
    {

        private Map<String, ?> values = Collections.emptyMap();

        private Builder()
        {
        }

        public Builder withValues(Map<String, ?> values)
        {
            this.values = values;
            return this;
        }

        public PayloadDto build()
        {
            return new PayloadDto(this);
        }
    }
}

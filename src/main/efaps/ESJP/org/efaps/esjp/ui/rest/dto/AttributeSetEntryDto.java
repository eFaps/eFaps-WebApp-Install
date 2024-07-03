package org.efaps.esjp.ui.rest.dto;

import java.util.Collections;
import java.util.List;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = AttributeSetEntryDto.Builder.class)
@EFapsUUID("2c80a394-23df-4d67-8264-4062eb1f600d")
@EFapsApplication("eFaps-WebApp")
public class AttributeSetEntryDto
{

    private final Long rowId;

    private final List<ValueDto> values;

    private AttributeSetEntryDto(Builder builder)
    {
        this.rowId = builder.rowId;
        this.values = builder.values;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public Long getRowId()
    {
        return rowId;
    }

    public List<ValueDto> getValues()
    {
        return values;
    }

    public static final class Builder
    {

        private Long rowId;
        private List<ValueDto> values = Collections.emptyList();

        private Builder()
        {
        }

        public Builder withRowId(Long rowId)
        {
            this.rowId = rowId;
            return this;
        }

        public Builder withValues(List<ValueDto> values)
        {
            this.values = values;
            return this;
        }

        public AttributeSetEntryDto build()
        {
            return new AttributeSetEntryDto(this);
        }
    }
}

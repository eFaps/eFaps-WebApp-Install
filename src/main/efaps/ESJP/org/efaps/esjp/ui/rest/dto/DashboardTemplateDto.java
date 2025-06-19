package org.efaps.esjp.ui.rest.dto;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = DashboardTemplateDto.Builder.class)
@EFapsUUID("97dfc78f-9422-4566-857a-1741644277d7")
@EFapsApplication("eFaps-WebApp")
public class DashboardTemplateDto
{

    private final String oid;
    private final String name;
    private final String description;

    private DashboardTemplateDto(Builder builder)
    {
        this.oid = builder.oid;
        this.name = builder.name;
        this.description = builder.description;
    }

    public String getOid()
    {
        return oid;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private String oid;
        private String name;
        private String description;

        private Builder()
        {
        }

        public Builder withOid(String oid)
        {
            this.oid = oid;
            return this;
        }

        public Builder withName(String name)
        {
            this.name = name;
            return this;
        }

        public Builder withDescription(String description)
        {
            this.description = description;
            return this;
        }

        public DashboardTemplateDto build()
        {
            return new DashboardTemplateDto(this);
        }
    }

}

package org.efaps.esjp.ui.rest.dto;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = DashboardWidgetDataDto.Builder.class)
@EFapsUUID("4bbc6cce-1bc3-43a5-9672-e14308e3f39f")
@EFapsApplication("eFaps-WebApp")
public class DashboardWidgetDataDto
{

    private final DashboardWidgetDto widget;
    private final Object data;

    private DashboardWidgetDataDto(Builder builder)
    {
        this.widget = builder.widget;
        this.data = builder.data;
    }

    public DashboardWidgetDto getWidget()
    {
        return widget;
    }

    public Object getData()
    {
        return data;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private DashboardWidgetDto widget;
        private Object data;

        private Builder()
        {
        }

        public Builder withWidget(DashboardWidgetDto widget)
        {
            this.widget = widget;
            return this;
        }

        public Builder withData(Object data)
        {
            this.data = data;
            return this;
        }

        public DashboardWidgetDataDto build()
        {
            return new DashboardWidgetDataDto(this);
        }
    }
}

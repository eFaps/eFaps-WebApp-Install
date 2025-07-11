package org.efaps.esjp.ui.rest.dto;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(builder = TableSectionDto.Builder.class)
@EFapsUUID("c7445c98-2a27-4143-9202-02bb341e0838")
@EFapsApplication("eFaps-WebApp")
public class TableSectionDto
    implements ISection
{

    private final String id;
    private final List<ColumnDto> columns;
    private final Collection<Map<String, ?>> values;
    private final boolean editable;
    private final String ref;

    private TableSectionDto(final Builder builder)
    {
        id = builder.id;
        columns = builder.columns;
        values = builder.values;
        editable = builder.editable;
        ref = builder.ref;
    }

    @Override
    public SectionType getType()
    {
        return SectionType.TABLE;
    }

    @Override
    public String getRef()
    {
        return ref;
    }

    public List<ColumnDto> getColumns()
    {
        return columns;
    }

    @JsonSerialize(contentUsing = TableValueSerializer.class)
    public Collection<Map<String, ?>> getValues()
    {
        return values;
    }

    public boolean isEditable()
    {
        return editable;
    }

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    /**
     * Creates builder to build {@link TableSectionDto}.
     *
     * @return created builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to build {@link TableSectionDto}.
     */
    public static final class Builder
    {

        private String id;
        private List<ColumnDto> columns = Collections.emptyList();
        private Collection<Map<String, ?>> values = Collections.emptyList();
        private boolean editable;
        private String ref;

        private Builder()
        {
        }

        public Builder withId(final String id)
        {
            this.id = id;
            return this;
        }

        public Builder withRef(final String ref)
        {
            this.ref = ref;
            return this;
        }

        public Builder withColumns(final List<ColumnDto> columns)
        {
            this.columns = columns;
            return this;
        }

        public Builder withValues(final Collection<Map<String, ?>> values)
        {
            this.values = values;
            return this;
        }

        public Builder withEditable(final boolean editable)
        {
            this.editable = editable;
            return this;
        }

        public TableSectionDto build()
        {
            return new TableSectionDto(this);
        }
    }

}

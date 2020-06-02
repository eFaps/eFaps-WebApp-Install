package org.efaps.esjp.ui.rest.dto;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

@EFapsUUID("c7445c98-2a27-4143-9202-02bb341e0838")
@EFapsApplication("eFaps-WebApp")
public class TableSectionDto
    implements ISection
{

    private final List<ColumnDto> columns;
    private final Collection<Map<String, ?>> values;

    private TableSectionDto(final Builder builder)
    {
        columns = builder.columns;
        values = builder.values;
    }

    @Override
    public SectionType getType()
    {
        return SectionType.TABLE;
    }

    public List<ColumnDto> getColumns()
    {
        return columns;
    }

    public Collection<Map<String, ?>> getValues()
    {
        return values;
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

        private List<ColumnDto> columns = Collections.emptyList();
        private Collection<Map<String, ?>> values = Collections.emptyList();

        private Builder()
        {
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

        public TableSectionDto build()
        {
            return new TableSectionDto(this);
        }
    }

}

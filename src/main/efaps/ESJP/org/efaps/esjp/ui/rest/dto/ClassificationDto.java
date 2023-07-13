package org.efaps.esjp.ui.rest.dto;

import java.util.Collections;
import java.util.List;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ClassificationDto.Builder.class)
@EFapsUUID("2a8f30fb-1e52-40f4-9a48-6fb33ab3f4c5")
@EFapsApplication("eFaps-WebApp")
public class ClassificationDto
{

    private final String id;
    private final String label;
    private final List<ClassificationDto> children;

    private ClassificationDto(Builder builder)
    {
        this.id = builder.id;
        this.label = builder.label;
        this.children = builder.children;
    }

    public String getId()
    {
        return id;
    }

    public String getLabel()
    {
        return label;
    }

    public List<ClassificationDto> getChildren()
    {
        return children;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private String id;
        private String label;
        private List<ClassificationDto> children = Collections.emptyList();

        private Builder()
        {
        }

        public Builder withId(String id)
        {
            this.id = id;
            return this;
        }

        public Builder withLabel(String label)
        {
            this.label = label;
            return this;
        }

        public Builder withChildren(List<ClassificationDto> children)
        {
            this.children = children;
            return this;
        }

        public ClassificationDto build()
        {
            return new ClassificationDto(this);
        }
    }
}

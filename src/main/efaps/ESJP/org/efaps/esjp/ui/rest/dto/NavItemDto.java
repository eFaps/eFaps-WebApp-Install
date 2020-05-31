/*
 * Copyright 2003 - 2020 The eFaps Team
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
 *
 */
package org.efaps.esjp.ui.rest.dto;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@EFapsUUID("aec663fc-2fe7-4a67-8b6e-891f6eaf07c5")
@EFapsApplication("eFaps-WebApp")
@JsonDeserialize(builder = NavItemDto.Builder.class)
public class NavItemDto
{

    private final String id;
    private final String label;
    private final List<NavItemDto> children;
    private final ActionDto action;

    private NavItemDto(final Builder _builder)
    {
        id = _builder.id;
        label = _builder.label;
        children = _builder.children;
        action = _builder.action;
    }

    public String getId()
    {
        return id;
    }

    public String getLabel()
    {
        return label;
    }

    public List<NavItemDto> getChildren()
    {
        return children;
    }

    public ActionDto getAction()
    {
        return action;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private String id;
        private String label;
        private List<NavItemDto> children;
        private ActionDto action;

        public Builder withId(final String _id)
        {
            id = _id;
            return this;
        }

        public Builder withLabel(final String _label)
        {
            label = _label;
            return this;
        }

        public Builder withChildren(final List<NavItemDto> _children)
        {
            children = _children;
            return this;
        }

        public Builder withAction(final ActionDto _action)
        {
            action = _action;
            return this;
        }

        public NavItemDto build()
        {
            return new NavItemDto(this);
        }
    }
}

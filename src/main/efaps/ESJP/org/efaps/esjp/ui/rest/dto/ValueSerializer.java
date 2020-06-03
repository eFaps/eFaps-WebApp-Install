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

import java.io.IOException;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;

@EFapsUUID("0537bca7-83aa-4ca2-b82a-fe876f79b4f1")
@EFapsApplication("eFaps-WebApp")
public class ValueSerializer extends AbstractSerializer<Object>
{

    @Override
    public void serialize(final Object _value, final JsonGenerator _gen, final SerializerProvider _serializers)
        throws IOException, JsonProcessingException
    {
        _gen.writeObject(getObjectValue(_value));
    }
}

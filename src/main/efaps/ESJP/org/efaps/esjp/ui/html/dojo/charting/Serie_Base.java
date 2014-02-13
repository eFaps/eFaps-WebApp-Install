/*
 * Copyright 2003 - 2014 The eFaps Team
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
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
 */


package org.efaps.esjp.ui.html.dojo.charting;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.efaps.admin.program.esjp.EFapsRevision;
import org.efaps.admin.program.esjp.EFapsUUID;


/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
@EFapsUUID("e1c7f4c7-9e0b-4681-bdcf-8cee80caea4b")
@EFapsRevision("$Rev$")
public abstract class Serie_Base<T extends AbstractData>
{
    private String name;


    private final List<T> data = new ArrayList<T>();


    /**
     * @return
     */
    public CharSequence getJavaScript()
    {
        final StringBuilder ret = new StringBuilder()
            .append("[\n");
        boolean first = true;
        for (final T dat  : this.data) {
            if (first) {
                first = false;
            } else {
                ret.append(",\n");
            }
            ret.append(dat.getJavaScript());
        }
        ret.append("\n]");
        return ret;
    }

    /**
     * Getter method for the instance variable {@link #name}.
     *
     * @return value of instance variable {@link #name}
     */
    public String getName()
    {
        if (this.name == null) {
            this.name = RandomStringUtils.randomAlphabetic(8);
        }
        return this.name;
    }

    /**
     * Setter method for instance variable {@link #name}.
     *
     * @param _name value for instance variable {@link #name}
     */
    public void setName(final String _name)
    {
        this.name = _name;
    }

    /**
     * Getter method for the instance variable {@link #data}.
     *
     * @return value of instance variable {@link #data}
     */
    public List<T> getData()
    {
        return this.data;
    }

    /**
     * Getter method for the instance variable {@link #data}.
     *
     * @return value of instance variable {@link #data}
     */
    public void addData(final T _data)
    {
        this.data.add(_data);
    }
}

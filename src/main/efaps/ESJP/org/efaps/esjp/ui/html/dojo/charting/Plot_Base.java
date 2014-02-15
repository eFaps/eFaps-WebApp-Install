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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
public abstract class Plot_Base<S extends Plot_Base<S>>
{

    private String name = "default";

    private final Map<String, Object> configMap = new LinkedHashMap<String, Object>();


    private String hAxis;

    private String vAxis;

    /**
     * "getThis" trick.
     *
     * @return this
     */
    protected abstract S getThis();

    protected void addJS(final StringBuilder _js,
                         final String _chartVarName)
    {
        _js.append(_chartVarName).append(".addPlot(\"").append(getName()).append("\", ")
                        .append(getConfigJS()).append(");\n");
    }

    public CharSequence getConfigJS()
    {
        if (getvAxis() != null && !this.configMap.containsKey("vAxis")) {
            this.configMap.put("vAxis", "\"" + getvAxis() + "\"");
        }
        if (gethAxis() != null && !this.configMap.containsKey("hAxis")) {
            this.configMap.put("hAxis", "\"" + getvAxis() + "\"");
        }
        return Util.mapToObjectList(this.configMap);
    }

    /**
     * Getter method for the instance variable {@link #name}.
     *
     * @return value of instance variable {@link #name}
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Setter method for instance variable {@link #name}.
     *
     * @param _name value for instance variable {@link #name}
     */
    public S setName(final String _name)
    {
        this.name = _name;
        return getThis();
    }

    public S addConfig(final String _key,
                       final Object _value)
    {
        this.configMap.put(_key, _value);
        return getThis();
    }


    /**
     * Getter method for the instance variable {@link #hAxis}.
     *
     * @return value of instance variable {@link #hAxis}
     */
    public String gethAxis()
    {
        return this.hAxis;
    }


    /**
     * Setter method for instance variable {@link #hAxis}.
     *
     * @param _hAxis value for instance variable {@link #hAxis}
     */
    public S sethAxis(final String _hAxis)
    {
        this.hAxis = _hAxis;
        return getThis();
    }


    /**
     * Getter method for the instance variable {@link #vAxis}.
     *
     * @return value of instance variable {@link #vAxis}
     */
    public String getvAxis()
    {
        return this.vAxis;
    }


    /**
     * Setter method for instance variable {@link #vAxis}.
     *
     * @param _vAxis value for instance variable {@link #vAxis}
     */
    public S setvAxis(final String _vAxis)
    {
        this.vAxis = _vAxis;
        return getThis();
    }
}

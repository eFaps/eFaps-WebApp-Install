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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.efaps.admin.program.esjp.EFapsRevision;
import org.efaps.admin.program.esjp.EFapsUUID;


/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 * @param <T> Data
 */
@EFapsUUID("e1c7f4c7-9e0b-4681-bdcf-8cee80caea4b")
@EFapsRevision("$Rev$")
public abstract class Serie_Base<T extends AbstractData<T>, S extends Serie_Base<T,S>>
{
    private String name;

    private String plot;

    private final Map<String, Object> configMap = new LinkedHashMap<String, Object>();

    private final List<T> data = new ArrayList<T>();

    private MouseIndicator mouseIndicator;

    /**
     * "getThis" trick.
     * @return this
     */
    protected abstract S getThis();

    /**
     * @param _js
     * @param _string
     */
    public void addJS(final StringBuilder _js,
                      final String _chartVarName)
    {
        _js.append(_chartVarName).append(".addSeries(\"")
            .append(getName()).append("\", ").append(getDataJS());

        final CharSequence config = getConfigJS();
        if (config.length() > 0) {
            _js.append(",").append(config);
        }
        _js.append(");\n");

        if (getMouseIndicator() != null) {
            getMouseIndicator().addMouseIndicatorJS(_js, _chartVarName, getName());
        }
    }

    /**
     * @return
     */
    public CharSequence getDataJS()
    {
        final List<CharSequence> js = new ArrayList<CharSequence>();
        for (final T dat : getData()) {
            js.add(dat.getJavaScript());
        }
        return Util.collectionToObjectArray(js);
    }

    public CharSequence getConfigJS()
    {
        if (this.plot != null && !this.configMap.containsKey("plot")) {
            this.configMap.put("plot", "\"" + getPlot() + "\"");
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
    public S setName(final String _name)
    {
        this.name = _name;
        return getThis();
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
    public S addData(final T _data)
    {
        this.data.add(_data);
        return getThis();
    }

    public S addConfig(final String _key,
                       final Object _value)
    {
        this.configMap.put(_key, _value);
        return getThis();
    }

    /**
     * Getter method for the instance variable {@link #plot}.
     *
     * @return value of instance variable {@link #plot}
     */
    public String getPlot()
    {
        return this.plot;
    }

    /**
     * Setter method for instance variable {@link #plot}.
     *
     * @param _plot value for instance variable {@link #plot}
     */
    public S setPlot(final String _plot)
    {
        this.plot = _plot;
        return getThis();
    }

    /**
     * Getter method for the instance variable {@link #configMap}.
     *
     * @return value of instance variable {@link #configMap}
     */
    public Map<String, Object> getConfigMap()
    {
        return this.configMap;
    }

    /**
     * Getter method for the instance variable {@link #mouseIndicator}.
     *
     * @return value of instance variable {@link #mouseIndicator}
     */
    public MouseIndicator getMouseIndicator()
    {
        return this.mouseIndicator;
    }

    /**
     * Setter method for instance variable {@link #mouseIndicator}.
     *
     * @param _mouseIndicator value for instance variable {@link #mouseIndicator}
     */
    public void setMouseIndicator(final MouseIndicator _mouseIndicator)
    {
        this.mouseIndicator = _mouseIndicator;
    }
}

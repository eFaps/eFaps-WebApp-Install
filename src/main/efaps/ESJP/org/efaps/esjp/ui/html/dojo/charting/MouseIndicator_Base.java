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
public abstract class MouseIndicator_Base<T extends MouseIndicator_Base<T>>
{
    private String name = "default";

    private final Map<String, Object> configMap = new LinkedHashMap<String, Object>();

    /**
     * Whether the label is rendered at the start or end of the indicator.
     * Default is false meaning end of the line.
     */
    private boolean start = false;

    /**
     *  Whether the indicator lines are visible or not. Default is true.
     */
    private boolean lines = true;

    /**
     * Whether the mouse indicator is enabled on mouse over or on mouse drag. Default is true.
     */
    private boolean mouseOver = true;

    /**
     * "getThis" trick.
     * @return this
     */
    protected abstract T getThis();

    protected void addMouseIndicatorJS(final StringBuilder _js,
                                       final String _chartVarName,
                                       final String _seriesName)
    {
       this.configMap.put("series", "\"" + _seriesName + "\"");
        _js.append(" new MouseIndicator(").append(_chartVarName)
            .append(",\"").append(getName()).append("\",")
            .append(getConfigJS()).append(");\n");
    }

    public CharSequence getConfigJS()
    {
        if (isMouseOver()) {
            this.configMap.put("mouseOver", isMouseOver());
        }
        if (isStart()) {
            this.configMap.put("start", isStart());
        }
        if (!isLines()) {
            this.configMap.put("lines", isLines());
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
    public T setName(final String _name)
    {
        this.name = _name;
        return getThis();
    }

    public T addConfig(final String _key,
                       final Object _value)
    {
        this.configMap.put(_key, _value);
        return getThis();
    }

    /**
     * Getter method for the instance variable {@link #mouseOver}.
     *
     * @return value of instance variable {@link #mouseOver}
     */
    public boolean isMouseOver()
    {
        return this.mouseOver;
    }

    /**
     * Setter method for instance variable {@link #mouseOver}.
     *
     * @param _mouseOver value for instance variable {@link #mouseOver}
     */
    public T setMouseOver(final boolean _mouseOver)
    {
        this.mouseOver = _mouseOver;
        return getThis();
    }

    /**
     * Getter method for the instance variable {@link #lines}.
     *
     * @return value of instance variable {@link #lines}
     */
    public boolean isLines()
    {
        return this.lines;
    }

    /**
     * Setter method for instance variable {@link #lines}.
     *
     * @param _lines value for instance variable {@link #lines}
     */
    public T setLines(final boolean _lines)
    {
        this.lines = _lines;
        return getThis();
    }

    /**
     * Getter method for the instance variable {@link #start}.
     *
     * @return value of instance variable {@link #start}
     */
    public boolean isStart()
    {
        return this.start;
    }

    /**
     * Setter method for instance variable {@link #start}.
     *
     * @param _start value for instance variable {@link #start}
     */
    public T setStart(final boolean _start)
    {
        this.start = _start;
        return getThis();
    }
}

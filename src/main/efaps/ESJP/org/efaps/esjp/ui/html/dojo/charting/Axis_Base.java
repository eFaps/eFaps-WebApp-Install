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
public abstract class Axis_Base<T extends Axis_Base<T>>
{
    private String name;

    private boolean vertical = false;

    /**
     * e.g. to get month<br/>
     * [{value: 1, text: "Jan"}, {value: 2, text: "Feb"},
     *  {value: 3, text: "Mar"}, {value: 4, text: "Apr"},
     *  {value: 5, text: "May"}, {value: 6, text: "Jun"},
     *  {value: 7, text: "Jul"}, {value: 8, text: "Aug"},
     *  {value: 9, text: "Sep"}, {value: 10, text: "Oct"},
     *  {value: 11, text: "Nov"}, {value: 12, text: "Dec"}]
     *
     */
    private CharSequence labels;


    private boolean leftBottom = true;

    /**
     * "getThis" trick.
     * @return this
     */
    protected abstract T getThis();


    protected void addJS(final StringBuilder _js,
                         final String _chartVarName)
    {
        _js.append(_chartVarName).append(".addAxis(\"")
        .append(getName()).append("\"");

        final CharSequence configjs = getConfigJS();

        _js.append(configjs.length() > 0 ? ("," + configjs) : "")
            .append(");\n");
    }


    /**
     * @return
     */
    public CharSequence getConfigJS()
    {
        final Map<String, Object> paraMap = new LinkedHashMap<String, Object>();
        if (isVertical()) {
            paraMap.put("vertical", true);
        }
        if (getLabels() != null) {
            paraMap.put("labels", getLabels());
        }
        if (!isLeftBottom()) {
            paraMap.put("leftBottom", isLeftBottom());
        }
        return Util.mapToObjectList(paraMap);
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

    /**
     * Getter method for the instance variable {@link #vertical}.
     *
     * @return value of instance variable {@link #vertical}
     */
    public boolean isVertical()
    {
        return this.vertical;
    }

    /**
     * Setter method for instance variable {@link #vertical}.
     *
     * @param _vertical value for instance variable {@link #vertical}
     */
    public T setVertical(final boolean _vertical)
    {
        this.vertical = _vertical;
        return getThis();
    }


    /**
     * Getter method for the instance variable {@link #labels}.
     *
     * @return value of instance variable {@link #labels}
     */
    public CharSequence getLabels()
    {
        return this.labels;
    }

    /**
     * Setter method for instance variable {@link #labels}.
     *
     * @param _labels value for instance variable {@link #labels}
     */
    public T setLabels(final CharSequence _labels)
    {
        this.labels = _labels;
        return getThis();
    }


    /**
     * Getter method for the instance variable {@link #leftBottom}.
     *
     * @return value of instance variable {@link #leftBottom}
     */
    public boolean isLeftBottom()
    {
        return this.leftBottom;
    }


    /**
     * Setter method for instance variable {@link #leftBottom}.
     *
     * @param _leftBottom value for instance variable {@link #leftBottom}
     */
    public T setLeftBottom(final boolean _leftBottom)
    {
        this.leftBottom = _leftBottom;
        return getThis();
    }
}

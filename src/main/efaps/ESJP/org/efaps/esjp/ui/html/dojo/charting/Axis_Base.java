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

    private final Map<String, Object> configMap = new LinkedHashMap<String, Object>();

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

    private CharSequence title;

    private boolean leftBottom = true;

    private Integer min;

    private Integer max;


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
        final Map<String, Object> confMap = getConfigMap();
        if (isVertical()) {
            confMap.put("vertical", true);
        }
        if (getLabels() != null) {
            confMap.put("labels", getLabels());
        }
        if (!isLeftBottom()) {
            confMap.put("leftBottom", isLeftBottom());
        }
        if (getTitle() != null) {
            confMap.put("title", "\"" + getTitle() + "\"");
        }
        if(getMin() != null) {
            confMap.put("min", getMin());
        }
        if(getMax() != null) {
            confMap.put("max", getMax());
        }
        return Util.mapToObjectList(confMap);
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

    public T addConfig(final String _key,
                       final Object _value)
    {
        this.configMap.put(_key, _value);
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
     * Getter method for the instance variable {@link #title}.
     *
     * @return value of instance variable {@link #title}
     */
    public CharSequence getTitle()
    {
        return this.title;
    }

    /**
     * Setter method for instance variable {@link #title}.
     *
     * @param _title value for instance variable {@link #title}
     */
    public T setTitle(final CharSequence _title)
    {
        this.title = _title;
        return getThis();
    }

    /**
     * Getter method for the instance variable {@link #min}.
     *
     * @return value of instance variable {@link #min}
     */
    public Integer getMin()
    {
        return this.min;
    }



    /**
     * Setter method for instance variable {@link #min}.
     *
     * @param _min value for instance variable {@link #min}
     */
    public T setMin(final Integer _min)
    {
        this.min = _min;
        return getThis();
    }



    /**
     * Getter method for the instance variable {@link #max}.
     *
     * @return value of instance variable {@link #max}
     */
    public Integer getMax()
    {
        return this.max;
    }



    /**
     * Setter method for instance variable {@link #max}.
     *
     * @param _max value for instance variable {@link #max}
     */
    public T setMax(final Integer _max)
    {
        this.max = _max;
        return getThis();
    }

}

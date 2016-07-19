/*
 * Copyright 2003 - 2016 The eFaps Team
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


package org.efaps.esjp.ui.html.dojo.charting;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;


/**
 * TODO comment!
 *
 * @author The eFaps Team
 */
@EFapsUUID("89b73dfc-c56a-4b93-9970-c8364d5bd052")
@EFapsApplication("eFaps-WebApp")
public abstract class AbstractData_Base<S extends AbstractData_Base<S>>
{
    private Number yValue;
    private Number xValue=1;


    private String tooltip;
    private final Map<String, Object> configMap = new LinkedHashMap<>();

    /**
     * "getThis" trick.
     * @return this
     */
    protected abstract S getThis();

    public abstract CharSequence getJavaScript();


    public CharSequence getConfigJS()
    {
        if (getTooltip() != null) {
            this.configMap.put("tooltip", "\"" + StringEscapeUtils.escapeEcmaScript(getTooltip()) + "\"");
        }
        return Util.mapToObjectList(this.configMap);
    }

    /**
     * Getter method for the instance variable {@link #yValue}.
     *
     * @return value of instance variable {@link #yValue}
     */
    public Number getXValue()
    {
        return this.xValue;
    }

    /**
     * Setter method for instance variable {@link #yValue}.
     *
     * @param _value value for instance variable {@link #yValue}
     */
    public S setXValue(final Number _value)
    {
        this.xValue = _value;
        return getThis();
    }


    /**
     * Getter method for the instance variable {@link #yValue}.
     *
     * @return value of instance variable {@link #yValue}
     */
    public Number getYValue()
    {
        return this.yValue;
    }

    /**
     * Setter method for instance variable {@link #yValue}.
     *
     * @param _value value for instance variable {@link #yValue}
     */
    public S setYValue(final Number _value)
    {
        this.yValue = _value;
        return getThis();
    }

    /**
     * Getter method for the instance variable {@link #tooltip}.
     *
     * @return value of instance variable {@link #tooltip}
     */
    public String getTooltip()
    {
        return this.tooltip;
    }

    /**
     * Setter method for instance variable {@link #tooltip}.
     *
     * @param _tooltip value for instance variable {@link #tooltip}
     */
    public S setTooltip(final String _tooltip)
    {
        this.tooltip = _tooltip;
        return getThis();
    }

    public S addConfig(final String _key,
                       final Object _value)
    {
        this.configMap.put(_key, _value);
        return getThis();
    }
}

/*
 * Copyright 2003 - 2017 The eFaps Team
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

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.util.RandomUtil;


/**
 * TODO comment!
 *
 * @author The eFaps Team
 */
@EFapsUUID("f155aac0-fa27-4cf8-bfd2-d02e4dd1c128")
@EFapsApplication("eFaps-WebApp")
public abstract class Legend_Base<S extends Legend_Base<S>>
{

    /** The vertical. */
    private boolean vertical = false;

    /** The config map. */
    private final Map<String, Object> configMap = new LinkedHashMap<>();

    /** The node id. */
    private String nodeId;

    /** The horizontal count. */
    private  int horizontalCount = 5;

    /** The chart var name. */
    private String chartVarName = "chart";

    /** The selectable. */
    private boolean selectable = false;

    /**
     * "getThis" trick.
     * @return this
     */
    protected abstract S getThis();


    /**
     * Adds the legend js.
     *
     * @param _js the _js
     */
    protected void addLegendJS(final StringBuilder _js)
    {
        _js.append(" if(typeof registry.byId('").append(getNodeId()).append("') != \"undefined\"){\n")
            .append("    registry.byId('").append(getNodeId()).append("').destroyRecursive();\n")
            .append(" }")
            .append("new Legend(").append(getConfigJS()).append(",\n\"").append(getNodeId()).append("\");\n");
    }

    /**
     * Gets the config js.
     *
     * @return the config js
     */
    public CharSequence getConfigJS()
    {
        this.configMap.put("chart", getChartVarName());
        if (isVertical()) {
            this.configMap.put("horizontal", false);
        } else if (getHorizontalCount() > 0) {
            this.configMap.put("horizontal", getHorizontalCount());
        }
        return Util.mapToObjectList(this.configMap);
    }

    /**
     * Getter method for the instance variable {@link #legendNodeId}.
     *
     * @return value of instance variable {@link #legendNodeId}
     */
    public String getNodeId()
    {
        if (this.nodeId == null) {
            this.nodeId = RandomUtil.randomAlphabetic(8);
        }
        return this.nodeId;
    }

    /**
     * Setter method for instance variable {@link #legendNodeId}.
     *
     * @param _legendNodeId value for instance variable {@link #legendNodeId}
     */
    public S setNodeId(final String _legendNodeId)
    {
        this.nodeId = _legendNodeId;
        return getThis();
    }

    /**
     * Getter method for the instance variable {@link #horizontal}.
     *
     * @return value of instance variable {@link #horizontal}
     */
    public boolean isVertical()
    {
        return this.vertical;
    }

    /**
     * Setter method for instance variable {@link #horizontal}.
     *
     * @param _horizontal value for instance variable {@link #horizontal}
     */
    public S setVertical(final boolean _vertical)
    {
        this.vertical = _vertical;
        return getThis();
    }

    /**
     * Getter method for the instance variable {@link #horizontalCount}.
     *
     * @return value of instance variable {@link #horizontalCount}
     */
    public int getHorizontalCount()
    {
        return this.horizontalCount;
    }

    /**
     * Setter method for instance variable {@link #horizontalCount}.
     *
     * @param _horizontalCount value for instance variable {@link #horizontalCount}
     */
    public S setHorizontalCount(final int _horizontalCount)
    {
        this.horizontalCount = _horizontalCount;
        return getThis();
    }


    /**
     * Getter method for the instance variable {@link #chartVarName}.
     *
     * @return value of instance variable {@link #chartVarName}
     */
    public String getChartVarName()
    {
        return this.chartVarName;
    }


    /**
     * Setter method for instance variable {@link #chartVarName}.
     *
     * @param _chartVarName value for instance variable {@link #chartVarName}
     */
    public S setChartVarName(final String _chartVarName)
    {
        this.chartVarName = _chartVarName;
        return getThis();
    }

    /**
     * Getter method for the instance variable {@link #selectable}.
     *
     * @return value of instance variable {@link #selectable}
     */
    public boolean isSelectable()
    {
        return this.selectable;
    }

    /**
     * Setter method for instance variable {@link #selectable}.
     *
     * @param _selectable value for instance variable {@link #selectable}
     */
    public S setSelectable(final boolean _selectable)
    {
        this.selectable = _selectable;
        return getThis();
    }

    /**
     * Adds the config.
     *
     * @param _key the _key
     * @param _value the _value
     * @return the s
     */
    public S addConfig(final String _key,
                       final Object _value)
    {
        this.configMap.put(_key, _value);
        return getThis();
    }
}

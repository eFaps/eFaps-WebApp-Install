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

import org.apache.commons.lang3.RandomStringUtils;
import org.efaps.admin.program.esjp.EFapsRevision;
import org.efaps.admin.program.esjp.EFapsUUID;


/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
@EFapsUUID("f155aac0-fa27-4cf8-bfd2-d02e4dd1c128")
@EFapsRevision("$Rev$")
public abstract class Legend_Base<S extends Legend_Base<S>>
{
    private boolean vertical = false;

    private String nodeId;

    private  int horizontalCount = 5;

    private String chartVarName = "chart";

    private boolean selectable = false;

    /**
     * "getThis" trick.
     * @return this
     */
    protected abstract S getThis();


    protected void addLegendJS(final StringBuilder _js)
    {
        _js.append("new Legend(").append(getConfigJS()).append(",\n\"").append(getNodeId()).append("\");\n");
    }

    public CharSequence getConfigJS()
    {
        final Map<String, Object> paraMap = new LinkedHashMap<String, Object>();
        paraMap.put("chart", getChartVarName());
        if (isVertical()) {
            paraMap.put("horizontal", false);
        } else if (getHorizontalCount() > 0) {
            paraMap.put("horizontal", getHorizontalCount());
        }
        return Util.mapToObjectList(paraMap);
    }

    /**
     * Getter method for the instance variable {@link #legendNodeId}.
     *
     * @return value of instance variable {@link #legendNodeId}
     */
    public String getNodeId()
    {
        if (this.nodeId == null) {
            this.nodeId = RandomStringUtils.randomAlphabetic(8);
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
    public void setSelectable(final boolean _selectable)
    {
        this.selectable = _selectable;
    }
}

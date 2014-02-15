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


/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
public abstract class AbstractCartesianChart_Base<T extends AbstractData<T>, S extends AbstractChart_Base<T,S>>
    extends AbstractChart<T, S>
{
    private final List<Axis> axis = new ArrayList<Axis>();



    @Override
    protected void initialize()
    {
        super.initialize();
        addModule("dojox/charting/axis2d/Default", "Default");
        for (final Serie<?> serie :  getSeries()) {
            if (serie.getMouseIndicator() != null) {
                addModule("dojox/charting/action2d/MouseIndicator", "MouseIndicator");
                break;
            }
        }
    }

    @Override
    protected void addBeforeRenderJS(final StringBuilder _js,
                                     final String _chartVarName)
    {
        super.addBeforeRenderJS(_js, _chartVarName);
        for (final Axis axisTmp : getAxis()) {
            axisTmp.addJS(_js, _chartVarName);
        }
    }

    /**
     * Getter method for the instance variable {@link #axis}.
     *
     * @return value of instance variable {@link #axis}
     */
    public List<Axis> getAxis()
    {
        return this.axis;
    }

    /**
     * @param _serie
     */
    public void addAxis(final Axis _axis)
    {
        this.axis.add(_axis);
    }
}

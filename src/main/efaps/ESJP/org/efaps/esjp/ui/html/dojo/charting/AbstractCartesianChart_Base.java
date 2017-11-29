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
 */

package org.efaps.esjp.ui.html.dojo.charting;

import java.util.ArrayList;
import java.util.List;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.ui.wicket.util.DojoClasses;


/**
 * TODO comment!.
 *
 * @author The eFaps Team
 * @param <T> the generic type
 * @param <S> the generic type
 */
@EFapsUUID("1b6525b1-44ac-45d5-8cea-38c2e8295f56")
@EFapsApplication("eFaps-WebApp")
public abstract class AbstractCartesianChart_Base<T extends AbstractData<T>, S extends AbstractChart_Base<T,S>>
    extends AbstractChart<T, S>
{

    /** The axis. */
    private final List<Axis> axis = new ArrayList<>();

    @Override
    protected void initialize()
    {
        super.initialize();
        addDojoClass(DojoClasses.ChartDefault);
        for (final Serie<?> serie :  getSeries()) {
            if (serie.getMouseIndicator() != null) {
                addDojoClass(DojoClasses.MouseIndicator);
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
     * Adds the axis.
     *
     * @param _axis the axis
     */
    public void addAxis(final Axis _axis)
    {
        this.axis.add(_axis);
    }
}

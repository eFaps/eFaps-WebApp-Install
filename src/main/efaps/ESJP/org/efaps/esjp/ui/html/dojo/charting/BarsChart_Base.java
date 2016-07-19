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

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

/**
 * TODO comment!
 *
 * @author The eFaps Team
 */
@EFapsUUID("9c359207-0edc-44e2-a81b-c565e700b384")
@EFapsApplication("eFaps-WebApp")
public abstract class BarsChart_Base<S extends AbstractBarColChart<Data, S>>
    extends AbstractBarColChart<Data, S>
{
    private PlotLayout plotLayout = PlotLayout.STANDART;

    @Override
    protected void initialize()
    {
        super.initialize();
        switch (getPlotLayout()) {
            case STACKED:
                addModule("dojox/charting/plot2d/StackedBars", "Bars");
                break;
            case CLUSTERED:
                addModule("dojox/charting/plot2d/ClusteredBars", "Bars");
                break;
            default:
                addModule("dojox/charting/plot2d/Bars", "Bars");
                break;
        }
        boolean addXAxis = true;
        boolean addYAxis = true;
        for (final Axis axis : getAxis()) {
            if (axis.getName().equals("x") && addXAxis) {
                addXAxis =false;
            }
            if (axis.getName().equals("y") && addYAxis) {
                addYAxis =false;
            }
        }
        if (addXAxis) {
            addAxis(new Axis().setName("x"));
        }
        if (addYAxis) {
            addAxis(new Axis().setName("y").setVertical(true).setMin(0));
        }
    }

    @Override
    protected void configurePlot(final Plot _plot)
    {
        super.configurePlot(_plot);
        _plot.addConfig("type", "Bars");
    }
    /**
     * Getter method for the instance variable {@link #plotLayout}.
     *
     * @return value of instance variable {@link #plotLayout}
     */
    public PlotLayout getPlotLayout()
    {
        return this.plotLayout;
    }

    /**
     * Setter method for instance variable {@link #plotLayout}.
     *
     * @param _plotLayout value for instance variable {@link #plotLayout}
     */
    public S setPlotLayout(final PlotLayout _plotLayout)
    {
        this.plotLayout = _plotLayout;
        return getThis();
    }
}

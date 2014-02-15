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

import org.efaps.admin.program.esjp.EFapsRevision;
import org.efaps.admin.program.esjp.EFapsUUID;


/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
@EFapsUUID("3613d031-fb22-4aad-80c6-b71755065f33")
@EFapsRevision("$Rev$")
public abstract class ColumnsChart_Base<S extends AbstractBarColChart<Data, S>>
    extends AbstractBarColChart<Data, S>
{
    private PlotLayout plotLayout = PlotLayout.STANDART;

    @Override
    protected void initialize()
    {
        super.initialize();
        switch (getPlotLayout()) {
            case STACKED:
                addModule("dojox/charting/plot2d/StackedColumns", "Columns");
                break;
            case CLUSTERED:
                addModule("dojox/charting/plot2d/ClusteredColumns", "Columns");
                break;
            default:
                addModule("dojox/charting/plot2d/Columns", "Columns");
                break;
        }
        addAxis(new Axis().setName("x"));
        addAxis(new Axis().setName("y").setVertical(true));
        addPlotConfig("type", "Columns");
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

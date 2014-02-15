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
@EFapsUUID("65db322f-4229-4382-aae6-3dc47402caa2")
@EFapsRevision("$Rev$")
public abstract class PieChart_Base<S extends AbstractChart_Base<PieData, S>>
    extends AbstractChart<PieData, S>
{
    @Override
    protected void initialize()
    {
        super.initialize();
        addModule("dojox/charting/plot2d/Pie", "PiePlot");
        addModule("dojox/charting/action2d/MoveSlice", "MoveSlice");
        addPlotConfig("type", "PiePlot");
        addPlotConfig("radius", 100);
        addPlotConfig("fontColor", "\"black\"");
        addPlotConfig("labelOffset", 0);
        addPlotConfig("omitLabels", true);
        addPlotConfig("labelStyle", "\"columns\"");
        // default/columns/rows/auto
    }

    @Override
    protected void addBeforeRenderJS(final StringBuilder _js)
    {
        super.addBeforeRenderJS(_js);
        _js.append("new MoveSlice(chart, \"default\");\n");
    }
}

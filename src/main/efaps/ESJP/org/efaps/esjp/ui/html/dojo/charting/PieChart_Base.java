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
import org.efaps.ui.wicket.util.DojoClasses;


/**
 * TODO comment!
 *
 * @author The eFaps Team
 */
@EFapsUUID("65db322f-4229-4382-aae6-3dc47402caa2")
@EFapsApplication("eFaps-WebApp")
public abstract class PieChart_Base<S extends AbstractChart_Base<PieData, S>>
    extends AbstractChart<PieData, S>
{
    @Override
    protected void initialize()
    {
        super.initialize();
        addDojoClass(DojoClasses.PiePlot);
        addDojoClass(DojoClasses.MoveSlice);

        final Plot plot = new Plot().addConfig("type", "PiePlot");
        addPlot(plot);
        plot.addConfig("radius", 100);
        plot.addConfig("fontColor", "\"black\"");
        plot.addConfig("labelOffset", 0);
        plot.addConfig("omitLabels", true);
        plot.addConfig("labelStyle", "\"columns\"");
        // default/columns/rows/auto
    }

    @Override
    protected void addBeforeRenderJS(final StringBuilder _js,
                                     final String _chartVarName)
    {
        super.addBeforeRenderJS(_js, _chartVarName);
        _js.append("new MoveSlice(chart, \"default\");\n");
    }
}

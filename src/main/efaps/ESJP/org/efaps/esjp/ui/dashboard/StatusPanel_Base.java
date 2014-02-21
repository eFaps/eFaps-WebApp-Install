/*
 * Copyright 2003 - 2013 The eFaps Team
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

package org.efaps.esjp.ui.dashboard;

import java.util.Random;

import org.efaps.admin.program.esjp.EFapsRevision;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.esjp.ui.html.dojo.charting.Axis;
import org.efaps.esjp.ui.html.dojo.charting.Data;
import org.efaps.esjp.ui.html.dojo.charting.LineChart;
import org.efaps.esjp.ui.html.dojo.charting.LineLayout;
import org.efaps.esjp.ui.html.dojo.charting.MouseIndicator;
import org.efaps.esjp.ui.html.dojo.charting.Orientation;
import org.efaps.esjp.ui.html.dojo.charting.Plot;
import org.efaps.esjp.ui.html.dojo.charting.Serie;
import org.efaps.ui.wicket.models.IEsjpSnipplet;
import org.efaps.util.EFapsException;

/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
@EFapsUUID("a1f59464-0354-47c4-ac92-0f393d5bfa8a")
@EFapsRevision("$Rev$")
public class StatusPanel_Base
    implements IEsjpSnipplet
{

    private static final Random RANDOM = new Random();
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public CharSequence getHtmlSnipplet()
        throws EFapsException
    {
        final LineChart chart = new LineChart().setLineLayout(LineLayout.LINES);
        chart.setTitle("Este es un titulo mu bueno");
        chart.addPlot(new Plot().setName("other").addConfig("type", "Lines").setvAxis("oy"));
        chart.addAxis(new Axis().setName("oy").setVertical(true).setLeftBottom(false).setTitle("hallo hier USD"));

        final Serie<Data> serie = new Serie<Data>();
        serie.setName("Demo").setMouseIndicator(new MouseIndicator());;
        chart.addSerie(serie);

        for (int i = 0; i < 12; i++) {
            final Data data = new Data();
            serie.addData(data);
            data.setYValue(StatusPanel_Base.RANDOM.nextInt(25));
        }


        final Serie<Data> serie2 = new Serie<Data>();
        serie2.setName("Demo2").setPlot("other");
        chart.addSerie(serie2);

        for (int i = 0; i < 12; i++) {
            final Data data = new Data();
            serie2.addData(data);
            data.setYValue(StatusPanel_Base.RANDOM.nextInt(25));
        }


        chart.setOrientation(Orientation.HORIZONTAL_LEGEND_CHART);

        return chart.getHtmlSnipplet();
    }

    @Override
    public boolean isVisible()
        throws EFapsException
    {
        return true;
    }
}

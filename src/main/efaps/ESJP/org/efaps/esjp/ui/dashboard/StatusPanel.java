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


/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
public class StatusPanel
{

    public CharSequence getHtmlSnipplet()
    {
        final StringBuilder script = new StringBuilder()
            .append("<script type=\"text/javascript\">")
            .append(" chartData = [\n")
            .append(" { x: 1, y: 19, text: \"Red\" , legend:\"Red 2\", tooltip: \"Red is 50%\"},\n")
            .append(" { x: 1, y: 12, text: \"Green\", legend:\"Green 2\", tooltip: \"Red is 50%\"},\n")
            .append(" { x: 1, y: 12, text: \"Blue\" , legend:\"Blue 2\", tooltip: \"Red is 50%\"},\n")
            .append(" { x: 1, y: 21, text: \"Other\" , legend:\"Other 2\", tooltip: \"Red is 50%\"}\n")
            .append(" ];\n")
            .append("require([\"dojox/charting/Chart\",\"dojox/charting/themes/Claro\", ")
            .append("\"dojox/charting/plot2d/Pie\",\"dojox/charting/widget/Legend\", \"dojox/charting/action2d/MoveSlice\",\"dojox/charting/action2d/Tooltip\", \"dojo/domReady!\"], ")
            .append("function(Chart, theme, PiePlot, Legend, MoveSlice, Tooltip){ \n")
            .append(" var pieChart = new Chart(\"chartNode\");\n")
            .append(" pieChart.setTheme(theme);\n")
            .append(" pieChart.addPlot(\"default\", {\n")
            .append(" type: PiePlot,\n")
            .append(" radius: 100,\n")
            .append(" fontColor: \"black\",\n")
            .append(" labelOffset: -20,\n")
            .append(" labelStyle: \"default\" \n")  // default/columns/rows/auto
            .append(" });\n")
            .append(" pieChart.addSeries(\"Serie1\", chartData);\n")
            .append(" new MoveSlice(pieChart, \"default\");\n")
            .append("new Tooltip(pieChart, \"default\");\n")
            .append(" pieChart.render();\n")
            .append(" var legendTwo = new Legend({chart: pieChart}, \"legend\");\n")
            .append(" });\n")
            .append("</script> \n")
            .append(" <div id=\"chartNode\" style=\"width: 350px; height: 350px;\"></div>\n")
            .append(" <div id=\"legend\"></div>\n");
        return script;
    }
}

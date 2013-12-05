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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.efaps.admin.datamodel.Status;
import org.efaps.admin.datamodel.Type;
import org.efaps.db.MultiPrintQuery;
import org.efaps.db.QueryBuilder;
import org.efaps.util.EFapsException;


/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
public class StatusPanel
{

    public CharSequence getHtmlSnipplet() throws EFapsException
    {
        final Map<Status, Integer> values = new HashMap<Status,Integer>();
        final QueryBuilder queryBldr = new QueryBuilder(Type.get(UUID.fromString("bcb8ba16-d485-477b-b198-b95d08f3915a")));
        final MultiPrintQuery multi = queryBldr.getPrint();
        multi.addAttribute("Status");
        multi.execute();
        final int all = multi.getInstanceList().size();
        while (multi.next()) {
            final Status status = Status.get(multi.<Long>getAttribute("Status"));
            int count = 0;
            if (values.containsKey(status)) {
                count = values.get(status);
            }
            values.put(status, count + 1);
        }
        final StringBuilder data = new StringBuilder();
        boolean first = true;
        for (final Entry<Status, Integer> entry : values.entrySet()) {
            final Integer y = entry.getValue();
            final String text = entry.getKey().getLabel();
            final String legend = entry.getKey().getLabel() + ": " + y;
            final BigDecimal percent = new BigDecimal(y).setScale(8).divide(new BigDecimal(all), BigDecimal.ROUND_HALF_UP)
                            .multiply(new BigDecimal(100)).setScale(2,  BigDecimal.ROUND_HALF_UP);
            final String tooltip = entry.getKey().getLabel() + ": " + y + " / " + percent + "%";
            if (first) {
                first = false;
            } else {
                data.append(",\n");
            }
            data.append("{ x: 1, y: ").append(y).append(", text: \"").append(text)
                .append("\" , legend:\"").append(legend) .append("\" , tooltip:\"").append(tooltip).append("\"}");
        }

        final StringBuilder script = new StringBuilder()
            .append("<style type=\"text/css\"> ")
            .append(".dojoxLegendNode Label {")
            .append("font-size: 9pt;")
            .append("}")
            .append("</style>")
            .append("<script type=\"text/javascript\">")
            .append(" chartData = [\n")
            .append(data)
            .append(" ];\n")
            .append("require([\"dojox/charting/Chart\",\"dojox/charting/themes/Julie\", ")
            .append("\"dojox/charting/plot2d/Pie\",\"dojox/charting/widget/Legend\", \"dojox/charting/action2d/MoveSlice\",\"dojox/charting/action2d/Tooltip\", \"dojo/domReady!\"], ")
            .append("function(Chart, theme, PiePlot, Legend, MoveSlice, Tooltip){ \n")
            .append(" var pieChart = new Chart(\"chartNode\");\n")
            .append(" pieChart.setTheme(theme);\n")
            .append(" pieChart.addPlot(\"default\", {\n")
            .append(" type: PiePlot,\n")
            .append(" radius: 100,\n")
            .append(" fontColor: \"black\",\n")
            .append(" labelOffset: 0,\n")
            .append(" omitLabels: true,\n")
            .append(" labelStyle: \"columns\" \n")  // default/columns/rows/auto
            .append(" });\n")
            .append(" pieChart.addSeries(\"Serie1\", chartData);\n")
            .append(" new MoveSlice(pieChart, \"default\");\n")
            .append("new Tooltip(pieChart, \"default\");\n")
            .append(" pieChart.render();\n")
            .append(" var legendTwo = new Legend({chart: pieChart}, \"legend\");\n")
            .append(" });\n")
            .append("</script> \n")
            .append(" <div id=\"chartNode\" style=\"width: 450px; height: 350px;\"></div>\n")
            .append(" <div id=\"legend\"></div>\n");
        return script;
    }
}

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

import org.apache.commons.lang3.RandomStringUtils;
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
public abstract class Pie_Base
{


    private Orientation orientation = Orientation.VERTICAL_CHART_LEGEND;

    private String charNodeId;

    private Theme theme = Theme.JULIE;

    private Legend legend = new Legend();

    private int height = 300;

    private int width = 450;

    private final List<Serie<PieData>> series = new ArrayList<Serie<PieData>>();

    /**
     * @return
     */
    public CharSequence getHtmlSnipplet()
    {
        final StringBuilder ret = new StringBuilder()
            .append(getCSS())
            .append("\n<script type=\"text/javascript\">\n")
            .append("require([\"dojox/charting/Chart\",\"").append(getTheme().getAmd()).append("\", ")
            .append("\"dojox/charting/plot2d/Pie\",\"dojox/charting/widget/Legend\", \"dojox/charting/action2d/MoveSlice\",\"dojox/charting/action2d/Tooltip\", \"dojo/domReady!\"], ")
            .append("function(Chart, theme, PiePlot, Legend, MoveSlice, Tooltip){ \n")
            .append(" var pieChart = new Chart(\"").append(getCharNodeId()).append("\");\n")
            .append(" pieChart.setTheme(theme);\n")
            .append(" pieChart.addPlot(\"default\", {\n")
            .append(" type: PiePlot,\n")
            .append(" radius: 100,\n")
            .append(" fontColor: \"black\",\n")
            .append(" labelOffset: 0,\n")
            .append(" omitLabels: true,\n")
            .append(" labelStyle: \"columns\" \n") // default/columns/rows/auto
            .append(" });\n");

        for (final Serie<PieData> serie : getSeries()) {
            ret.append(" pieChart.addSeries(\"").append(serie.getName()).append("\", ").append(serie.getJavaScript())
                            .append(");\n");
        }

        ret.append(" new MoveSlice(pieChart, \"default\");\n")
            .append(" new Tooltip(pieChart, \"default\");\n")
            .append(" pieChart.render();\n")
            .append(getLegend().getScriptPart("pieChart"))
            .append("</script> \n")
            .append(getHtmlNodes());
        return ret;
    }

    public CharSequence getCSS()
    {
        final StringBuilder ret = new StringBuilder()
            .append("\n<style type=\"text/css\"> ")
            .append(".dojoxLegendNode Label {")
            .append("font-size: 8pt;")
            .append("}")
            .append("</style>\n");
        return ret;
    }

    public CharSequence getHtmlNodes()
    {
        final StringBuilder ret = new StringBuilder();
        switch (getOrientation()) {
            case HORIZONTAL_CHART_LEGEND:
                ret.append(" <div style=\"display: table-row\">")
                    .append(" <div style=\"display: table-cell\">")
                    .append(" <div id=\"").append(getCharNodeId()).append("\" style=\"width: ")
                        .append(getWidth()).append("px; height: ").append(getHeight()).append("px;\"></div>\n")
                    .append(" </div>")
                    .append(" <div style=\"display: table-cell\">")
                    .append(" <div id=\"").append(getLegend().getNodeId()).append("\"></div>\n")
                    .append(" </div>")
                    .append(" </div>");
                break;
            case HORIZONTAL_LEGEND_CHART:
                ret.append(" <div style=\"display: table-row\">")
                    .append(" <div style=\"display: table-cell\">")
                    .append(" <div id=\"").append(getLegend().getNodeId()).append("\"></div>\n")
                    .append(" </div>")
                    .append(" <div style=\"display: table-cell\">")
                    .append(" <div id=\"").append(getCharNodeId()).append("\" style=\"width: ")
                        .append(getWidth()).append("px; height: ").append(getHeight()).append("px;\"></div>\n")
                    .append(" </div>")
                    .append(" </div>");
                break;
            case VERTICAL_CHART_LEGEND:
                ret.append(" <div id=\"").append(getCharNodeId()).append("\" style=\"width: ")
                        .append(getWidth()).append("px; height: ").append(getHeight()).append("px;\"></div>\n")
                    .append(" <div id=\"").append(getLegend().getNodeId()).append("\"></div>\n");

                break;
            case VERTICAL_LEGEND_CHART:
                ret .append(" <div id=\"").append(getLegend().getNodeId()).append("\"></div>\n")
                    .append(" <div id=\"").append(getCharNodeId()).append("\" style=\"width: ")
                        .append(getWidth()).append("px; height: ").append(getHeight()).append("px;\"></div>\n");
                break;
            default:
                break;
        }
        return ret;
    }

    /**
     * Getter method for the instance variable {@link #charNodeId}.
     *
     * @return value of instance variable {@link #charNodeId}
     */
    public String getCharNodeId()
    {
        if (this.charNodeId == null) {
            this.charNodeId = RandomStringUtils.randomAlphabetic(8);
        }
        return this.charNodeId;
    }

    /**
     * Setter method for instance variable {@link #charNodeId}.
     *
     * @param _charNodeId value for instance variable {@link #charNodeId}
     */
    public void setCharNodeId(final String _charNodeId)
    {
        this.charNodeId = _charNodeId;
    }

    /**
     * Getter method for the instance variable {@link #theme}.
     *
     * @return value of instance variable {@link #theme}
     */
    public Theme getTheme()
    {
        return this.theme;
    }

    /**
     * Setter method for instance variable {@link #theme}.
     *
     * @param _theme value for instance variable {@link #theme}
     */
    public void setTheme(final Theme _theme)
    {
        this.theme = _theme;
    }

    /**
     * Getter method for the instance variable {@link #legend}.
     *
     * @return value of instance variable {@link #legend}
     */
    public Legend getLegend()
    {
        return this.legend;
    }

    /**
     * Setter method for instance variable {@link #legend}.
     *
     * @param _legend value for instance variable {@link #legend}
     */
    public void setLegend(final Legend _legend)
    {
        this.legend = _legend;
    }


    /**
     * Getter method for the instance variable {@link #height}.
     *
     * @return value of instance variable {@link #height}
     */
    public int getHeight()
    {
        return this.height;
    }


    /**
     * Setter method for instance variable {@link #height}.
     *
     * @param _height value for instance variable {@link #height}
     */
    public void setHeight(final int _height)
    {
        this.height = _height;
    }

    /**
     * Getter method for the instance variable {@link #width}.
     *
     * @return value of instance variable {@link #width}
     */
    public int getWidth()
    {
        return this.width;
    }

    /**
     * Setter method for instance variable {@link #width}.
     *
     * @param _width value for instance variable {@link #width}
     */
    public void setWidth(final int _width)
    {
        this.width = _width;
    }

    /**
     * Getter method for the instance variable {@link #orientation}.
     *
     * @return value of instance variable {@link #orientation}
     */
    public Orientation getOrientation()
    {
        return this.orientation;
    }

    /**
     * Setter method for instance variable {@link #orientation}.
     *
     * @param _orientation value for instance variable {@link #orientation}
     */
    public void setOrientation(final Orientation _orientation)
    {
        if (_orientation.equals(Orientation.HORIZONTAL_CHART_LEGEND)
                        || _orientation.equals(Orientation.HORIZONTAL_LEGEND_CHART)) {
            getLegend().setVertical(true);
        }
        this.orientation = _orientation;
    }


    /**
     * Getter method for the instance variable {@link #series}.
     *
     * @return value of instance variable {@link #series}
     */
    public List<Serie<PieData>> getSeries()
    {
        return this.series;
    }

    /**
     * @param _serie
     */
    public void addSerie(final Serie<PieData> _serie)
    {
        this.series.add(_serie);
    }

}

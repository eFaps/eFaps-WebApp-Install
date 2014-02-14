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
import org.apache.commons.lang3.StringEscapeUtils;
import org.efaps.admin.program.esjp.EFapsRevision;
import org.efaps.admin.program.esjp.EFapsUUID;


/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
@EFapsUUID("dfe47174-e442-4244-807b-5e11f56f0630")
@EFapsRevision("$Rev$")
public abstract class AbstractChart_Base<T extends AbstractData>
{
    private Orientation orientation = Orientation.VERTICAL_CHART_LEGEND;
    private Theme theme = Theme.JULIE;

    private Legend legend = new Legend();

    private String chartNodeId;

    private int height = 300;

    private int width = 450;

    private final List<Serie<T>> series = new ArrayList<Serie<T>>();

    private String title;

    private TitlePos titlePos = TitlePos.top;

    /**
     * @return
     */
    public CharSequence getHtmlSnipplet()
    {
        final StringBuilder ret = new StringBuilder()
            .append(getCSS())
            .append("\n<script type=\"text/javascript\">\n")
            .append("require([\"dojox/charting/Chart\", \"")
            .append(getTheme().getAmd()).append("\", ")
            .append("\"dojox/charting/widget/Legend\", ")
            .append("\"dojox/charting/action2d/Tooltip\"")
            .append(getRequireJS())
            .append(", \"dojo/domReady!\"], ")
            .append("function(Chart, theme, Legend, Tooltip ").append(getParameterJS()).append(" ){ \n")
            .append(" var chart = new Chart(\"").append(getChartNodeId()).append("\", {\n")
            .append(getTitleJS())
            .append(" });\n")
            .append(" chart.setTheme(theme);\n")
            .append(getAddPlotJS())
            .append(getSeriesJS())
            .append(getAdditionalJS())
            .append(" new Tooltip(chart, \"default\");\n")
            .append(" chart.render();\n")
            .append(getLegend().getScriptPart("chart"))
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

    public CharSequence getTitleJS()
    {
        final StringBuilder ret = new StringBuilder();
        final String titleTmp = getTitle();
        if (titleTmp != null) {
            ret.append("title: \"").append(StringEscapeUtils.escapeEcmaScript(titleTmp)).append("\",")
                .append("titlePos: \"").append(getTitlePos()).append("\"");
        }
        //titleGap: 25,
        //titleFont: "normal normal normal 15pt Arial",
        //titleFontColor: "orange"
        return ret;
    }


    public CharSequence getAdditionalJS(){
        return "";
    }

    public abstract CharSequence getParameterJS();

    public abstract CharSequence getRequireJS();

    public abstract CharSequence getAddPlotJS();

    public CharSequence getSeriesJS()
    {
        final StringBuilder ret = new StringBuilder();
        for (final Serie<T> serie : getSeries()) {
            ret.append(" chart.addSeries(\"")
                .append(serie.getName()).append("\", ").append(serie.getJavaScript()).append(");\n");
        }
        return ret;
    }

    public CharSequence getHtmlNodes()
    {
        final StringBuilder ret = new StringBuilder();
        switch (getOrientation()) {
            case HORIZONTAL_CHART_LEGEND:
                ret.append(" <div style=\"display: table-row\">")
                    .append(" <div style=\"display: table-cell\">")
                    .append(" <div id=\"").append(getChartNodeId()).append("\" style=\"width: ")
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
                    .append(" <div id=\"").append(getChartNodeId()).append("\" style=\"width: ")
                        .append(getWidth()).append("px; height: ").append(getHeight()).append("px;\"></div>\n")
                    .append(" </div>")
                    .append(" </div>");
                break;
            case VERTICAL_CHART_LEGEND:
                ret.append(" <div id=\"").append(getChartNodeId()).append("\" style=\"width: ")
                        .append(getWidth()).append("px; height: ").append(getHeight()).append("px;\"></div>\n")
                    .append(" <div id=\"").append(getLegend().getNodeId()).append("\"></div>\n");

                break;
            case VERTICAL_LEGEND_CHART:
                ret .append(" <div id=\"").append(getLegend().getNodeId()).append("\"></div>\n")
                    .append(" <div id=\"").append(getChartNodeId()).append("\" style=\"width: ")
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
    public String getChartNodeId()
    {
        if (this.chartNodeId == null) {
            this.chartNodeId = RandomStringUtils.randomAlphabetic(8);
        }
        return this.chartNodeId;
    }

    /**
     * Setter method for instance variable {@link #charNodeId}.
     *
     * @param _charNodeId value for instance variable {@link #charNodeId}
     */
    public void setChartNodeId(final String _chartNodeId)
    {
        this.chartNodeId = _chartNodeId;
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
     * Getter method for the instance variable {@link #series}.
     *
     * @return value of instance variable {@link #series}
     */
    public List<Serie<T>> getSeries()
    {
        return this.series;
    }

    /**
     * @param _serie
     */
    public void addSerie(final Serie<T> _serie)
    {
        this.series.add(_serie);
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
     * Getter method for the instance variable {@link #title}.
     *
     * @return value of instance variable {@link #title}
     */
    public String getTitle()
    {
        return this.title;
    }


    /**
     * Setter method for instance variable {@link #title}.
     *
     * @param _title value for instance variable {@link #title}
     */
    public void setTitle(final String _title)
    {
        this.title = _title;
    }


    /**
     * Getter method for the instance variable {@link #titlePos}.
     *
     * @return value of instance variable {@link #titlePos}
     */
    public TitlePos getTitlePos()
    {
        return this.titlePos;
    }


    /**
     * Setter method for instance variable {@link #titlePos}.
     *
     * @param _titlePos value for instance variable {@link #titlePos}
     */
    public void setTitlePos(final TitlePos _titlePos)
    {
        this.titlePos = _titlePos;
    }
}

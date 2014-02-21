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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
public abstract class AbstractChart_Base<T extends AbstractData<T>, S extends AbstractChart_Base<T,S>>
{
    private final Map<CharSequence, String> modules = new LinkedHashMap<CharSequence, String>();

    private final Map<String, Plot_Base<?>> plots = new LinkedHashMap<String, Plot_Base<?>>();

    private Orientation orientation = Orientation.VERTICAL_CHART_LEGEND;

    private Theme theme = Theme.JULIE;

    private Legend legend = new Legend();

    private String chartNodeId;

    private int height = 300;

    private int width = 450;

    private final List<Serie<T>> series = new ArrayList<Serie<T>>();

    private String title;

    private TitlePos titlePos = TitlePos.top;

    private boolean initialized = false;


    /**
     * "getThis" trick.
     * @return this
     */
    protected abstract S getThis();


    protected void initialize()
    {
        this.initialized = true;
        addModule("dojox/charting/Chart", "Chart");
        addModule(getTheme().getAmd(), "theme");

        if (getLegend() != null) {
            if (getLegend().isSelectable()) {
                addModule("dojox/charting/widget/SelectableLegend", "Legend");
            } else {
                addModule("dojox/charting/widget/Legend", "Legend");
            }
        }
        addModule("dojox/charting/action2d/Tooltip", "Tooltip");
    }

    /**
     * @return
     */
    public CharSequence getHtmlSnipplet()
    {
        if (!this.initialized) {
            initialize();
        }
        final StringBuilder ret = new StringBuilder()
            .append("\n<style type=\"text/css\"> ");

        addCSS(ret);

        ret.append("</style>\n")
            .append("<script type=\"text/javascript\">\n");

        addJavaScript(ret);

        ret.append("</script> \n");

        addHtmlNodes(ret);
        return ret;
    }

    protected void addCSS(final StringBuilder _js)
    {
        _js.append(".dojoxLegendNode Label {")
            .append("font-size: 8pt;")
            .append("}");
    }

    protected void addJavaScript(final StringBuilder _js)
    {
        // to be sure that it is the last module, add now
        addModule("dojo/domReady!", null);

        _js.append("require(").append(Util.collectionToObjectArray(this.modules.keySet())).append(", ")
            .append("function(");
        boolean first = true;
        for (final String para : this.modules.values()) {
            if (first) {
                first = false;
                _js.append(para);
            } else if (para != null) {
                _js.append(",").append(para);
            }
        }
        _js.append(")")
            .append(" { \n");

        addFunctionJS(_js);

        _js.append(" });");
    }


    protected void addFunctionJS(final StringBuilder _js)
    {
        _js.append(" var chart = new Chart(\"").append(getChartNodeId()).append("\", {\n");
        addChartJS(_js, "chart");
        _js.append(" });\n");
        addBeforeRenderJS(_js, "chart");
        addRenderJS(_js, "chart");
        addAfterRenderJS(_js, "chart");
    }

    protected void addBeforeRenderJS(final StringBuilder _js,
                                     final String _chartVarName)
    {
        _js.append(" chart.setTheme(theme);\n");
        addPlotJS(_js, _chartVarName);
        addSeriesJS(_js, _chartVarName);
        _js.append(" new Tooltip(chart, \"default\");\n");
    }

    protected void addRenderJS(final StringBuilder _js,
                               final String _chartVarName)
    {
        _js.append(" chart.render();\n");
    }

    protected void addAfterRenderJS(final StringBuilder _js,
                                    final String _chartVarName)
    {
        getLegend().addLegendJS(_js);
    }

    protected void addChartJS(final StringBuilder _js,
                              final String _chartVarName)
    {
        _js.append(getTitleJS());
    }

    protected void addPlotJS(final StringBuilder _js,
                             final String _chartVarName)
    {
        for (final Plot_Base<?> plot : getPlots().values()) {
            plot.addJS(_js, _chartVarName);
        }
    }

    protected CharSequence getTitleJS()
    {
        final StringBuilder ret = new StringBuilder();
        final String titleTmp = getTitle();
        if (titleTmp != null) {
            ret.append("title: \"").append(StringEscapeUtils.escapeEcmaScript(titleTmp)).append("\",")
                .append("titlePos: \"").append(getTitlePos()).append("\",")
                .append("titleFont: \"bold 8pt Tahoma\"");
        }
        //titleGap: 25,

        //titleFontColor: "orange"
        return ret;
    }

    protected void addSeriesJS(final StringBuilder _js,
                               final String _chartVarName)
    {
        for (final Serie<T> serie : getSeries()) {
            serie.addJS(_js, _chartVarName);
        }
    }

    protected void addHtmlNodes(final StringBuilder _js)
    {
        switch (getOrientation()) {
            case HORIZONTAL_CHART_LEGEND:
                _js.append(" <div style=\"display: table-row\">")
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
                _js.append(" <div style=\"display: table-row\">")
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
                _js.append(" <div id=\"").append(getChartNodeId()).append("\" style=\"width: ")
                        .append(getWidth()).append("px; height: ").append(getHeight()).append("px;\"></div>\n")
                    .append(" <div id=\"").append(getLegend().getNodeId()).append("\"></div>\n");

                break;
            case VERTICAL_LEGEND_CHART:
                _js .append(" <div id=\"").append(getLegend().getNodeId()).append("\"></div>\n")
                    .append(" <div id=\"").append(getChartNodeId()).append("\" style=\"width: ")
                        .append(getWidth()).append("px; height: ").append(getHeight()).append("px;\"></div>\n");
                break;
            default:
                break;
        }
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
    public S setChartNodeId(final String _chartNodeId)
    {
        this.chartNodeId = _chartNodeId;
        return getThis();
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
    public S setHeight(final int _height)
    {
        this.height = _height;
        return getThis();
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
    public S setWidth(final int _width)
    {
        this.width = _width;
        return getThis();
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
    public S addSerie(final Serie<T> _serie)
    {
        this.series.add(_serie);
        return getThis();
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
    public S setTheme(final Theme _theme)
    {
        this.theme = _theme;
        return getThis();
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
    public S setLegend(final Legend _legend)
    {
        this.legend = _legend;
        return getThis();
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
    public S setOrientation(final Orientation _orientation)
    {
        if (_orientation.equals(Orientation.HORIZONTAL_CHART_LEGEND)
                        || _orientation.equals(Orientation.HORIZONTAL_LEGEND_CHART)) {
            getLegend().setVertical(true);
        }
        this.orientation = _orientation;
        return getThis();
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
    public S setTitle(final String _title)
    {
        this.title = _title;
        return getThis();
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
    public S setTitlePos(final TitlePos _titlePos)
    {
        this.titlePos = _titlePos;
        return getThis();
    }

    protected void addModule(final String _module,
                                              final String _var)
    {
        this.modules.put("\"" + _module + "\"", _var);

    }

    /**
     * @param _string
     * @param _string2
     */
    public void addPlot(final Plot_Base<?> _plot)
    {
        getPlots().put(_plot.getName(), _plot);
    }

    /**
     * Getter method for the instance variable {@link #plots}.
     *
     * @return value of instance variable {@link #plots}
     */
    public Map<String, Plot_Base<?>> getPlots()
    {
        return this.plots;
    }

}

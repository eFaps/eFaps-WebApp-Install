/*
 * Copyright 2003 - 2017 The eFaps Team
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.ui.wicket.util.DojoClass;
import org.efaps.ui.wicket.util.DojoClasses;
import org.efaps.ui.wicket.util.DojoWrapper;
import org.efaps.util.RandomUtil;


/**
 * TODO comment!
 *
 * @author The eFaps Team
 */
@EFapsUUID("dfe47174-e442-4244-807b-5e11f56f0630")
@EFapsApplication("eFaps-WebApp")
public abstract class AbstractChart_Base<T extends AbstractData<T>, S extends AbstractChart_Base<T,S>>
{
    /** The clazzes. */
    private final Set<DojoClass> dojoClasses = new HashSet<>();

    /** The plots. */
    private final Map<String, Plot_Base<?>> plots = new LinkedHashMap<>();

    /** The orientation. */
    private Orientation orientation = Orientation.VERTICAL_CHART_LEGEND;

    /** The legend. */
    private Legend legend = new Legend();

    /** The chart node id. */
    private String chartNodeId;

    /** The height. */
    private int height = 300;

    /** The width. */
    private int width = 450;

    /** The series. */
    private final List<Serie<T>> series = new ArrayList<>();

    /** The title. */
    private String title;

    /** The title pos. */
    private TitlePos titlePos = TitlePos.top;

    /** The initialized. */
    private boolean initialized = false;

    /**
     * "getThis" trick.
     * @return this
     */
    protected abstract S getThis();

    /**
     * Initialize.
     */
    protected void initialize()
    {
        this.initialized = true;
        addDojoClass(DojoClasses.Tooltip, DojoClasses.Julie, DojoClasses.Chart, DojoClasses.registry,
                        DojoClasses.domReady);
        if (getLegend() != null) {
            if (getLegend().isSelectable()) {
                addDojoClass(DojoClasses.SelectableLegend);
            } else {
                addDojoClass(DojoClasses.Legend);
            }
        }
    }

    /**
     * Gets the html snipplet.
     *
     * @return the html snipplet
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

    /**
     * Adds the dojo class.
     *
     * @param _dojoClasses the dojo classes to add
     */
    protected void addDojoClass(final DojoClass... _dojoClasses) {
        CollectionUtils.addAll( this.dojoClasses, _dojoClasses);
    }

    /**
     * Adds the css.
     *
     * @param _js the _js
     */
    protected void addCSS(final StringBuilder _js)
    {
        _js.append(".dojoxLegendNode Label {")
            .append("font-size: 8pt;")
            .append("}");
    }

    /**
     * Adds the java script.
     *
     * @param _html the _html
     */
    protected void addJavaScript(final StringBuilder _html)
    {
        final StringBuilder js = new StringBuilder();
        addFunctionJS(js);
        js.append(" if(typeof registry.byId('").append(getChartNodeId()).append("') != \"undefined\"){\n")
            .append("    registry.byId('").append(getChartNodeId()).append("').destroyRecursive();\n")
            .append(" }");
        _html.append(DojoWrapper.require(js, getDojoClasses().toArray(new DojoClass[getDojoClasses().size()])));
    }

    /**
     * Adds the function js.
     *
     * @param _js the _js
     */
    protected void addFunctionJS(final StringBuilder _js)
    {
        _js.append(" var chart = new Chart(\"").append(getChartNodeId()).append("\", {\n");
        addChartJS(_js, "chart");
        _js.append(" });\n");
        addBeforeRenderJS(_js, "chart");
        addRenderJS(_js, "chart");
        addAfterRenderJS(_js, "chart");
    }

    /**
     * Adds the before render js.
     *
     * @param _js the _js
     * @param _chartVarName the _chart var name
     */
    protected void addBeforeRenderJS(final StringBuilder _js,
                                     final String _chartVarName)
    {
        _js.append(" chart.setTheme(theme);\n");
        addPlotJS(_js, _chartVarName);
        addSeriesJS(_js, _chartVarName);
        _js.append(" new Tooltip(chart, \"default\");\n");
    }

    /**
     * Adds the render js.
     *
     * @param _js the _js
     * @param _chartVarName the _chart var name
     */
    protected void addRenderJS(final StringBuilder _js,
                               final String _chartVarName)
    {
        _js.append(" chart.render();\n");
    }

    /**
     * Adds the after render js.
     *
     * @param _js the _js
     * @param _chartVarName the _chart var name
     */
    protected void addAfterRenderJS(final StringBuilder _js,
                                    final String _chartVarName)
    {
        getLegend().addLegendJS(_js);
    }

    /**
     * Adds the chart js.
     *
     * @param _js the _js
     * @param _chartVarName the _chart var name
     */
    protected void addChartJS(final StringBuilder _js,
                              final String _chartVarName)
    {
        _js.append(getTitleJS());
    }

    /**
     * Adds the plot js.
     *
     * @param _js the _js
     * @param _chartVarName the _chart var name
     */
    protected void addPlotJS(final StringBuilder _js,
                             final String _chartVarName)
    {
        for (final Plot_Base<?> plot : getPlots().values()) {
            plot.addJS(_js, _chartVarName);
        }
    }

    /**
     * Gets the title js.
     *
     * @return the title js
     */
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

    /**
     * Adds the series js.
     *
     * @param _js the _js
     * @param _chartVarName the _chart var name
     */
    protected void addSeriesJS(final StringBuilder _js,
                               final String _chartVarName)
    {
        for (final Serie<T> serie : getSeries()) {
            serie.addJS(_js, _chartVarName);
        }
    }

    /**
     * Adds the html nodes.
     *
     * @param _js the _js
     */
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

            case CHART_ONLY:
                _js.append(" <div id=\"").append(getChartNodeId()).append("\" style=\"width: ")
                .append(getWidth()).append("px; height: ").append(getHeight()).append("px;\"></div>\n");
            break;
            default:
                break;
        }
    }

    /**
     * Gets the clazzes.
     *
     * @return the clazzes
     */
    public Set<DojoClass> getDojoClasses()
    {
        return this.dojoClasses;
    }

    /**
     * Getter method for the instance variable {@link #charNodeId}.
     *
     * @return value of instance variable {@link #charNodeId}
     */
    public String getChartNodeId()
    {
        if (this.chartNodeId == null) {
            this.chartNodeId = RandomUtil.randomAlphabetic(8);
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

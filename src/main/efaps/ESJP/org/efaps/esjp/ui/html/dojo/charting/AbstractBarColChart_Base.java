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

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.ui.wicket.util.DojoClasses;

/**
 * TODO comment!.
 *
 * @author The eFaps Team
 * @param <T> the generic type
 * @param <S> the generic type
 */
@EFapsUUID("f5dac420-b326-499c-8fac-c9025728cb30")
@EFapsApplication("eFaps-WebApp")
public abstract class AbstractBarColChart_Base <T extends AbstractData<T>, S extends AbstractCartesianChart<T,S>>
    extends AbstractCartesianChart<T,S>
{
    /**
     * Determines the spacing between your bars or columns in pixels.
     */
    private Integer gap;

    /**
     * Defines the minimal width of a column/candle, or a height of bar.
     */
    private Integer minBarSize;

    /**
     * Defines the maximal width of a column/candle, or a height of bar.
     */
    private Integer maxBarSize;

    /** The highlight. */
    private boolean highlight = true;

    public AbstractBarColChart_Base()
    {
        addPlot(new Plot());
    }

    @Override
    protected void initialize()
    {
        super.initialize();

        configurePlot((Plot) getPlots().get("default"));

        if (isHighlight()) {
            addDojoClass(DojoClasses.Highlight);
        }
    }

    @Override
    protected void addBeforeRenderJS(final StringBuilder _js,
                                     final String _chartVarName)
    {
        super.addBeforeRenderJS(_js, _chartVarName);
        if (isHighlight()) {
            _js.append("var hl = new Highlight(chart, \"default\");\n");

            if (getDojoClasses().contains(DojoClasses.Julie)) {
                    _js.append("hl.process = function(o) {\n")
                        .append("if(!o.shape || !(o.type in this.overOutEvents)){ return; }\n")
                        .append("if(o.type == \"onmouseout\"){\n")
                        .append("o.shape.setFill(o.oldFill );\n")
                        .append("} else {\n")
                        .append("o.oldFill = o.shape.getFill();\n")
                        .append("o.shape.setFill(\"yellow\");\n")
                        .append("}\n")
                        .append("}\n");
            }
        }
    }

    /**
     * Configure plot.
     *
     * @param _plot the _plot
     */
    protected void configurePlot(final Plot _plot)
    {
        if (getGap() != null) {
            _plot.addConfig("gap", getGap());
        }
        if (getMinBarSize() != null) {
            _plot.addConfig("minBarSize", getMinBarSize());
        }
        if (getMaxBarSize() != null) {
            _plot.addConfig("maxBarSize", getMaxBarSize());
        }
    }

    /**
     * Getter method for the instance variable {@link #gap}.
     *
     * @return value of instance variable {@link #gap}
     */
    public Integer getGap()
    {
        return this.gap;
    }

    /**
     * Setter method for instance variable {@link #gap}.
     *
     * @param _gap value for instance variable {@link #gap}
     * @return the s
     */
    public S setGap(final Integer _gap)
    {
        this.gap = _gap;
        return getThis();
    }

    /**
     * Getter method for the instance variable {@link #minBarSize}.
     *
     * @return value of instance variable {@link #minBarSize}
     */
    public Integer getMinBarSize()
    {
        return this.minBarSize;
    }

    /**
     * Setter method for instance variable {@link #minBarSize}.
     *
     * @param _minBarSize value for instance variable {@link #minBarSize}
     * @return the s
     */
    public S setMinBarSize(final Integer _minBarSize)
    {
        this.minBarSize = _minBarSize;
        return getThis();
    }

    /**
     * Getter method for the instance variable {@link #maxBarSize}.
     *
     * @return value of instance variable {@link #maxBarSize}
     */
    public Integer getMaxBarSize()
    {
        return this.maxBarSize;
    }

    /**
     * Setter method for instance variable {@link #maxBarSize}.
     *
     * @param _maxBarSize value for instance variable {@link #maxBarSize}
     * @return the s
     */
    public S setMaxBarSize(final Integer _maxBarSize)
    {
        this.maxBarSize = _maxBarSize;
        return getThis();
    }


    /**
     * Getter method for the instance variable {@link #highlight}.
     *
     * @return value of instance variable {@link #highlight}
     */
    public boolean isHighlight()
    {
        return this.highlight;
    }

    /**
     * Setter method for instance variable {@link #highlight}.
     *
     * @param _highlight value for instance variable {@link #highlight}
     * @return the s
     */
    public S setHighlight(final boolean _highlight)
    {
        this.highlight = _highlight;
        return getThis();
    }
}

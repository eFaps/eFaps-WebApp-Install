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


/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
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


    @Override
    protected void initialize()
    {
        super.initialize();

        final Plot plot = new Plot();
        configurePlot(plot);
        addPlot(plot);
    }

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
     */
    public S setMaxBarSize(final Integer _maxBarSize)
    {
        this.maxBarSize = _maxBarSize;
        return getThis();
    }
}

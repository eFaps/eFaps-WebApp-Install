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
 * TODO comment!.
 *
 * @author The eFaps Team
 * @param <S> the generic type
 */
@EFapsUUID("b671b77d-0aa7-4dfa-8486-e2cc874f76b9")
@EFapsApplication("eFaps-WebApp")
public abstract class LineChart_Base <S extends AbstractCartesianChart<Data, S>>
    extends AbstractCartesianChart<Data, S>
{
    /** The line layout. */
    private LineLayout lineLayout = LineLayout.LINES;

    /** The magnify. */
    private boolean magnify = true;

    @Override
    protected void initialize()
    {
        super.initialize();

        switch (getLineLayout()) {
            case STACKEDLINES:
                addDojoClass(DojoClasses.StackedLines);
                break;
            case AREAS:
                addDojoClass(DojoClasses.Areas);
                break;
            case STACKEDAREAS:
                addDojoClass(DojoClasses.StackedAreas);
                break;
            case LINES:
            default:
                addDojoClass(DojoClasses.Lines);
                break;
        }
        if (isMagnify()) {
            addDojoClass(DojoClasses.Magnify);
        }
        boolean hasXAxis = false;
        boolean hasYAxis = false;
        for (final Axis axis : getAxis()) {
            if ("x".equals(axis.getName())) {
                hasXAxis = true;
            } else if ("y".equals(axis.getName())) {
                hasYAxis = true;
            }
        }
        if (!hasXAxis) {
            addAxis(new Axis().setName("x"));
        }
        if (!hasYAxis) {
            addAxis(new Axis().setName("y").setVertical(true));
        }
        if (!getPlots().containsKey("default")) {
            addPlot(new Plot().addConfig("type", "Lines"));
        }
    }

    @Override
    protected void addBeforeRenderJS(final StringBuilder _js,
                                     final String _chartVarName)
    {
        super.addBeforeRenderJS(_js, _chartVarName);
        if (isMagnify()) {
            _js.append("new Magnify(chart, \"default\");\n");
        }
    }

    /**
     * Getter method for the instance variable {@link #layout}.
     *
     * @return value of instance variable {@link #layout}
     */
    public LineLayout getLineLayout()
    {
        return this.lineLayout;
    }

    /**
     * Setter method for instance variable {@link #layout}.
     *
     * @param _layout value for instance variable {@link #layout}
     * @return the s
     */
    public S setLineLayout(final LineLayout _layout)
    {
        this.lineLayout = _layout;
        return getThis();
    }

    /**
     * Getter method for the instance variable {@link #magnify}.
     *
     * @return value of instance variable {@link #magnify}
     */
    public boolean isMagnify()
    {
        return this.magnify;
    }

    /**
     * Setter method for instance variable {@link #magnify}.
     *
     * @param _magnify value for instance variable {@link #magnify}
     * @return the s
     */
    public S setMagnify(final boolean _magnify)
    {
        this.magnify = _magnify;
        return getThis();
    }
}

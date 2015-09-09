/*
 * Copyright 2003 - 2015 The eFaps Team
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

/**
 * TODO comment!
 *
 * @author The eFaps Team
 */
@EFapsUUID("b671b77d-0aa7-4dfa-8486-e2cc874f76b9")
@EFapsApplication("eFaps-WebApp")
public abstract class LineChart_Base <S extends AbstractCartesianChart<Data, S>>
    extends AbstractCartesianChart<Data, S>
{
    private LineLayout lineLayout = LineLayout.LINES;

    @Override
    protected void initialize()
    {
        super.initialize();

        switch (getLineLayout()) {
            case LINES:
                addModule("dojox/charting/plot2d/Lines", "Lines");
                break;
            case STACKEDLINES:
                addModule("dojox/charting/plot2d/StackedLines", "Lines");
                break;
            case AREAS:
                addModule("dojox/charting/plot2d/Areas", "Lines");
                break;
            case STACKEDAREAS:
                addModule("dojox/charting/plot2d/StackedAreas", "Lines");
                break;
            default:
                addModule("dojox/charting/plot2d/Lines", "Lines");
                break;
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
     */
    public S setLineLayout(final LineLayout _layout)
    {
        this.lineLayout = _layout;
        return getThis();
    }
}

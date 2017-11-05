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

import org.apache.commons.text.StringEscapeUtils;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;


/**
 * TODO comment!
 *
 * @author The eFaps Team
 */
@EFapsUUID("48437957-49f9-40b0-9dc5-d5169f16a2d4")
@EFapsApplication("eFaps-WebApp")
public abstract class PieData_Base<S extends AbstractData_Base<S>>
    extends AbstractData<S>
{

    /** The text. */
    private String text;

    /** The legend. */
    private String legend;

    @Override
    public CharSequence getJavaScript()
    {
        addConfig("x", 1);
        addConfig("y", getYValue());
        if (getText() != null) {
            addConfig("text", "\"" + StringEscapeUtils.escapeEcmaScript(getText()) + "\"");
        }
        if (getLegend() != null) {
            addConfig("legend", "\"" + StringEscapeUtils.escapeEcmaScript(getLegend()) + "\"");
        }
        return getConfigJS();
    }

    /**
     * Getter method for the instance variable {@link #text}.
     *
     * @return value of instance variable {@link #text}
     */
    public String getText()
    {
        return this.text;
    }

    /**
     * Setter method for instance variable {@link #text}.
     *
     * @param _text value for instance variable {@link #text}
     */
    public S setText(final String _text)
    {
        this.text = _text;
        return getThis();
    }

    /**
     * Getter method for the instance variable {@link #legend}.
     *
     * @return value of instance variable {@link #legend}
     */
    public String getLegend()
    {
        return this.legend;
    }

    /**
     * Setter method for instance variable {@link #legend}.
     *
     * @param _legend value for instance variable {@link #legend}
     */
    public S setLegend(final String _legend)
    {
        this.legend = _legend;
        return getThis();
    }
}

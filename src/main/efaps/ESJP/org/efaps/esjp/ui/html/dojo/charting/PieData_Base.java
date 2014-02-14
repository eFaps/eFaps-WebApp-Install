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

import org.apache.commons.lang3.StringEscapeUtils;
import org.efaps.admin.program.esjp.EFapsRevision;
import org.efaps.admin.program.esjp.EFapsUUID;


/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
@EFapsUUID("48437957-49f9-40b0-9dc5-d5169f16a2d4")
@EFapsRevision("$Rev$")
public abstract class PieData_Base
    extends AbstractData
{
    private String text;
    private String legend;
    private String tooltip;

    @Override
    public CharSequence getJavaScript()
    {
        final StringBuilder ret = new StringBuilder()
            .append("{ x:1,y:").append(getValue());
        if (getText() != null) {
            ret.append(", text:\"").append(StringEscapeUtils.escapeEcmaScript(getText())).append("\"");
        }
        if (getLegend() != null) {
            ret.append(", legend:\"").append(StringEscapeUtils.escapeEcmaScript(getLegend())).append("\"");
        }
        if (getTooltip() != null) {
            ret.append(", tooltip:\"").append(StringEscapeUtils.escapeEcmaScript(getTooltip())).append("\"");
        }
        ret.append("}");
        return ret;
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
    public void setText(final String _text)
    {
        this.text = _text;
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
    public void setLegend(final String _legend)
    {
        this.legend = _legend;
    }

    /**
     * Getter method for the instance variable {@link #tooltip}.
     *
     * @return value of instance variable {@link #tooltip}
     */
    public String getTooltip()
    {
        return this.tooltip;
    }

    /**
     * Setter method for instance variable {@link #tooltip}.
     *
     * @param _tooltip value for instance variable {@link #tooltip}
     */
    public void setTooltip(final String _tooltip)
    {
        this.tooltip = _tooltip;
    }
}

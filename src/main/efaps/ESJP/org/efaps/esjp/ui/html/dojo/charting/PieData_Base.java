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

import java.util.LinkedHashMap;
import java.util.Map;

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
public abstract class PieData_Base<S extends AbstractData_Base<S>>
    extends AbstractData<S>
{
    private String text;
    private String legend;
    private String tooltip;

    @Override
    public CharSequence getJavaScript()
    {
        final Map<String, Object> paraMap = new LinkedHashMap<String, Object>();
        paraMap.put("x", 1);
        paraMap.put("y", getValue());
        if (getText() != null) {
            paraMap.put("text", "\"" + StringEscapeUtils.escapeEcmaScript(getText()) + "\"");
        }
        if (getLegend() != null) {
            paraMap.put("legend", "\"" + StringEscapeUtils.escapeEcmaScript(getLegend()) + "\"");
        }
        if (getTooltip() != null) {
            paraMap.put("tooltip", "\"" + StringEscapeUtils.escapeEcmaScript(getTooltip()) + "\"");
        }
        return Util.mapToObjectList(paraMap);
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
    public S setTooltip(final String _tooltip)
    {
        this.tooltip = _tooltip;
        return getThis();
    }
}

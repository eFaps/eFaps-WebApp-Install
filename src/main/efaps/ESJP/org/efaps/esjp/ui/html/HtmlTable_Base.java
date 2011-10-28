/*
 * Copyright 2003 - 2011 The eFaps Team
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


package org.efaps.esjp.ui.html;

import org.efaps.admin.program.esjp.EFapsRevision;
import org.efaps.admin.program.esjp.EFapsUUID;


/**
 * Helper Class to build a Html Table.
 *
 * @author The eFaps Team
 * @version $Id$
 */
@EFapsUUID("3a46c305-625f-4c05-a708-67fd25465620")
@EFapsRevision("$Rev$")
public abstract class HtmlTable_Base
{
    private final StringBuilder html = new StringBuilder();

    public HtmlTable_Base table() {
        return tag("table", false);
    }
    public HtmlTable_Base tableC() {
        return tag("table", true);
    }
    public HtmlTable_Base tr() {
        return tag("tr", false);
    }

    public HtmlTable_Base trC() {
        return tag("tr", true);
    }

    public HtmlTable_Base append(final Object _object)
    {
        this.html.append(_object);
        return this;
    }

    public HtmlTable_Base td(final String _value) {
        td(_value, null);
        return this;
    }

    public HtmlTable_Base td(final String _value,
                             final String _styleSheet) {
        td(_value, _styleSheet, 0, 0);
        return this;
    }

    public HtmlTable_Base td(final String _value,
                             final String _styleSheet,
                             final int _colspan,
                             final int _rowspan) {
        td(_value, _styleSheet, _colspan, _rowspan, null);
        return this;
    }

    public HtmlTable_Base td(final String _value,
                             final String _styleSheet,
                             final int _colspan,
                             final int _rowspan,
                             final String _inner) {
        return tag(_value, _styleSheet, _colspan, _rowspan, _inner, "td");
    }

    public HtmlTable_Base th(final String _value) {
        th(_value, null);
        return this;
    }

    public HtmlTable_Base th(final String _value,
                             final String _styleSheet) {
        th(_value, _styleSheet, 0, 0);
        return this;
    }

    public HtmlTable_Base th(final String _value,
                             final String _styleSheet,
                             final int _colspan,
                             final int _rowspan) {
        th(_value, _styleSheet, _colspan, _rowspan, null);
        return this;
    }

    public HtmlTable_Base th(final String _value,
                             final String _styleSheet,
                             final int _colspan,
                             final int _rowspan,
                             final String _inner) {
        return tag(_value, _styleSheet, _colspan, _rowspan, _inner, "th");
    }

    public HtmlTable_Base tag(final String _tag,
                               final boolean _close) {
        this.html.append("<");
        if (_close) {
            this.html.append("/");
        }
        this.html.append(_tag).append(">");
        return this;
    }
    public HtmlTable_Base tag(final String _value,
                               final String _styleSheet,
                               final int _colspan,
                               final int _rowspan,
                               final String _inner,
                               final String _tag) {
        this.html.append("<").append(_tag);
        if (_styleSheet != null) {
            this.html.append(" style=\"").append(_styleSheet).append("\"");
        }
        if (_colspan != 0) {
            this.html.append(" colspan=\"").append(_colspan).append("\"");
        }
        if (_rowspan != 0) {
            this.html.append(" rowspan=\"").append(_rowspan).append("\"");
        }
        if (_inner != null) {
            this.html.append(" ").append(_inner);
        }
        this.html.append(">").append(_value).append("</").append(_tag).append(">");
        return this;
    }

    @Override
    public String toString()
    {
        return this.html.toString();
    }

}

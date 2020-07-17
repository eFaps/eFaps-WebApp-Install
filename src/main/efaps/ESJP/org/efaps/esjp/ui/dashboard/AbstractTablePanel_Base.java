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
package org.efaps.esjp.ui.dashboard;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.api.ui.IEsjpSnipplet;
import org.efaps.db.Instance;
import org.efaps.esjp.common.dashboard.AbstractDashboardPanel;
import org.efaps.esjp.ui.html.Table;
import org.efaps.ui.wicket.EFapsSession;
import org.efaps.ui.wicket.models.EmbeddedLink;
import org.efaps.util.EFapsBaseException;

/**
 * The Class AbstractTablePanel_Base.
 *
 * @author The eFaps Team
 */
@EFapsUUID("b0b04f2c-1bbd-4000-828e-158725653a61")
@EFapsApplication("eFaps-WebApp")
public abstract class AbstractTablePanel_Base
    extends AbstractDashboardPanel
    implements IEsjpSnipplet
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new abstract dashboard panel_ base.
     *
     * @param _config the _config
     */
    public AbstractTablePanel_Base(final String _config)
    {
        super(_config);
    }


    @Override
    public CharSequence getHtmlSnipplet()
        throws EFapsBaseException
    {
        final List<Map<String, Object>> ds =  getDataSource();
        final StringBuilder html = new StringBuilder();
        final String cssClss = getCssClass();
        final Table table = new Table().setCssClass("dashboardtable" + (cssClss == null ? "" : " " + cssClss));
        if (!ds.isEmpty()) {
            for (final Entry<String, Object> entry: ds.get(0).entrySet()) {
                if (entry.getValue() instanceof Instance) {
                    table.addHeaderColumn("");
                } else {
                    table.addHeaderColumn(entry.getKey());
                }
            }
            for (final Map<String, Object> map : ds) {
                table.addRow();
                for (final Entry<String, Object> entry : map.entrySet()) {
                    final Object value = entry.getValue();
                    if (value instanceof Instance) {
                        final EmbeddedLink link = EmbeddedLink.getDashboardLink(((Instance) value).getOid(),
                                        getIdentifier());
                        EFapsSession.get().addEmbededLink(link);
                        table.addColumn(link.getTag());
                    } else if (value != null) {
                        table.addColumn(value.toString());
                    } else {
                        table.addColumn("");
                    }
                }
            }
        }
        html.append("<div style=\"width: ").append(getWidth())
            .append("px; height: ").append(getHeight())
            .append("px; overflow:auto;\">");

        final String style = getStyle();
        if (style != null) {
            html.append("<style type=\"text/css\">\n")
                .append(style)
                .append("\n</style>");
        }

        if (getTitle() != null) {
            html.append("<div class=\"title\">").append(getTitle()).append("</div>");
        }
        html.append(table.toHtml()).append("</div>");
        return html;
    }

    @Override
    public boolean isVisible()
        throws EFapsBaseException
    {
        return true;
    }

    /**
     * Gets the data source.
     *
     * @return the data source
     * @throws EFapsBaseException the e faps base exception
     */
    protected abstract List<Map<String, Object>> getDataSource()
        throws EFapsBaseException;

}

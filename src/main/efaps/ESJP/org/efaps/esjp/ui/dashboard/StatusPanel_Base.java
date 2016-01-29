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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.api.ui.IEsjpSnipplet;
import org.efaps.db.Context;
import org.efaps.db.MultiPrintQuery;
import org.efaps.db.QueryBuilder;
import org.efaps.esjp.ci.CICommon;
import org.efaps.esjp.common.dashboard.AbstractDashboardPanel;
import org.efaps.esjp.ui.html.dojo.charting.Axis;
import org.efaps.esjp.ui.html.dojo.charting.Data;
import org.efaps.esjp.ui.html.dojo.charting.LineChart;
import org.efaps.esjp.ui.html.dojo.charting.LineLayout;
import org.efaps.esjp.ui.html.dojo.charting.MouseIndicator;
import org.efaps.esjp.ui.html.dojo.charting.Orientation;
import org.efaps.esjp.ui.html.dojo.charting.Serie;
import org.efaps.esjp.ui.html.dojo.charting.Util;
import org.efaps.util.EFapsException;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

/**
 * TODO comment!
 *
 * @author The eFaps Team
 */
@EFapsUUID("a1f59464-0354-47c4-ac92-0f393d5bfa8a")
@EFapsApplication("eFaps-WebApp")
public class StatusPanel_Base
    extends AbstractDashboardPanel
    implements IEsjpSnipplet
{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Gets the currency inst.
     *
     * @return the currency inst @throws EFapsException on error
     * @throws EFapsException the e faps exception
     */
    protected String getDateFormat()
        throws EFapsException
    {
        return getConfig().getProperty("DateFormat", "dd/MM/yyyy");
    }

    /**
     * Gets the duration quantity.
     *
     * @return the duration quantity
     */
    protected Integer getDays()
    {
        return Integer.valueOf(getConfig().getProperty("Days", "14"));
    }


    @Override
    public CharSequence getHtmlSnipplet()
        throws EFapsException
    {
        final Map<LocalDate, Set<Long>> map = new TreeMap<>();

        final QueryBuilder queryBldr = new QueryBuilder(CICommon.HistoryLogin);
        queryBldr.addWhereAttrGreaterValue(CICommon.HistoryLogin.Created, new DateTime().minusDays(getDays()));
        queryBldr.addOrderByAttributeAsc(CICommon.HistoryLogin.Created);
        final MultiPrintQuery multi = queryBldr.getPrint();
        multi.addAttribute(CICommon.HistoryLogin.Created, CICommon.HistoryLogin.GeneralInstanceLink);
        multi.execute();
        while (multi.next()) {
            final DateTime created = multi.getAttribute(CICommon.HistoryLogin.Created);
            final LocalDate date = created.toLocalDate();
            final Long gId = multi.getAttribute(CICommon.HistoryLogin.GeneralInstanceLink);
            Set<Long> set;
            if (map.containsKey(date)) {
                set = map.get(date);
            } else {
                set = new HashSet<>();
                map.put(date, set);
            }
            set.add(gId);
        }

        final LineChart chart = new LineChart().setLineLayout(LineLayout.LINES)
                        .setWidth(getWidth()).setHeight(getHeight());
        final String title = getTitle();
        if (title != null && !title.isEmpty()) {
            chart.setTitle(getTitle());
        }
        final Axis xAxis = new Axis().setName("x");
        chart.addAxis(xAxis);

        final Serie<Data> serie = new Serie<Data>();
        serie.setName("Usuarios").setMouseIndicator(new MouseIndicator());
        ;
        chart.addSerie(serie);

        final List<Map<String, Object>> labels = new ArrayList<>();
        int idx = 1;
        for (final Entry<LocalDate, Set<Long>> entry : map.entrySet()) {
            final Data data = new Data();
            serie.addData(data);
            data.setYValue(entry.getValue().size()).setXValue(idx);
            final Map<String, Object> labelMap = new HashMap<>();
            labelMap.put("value", idx);
            labelMap.put("text", Util.wrap4String(
                            entry.getKey().toString(getDateFormat(), Context.getThreadContext().getLocale())));
            labels.add(labelMap);
            idx++;
        }
        xAxis.setLabels(Util.mapCollectionToObjectArray(labels));
        chart.setOrientation(Orientation.HORIZONTAL_LEGEND_CHART);

        return chart.getHtmlSnipplet();
    }

    @Override
    public boolean isVisible()
        throws EFapsException
    {
        return true;
    }
}

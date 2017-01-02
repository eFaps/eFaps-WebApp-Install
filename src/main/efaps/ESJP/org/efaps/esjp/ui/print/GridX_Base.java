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

package org.efaps.esjp.ui.print;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.efaps.admin.event.EventExecution;
import org.efaps.admin.event.Parameter;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.event.Return;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.esjp.common.jasperreport.AbstractDynamicReport;
import org.efaps.esjp.common.parameter.ParameterUtil;
import org.efaps.ui.wicket.models.objects.UIGrid;
import org.efaps.ui.wicket.models.objects.UIGrid.Cell;
import org.efaps.ui.wicket.models.objects.UIGrid.Column;
import org.efaps.ui.wicket.models.objects.UIGrid.Row;
import org.efaps.util.EFapsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

/**
 * TODO comment!
 *
 * @author The eFaps Team
 */
@EFapsUUID("c64916f8-d670-4422-8284-e452ad196d95")
@EFapsApplication("eFaps-WebApp")
public abstract class GridX_Base
    extends AbstractDynamicReport
    implements EventExecution
{
    /**
     * Logger for this class.
     */
    public static final Logger LOG = LoggerFactory.getLogger(GridX.class);

    @Override
    public Return execute(final Parameter _parameter)
        throws EFapsException
    {
        final Return ret = new Return();
        ParameterUtil.setProperty(_parameter, "demo", "1");
        final String mime = _parameter.getParameterValue("MIME");
        final UIGrid uiGrid = (UIGrid) _parameter.get(ParameterValues.CLASS);
        setFileName(uiGrid.getTitle());
        if ("pdf".equalsIgnoreCase(mime)) {
            setPageOrientation(PageOrientation.LANDSCAPE);
            ret.put(ReturnValues.VALUES, getPDF(_parameter));
        } else {
            ret.put(ReturnValues.VALUES, getExcel(_parameter));
        }
        return ret;
    }

    @Override
    protected ComponentBuilder<?, ?> getPageHeader(final Parameter _parameter,
                                                   final JasperReportBuilder _report)
        throws EFapsException
    {
        final UIGrid uiGrid = (UIGrid) _parameter.get(ParameterValues.CLASS);
        ComponentBuilder<?, ?> ret;
        if (ExportType.PDF.equals(getExType())) {
            ret = DynamicReports.cmp.horizontalList(DynamicReports.cmp.text(uiGrid.getTitle()),
                            DynamicReports.cmp.text(new Date()).setHorizontalTextAlignment(
                                            HorizontalTextAlignment.RIGHT).setDataType(DynamicReports.type
                                                            .dateYearToMinuteType()));
        } else {
            ret = DynamicReports.cmp.verticalList(DynamicReports.cmp.text(uiGrid.getTitle()),
                            DynamicReports.cmp.text(new Date())
                                .setDataType(DynamicReports.type.dateYearToMinuteType())
                                .setHorizontalTextAlignment(HorizontalTextAlignment.LEFT));
        }
        return ret;
    }

    @Override
    protected JRDataSource createDataSource(final Parameter _parameter)
        throws EFapsException
    {
        final UIGrid uiGrid = (UIGrid) _parameter.get(ParameterValues.CLASS);
        final String[] srs = _parameter.getParameterValue("sr").split(",");
        final Object[] rows = uiGrid.getValues().toArray();
        final List<Map<String, ?>> values = new ArrayList<>();
        for (final String sr : srs) {
            if (!sr.isEmpty()) {
                final Map<String, Object> map = new HashMap<>();
                values.add(map);
                final Row row = (Row) rows[Integer.parseInt(sr)];
                for (final Cell cell : row) {
                    map.put(cell.getFieldConfig().getName(), cell.getValue());
                }
            }
        }
        return new JRMapCollectionDataSource(values);
    }

    @Override
    protected void addColumnDefintion(final Parameter _parameter,
                                      final JasperReportBuilder _builder)
        throws EFapsException
    {
        final UIGrid uiGrid = (UIGrid) _parameter.get(ParameterValues.CLASS);
        final String[] cms = _parameter.getParameterValue("cm").split(",");

        final Map<Long, Column> id2colum = uiGrid.getColumns().stream().collect(Collectors.toMap(c -> c.getField()
                        .getId(), Function.identity()));

        for (final String colId : cms) {
            final Column column = id2colum.get(Long.valueOf(colId));
            if (column != null) {
                final TextColumnBuilder<String> col = DynamicReports.col.column(column.getLabel(), column
                                .getFieldName(), String.class);
                if ("right".equals(column.getFieldConfig().getAlign())) {
                    final StyleBuilder colStyle;
                    if (ExportType.PDF.equals(getExType())) {
                        colStyle = getColumnStyle4Pdf(_parameter);
                    } else {
                        colStyle = getColumnStyle4Excel(_parameter);
                    }
                    colStyle.setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
                    col.setStyle(colStyle);
                }
                _builder.addColumn(col);
            }
        }
    }
}

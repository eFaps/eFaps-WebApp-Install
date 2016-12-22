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
import org.efaps.ui.wicket.models.objects.UIGrid.Column;
import org.efaps.util.EFapsException;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.jasperreports.engine.JRDataSource;

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

    @Override
    public Return execute(final Parameter _parameter)
        throws EFapsException
    {
        final Return ret = new Return();
        ParameterUtil.setProperty(_parameter, "demo", "1");
        final String mime = _parameter.getParameterValue("MIME");
        if ("pdf".equalsIgnoreCase(mime)) {
            ret.put(ReturnValues.VALUES, getPDF(_parameter));
        } else {
            ret.put(ReturnValues.VALUES, getExcel(_parameter));
        }
        return ret;
    }

    @Override
    protected JRDataSource createDataSource(final Parameter _parameter)
        throws EFapsException
    {
        _parameter.get(ParameterValues.CLASS);
        return null;
    }

    @Override
    protected void addColumnDefintion(final Parameter _parameter,
                                      final JasperReportBuilder _builder)
        throws EFapsException
    {
        final UIGrid uiGrid = (UIGrid) _parameter.get(ParameterValues.CLASS);
        for (final Column column : uiGrid.getColumns()) {
            final TextColumnBuilder<String> col = DynamicReports.col.column(column.getLabel(), column.getLabel(),
                            String.class);
            _builder.addColumn(col);
        }
    }
}

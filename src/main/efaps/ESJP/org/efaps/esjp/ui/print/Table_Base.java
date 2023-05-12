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

import java.awt.Color;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.PageReference;
import org.efaps.admin.datamodel.Attribute;
import org.efaps.admin.datamodel.attributetype.BooleanType;
import org.efaps.admin.datamodel.attributetype.DateTimeType;
import org.efaps.admin.datamodel.attributetype.DecimalType;
import org.efaps.admin.datamodel.attributetype.IntegerType;
import org.efaps.admin.datamodel.attributetype.LongType;
import org.efaps.admin.datamodel.attributetype.RateType;
import org.efaps.admin.event.Parameter;
import org.efaps.admin.event.Return;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.AbstractUserInterfaceObject.TargetMode;
import org.efaps.admin.ui.field.Field;
import org.efaps.db.Context;
import org.efaps.ui.wicket.models.field.AbstractUIField;
import org.efaps.ui.wicket.models.field.IFilterable;
import org.efaps.ui.wicket.models.field.UIField;
import org.efaps.ui.wicket.models.objects.AbstractUIHeaderObject;
import org.efaps.ui.wicket.models.objects.AbstractUIPageObject;
import org.efaps.ui.wicket.models.objects.UIRow;
import org.efaps.ui.wicket.models.objects.UIStructurBrowser;
import org.efaps.ui.wicket.models.objects.UITable;
import org.efaps.ui.wicket.models.objects.UITableHeader;
import org.efaps.util.EFapsException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.builder.style.Styles;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.constant.VerticalTextAlignment;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

/**
 * TODO comment!
 *
 * @author The eFaps Team
 */
@EFapsUUID("99ce434b-4177-4e65-99d1-0195434f628d")
@EFapsApplication("eFaps-WebApp")
public abstract class Table_Base
    extends UserInterface
{

    /**
     * Logger for this class.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(Table_Base.class);

    /**
     * Sections of the report.
     */
    public enum Section
    {
        /**
         * DateilSection, Header, Title, Subtitle, Column.
         */
        DETAIL, HEADER, TITLE, SUBTITLE, COLUMN, COLUMNHEADER;
    }

    /**
     * @param _parameter Parameter
     * @return return PDF with Table
     * @throws EFapsException on error
     */
    @Override
    public Return execute(final Parameter _parameter)
        throws EFapsException
    {
        final Return ret = new Return();

        final PageReference reference = (PageReference) Context.getThreadContext().getSessionAttribute(
                        UserInterface_Base.UIOBJECT_CACHEKEY);
        final Object object = reference.getPage().getDefaultModelObject();
        if (object instanceof AbstractUIPageObject) {
            String mime = getProperty(_parameter, "Mime");
            if (mime == null) {
                mime = _parameter.getParameterValue("mime");
            }
            setFileName(((AbstractUIPageObject) object).getTitle());
            if (object instanceof UITable || object instanceof UIStructurBrowser) {
                try {
                    final JasperReportBuilder jrb = execute4Table(_parameter, (AbstractUIPageObject) object, mime);
                    ret.put(ReturnValues.VALUES, super.getFile(jrb.toJasperPrint(), mime));
                    ret.put(ReturnValues.TRUE, true);
                } catch (final JRException e) {
                    throw new EFapsException(Table_Base.class, "JRException", e);
                } catch (final IOException e) {
                    throw new EFapsException(Table_Base.class, "IOException", e);
                } catch (final DRException e) {
                    throw new EFapsException(Table_Base.class, "DRException", e);
                }
            } else {
                Table_Base.LOG.error("Not implemented!");
            }
        }
        return ret;
    }

    /**
     * Execute for table.
     *
     * @param _parameter Parameter as passed by the eFaps API
     * @param _object the object
     * @param _mime the mime
     * @return the jasper report builder
     * @throws EFapsException on error
     */
    protected JasperReportBuilder execute4Table(final Parameter _parameter,
                                                final AbstractUIPageObject _object,
                                                final String _mime)
        throws EFapsException
    {
        final String[] oids = (String[]) Context.getThreadContext().getSessionAttribute("selectedOIDs4print");

        final TargetMode mode = "xls".equalsIgnoreCase(_mime) ? TargetMode.PRINT : TargetMode.VIEW;
        final boolean print = mode.equals(TargetMode.VIEW);
        if (!mode.equals(_object.getMode())) {
            _object.resetModel();
            _object.setMode(mode);
            _object.execute();
        }

        final JasperReportBuilder jrb = getBuilder(_parameter, _object, _mime);
        if (print) {
            jrb.setPageMargin(DynamicReports.margin(20))
                            .setPageFormat(PageType.A4, PageOrientation.LANDSCAPE)
                            .setColumnHeaderStyle(getStyle(_parameter, Table_Base.Section.HEADER))
                            .highlightDetailEvenRows()
                            .pageFooter(DynamicReports.cmp.pageXofY().setStyle(DynamicReports.stl.style()
                                            .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)));
        } else {
            jrb.setIgnorePagination(true)
                            .setPageMargin(DynamicReports.margin(0));
        }
        final int widthWeight = ((AbstractUIHeaderObject) _object).getWidthWeight();
        final String[] columns = _parameter.getParameterValues("columns");
        final Set<String> selCols = new HashSet<>();
        for (final String col : columns) {
            selCols.add(col);
        }
        final Map<String, Attribute> selAttr = new HashMap<>();
        final List<Map<String, Object>> values = new ArrayList<>();
        if (_object instanceof UITable) {
            for (final UIRow row : ((UITable) _object).getValues()) {
                if (oids == null ||  ArrayUtils.contains(oids, row.getInstanceKey())) {
                    final Map<String, Object> map = new HashMap<>();
                    for (final IFilterable filterable : row.getCells()) {
                        if (filterable instanceof UIField) {
                            final UIField uiField = (UIField) filterable;
                            if (selCols.contains(uiField.getFieldConfiguration().getName())) {
                                Object value = print ? uiField.getPickListValue()
                                            : uiField.getCompareValue() != null
                                                ? uiField.getCompareValue() : uiField.getPickListValue();
                                if (value instanceof DateTime) {
                                    value = ((DateTime) value).toDate();
                                } else if (uiField.getFactory() != null &&
                                                uiField.getFactoryKey().endsWith("DateUIFactory")
                                                && value instanceof String && StringUtils.isEmpty((String) value)) {
                                    value = null;
                                }
                                map.put(uiField.getFieldConfiguration().getName(), value);
                                selAttr.put(uiField.getFieldConfiguration().getName(), null);
                            }
                        }
                    }
                    values.add(map);
                }
            }
        } else if (_object instanceof UIStructurBrowser) {
            final List<UIStructurBrowser> roots = ((UIStructurBrowser) _object).getChildren();
            add2Values4StrBrws(selCols, selAttr, print, values, roots);
        }

        for (final UITableHeader header : ((AbstractUIHeaderObject) _object).getHeaders()) {
            final boolean add;
            if (print) {
                add = selCols.contains(header.getFieldName());
            } else {
                final Field field = Field.get(header.getFieldId());
                add = field.isNoneDisplay(TargetMode.VIEW) && !field.isNoneDisplay(TargetMode.PRINT)
                                || selCols.contains(header.getFieldName());
                if (add && !selCols.contains(header.getFieldName())) {
                    selCols.add(header.getFieldName());
                }
            }
            if (add) {
                final BigDecimal width = new BigDecimal(header.getWidth()).setScale(2)
                                .divide(new BigDecimal(widthWeight), RoundingMode.HALF_UP)
                                .multiply(new BigDecimal(555));

                TextColumnBuilder<?> clbdr = null;
                final Attribute attr = selAttr.get(header.getFieldName());
                if (attr != null && !print) {
                    if (attr.getAttributeType().getDbAttrType() instanceof LongType) {
                        if (checkValues(values, header.getFieldName(), Long.class)) {
                            clbdr = DynamicReports.col.column(header.getLabel(), header.getFieldName(),
                                            DynamicReports.type.longType());
                        }
                    } else if (attr.getAttributeType().getDbAttrType() instanceof DecimalType) {
                        if (checkValues(values, header.getFieldName(), BigDecimal.class)) {
                            clbdr = DynamicReports.col.column(header.getLabel(), header.getFieldName(),
                                            DynamicReports.type.bigDecimalType());
                        }
                    } else if (attr.getAttributeType().getDbAttrType() instanceof IntegerType) {
                        clbdr = DynamicReports.col.column(header.getLabel(), header.getFieldName(),
                                        DynamicReports.type.integerType());
                    } else if (attr.getAttributeType().getDbAttrType() instanceof BooleanType) {
                        if (checkValues(values, header.getFieldName(), Boolean.class)) {
                            clbdr = DynamicReports.col.column(header.getLabel(), header.getFieldName(),
                                            DynamicReports.type.booleanType());
                        }
                    } else if (attr.getAttributeType().getDbAttrType() instanceof DateTimeType) {
                        if (checkValues(values, header.getFieldName(), Date.class)) {
                            clbdr = DynamicReports.col.column(header.getLabel(), header.getFieldName(),
                                            DynamicReports.type.dateType());
                        }
                    } else if (attr.getAttributeType().getDbAttrType() instanceof RateType) {
                        clbdr = DynamicReports.col.column(header.getLabel(), header.getFieldName(),
                                        DynamicReports.type.bigDecimalType());
                    }
                }

                if (clbdr == null) {
                    clbdr = getColumnBuilder4Values(values, header);
                }
                if (print) {
                    clbdr.setWidth(header.isFixedWidth() ? header.getWidth() : width.intValue());
                }
                jrb.addColumn(clbdr);
            }
        }
        jrb.setLocale(Context.getThreadContext().getLocale()).setDataSource(getSource(_parameter, values));
        return jrb;
    }

    /**
     * Gets the builder.
     *
     * @param _parameter Parameter as passed by the eFaps API
     * @param _object the object
     * @param _mime the mime
     * @return the builder
     */
    protected JasperReportBuilder getBuilder(final Parameter _parameter,
                                             final AbstractUIPageObject _object,
                                             final String _mime)
    {

        final JasperReportBuilder ret = DynamicReports.report();
        if ("pdf".equalsIgnoreCase(_mime)) {
            ret.addPageHeader(DynamicReports.cmp.horizontalList(DynamicReports.cmp.text(_object.getTitle()),
                            DynamicReports.cmp.text(new Date()).setHorizontalTextAlignment(
                                            HorizontalTextAlignment.RIGHT).setDataType(DynamicReports.type
                                                            .dateYearToMinuteType())));
        } else {
            ret.addPageHeader(DynamicReports.cmp.verticalList(DynamicReports.cmp.text(_object.getTitle()),
                            DynamicReports.cmp.text(new Date())
                                .setDataType(DynamicReports.type.dateYearToMinuteType())
                                .setHorizontalTextAlignment(HorizontalTextAlignment.LEFT)));
        }
        return ret;
    }

    /**
     * Recursive Method to add to the values map for StructurBrowser.
     *
     * @param _selCols selected Columns
     * @param _selAttr selected Attributes
     * @param _print print mode
     * @param _values values to be added to
     * @param _children children to be evaluated for values
     * @throws EFapsException on error
     */
    protected void add2Values4StrBrws(final Set<String> _selCols,
                                      final Map<String, Attribute> _selAttr,
                                      final boolean _print,
                                      final List<Map<String, Object>> _values,
                                      final List<UIStructurBrowser> _children)
        throws EFapsException
    {
        for (final UIStructurBrowser child : _children) {
            final List<AbstractUIField> cols = child.getColumns();
            final Map<String, Object> map = new HashMap<>();
            for (final AbstractUIField uiField : cols) {
                if (_selCols.contains(uiField.getFieldConfiguration().getName())) {
                    Object value = _print ? uiField.getPickListValue()
                                    : uiField.getCompareValue() != null
                                        ? uiField.getCompareValue() : uiField.getPickListValue();
                    if (value instanceof DateTime) {
                        value = ((DateTime) value).toDate();
                    }
                    map.put(uiField.getFieldConfiguration().getName(), value);
                    _selAttr.put(uiField.getFieldConfiguration().getName(), null);
                }
            }
            _values.add(map);
            add2Values4StrBrws(_selCols, _selAttr, _print, _values, child.getChildren());
        }
    }

    /**
     * @param _parameter Parameter as passed by the eFaps API
     * @param _values Map of values for the DateSource
     * @return DataSource
     * @throws EFapsException on error
     */
    protected JRDataSource getSource(final Parameter _parameter,
                                     final List<Map<String, Object>> _values)
        throws EFapsException
    {
        return new TableSource(_values);
    }

    /**
     * Get the Style for the different Sections.
     *
     * @param _parameter Parameter as passed by the eFaps API
     * @param _detail Section
     * @return Style for Dynamic JAsper
     * @throws EFapsException on error
     */
    protected StyleBuilder getStyle(final Parameter _parameter,
                                    final Section _detail)
        throws EFapsException
    {
        final StyleBuilder ret;
        switch (_detail) {
            case TITLE:
                ret = DynamicReports.stl.style().setBold(true);
                break;
            case COLUMN:
                ret = DynamicReports.stl.style()
                                .setFontSize(8)
                                .setBorder(Styles.penThin())
                                .setPadding(2);
                break;
            case COLUMNHEADER:
                ret = DynamicReports.stl.style()
                                .setFont(Styles.font().bold().setFontSize(8))
                                .setBorder(Styles.penThin())
                                .setTextAlignment(HorizontalTextAlignment.CENTER, VerticalTextAlignment.MIDDLE)
                                .setBackgroundColor(Color.LIGHT_GRAY);
                break;
            default:
                ret = DynamicReports.stl.style()
                                .setFont(Styles.font().setFontSize(8))
                                .setBorder(Styles.pen1Point());
                break;
        }
        return ret;
    }

    /**
     * Gets the column builder for values.
     *
     * @param _values the values
     * @param _header the header
     * @return the column builder4 values
     */
    protected TextColumnBuilder<?> getColumnBuilder4Values(final List<Map<String, Object>> _values,
                                                           final UITableHeader _header)
    {
        TextColumnBuilder<?> ret = null;
        final Class<?>[] clazzes = new Class[] { BigDecimal.class, Long.class, Integer.class, Date.class,
                                                   Boolean.class };
        int idx = 0;
        Iterator<Map<String, Object>> iter = _values.iterator();
        while (iter.hasNext() && idx < clazzes.length) {
            final Map<String, Object> map = iter.next();
            final Object object = map.get(_header.getFieldName());
            // ignore null values
            if (object != null) {
                // if not assignable swithc to next
                if (!object.getClass().equals(clazzes[idx])) {
                    iter = _values.iterator();
                    idx++;
                }
            }
        }
        if (idx < clazzes.length) {
            checkValues(_values, _header.getFieldName(), clazzes[idx]);
            ret = DynamicReports.col.column(_header.getLabel(), _header.getFieldName(),
                            clazzes[idx]);
        } else {
            ret = DynamicReports.col.column(_header.getLabel(), _header.getFieldName(),
                            DynamicReports.type.stringType());
        }
        return ret;
    }

    /**
     * Check values of a map to return true or false.
     *
     * @param _values map of values.
     * @param _name name of the Attribute.
     * @param _type of the Class.
     * @return ret Boolean.
     */
    protected boolean checkValues(final List<Map<String, Object>> _values,
                                  final String _name,
                                  final Class<?> _type)
    {
        boolean ret = true;
        for (final Map<String, Object> map : _values) {
            final Object object = map.get(_name);
            if (!_type.isInstance(object)) {
                if (_type.equals(Long.class)) {
                    if (object == null) {
                        map.put(_name, 0L);
                    } else {
                        ret = false;
                        break;
                    }
                } else if (_type.equals(Boolean.class)) {
                    if (object == null) {
                        map.put(_name, Boolean.FALSE);
                    } else {
                        ret = false;
                        break;
                    }
                } else if (_type.equals(Date.class)) {
                    if (object instanceof String) {
                        map.put(_name, null);
                    } else {
                        ret = false;
                        break;
                    }
                } else if (_type.equals(BigDecimal.class)) {
                    if (object == null) {
                        map.put(_name, BigDecimal.ZERO);
                    } else if (((String) object).isEmpty()) {
                        map.put(_name, BigDecimal.ZERO);
                    } else {
                        ret = false;
                        break;
                    }
                } else {
                    ret = false;
                    break;
                }
            }
        }
        return ret;
    }

    /**
     * Source for a Table.
     */
    public class TableSource
        implements JRDataSource
    {

        /**
         * Values for the rows.
         */
        private final Iterator<Map<String, Object>> values;

        /**
         * Value for the current row.
         */
        private Map<String, Object> current;

        /**
         * @param _values values for the rows
         */
        public TableSource(final List<Map<String, Object>> _values)
        {
            this.values = _values.iterator();
        }

        /**
         * @see net.sf.jasperreports.engine.JRDataSource#getFieldValue(net.sf.jasperreports.engine.JRField)
         * @param _field Field the value is returned for
         * @return value for the field
         * @throws JRException on error
         */
        @Override
        public Object getFieldValue(final JRField _field)
            throws JRException
        {
            return this.current.get(_field.getName());
        }

        /**
         * @see net.sf.jasperreports.engine.JRDataSource#next()
         * @return true if next
         * @throws JRException on error
         */
        @Override
        public boolean next()
            throws JRException
        {
            final boolean ret = this.values.hasNext();
            if (ret) {
                this.current = this.values.next();
            }
            return ret;
        }
    }
}

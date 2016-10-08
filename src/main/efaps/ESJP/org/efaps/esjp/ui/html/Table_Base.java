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

package org.efaps.esjp.ui.html;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

/**
 * TODO comment!.
 *
 * @author The eFaps Team
 */
@EFapsUUID("973d83d2-d019-4aee-a121-e5d4fe19755d")
@EFapsApplication("eFaps-WebApp")
public abstract class Table_Base
{

    /** The rows. */
    private final List<Row> rows = new ArrayList<>();

    /** The current row. */
    private Row currentRow;

    /** The style. */
    private CharSequence style;

    /** The css class. */
    private CharSequence cssClass;

    /** The attributes. */
    private final Map<String,String> attributes = new HashMap<>();

    /**
     * Adds the attribute.
     *
     * @param _key the key
     * @param _value the value
     * @return the table
     */
    public Table addAttribute(final String _key,
                              final String _value)
    {
        this.attributes.put(_key, _value);
        return (Table) this;
    }

    /**
     * Adds the row.
     *
     * @return the table_ base
     */
    public Table addRow()
    {
        this.currentRow = new Row(this);
        this.rows.add(this.currentRow);
        return (Table) this;
    }

    /**
     * Getter method for the instance variable {@link #rows}.
     *
     * @return value of instance variable {@link #rows}
     */
    public List<Row> getRows()
    {
        return this.rows;
    }

    /**
     * Gets the current row.
     *
     * @return the current row
     */
    public Row getCurrentRow()
    {
        if (this.currentRow == null) {
            addRow();
        }
        return this.currentRow;
    }

    /**
     * Adds the column.
     *
     * @return the table_ base
     */
    public Table addColumn()
    {
        addColumn(null);
        return (Table) this;
    }

    /**
     * Adds the header column.
     *
     * @return the table_ base
     */
    public Table addHeaderColumn()
    {
        addHeaderColumn(null);
        return (Table) this;
    }

    /**
     * Gets the current column.
     *
     * @return the current column
     */
    public Column getCurrentColumn()
    {
        return getCurrentRow().getCurrentColumn();
    }

    /**
     * Adds the column.
     *
     * @param _innerHtml the inner html
     * @return the table_ base
     */
    public Table addColumn(final CharSequence _innerHtml)
    {
        getCurrentRow().addColumn(_innerHtml);
        return (Table) this;
    }

    /**
     * Adds the column.
     *
     * @param _innerHtml the inner html
     * @param _style the style
     * @return the table_ base
     */
    public Table addColumn(final CharSequence _innerHtml,
                           final CharSequence _style)
    {
        getCurrentRow().addColumn(_innerHtml);
        getCurrentColumn().setStyle(_style);
        return (Table) this;
    }

    /**
     * Adds the column.
     *
     * @param _innerHtml the inner html
     * @param _colSpan the col span
     * @return the table_ base
     */
    public Table addColumn(final CharSequence _innerHtml,
                           final int _colSpan)
    {
        getCurrentRow().addColumn(_innerHtml);
        getCurrentColumn().setColSpan(_colSpan);
        return (Table) this;
    }

    /**
     * Adds the column.
     *
     * @param _innerHtml the inner html
     * @param _style the style
     * @param _colSpan the col span
     * @return the table_ base
     */
    public Table addColumn(final CharSequence _innerHtml,
                           final CharSequence _style,
                           final int _colSpan)
    {
        getCurrentRow().addColumn(_innerHtml);
        getCurrentColumn().setColSpan(_colSpan);
        getCurrentColumn().setStyle(_style);
        return (Table) this;
    }

    /**
     * Adds the header column.
     *
     * @param _innerHtml the inner html
     * @return the table_ base
     */
    public Table addHeaderColumn(final CharSequence _innerHtml)
    {
        getCurrentRow().addHeaderColumn(_innerHtml);
        return (Table) this;
    }

    /**
     * Adds the header column.
     *
     * @param _innerHtml the inner html
     * @param _style the style
     * @return the table_ base
     */
    public Table addHeaderColumn(final CharSequence _innerHtml,
                                 final CharSequence _style)
    {
        getCurrentRow().addHeaderColumn(_innerHtml);
        getCurrentColumn().setStyle(_style);
        return (Table) this;
    }

    /**
     * Adds the header column.
     *
     * @param _innerHtml the inner html
     * @param _colSpan the col span
     * @return the table_ base
     */
    public Table addHeaderColumn(final CharSequence _innerHtml,
                                 final int _colSpan)
    {
        getCurrentRow().addHeaderColumn(_innerHtml);
        getCurrentColumn().setColSpan(_colSpan);
        return (Table) this;
    }

    /**
     * Adds the header column.
     *
     * @param _innerHtml the inner html
     * @param _style the style
     * @param _colSpan the col span
     * @return the table_ base
     */
    public Table addHeaderColumn(final CharSequence _innerHtml,
                                 final CharSequence _style,
                                 final int _colSpan)
    {
        getCurrentRow().addHeaderColumn(_innerHtml);
        getCurrentColumn().setColSpan(_colSpan);
        getCurrentColumn().setStyle(_style);
        return (Table) this;
    }

    /**
     * To html.
     *
     * @return the char sequence
     */
    public CharSequence toHtml()
    {
        final StringBuilder ret = new StringBuilder();
        prepare4html();
        ret.append("<table");

        for (final Entry<String, String> entry : this.attributes.entrySet()) {
            ret.append(" ").append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
        }

        if (getStyle() != null) {
            ret.append(" style=\"").append(getStyle()).append("\"");
        }
        if (getCssClass() != null) {
            ret.append(" class=\"").append(getCssClass()).append("\"");
        }
        ret.append(">");
        for (final Row row : getRows()) {
            ret.append(row.toHtml());
        }
        ret.append("</table>");
        return ret;
    }

    /**
     * Prepare4html.
     */
    public void prepare4html()
    {
        final Stack<Column> phs = new Stack<>();
        for (final Row row : this.rows) {
            if (!phs.isEmpty()) {
                final Column phColumn = phs.pop();
                int pos = 0;
                int idx = 0;
                for (final Column column : row.getColumns()) {
                    pos = pos + column.getColSpan();
                    if (column.getPosition() < pos) {
                        break;
                    } else {
                        idx++;
                    }
                }
                phColumn.setRow(row);
                row.getColumns().add(idx, phColumn);
            }
            int pos = 0;
            for (final Column column : row.getColumns()) {
                if (column.getRowSpan() > 1) {
                    for (int i = 1; i < column.getRowSpan(); i++) {
                        final Column placeHolder = new Column(null).setPlaceHolder(true)
                                        .setColSpan(column.getColSpan()).setPosition(pos);
                        phs.add(placeHolder);
                    }
                }
                pos = pos + column.getColSpan();
            }
        }

        // analyse and correct colspan
        final int maxColumns = getMaxColumns();
        for (final Row row : this.rows) {
            row.prepare4html(maxColumns);
        }
    }

    /**
     * Gets the max columns.
     *
     * @return the max columns
     */
    public int getMaxColumns()
    {
        int ret = 0;
        for (final Row row : this.rows) {
            if (row.getColSum() > ret) {
                ret = row.getColSum();
            }
        }
        return ret;
    }

    /**
     * Getter method for the instance variable {@link #style}.
     *
     * @return value of instance variable {@link #style}
     */
    public CharSequence getStyle()
    {
        return this.style;
    }

    /**
     * Setter method for instance variable {@link #style}.
     *
     * @param _style value for instance variable {@link #style}
     * @return the table_ base
     */
    public Table setStyle(final CharSequence _style)
    {
        this.style = _style;
        return (Table) this;
    }

    /**
     * Setter method for instance variable {@link #style}.
     *
     * @param _cssClass the css class
     * @return the table_ base
     */
    public Table setCssClass(final CharSequence _cssClass)
    {
        this.cssClass = _cssClass;
        return (Table) this;
    }

    /**
     * Gets the css class.
     *
     * @return the css class
     */
    public CharSequence getCssClass()
    {
        return this.cssClass;
    }


    /**
     * The Class Row.
     *
     */
    public static class Row
    {

        /** The style. */
        private CharSequence style;

        /** The columns. */
        private final List<Column> columns = new ArrayList<>();

        /** The current column. */
        private Column currentColumn;

        /** The table. */
        private final Table_Base table;

        /**
         * Instantiates a new row.
         *
         * @param _table the table
         */
        public Row(final Table_Base _table)
        {
            this.table = _table;
        }

        /**
         * Adds the column.
         *
         * @return the row
         */
        public Row addColumn()
        {
            addColumn(null);
            return this;
        }

        /**
         * Adds the header column.
         *
         * @return the row
         */
        public Row addHeaderColumn()
        {
            addHeaderColumn(null);
            return this;
        }

        /**
         * Adds the column.
         *
         * @param _innerHtml the inner html
         * @return the row
         */
        public Row addColumn(final CharSequence _innerHtml)
        {
            this.currentColumn = new Column(this);
            if (_innerHtml != null) {
                this.currentColumn.setInnerHtml(_innerHtml);
            }
            this.columns.add(this.currentColumn);
            return this;
        }

        /**
         * Adds the header column.
         *
         * @param _innerHtml the inner html
         * @return the row
         */
        public Row addHeaderColumn(final CharSequence _innerHtml)
        {
            this.currentColumn = new HeaderColumn(this);
            if (_innerHtml != null) {
                this.currentColumn.setInnerHtml(_innerHtml);
            }
            this.columns.add(this.currentColumn);
            return this;
        }

        /**
         * Insert column.
         *
         * @param _index the index
         * @return the column
         */
        public Column insertColumn(final int _index)
        {
            return insertColumn(_index, null);
        }

        /**
         * Insert column.
         *
         * @param _index the index
         * @param _innerHtml the inner html
         * @return the column
         */
        public Column insertColumn(final int _index,
                                   final CharSequence _innerHtml)
        {
            final Column column = new Column(this);
            if (_innerHtml != null) {
                column.setInnerHtml(_innerHtml);
            }
            this.columns.add(_index, column);
            return column;
        }

        /**
         * To html.
         *
         * @return the char sequence
         */
        public CharSequence toHtml()
        {
            final StringBuilder ret = new StringBuilder();
            ret.append("<tr");
            if (getStyle() != null) {
                ret.append(" style=\"").append(getStyle()).append("\"");
            }
            ret.append(">");
            for (final Column column : getColumns()) {
                ret.append(column.toHtml());
            }
            ret.append("</tr>");
            return ret;
        }

        /**
         * Prepare4html.
         *
         * @param _maxColumns the max columns
         */
        public void prepare4html(final int _maxColumns)
        {
            final int sum = getColSum();
            if (sum < _maxColumns) {
                for (final Column column : getColumns()) {
                    if (!column.placeHolder && column.getRowSpan() == 1) {
                        final BigDecimal newColSpan = new BigDecimal(column.getColSpan()).setScale(2)
                                        .divide(new BigDecimal(sum), BigDecimal.ROUND_HALF_UP)
                                        .multiply(new BigDecimal(_maxColumns)).setScale(0, BigDecimal.ROUND_HALF_UP);
                        column.setColSpan(newColSpan.intValue());
                    }
                }
                int dif = _maxColumns - getColSum();
                while (dif != 0) {
                    if (dif > 0) {
                        // still missing add to last
                        final Column lastCol = getColumns().get(getColumns().size() - 1);
                        lastCol.setColSpan(lastCol.getColSpan() + dif);
                    } else if (dif < 0) {
                        // to much remove from first possible
                        int colSum = 0;
                        for (final Column column : getColumns()) {
                            colSum = colSum + column.getColSpan();
                            if (colSum > -dif) {
                                column.setColSpan(column.getColSpan() + dif);
                                break;
                            }
                        }
                    }
                    dif = _maxColumns - getColSum();
                }
            }
        }

        /**
         * Gets the col sum.
         *
         * @return the col sum
         */
        public int getColSum()
        {
            int ret = 0;
            for (final Column column : getColumns()) {
                ret = ret + column.getColSpan();
            }
            return ret;
        }

        /**
         * Gets the col spans.
         *
         * @return the col spans
         */
        public int[] getColSpans()
        {
            final int[] ret = new int[this.columns.size()];
            int i = 0;
            for (final Column column : getColumns()) {
                ret[i] = column.getColSpan();
                i++;
            }
            return ret;
        }

        /**
         * Gets the current column.
         *
         * @return the current column
         */
        public Column getCurrentColumn()
        {
            if (this.currentColumn == null) {
                addColumn();
            }
            return this.currentColumn;
        }

        /**
         * Getter method for the instance variable {@link #columns}.
         *
         * @return value of instance variable {@link #columns}
         */
        public List<Column> getColumns()
        {
            return this.columns;
        }

        /**
         * Setter method for instance variable {@link #currentColumn}.
         *
         * @param _currentColumn value for instance variable
         *            {@link #currentColumn}
         */
        public void setCurrentColumn(final Column _currentColumn)
        {
            this.currentColumn = _currentColumn;
        }

        /**
         * Getter method for the instance variable {@link #style}.
         *
         * @return value of instance variable {@link #style}
         */
        public CharSequence getStyle()
        {
            return this.style;
        }

        /**
         * Setter method for instance variable {@link #style}.
         *
         * @param _style value for instance variable {@link #style}
         * @return the row
         */
        public Row setStyle(final CharSequence _style)
        {
            this.style = _style;
            return this;
        }

        /**
         * Getter method for the instance variable {@link #table}.
         *
         * @return value of instance variable {@link #table}
         */
        public Table_Base getTable()
        {
            return this.table;
        }
    }

    /**
     * The Class HeaderColumn.
     *
     * @author The eFaps Team
     */
    public static class HeaderColumn
        extends Column
    {

        /**
         * Instantiates a new header column.
         *
         * @param _row the row
         */
        public HeaderColumn(final Row _row)
        {
            super(_row);
        }

        @Override
        protected String getTag()
        {
            return "th";
        }
    }

    /**
     * The Class Column.
     *
     * @author The eFaps Team
     */
    public static class Column
    {

        /** The place holder. */
        private boolean placeHolder = false;

        /** The position. */
        private int position = 0;

        /** The col span. */
        private int colSpan = 1;

        /** The row span. */
        private int rowSpan = 1;

        /** The inner html. */
        private CharSequence innerHtml;

        /** The style. */
        private CharSequence style;

        /** The CSS class. */
        private CharSequence cssClass;

        /** The row. */
        private Row row;

        /**
         * Instantiates a new column.
         *
         * @param _row the row
         */
        public Column(final Row _row)
        {
            this.row = _row;
        }

        /**
         * Getter method for the instance variable {@link #innerHtml}.
         *
         * @return value of instance variable {@link #innerHtml}
         */
        public CharSequence getInnerHtml()
        {
            return this.innerHtml;
        }

        /**
         * To html.
         *
         * @return the char sequence
         */
        public CharSequence toHtml()
        {
            final StringBuilder ret = new StringBuilder();
            if (!isPlaceHolder()) {
                ret.append("<").append(getTag());
                if (getColSpan() > 1) {
                    ret.append(" colspan=\"").append(getColSpan()).append("\"");
                }
                if (getRowSpan() > 1) {
                    ret.append(" rowspan=\"").append(getRowSpan()).append("\"");
                }
                if (getStyle() != null) {
                    ret.append(" style=\"").append(getStyle()).append("\"");
                }
                if (getCSSClass() != null) {
                    ret.append(" class=\"").append(getCSSClass()).append("\"");
                }
                ret.append(">")
                    .append(getInnerHtml())
                    .append("</").append(getTag()).append(">");
            }
            return ret;
        }

        /**
         * Gets the tag.
         *
         * @return the tag
         */
        protected String getTag()
        {
            return "td";
        }

        /**
         * Setter method for instance variable {@link #innerHtml}.
         *
         * @param _innerHtml value for instance variable {@link #innerHtml}
         */
        public void setInnerHtml(final CharSequence _innerHtml)
        {
            this.innerHtml = _innerHtml;
        }

        /**
         * Getter method for the instance variable {@link #colspan}.
         *
         * @return value of instance variable {@link #colspan}
         */
        public int getColSpan()
        {
            return this.colSpan;
        }

        /**
         * Setter method for instance variable {@link #colspan}.
         *
         * @param _colspan value for instance variable {@link #colspan}
         * @return the column
         */
        public Column setColSpan(final int _colspan)
        {
            this.colSpan = _colspan;
            return this;
        }

        /**
         * Getter method for the instance variable {@link #rowSpan}.
         *
         * @return value of instance variable {@link #rowSpan}
         */
        public int getRowSpan()
        {
            return this.rowSpan;
        }

        /**
         * Setter method for instance variable {@link #rowSpan}.
         *
         * @param _rowSpan value for instance variable {@link #rowSpan}
         */
        public void setRowSpan(final int _rowSpan)
        {
            this.rowSpan = _rowSpan;
        }

        /**
         * Getter method for the instance variable {@link #placeHolder}.
         *
         * @return value of instance variable {@link #placeHolder}
         */
        public boolean isPlaceHolder()
        {
            return this.placeHolder;
        }

        /**
         * Setter method for instance variable {@link #placeHolder}.
         *
         * @param _placeHolder value for instance variable {@link #placeHolder}
         * @return the column
         */
        public Column setPlaceHolder(final boolean _placeHolder)
        {
            this.placeHolder = _placeHolder;
            return this;
        }

        /**
         * Getter method for the instance variable {@link #position}.
         *
         * @return value of instance variable {@link #position}
         */
        public int getPosition()
        {
            return this.position;
        }

        /**
         * Setter method for instance variable {@link #position}.
         *
         * @param _position value for instance variable {@link #position}
         * @return the column
         */
        public Column setPosition(final int _position)
        {
            this.position = _position;
            return this;
        }

        /**
         * Getter method for the instance variable {@link #style}.
         *
         * @return value of instance variable {@link #style}
         */
        public CharSequence getStyle()
        {
            return this.style;
        }

        /**
         * Setter method for instance variable {@link #style}.
         *
         * @param _style value for instance variable {@link #style}
         * @return the column
         */
        public Column setStyle(final CharSequence _style)
        {
            this.style = _style;
            return this;
        }

        /**
         * Gets the current table.
         *
         * @return the current table
         */
        public Table_Base getCurrentTable()
        {
            return getRow().getTable();
        }

        /**
         * Getter method for the instance variable {@link #row}.
         *
         * @return value of instance variable {@link #row}
         */
        public Row getRow()
        {
            return this.row;
        }

        /**
         * Setter method for instance variable {@link #row}.
         *
         * @param _row value for instance variable {@link #row}
         */
        public void setRow(final Row _row)
        {
            this.row = _row;
        }

        /**
         * Getter method for the instance variable {@link #cssClass}.
         *
         * @return value of instance variable {@link #cssClass}
         */
        public CharSequence getCSSClass()
        {
            return this.cssClass;
        }

        /**
         * Setter method for instance variable {@link #cssClass}.
         *
         * @param _cssClass value for instance variable {@link #cssClass}
         * @return the column
         */
        public Column setCSSClass(final CharSequence _cssClass)
        {
            this.cssClass = _cssClass;
            return this;
        }
    }
}

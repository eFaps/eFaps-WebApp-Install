/*
 * Copyright 2003 - 2009 The eFaps Team
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

package org.efaps.esjp.ui.print;

import org.apache.wicket.RequestCycle;

import org.efaps.admin.datamodel.ui.FieldValue;
import org.efaps.admin.dbproperty.DBProperties;
import org.efaps.admin.event.EventExecution;
import org.efaps.admin.event.Parameter;
import org.efaps.admin.event.Return;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.program.esjp.EFapsRevision;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.db.Context;
import org.efaps.esjp.common.jasperreport.StandartReport;
import org.efaps.ui.wicket.models.objects.UIAbstractPageObject;
import org.efaps.ui.wicket.models.objects.UITable;
import org.efaps.ui.wicket.models.objects.UITableHeader;
import org.efaps.util.EFapsException;

/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
@EFapsUUID("1caa2277-7285-470f-976c-718df3363679")
@EFapsRevision("$Rev$")
public abstract class UserInterface_Base extends StandartReport implements EventExecution
{
    /**
     * Key to the ui object stored in the context.
     */
    public static final String UIOBJECT_CACHEKEY = "eFaps_UIObject4PrintCacheKey";

    /**
     * Execute the export.
     * @param _parameter Parameter as passed form the eFaps API
     * @return Return containing the file
     * @throws EFapsException on error
     */
    @Override
    public Return execute(final Parameter _parameter)
        throws EFapsException
    {
        return new Table().execute(_parameter);
    }

    /**
     * Get the html for the mime field.
     * @param _parameter Parameter as passed form the eFaps API
     * @return html snipplet
     * @throws EFapsException on error
     */
    public Return getMimeFieldValueUI(final Parameter _parameter)
        throws EFapsException
    {

        final FieldValue fieldValue = (FieldValue) _parameter.get(ParameterValues.UIOBJECT);
        final Return ret = new Return();
        final StringBuilder html = new StringBuilder();

        html.append("<input type=\"radio\" checked=\"checked\" name=\"").append(fieldValue.getField().getName())
            .append("\" value=\"pdf\">").append(DBProperties.getProperty("org.efaps.esjp.ui.print.PDF")).append("<br/>")
            .append("<input type=\"radio\" name=\"").append(fieldValue.getField().getName())
            .append("\" value=\"ods\">").append(DBProperties.getProperty("org.efaps.esjp.ui.print.ODS"))
            .append("<br/>")
            .append("<input type=\"radio\" name=\"").append(fieldValue.getField().getName())
            .append("\" value=\"xls\">").append(DBProperties.getProperty("org.efaps.esjp.ui.print.XLS"))
            .append("<br/>");
        ret.put(ReturnValues.SNIPLETT, html.toString());
        return ret;
    }

    /**
     * Get the html for the columns field.
     * @param _parameter Parameter as passed form the eFaps API
     * @return html snipplet
     * @throws EFapsException on error
     */
    public Return getColumnsFieldValueUI(final Parameter _parameter)
        throws EFapsException
    {
        final UIAbstractPageObject uiObject = (UIAbstractPageObject) RequestCycle.get().getResponsePage()
                        .getDefaultModelObject();
        Context.getThreadContext().setSessionAttribute(UserInterface.UIOBJECT_CACHEKEY, uiObject);
        final FieldValue fieldValue = (FieldValue) _parameter.get(ParameterValues.UIOBJECT);
        final Return ret = new Return();
        final StringBuilder html = new StringBuilder();
        if (uiObject instanceof UITable) {
            int i = 1;
            for (final UITableHeader header : ((UITable) uiObject).getHeaders()) {
                html.append("<input type=\"checkbox\" ")
                    .append(header.getLabel().length() < 1 ? "" : "checked=\"checked\"")
                    .append(" name=\"")
                    .append(fieldValue.getField().getName()).append("\" value=\"").append(header.getFieldName())
                    .append("\">").append(i++).append(": ").append(header.getLabel()).append("<br/>");
            }
        }
        ret.put(ReturnValues.SNIPLETT, html.toString());
        return ret;
    }
}

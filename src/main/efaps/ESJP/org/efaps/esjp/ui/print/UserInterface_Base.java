/*
 * Copyright 2003 - 2012 The eFaps Team
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.RequestCycle;
import org.efaps.admin.datamodel.ui.FieldValue;
import org.efaps.admin.datamodel.ui.UIInterface;
import org.efaps.admin.dbproperty.DBProperties;
import org.efaps.admin.event.EventExecution;
import org.efaps.admin.event.Parameter;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.event.Return;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.program.esjp.EFapsRevision;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.AbstractUserInterfaceObject.TargetMode;
import org.efaps.db.Context;
import org.efaps.esjp.common.jasperreport.StandartReport;
import org.efaps.ui.wicket.models.objects.AbstractUIPageObject;
import org.efaps.ui.wicket.models.objects.UIStructurBrowser;
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

        html.append("<select ").append(UIInterface.EFAPSTMPTAG).append(" name=\"")
            .append(fieldValue.getField().getName()).append("\" size=\"1\">")
            .append("<option value=\"pdf\" selected=\"selected\">")
            .append(DBProperties.getProperty("org.efaps.esjp.ui.print.PDF")).append("</option>")
            .append("<option value=\"xls\">").append(DBProperties.getProperty("org.efaps.esjp.ui.print.XLS"))
            .append("</option></select>");
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
        final AbstractUIPageObject uiObject = (AbstractUIPageObject) RequestCycle.get().getResponsePage()
                        .getDefaultModelObject();
        Context.getThreadContext().setSessionAttribute(UserInterface_Base.UIOBJECT_CACHEKEY, uiObject);
        final Return ret = new Return();
        final StringBuilder html = new StringBuilder();
        html.append("<span id=\"eFapsColumns4Report\">");
        html.append(updateColumns(uiObject));
        html.append("</span>");
        ret.put(ReturnValues.SNIPLETT, html.toString());
        return ret;
    }

    /**
     * Method to update the columns to show depending if it's pdf or xls.
     *
     * @param _parameter as passed from eFaps API.
     * @return Return with javascript.
     * @throws EFapsException on error.
     */
    public Return updateColumnsFieldValueUI(final Parameter _parameter) throws EFapsException {
        final Return ret = new Return();
        final List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        final Map<String, String> map = new HashMap<String, String>();
        final String mime = _parameter.getParameterValue("mime");
        final TargetMode print = "xls".equalsIgnoreCase(mime) ? TargetMode.PRINT : TargetMode.VIEW;

        final Object object = Context.getThreadContext().getSessionAttribute(UserInterface_Base.UIOBJECT_CACHEKEY);
        final AbstractUIPageObject page = (AbstractUIPageObject) object;
        page.resetModel();
        page.setMode(print);
        page.execute();

        final StringBuilder html = updateColumns(page);
        final StringBuilder js = new StringBuilder();
        js.append("document.getElementById('eFapsColumns4Report').innerHTML='").append(html).append("';");

        map.put("eFapsFieldUpdateJS", js.toString());
        list.add(map);

        ret.put(ReturnValues.VALUES, list);
        return ret;
    }

    /**
     * Method to get the columns of the table.
     *
     * @param uiObject with the data.
     * @return StringBuilder
     */
    public StringBuilder updateColumns(final AbstractUIPageObject uiObject) {
        final StringBuilder html = new StringBuilder();
        List<UITableHeader> headers = null;
        if (uiObject instanceof UITable) {
            headers = ((UITable) uiObject).getHeaders();
        } else if (uiObject instanceof UIStructurBrowser) {
            headers =  ((UIStructurBrowser) uiObject).getHeaders();
        }
        if (headers != null) {
            int i = 1;
            for (final UITableHeader header : headers) {
                html.append("<input type=\"checkbox\" ")
                    .append(header.getLabel().length() < 1 ? "" : "checked=\"checked\"")
                    .append(" name=\"")
                    .append("columns").append("\" value=\"").append(header.getFieldName())
                    .append("\">").append(i++).append(": ").append(header.getLabel()).append("<br/>");
            }
        }
        return html;
    }
}

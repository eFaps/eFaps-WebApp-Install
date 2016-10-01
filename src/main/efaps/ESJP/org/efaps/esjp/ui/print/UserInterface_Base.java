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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.wicket.Page;
import org.apache.wicket.PageReference;
import org.apache.wicket.ajax.AjaxRequestHandler;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.RequestCycle;
import org.efaps.admin.dbproperty.DBProperties;
import org.efaps.admin.event.EventExecution;
import org.efaps.admin.event.Parameter;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.event.Return;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.AbstractUserInterfaceObject.TargetMode;
import org.efaps.db.Context;
import org.efaps.esjp.common.jasperreport.StandartReport;
import org.efaps.esjp.common.uiform.Field_Base.DropDownPosition;
import org.efaps.ui.wicket.models.objects.AbstractUIPageObject;
import org.efaps.ui.wicket.models.objects.UIStructurBrowser;
import org.efaps.ui.wicket.models.objects.UITable;
import org.efaps.ui.wicket.models.objects.UITableHeader;
import org.efaps.ui.wicket.util.EFapsKey;
import org.efaps.util.EFapsException;

/**
 * TODO comment!
 *
 * @author The eFaps Team
 */
@EFapsUUID("1caa2277-7285-470f-976c-718df3363679")
@EFapsApplication("eFaps-WebApp")
public abstract class UserInterface_Base
    extends StandartReport
    implements EventExecution
{

    /**
     * Key to the ui object stored in the context.
     */
    public static final String UIOBJECT_CACHEKEY = "eFaps_UIObject4PrintCacheKey";

    /** The Constant MIME_MAP. */
    private static final Map<String, String> MIME_MAP = new LinkedHashMap<>();
    static {
        UserInterface_Base.MIME_MAP.put("pdf", "org.efaps.esjp.ui.print.PDF");
        UserInterface_Base.MIME_MAP.put("xls", "org.efaps.esjp.ui.print.XLS");
    };

    /**
     * Execute the export.
     *
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
     *
     * @param _parameter Parameter as passed form the eFaps API
     * @return html snipplet
     * @throws EFapsException on error
     */
    public Return getMimeFieldValueUI(final Parameter _parameter)
        throws EFapsException
    {
        final List<DropDownPosition> values = new ArrayList<>();

        final Map<?, ?> props = (Map<?, ?>) _parameter.get(ParameterValues.PROPERTIES);
        final String defaultMime = (String) props.get("DefaultMime");
        final Set<String> ignoreSet = new HashSet<>();
        if (props.containsKey("IgnoreMime")) {
            final String ignoreMime = (String) props.get("IgnoreMime");
            final String[] ignoreMimeStr = ignoreMime.split(";");
            for (final String ignore : ignoreMimeStr) {
                ignoreSet.add(ignore.toLowerCase());
            }
        }

        for (final Entry<String, String> mimeEntry : UserInterface_Base.MIME_MAP.entrySet()) {
            if (!ignoreSet.contains(mimeEntry.getKey())) {
                final DropDownPosition position = new DropDownPosition(mimeEntry.getKey(),
                                DBProperties.getProperty(mimeEntry.getValue()));
                if (defaultMime != null && mimeEntry.getKey().equalsIgnoreCase(defaultMime)) {
                    position.setSelected(true);
                }
                values.add(position);
            }
        }

        final Return ret = new Return();
        ret.put(ReturnValues.VALUES, values);
        return ret;
    }

    /**
     * Get the html for the columns field.
     *
     * @param _parameter Parameter as passed form the eFaps API
     * @return html snipplet
     * @throws EFapsException on error
     */
    public Return getColumnsFieldValueUI(final Parameter _parameter)
        throws EFapsException
    {
        final Return ret = new Return();
        final StringBuilder html = new StringBuilder();
        html.append("<span id=\"eFapsColumns4Report\">");

        final IRequestHandler handler = RequestCycle.get().getRequestHandlerScheduledAfterCurrent();
        if (handler instanceof AjaxRequestHandler) {
            final Page page = ((AjaxRequestHandler) handler).getPage();
            final PageReference reference = page.getPageReference();
            Context.getThreadContext().setSessionAttribute(UserInterface_Base.UIOBJECT_CACHEKEY, reference);
            final AbstractUIPageObject uiObject = (AbstractUIPageObject) page.getDefaultModelObject();
            html.append(updateColumns(uiObject));
        }
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
    public Return updateColumnsFieldValueUI(final Parameter _parameter)
        throws EFapsException
    {
        final Return ret = new Return();
        if (_parameter.getParameterValue("columns") != null) {
            final List<Map<String, String>> list = new ArrayList<>();
            final Map<String, String> map = new HashMap<>();
            final String mime = _parameter.getParameterValue("mime");
            final TargetMode print = "xls".equalsIgnoreCase(mime) ? TargetMode.PRINT : TargetMode.VIEW;

            final PageReference reference = (PageReference) Context.getThreadContext().getSessionAttribute(
                            UserInterface_Base.UIOBJECT_CACHEKEY);

            final AbstractUIPageObject object = (AbstractUIPageObject) reference.getPage().getDefaultModelObject();
            if (!print.equals(object.getMode())) {
                object.resetModel();
                object.setMode(print);
                object.execute();
            }

            final StringBuilder html = updateColumns(object);
            final StringBuilder js = new StringBuilder();
            js.append("document.getElementById('eFapsColumns4Report').innerHTML='").append(html).append("';");
            map.put(EFapsKey.FIELDUPDATE_JAVASCRIPT.getKey(), js.toString());
            list.add(map);

            ret.put(ReturnValues.VALUES, list);
        }
        return ret;
    }

    /**
     * Method to get the columns of the table.
     *
     * @param _uiObject the ui object
     * @return StringBuilder
     */
    public StringBuilder updateColumns(final AbstractUIPageObject _uiObject)
    {
        final StringBuilder html = new StringBuilder();
        List<UITableHeader> headers = null;
        if (_uiObject instanceof UITable) {
            headers = ((UITable) _uiObject).getHeaders();
        } else if (_uiObject instanceof UIStructurBrowser) {
            headers = ((UIStructurBrowser) _uiObject).getHeaders();
        }
        if (headers != null) {
            int i = 1;
            for (final UITableHeader header : headers) {
                html.append("<input type=\"checkbox\" ").append(header.getLabel().length() < 1 ? ""
                                : "checked=\"checked\"").append(" name=\"").append("columns").append("\" value=\"")
                                .append(header.getFieldName()).append("\">").append(i++).append(": ").append(header
                                                .getLabel()).append("<br/>");
            }
        }
        return html;
    }
}

/*
 * Copyright 2003 - 2014 The eFaps Team
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


package org.efaps.esjp.common.uiform;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.efaps.admin.datamodel.Attribute;
import org.efaps.admin.datamodel.Status;
import org.efaps.admin.event.Parameter;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.event.Return;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.program.esjp.EFapsRevision;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.AbstractUserInterfaceObject.TargetMode;
import org.efaps.db.Instance;
import org.efaps.db.PrintQuery;
import org.efaps.esjp.common.AbstractCommon;
import org.efaps.ui.wicket.models.objects.UIForm;
import org.efaps.util.EFapsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
@EFapsUUID("6a38e185-4797-4a8a-8356-613d940404b7")
@EFapsRevision("$Rev$")
public abstract class Evaluate_Base
    extends AbstractCommon
{
    /**
     * Logging instance used in this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(Evaluate.class);

    /**
     * Used to select one row of a table and sets the instance selected,
     * so it can be used by a form.
     * @param _parameter Parameter as passed by the eFaps API
     * @return Return containing instance
     * @throws EFapsException on error
     */
    public Return execute(final Parameter _parameter)
        throws EFapsException
    {
        final String oid = _parameter.getParameterValue("selectedRow");
        final Instance instance = Instance.get(oid);
        setMode(_parameter, instance);
        final Return ret = new Return();
        ret.put(ReturnValues.INSTANCE, instance);
        return ret;
    }

    /**
    * Used to select one row of a table and sets the instance selected,
    * so it can be used by a form.
    * @param _parameter Parameter as passed by the eFaps API
    * @return Return containing instance
    * @throws EFapsException on error
    */
   public Return validateStatus(final Parameter _parameter)
       throws EFapsException
   {
       final String oid = _parameter.getParameterValue("selectedRow");
       Instance instance = Instance.get(oid);
       if (containsProperty(_parameter, "Select4Instance")) {
           final String select = getProperty(_parameter, "Select4Instance");
           final PrintQuery print = new PrintQuery(instance);
           print.addSelect(select);
           print.executeWithoutAccessCheck();
           instance = print.getSelect(select);
       }

       final List<Status> statusList = getStatusListFromProperties(_parameter);
       final Attribute statusAttr = instance.getType().getStatusAttribute();
       final PrintQuery print = new PrintQuery(instance);
       print.addAttribute(statusAttr);
       boolean valid = false;
       if (print.execute()) {
           final Long statusid = print.getAttribute(statusAttr);
           for (final Status status : statusList) {
               if (status.getId() == statusid) {
                   valid = true;
                   break;
               }
           }
       }
       final Return ret = new Return();
       if (valid) {
           if("true".equalsIgnoreCase(getProperty(_parameter, "SetInstance"))) {
               ret.put(ReturnValues.INSTANCE, instance);
           } else {
               ret.put(ReturnValues.INSTANCE, _parameter.getInstance());
           }
       }
       return ret;
   }

    /**
     * To be overwritten by implementations.
     * @param _parameter Parameter as passed by the eFaps API
     * @param _instance  instance that was evaluated
     * @throws EFapsException on error
     */
    protected void setMode(final Parameter _parameter,
                           final Instance _instance)
        throws EFapsException
    {
        final UIForm uiForm = (UIForm) _parameter.get(ParameterValues.CLASS);
        Evaluate_Base.LOG.debug("Setting mode for {}", uiForm);
        final Map<Integer, String> status = analyseProperty(_parameter, "Status");
        if (_instance != null && _instance.isValid() && _instance.getType().isCheckStatus() && !status.isEmpty()) {
            boolean access = false;
            final Attribute statusAttr = _instance.getType().getStatusAttribute();
            final PrintQuery print = new PrintQuery(_instance);
            print.addAttribute(statusAttr);
            if (print.execute()) {
                final Long statusID = print.<Long>getAttribute(statusAttr);
                final Map<Integer, String> statusGrp = analyseProperty(_parameter, "StatusGroup");
                for (final Entry<Integer, String> entry : status.entrySet()) {
                    Status stat;
                    if (statusGrp.containsKey(entry.getKey())) {
                        stat = Status.find(statusGrp.get(entry.getKey()), entry.getValue());
                    } else {
                        stat = Status.find(statusAttr.getLink().getUUID(), entry.getValue());
                    }
                    if (stat != null && statusID.equals(stat.getId())) {
                        access = true;
                        break;
                    }
                }
            }
            if (!access) {
                uiForm.setMode(TargetMode.PRINT);
            }
        }
    }
}

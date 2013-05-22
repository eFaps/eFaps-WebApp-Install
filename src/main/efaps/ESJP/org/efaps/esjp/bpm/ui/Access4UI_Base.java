/*
 * Copyright 2003 - 2013 The eFaps Team
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


package org.efaps.esjp.bpm.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.efaps.admin.event.Parameter;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.event.Return;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.program.esjp.EFapsRevision;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.db.Context;
import org.efaps.esjp.bpm.BProcess;
import org.efaps.util.EFapsException;
import org.jbpm.task.User;
import org.jbpm.task.query.TaskSummary;


/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
@EFapsUUID("2e4b2a72-69f8-492d-a1ca-c1384109316a")
@EFapsRevision("$Rev$")
public abstract class Access4UI_Base
{

    public Return check4TaskClaim(final Parameter _parameter)
        throws EFapsException
    {
        final Return ret = new Return();
        final Map<?, ?> properties = (Map<?, ?>) _parameter.get(ParameterValues.PROPERTIES);

        final List<org.jbpm.task.Status> status = new ArrayList<org.jbpm.task.Status>();
        status.add(org.jbpm.task.Status.Reserved);

        final BProcess process = new BProcess();

        final List<TaskSummary> taskSummaries = process.getTaskSummary4Instance(_parameter, null, status);
        boolean access = false;
        for (final TaskSummary taskSummary : taskSummaries) {
            final User owner = taskSummary.getActualOwner();
            if (owner.getId().equals(Context.getThreadContext().getPerson().getUUID().toString())) {
                access = true;
                break;
            }
        }
        final boolean inverse = "true".equalsIgnoreCase((String) properties.get("Inverse"));
        if ((!inverse && access) || (inverse && !access)) {
            ret.put(ReturnValues.TRUE, true);
        }
        return ret;
    }
}

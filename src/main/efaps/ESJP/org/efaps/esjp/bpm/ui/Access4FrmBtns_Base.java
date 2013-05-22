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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.efaps.admin.event.Parameter;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.event.Return;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.program.esjp.EFapsRevision;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.db.Context;
import org.efaps.util.EFapsException;
import org.jbpm.task.Status;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.Operation;


/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
@EFapsUUID("0d2afb83-5e21-4d32-8b83-741bb08b4a59")
@EFapsRevision("$Rev$")
public abstract class Access4FrmBtns_Base
{

    public Return execute(final Parameter _parameter)
        throws EFapsException
    {
        final Return ret = new Return();
        final Set<Operation> operations = new HashSet<Operation>();
        ret.put(ReturnValues.VALUES, operations);
        final Map<?, ?> properties = (Map<?, ?>) _parameter.get(ParameterValues.PROPERTIES);

        final boolean requireClaim = "true".equalsIgnoreCase((String) properties.get("RequireClaim"));

        final TaskSummary taskSummary = (TaskSummary) _parameter.get(ParameterValues.BPM_TASK);

        if (requireClaim) {
            if (Status.Ready.equals(taskSummary.getStatus())) {
                operations.add(Operation.Claim);
            } else if (Status.Reserved.equals(taskSummary.getStatus())) {
                if (taskSummary.getActualOwner().getId()
                                .equals(Context.getThreadContext().getPerson().getUUID().toString())) {
                    operations.add(Operation.Complete);
                    operations.add(Operation.Fail);
                } else {
                    operations.add(Operation.Start);
                }
            }
        }
        return ret;
    }
}

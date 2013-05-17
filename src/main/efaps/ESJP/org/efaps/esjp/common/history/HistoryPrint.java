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

package org.efaps.esjp.common.history;

import org.efaps.admin.event.EventExecution;
import org.efaps.admin.event.Parameter;
import org.efaps.admin.event.Return;
import org.efaps.admin.program.esjp.EFapsRevision;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.db.Instance;
import org.efaps.db.QueryBuilder;
import org.efaps.esjp.ci.CICommon;
import org.efaps.esjp.common.uitable.MultiPrint;
import org.efaps.util.EFapsException;


/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
@EFapsUUID("6a954943-d7b8-45e2-b211-3b5f4731e64a")
@EFapsRevision("$Rev$")
public class HistoryPrint
    implements EventExecution
{
    /**
     * @param _parameter Parameter as passed by the eFaps API
     * @return instances
     */
    public Return execute(final Parameter _parameter)
        throws EFapsException
    {
        final MultiPrint multi = new MultiPrint() {
            @Override
            protected void add2QueryBldr(final Parameter _parameter,
                                         final QueryBuilder _queryBldr)
                throws EFapsException
            {
                final Instance inst = _parameter.getInstance();
                _queryBldr.addWhereAttrEqValue(CICommon.HistoryAbstract.GeneralInstanceLink, inst.getGeneralId());
            }
        };
        return multi.execute(_parameter);
    }
}

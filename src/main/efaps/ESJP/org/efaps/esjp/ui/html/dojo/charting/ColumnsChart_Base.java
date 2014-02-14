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


package org.efaps.esjp.ui.html.dojo.charting;

import org.efaps.admin.program.esjp.EFapsRevision;
import org.efaps.admin.program.esjp.EFapsUUID;


/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
@EFapsUUID("3613d031-fb22-4aad-80c6-b71755065f33")
@EFapsRevision("$Rev$")
public abstract class ColumnsChart_Base
    extends AbstractChart<Data>
{

    @Override
    public CharSequence getRequireJS()
    {
        return ",\"dojox/charting/plot2d/Columns\"";
    }

    @Override
    public CharSequence getParameterJS()
    {
        return ", Columns";
    }

    @Override
    public CharSequence getAddPlotJS()
    {
        final StringBuilder ret = new StringBuilder()
            .append(" chart.addPlot(\"default\", {\n")
            .append(" type: Columns\n")
            .append(" });\n");
        return ret;
    }
}

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
@EFapsUUID("63e02ff1-f75f-444a-8932-25af17159306")
@EFapsRevision("$Rev$")
public abstract class Data_Base<S extends AbstractData_Base<S>>
    extends AbstractData<S>
{

    private boolean simple = true;

    @Override
    public CharSequence getJavaScript()
    {
        final StringBuilder ret = new StringBuilder();
        if (simple) {
            ret.append(getYValue());
        } else {
            addConfig("x", getXValue());
            addConfig("y", getYValue());
            ret.append(getConfigJS());
        }

        return ret;
    }

    public boolean isSimple()
    {
        return simple;
    }

    public S setSimple(final boolean simple)
    {
        this.simple = simple;
        return getThis();
    }

}
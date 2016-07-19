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


package org.efaps.esjp.ui.html.dojo.charting;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;


/**
 * TODO comment!
 *
 * @author The eFaps Team
 */
@EFapsUUID("63e02ff1-f75f-444a-8932-25af17159306")
@EFapsApplication("eFaps-WebApp")
public abstract class Data_Base<S extends AbstractData_Base<S>>
    extends AbstractData<S>
{

    /** The simple. */
    private boolean simple = true;

    @Override
    public CharSequence getJavaScript()
    {
        final StringBuilder ret = new StringBuilder();
        if (this.simple) {
            ret.append(getYValue());
        } else {
            addConfig("x", getXValue());
            addConfig("y", getYValue());
            ret.append(getConfigJS());
        }

        return ret;
    }

    /**
     * Checks if is simple.
     *
     * @return the simple
     */
    public boolean isSimple()
    {
        return this.simple;
    }

    /**
     * Sets the simple.
     *
     * @param simple the simple
     * @return the s
     */
    public S setSimple(final boolean simple)
    {
        this.simple = simple;
        return getThis();
    }

}

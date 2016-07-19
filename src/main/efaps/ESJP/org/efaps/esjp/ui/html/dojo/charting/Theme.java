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
@EFapsUUID("96774d59-6a26-46d1-b14f-45024ea4f166")
@EFapsApplication("eFaps-WebApp")
public enum Theme
{

    /** The julie. */
    JULIE("dojox/charting/themes/Julie");


    /** The amd. */
    private final String amd;

    /**
     * Instantiates a new theme.
     *
     * @param _amd the amd
     */
    private Theme(final String _amd) {
        this.amd = _amd;
    }

    /**
     * Getter method for the instance variable {@link #amd}.
     *
     * @return value of instance variable {@link #amd}
     */
    public String getAmd()
    {
        return this.amd;
    }
}

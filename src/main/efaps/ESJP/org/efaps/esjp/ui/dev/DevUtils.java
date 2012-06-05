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


package org.efaps.esjp.ui.dev;

import org.apache.wicket.protocol.http.WebApplication;
import org.efaps.admin.event.Parameter;
import org.efaps.admin.event.Return;
import org.efaps.admin.program.esjp.EFapsRevision;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.user.Role;
import org.efaps.util.EFapsException;


/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
@EFapsUUID("977d266e-c1c1-41ab-ae74-6ff2b647daab")
@EFapsRevision("$Rev$")
public class DevUtils
{
    //http://localhost:8888/eFaps/wicket/bookmarkable/org.apache.wicket.devutils.DevUtilsPage
    public Return toggleDevUtils(final Parameter _parameter)
        throws EFapsException
    {
        if (Role.get("Administration").isAssigned()) {
            WebApplication.get().getDebugSettings().setDevelopmentUtilitiesEnabled(
                            !WebApplication.get().getDebugSettings().isDevelopmentUtilitiesEnabled());
        }
        return new Return();
    }
}

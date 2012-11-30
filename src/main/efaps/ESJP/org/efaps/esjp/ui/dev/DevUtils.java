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

import java.util.UUID;

import org.apache.wicket.MetaDataKey;
import org.apache.wicket.devutils.debugbar.DebugBar;
import org.apache.wicket.devutils.debugbar.InspectorDebugPanel;
import org.apache.wicket.devutils.debugbar.PageSizeDebugPanel;
import org.apache.wicket.devutils.debugbar.SessionSizeDebugPanel;
import org.apache.wicket.devutils.debugbar.VersionDebugContributor;
import org.apache.wicket.protocol.http.WebApplication;
import org.efaps.admin.event.Parameter;
import org.efaps.admin.event.Return;
import org.efaps.admin.program.esjp.EFapsRevision;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.user.Role;
import org.efaps.util.EFapsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
    /**
     * Logger for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(DevUtils.class);

    /**
     * Metakey to store if the Debugbars were added or not.
     */
    private static final MetaDataKey<Boolean> DEBUG_BAR_CONTRIBUTED = new MetaDataKey<Boolean>()
    {
        private static final long serialVersionUID = 1L;
    };

    public Return toggleDevUtils(final Parameter _parameter)
        throws EFapsException
    {
        //Administration
        if (Role.get(UUID.fromString("1d89358d-165a-4689-8c78-fc625d37aacd")).isAssigned()) {
            final WebApplication application = WebApplication.get();
            DevUtils.LOG.info("Toggle devutils: " + !application.getDebugSettings().isDevelopmentUtilitiesEnabled());
            application.getDebugSettings().setDevelopmentUtilitiesEnabled(
                            !application.getDebugSettings().isDevelopmentUtilitiesEnabled());

            // check to add them only once
            if (application.getMetaData(DevUtils.DEBUG_BAR_CONTRIBUTED) == null) {
                DebugBar.registerContributor(VersionDebugContributor.DEBUG_BAR_CONTRIB, application);
                DebugBar.registerContributor(InspectorDebugPanel.DEBUG_BAR_CONTRIB, application);
                DebugBar.registerContributor(SessionSizeDebugPanel.DEBUG_BAR_CONTRIB, application);
                DebugBar.registerContributor(PageSizeDebugPanel.DEBUG_BAR_CONTRIB, application);
                application.setMetaData(DevUtils.DEBUG_BAR_CONTRIBUTED, true);
            }
        }
        return new Return();
    }
}

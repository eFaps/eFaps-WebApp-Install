/*
 * Copyright 2003 - 2020 The eFaps Team
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
package org.efaps.esjp.ui.util;

import java.util.UUID;

import org.efaps.admin.common.SystemConfiguration;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.api.annotation.EFapsSysConfAttribute;
import org.efaps.api.annotation.EFapsSystemConfiguration;
import org.efaps.esjp.admin.common.systemconfiguration.BooleanSysConfAttribute;
import org.efaps.esjp.admin.common.systemconfiguration.StringSysConfAttribute;
import org.efaps.util.cache.CacheReloadException;

@EFapsUUID("6833238a-a294-4686-a08b-ca78be5800f5")
@EFapsApplication("eFaps-WebApp")
@EFapsSystemConfiguration("50a65460-2d08-4ea8-b801-37594e93dad5")
public class WebApp
{
    /** The base. */
    public static final String BASE = "org.efaps.ui.";

    /** Sales-Configuration. */
    public static final UUID SYSCONFUUID = UUID.fromString("50a65460-2d08-4ea8-b801-37594e93dad5");

    /** See description. */
    @EFapsSysConfAttribute
    public static final StringSysConfAttribute MAINTOOLBAR = new StringSysConfAttribute()
                    .sysConfUUID(SYSCONFUUID)
                    .key(BASE + "MainToolBar")
                    .defaultValue("87001cc3-c45c-44de-b8f1-776df507f268")
                    .description("MainToolbar to be used by the SPA");

    @EFapsSysConfAttribute
    public static final BooleanSysConfAttribute DASHBOARD_ACTIVE = new BooleanSysConfAttribute()
                    .sysConfUUID(SYSCONFUUID)
                    .key(BASE + "dashboard.Active")
                    .defaultValue(false)
                    .description("Activate the dashboard");

    /**
     * @return the SystemConfigruation for Sales
     * @throws CacheReloadException on error
     */
    public static SystemConfiguration getSysConfig()
        throws CacheReloadException
    {
        return SystemConfiguration.get(WebApp.SYSCONFUUID);
    }

}

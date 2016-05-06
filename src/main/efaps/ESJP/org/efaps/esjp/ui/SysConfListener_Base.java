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

package org.efaps.esjp.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.esjp.admin.common.systemconfiguration.BooleanSysConfAttribute;
import org.efaps.esjp.admin.common.systemconfiguration.ISysConfAttribute;
import org.efaps.esjp.admin.common.systemconfiguration.ISysConfLink;
import org.efaps.esjp.admin.common.systemconfiguration.ISysConfListener;
import org.efaps.esjp.admin.common.systemconfiguration.IntegerSysConfAttribute;
import org.efaps.esjp.admin.common.systemconfiguration.PropertiesSysConfAttribute;
import org.efaps.esjp.admin.common.systemconfiguration.StringSysConfAttribute;
import org.efaps.esjp.admin.common.systemconfiguration.SysConfResourceConfig;
import org.efaps.ui.wicket.util.Configuration;
import org.efaps.ui.wicket.util.Configuration.ConfigAttribute;
import org.efaps.util.cache.CacheReloadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class SysConfListener_Base.
 *
 * @author The eFaps Team
 */
@EFapsUUID("0ce6113f-5f03-4348-bbd5-6ce7660717e5")
@EFapsApplication("eFaps-WebApp")
public abstract class SysConfListener_Base
    implements ISysConfListener
{
    /**
     * Logging instance used in this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(SysConfResourceConfig.class);

    @Override
    public int getWeight()
    {
        return 0;
    }

    @Override
    public void addAttributes(final Map<String, List<ISysConfAttribute>> _uuid2attr)
        throws CacheReloadException
    {
        List<ISysConfAttribute> attributes;
        if (_uuid2attr.containsKey(Configuration.getSysConfig().getUUID().toString())) {
            attributes = _uuid2attr.get(Configuration.getSysConfig().getUUID().toString());
        } else {
            attributes = new ArrayList<ISysConfAttribute>();
            _uuid2attr.put(Configuration.getSysConfig().getUUID().toString(), attributes);
        }
        LOG.info("Found SystemConfiguration: {} - {}",
                        Configuration.getSysConfig().getName(), Configuration.getSysConfig().getUUID());
        for (final ConfigAttribute val : Configuration.ConfigAttribute.values()) {
            if (val.isSystem()) {
                if (val.getAttrClass().isAssignableFrom(Integer.class)) {
                    final IntegerSysConfAttribute attr = new IntegerSysConfAttribute()
                                    .sysConfUUID(Configuration.getSysConfig().getUUID())
                                    .key(val.getKey())
                                    .defaultValue(Integer.valueOf(val.getDefaultvalue()))
                                    .description(val.getDescription());
                    attributes.add(attr);
                    LOG.info("    Found Attribute: {}", attr);
                } else if (val.getAttrClass().isAssignableFrom(Boolean.class)) {
                    final BooleanSysConfAttribute attr = new BooleanSysConfAttribute()
                                    .sysConfUUID(Configuration.getSysConfig().getUUID())
                                    .key(val.getKey())
                                    .defaultValue(Boolean.parseBoolean(val.getDefaultvalue()))
                                    .description(val.getDescription());
                    attributes.add(attr);
                    LOG.info("    Found Attribute: {}", attr);
                } else if (val.getAttrClass().isAssignableFrom(Properties.class)) {
                    final PropertiesSysConfAttribute attr = new PropertiesSysConfAttribute()
                                    .sysConfUUID(Configuration.getSysConfig().getUUID())
                                    .key(val.getKey())
                                    .description(val.getDescription());
                    attributes.add(attr);
                    LOG.info("    Found Attribute: {}", attr);
                } else if (val.getAttrClass().isAssignableFrom(String.class)) {
                    final StringSysConfAttribute attr = new StringSysConfAttribute()
                                    .sysConfUUID(Configuration.getSysConfig().getUUID())
                                    .key(val.getKey())
                                    .defaultValue(val.getDefaultvalue())
                                    .description(val.getDescription());
                    attributes.add(attr);
                    LOG.info("    Found Attribute: {}", attr);
                }
            }
        }
    }

    @Override
    public void addLinks(final Map<String, List<ISysConfLink>> _uuid2link)
    {
        // nothing to add yet
    }
}

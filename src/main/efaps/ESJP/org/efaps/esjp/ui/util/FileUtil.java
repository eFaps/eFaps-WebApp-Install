/*
 * Copyright © 2003 - 2024 The eFaps Team (-)
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
 */
package org.efaps.esjp.ui.util;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.util.cache.InfinispanCache;
import org.infinispan.Cache;

@EFapsUUID("08edc3d3-15e3-46fe-9b83-f83204a79fbd")
@EFapsApplication("eFaps-WebApp")
public class FileUtil
{

    public static final String CACHENAME = FileUtil.class.getName() + ".Cache";

    private FileUtil()
    {
    }

    public static String put(final File file)
    {
        final var key = RandomStringUtils.randomAlphanumeric(8);
        getCache().put(key, file, 1, TimeUnit.HOURS);
        return key;
    }

    public static File get(final String key)
    {
        return getCache().get(key);
    }

    private static Cache<String, File> getCache()
    {
        if (!InfinispanCache.get().exists(FileUtil.CACHENAME)) {
            InfinispanCache.get().initCache(FileUtil.CACHENAME);
        }
        return InfinispanCache.get().<String, File>getCache(FileUtil.CACHENAME);
    }
}

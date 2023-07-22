/*
 * Copyright 2003 - 2023 The eFaps Team
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
package org.efaps.esjp.ui.rest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.efaps.admin.EFapsSystemConfiguration;
import org.efaps.admin.index.Index;
import org.efaps.admin.index.Searcher;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsClassLoader;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.util.EFapsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EFapsUUID("e9be9f49-453e-4a81-abba-e19ce814a3ed")
@EFapsApplication("eFaps-WebApp")
@Path("/ui/index")
public class IndexSearchController
{
    private static final Logger LOG = LoggerFactory.getLogger(IndexSearchController.class);

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public Response search(@QueryParam("query") final String query)
        throws EFapsException
    {
        final var search = Index.getSearch();
        search.setQuery(getQuery(query));
        final var result = Searcher.search(search);
        LOG.info("Search result: {}", result);
        final Response ret = Response.ok()
                        .entity(result)
                        .build();
        return ret;
    }

    protected String getQuery(String query)
    {
        final StringBuilder ret = new StringBuilder();
        try {
            final String clazzname;
            if (EFapsSystemConfiguration.get().containsAttributeValue("org.efaps.kernel.index.QueryBuilder")) {
                clazzname = EFapsSystemConfiguration.get().getAttributeValue("org.efaps.kernel.index.QueryBuilder");
            } else {
                clazzname = "org.efaps.esjp.admin.index.LucenceQueryBuilder";
            }
            final Class<?> clazz = Class.forName(clazzname, false, EFapsClassLoader.getInstance());
            final Object obj = clazz.getConstructor().newInstance();
            final Method method = clazz.getMethod("getQuery4DimValues", String.class, List.class, List.class);
            final Object newQuery = method.invoke(obj, query, Collections.emptyList(), Collections.emptyList());
            ret.append(newQuery);
        } catch (final EFapsException | ClassNotFoundException | InstantiationException | IllegalAccessException
                        | NoSuchMethodException | SecurityException | IllegalArgumentException
                        | InvocationTargetException e) {
            LOG.error("Catched", e);
        }
        return ret.toString();
    }

}

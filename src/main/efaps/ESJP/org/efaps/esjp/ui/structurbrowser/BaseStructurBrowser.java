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
package org.efaps.esjp.ui.structurbrowser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.efaps.admin.event.EventExecution;
import org.efaps.admin.event.Parameter;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.event.Return;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.AbstractCommand;
import org.efaps.admin.ui.field.Field;
import org.efaps.db.Instance;
import org.efaps.eql.EQL;
import org.efaps.esjp.common.AbstractCommon;
import org.efaps.esjp.ui.rest.dto.StructurBrowserEntryDto;
import org.efaps.esjp.ui.util.SelectUtils;
import org.efaps.esjp.ui.util.ValueUtils;
import org.efaps.util.EFapsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EFapsUUID("5825864f-32ef-4a61-a71a-e1321d127299")
@EFapsApplication("eFaps-WebApp")
public class BaseStructurBrowser
    extends AbstractCommon
    implements EventExecution
{

    private static final Logger LOG = LoggerFactory.getLogger(BaseStructurBrowser.class);

    @Override
    public Return execute(final Parameter parameter)
        throws EFapsException
    {
        final Return ret = new Return();
        final var typeKeys = analyseProperty(parameter, "Type").values();

        final var types = typeKeys.stream().map(ValueUtils::evalType).collect(Collectors.toList());

        final var print = EQL.builder().print().query(typeKeys.toArray(String[]::new));
        if (containsProperty(parameter, "LinkFromIsNull")) {
            print.where().attr(getProperty(parameter, "LinkFromIsNull")).isNull();
        }

        final var fields = getFields(parameter);

        final var select = print.select();
        SelectUtils.add2Select(select, types, fields);

        final var eval = select.evaluate();

        final var entries = new ArrayListValuedHashMap<Instance, Entry>();
        final var nextLevelParents = new HashSet<Instance>();
        while (eval.next()) {
            final var inst = eval.inst();
            final var values = ValueUtils.values(eval, fields);
            nextLevelParents.add(inst);
            entries.put(null, new Entry(inst, values));
        }
        evalChildren(parameter, nextLevelParents, entries, fields);
        ret.put(ReturnValues.VALUES, toStructured(entries, null));
        return ret;
    }

    protected void evalChildren(final Parameter parameter,
                                final Collection<Instance> parents,
                                final MultiValuedMap<Instance, Entry> entries,
                                final List<Field> fields)
        throws EFapsException
    {
        if (!parents.isEmpty()) {
            final var childTypes = analyseProperty(parameter, "Child_Type").values();
            final var linkFromAttr = getProperty(parameter, "Child_LinkFrom");
            final var print = EQL.builder().print().query(childTypes.toArray(String[]::new))
                            .where()
                            .attribute(linkFromAttr).in(parents);

            final var select = print.select();
            final var types = childTypes.stream().map(ValueUtils::evalType).collect(Collectors.toList());
            SelectUtils.add2Select(select, types, fields);
            select.linkto(linkFromAttr).instance().as("TMPARENTLINK");

            final var eval = select.evaluate();
            final var nextLevelParents = new HashSet<Instance>();
            while (eval.next()) {
                final var inst = eval.inst();
                final var parentInst = eval.<Instance>get("TMPARENTLINK");
                final var values = ValueUtils.values(eval, fields);
                entries.put(parentInst, new Entry(inst, values));
                nextLevelParents.add(inst);
            }
            evalChildren(parameter, nextLevelParents, entries, fields);
        }
    }

    protected List<StructurBrowserEntryDto> toStructured(final MultiValuedMap<Instance, Entry> entries,
                                                         final Instance currentInstance)
    {
        final var ret = new ArrayList<StructurBrowserEntryDto>();
        for (final var entry : entries.get(currentInstance)) {
            ret.add(StructurBrowserEntryDto.builder()
                            .withChildren(toStructured(entries, entry.getinstance()))
                            .withValues(entry.values)
                            .build());
        }
        return ret;
    }

    protected List<Field> getFields(final Parameter parameter)
    {
        final var cmd = (AbstractCommand) parameter.get(ParameterValues.UIOBJECT);
        final var table = cmd.getTargetTable();
        return ValueUtils.getFields(table);
    }

    public static class Entry
    {

        private final Instance instance;
        private final Map<String, ?> values;

        public Entry(final Instance instance,
                     final Map<String, ?> values)
        {
            this.instance = instance;
            this.values = values;
        }

        public Map<String, ?> getValues()
        {
            return values;
        }

        public Instance getinstance()
        {
            return instance;
        }
    }

}

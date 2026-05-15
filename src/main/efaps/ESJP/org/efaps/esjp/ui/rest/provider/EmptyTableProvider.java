package org.efaps.esjp.ui.rest.provider;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.util.EFapsException;

@EFapsUUID("f2340d75-428b-49f0-9dc7-b1c9e7190786")
@EFapsApplication("eFaps-WebApp")
public class EmptyTableProvider
    extends AbstractTableProvider
{

    @Override
    public Collection<Map<String, ?>> getValues()
        throws EFapsException
    {
        return Collections.emptyList();
    }

}

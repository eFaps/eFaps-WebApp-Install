package org.efaps.esjp.ui.rest.provider;

import java.util.HashMap;
import java.util.Map;

import org.efaps.admin.event.EventType;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.AbstractUserInterfaceObject;
import org.efaps.db.Instance;
import org.efaps.util.EFapsException;

@EFapsUUID("84ca8fda-4743-422e-b49a-9bbe12f1102e")
@EFapsApplication("eFaps-WebApp")
public interface IFormProvider
{

    default IFormProvider init(final AbstractUserInterfaceObject cmd,
                               final Map<String, String> properties,
                               final Map<String, ?> payloadValues)
    {
        return this;
    }

    default Instance evalSectionInstance(Instance instance)
        throws EFapsException
    {
        return instance;
    }

    default Map<String, ?> getValues()
    {
        return null;
    }

    default Map<String, String> evalEventProperties(final AbstractUserInterfaceObject cmd,
                                                    final EventType eventType)
    {
        if (cmd.hasEvents(eventType)) {
            return cmd.getEvents(eventType).get(0).getPropertyMap();
        } else {
            return new HashMap<>();
        }
    }
}

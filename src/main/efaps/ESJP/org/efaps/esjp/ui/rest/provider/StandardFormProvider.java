package org.efaps.esjp.ui.rest.provider;

import java.util.Map;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.AbstractCommand;
import org.efaps.admin.ui.AbstractUserInterfaceObject;
import org.efaps.admin.ui.AbstractUserInterfaceObject.TargetMode;
import org.efaps.db.Instance;
import org.efaps.util.EFapsException;

@EFapsUUID("60a866cf-a3e3-4a99-9725-0ad2dc44ce83")
@EFapsApplication("eFaps-WebApp")
public class StandardFormProvider
    implements IFormProvider
{

    private AbstractCommand callCmd;
    private Map<String, String> properties;
    private Map<String, ?> payloadValues;

    @Override
    public IFormProvider init(final AbstractUserInterfaceObject cmd,
                              final Map<String, String> properties,
                              final Map<String, ?> payloadValues)
    {
        this.callCmd = (AbstractCommand) cmd;
        this.properties = properties;
        this.payloadValues = payloadValues;
        return this;
    }

    public AbstractCommand getCallCmd()
    {
        return callCmd;
    }

    public Map<String, String> getProperties()
    {
        return properties;
    }

    public Map<String, ?> getPayloadValues()
    {
        return payloadValues;
    }

    @Override
    public Instance evalSectionInstance(final Instance instance)
        throws EFapsException
    {
        final var targetMode = TargetMode.UNKNOWN.equals(getCallCmd().getTargetMode()) ? TargetMode.VIEW
                        : getCallCmd().getTargetMode();

        final Instance sectionInstance;
        if (TargetMode.CREATE.equals(targetMode) && getCallCmd().getTargetCreateType() != null) {
            sectionInstance = Instance.get(getCallCmd().getTargetCreateType(), null);
        } else {
            sectionInstance = instance;
        }
        return sectionInstance;
    }
}

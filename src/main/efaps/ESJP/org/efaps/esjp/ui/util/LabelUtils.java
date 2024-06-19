package org.efaps.esjp.ui.util;

import java.util.Optional;

import org.efaps.admin.datamodel.Attribute;
import org.efaps.admin.datamodel.Type;
import org.efaps.admin.dbproperty.DBProperties;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

@EFapsUUID("428adeae-a608-4941-ba3e-a8c2ae2c2449")
@EFapsApplication("eFaps-WebApp")
public class LabelUtils
{

    public static Optional<String> evalForTypeAndAttribute(final Type type,
                                                           final String attributeName)
    {
        final var attribute = type.getAttribute(attributeName);
        String label = null;
        if (attribute != null) {
            var currentType = type;
            while (label == null && currentType != null) {
                label = DBProperties.getProperty(getKey(type, attribute));
                currentType = currentType.getParentType();
            }
        }
        return label == null ? Optional.empty() : Optional.of(label);
    }

    private static String getKey(final Type type,
                                 final Attribute attribute)
    {
        return type.getName() + "/" + attribute.getName() + ".Label";
    }

}

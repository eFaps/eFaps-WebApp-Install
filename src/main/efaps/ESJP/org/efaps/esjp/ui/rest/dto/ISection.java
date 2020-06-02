package org.efaps.esjp.ui.rest.dto;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

@EFapsUUID("f8f9bf1a-015c-4698-93c8-545b369c3d4b")
@EFapsApplication("eFaps-WebApp")
public interface ISection
{
    SectionType getType();
}

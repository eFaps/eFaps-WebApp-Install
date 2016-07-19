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
 *
 */


package org.efaps.esjp.common.pushmsg;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.wicket.protocol.ws.api.IWebSocketConnection;
import org.efaps.admin.event.Parameter;
import org.efaps.admin.event.Return;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.user.AbstractUserObject;
import org.efaps.admin.user.Group;
import org.efaps.admin.user.Person;
import org.efaps.admin.user.Role;
import org.efaps.ci.CIAdminUser;
import org.efaps.db.MultiPrintQuery;
import org.efaps.db.QueryBuilder;
import org.efaps.esjp.common.AbstractCommon;
import org.efaps.ui.wicket.EFapsApplication;
import org.efaps.ui.wicket.models.PushMsg;
import org.efaps.util.EFapsException;


/**
 * TODO comment!
 *
 * @author The eFaps Team
 */
@EFapsUUID("d2c27870-19cc-411f-95db-8db817fae956")
@EFapsApplication("eFaps-WebApp")
public abstract class MessageSender_Base
    extends AbstractCommon
{

    /**
     * Send a TextMessage to a user, group or role.
     * @param _parameter Parameter as passed by the eFaps API
     * @return new Return
     * @throws EFapsException on error
     */
    public Return sendTextMessage(final Parameter _parameter)
        throws EFapsException
    {
        String reciever = getProperty(_parameter, "Reciever");
        String message = getProperty(_parameter, "Message");
        if (reciever == null) {
            reciever = _parameter.getParameterValue("Reciever");
        }
        if (message == null) {
            message = _parameter.getParameterValue("Message");
        }
        if (reciever != null && !reciever.isEmpty()) {
            final List<Person> persons = new ArrayList<>();
            AbstractUserObject user;
            if (isUUID(reciever)) {
                user = AbstractUserObject.getUserObject(UUID.fromString(reciever));
            } else {
                user = AbstractUserObject.getUserObject(reciever);
            }
            if (user instanceof Role || user instanceof Group) {
                final QueryBuilder queryBldr = new QueryBuilder(user instanceof Role ? CIAdminUser.Person2Role
                                : CIAdminUser.Person2Group);
                queryBldr.addWhereAttrEqValue(CIAdminUser._Abstract2Abstract.UserToAbstractLink, user.getId());
                final MultiPrintQuery multi = queryBldr.getPrint();
                multi.addAttribute(CIAdminUser._Abstract2Abstract.UserFromAbstractLink);
                multi.executeWithoutAccessCheck();
                while (multi.next()) {
                    final Long personid = multi.<Long>getAttribute(CIAdminUser._Abstract2Abstract.UserFromAbstractLink);
                    persons.add(Person.get(personid));
                }
            }
            for (final Person person : persons) {
                final List<IWebSocketConnection> conns = EFapsApplication.get().getConnectionRegistry()
                                .getConnections4User(person.getName());
                for (final IWebSocketConnection conn : conns) {
                    conn.sendMessage(new PushMsg(message));
                }
            }
        }
        return new Return();
    }
}

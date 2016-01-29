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

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsListener;
import org.efaps.admin.program.esjp.EFapsUUID;

/**
 * The listener interface for receiving sysConf events. The class that is
 * interested in processing a sysConf event implements this interface, and the
 * object created with that class is registered with a component using the
 * component's <code>addSysConfListener<code> method. When the sysConf event
 * occurs, that object's appropriate method is invoked.
 *
 * @see SysConfEvent
 */
@EFapsUUID("d6548826-830b-4540-a46d-d861c3f21f15")
@EFapsApplication("eFaps-WebApp")
@EFapsListener
public class SysConfListener
    extends SysConfListener_Base
{

}

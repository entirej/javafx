/*******************************************************************************
 * Copyright 2013 Mojave Innovations GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributors:
 *     Mojave Innovations GmbH - initial API and implementation
 ******************************************************************************/
package org.entirej.applicationframework.fx.application.interfaces;

import org.entirej.framework.core.internal.EJInternalForm;

/**
 * Listens for open form events
 * <p>
 * All registered listeners will be notified when a new form is opened. The form
 * opened event is fired after the form chosen event. The form chosen event is
 * used to inform EJ that a new form must be opened. Once EJ has opened the
 * form, a form opened event is fired
 * 
 */
public interface EJFXFormOpenedListener
{
    /**
     * called when a new form is opened
     * 
     * @param openedForm
     *            The form that was opened
     */
    public void fireFormOpened(EJInternalForm openedForm);
}

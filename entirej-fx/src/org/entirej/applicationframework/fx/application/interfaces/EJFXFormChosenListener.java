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

/**
 * The form chosen event is fired when the used chooses a form to open
 * <p>
 * The form chosen event should be used to inform EJ that a form should be
 * opened. Once EJ has opened the form, a form opened event is fired
 * 
 * 
 */
public interface EJFXFormChosenListener
{
    /**
     * Called by the form selector whenever a new form is chosen.
     * 
     * @param pEvent
     *            The event containing the chosen form name
     */
    public void formChosen(EJFXFormChosenEvent event);
}

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
package org.entirej.applicationframework.fx.application.components;

import javafx.scene.Node;

import org.entirej.framework.core.data.controllers.EJBlockController;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.internal.EJInternalForm;
import org.entirej.framework.core.renderers.eventhandlers.EJBlockFocusedListener;
import org.entirej.framework.core.renderers.eventhandlers.EJFormEventListener;
import org.entirej.framework.core.renderers.eventhandlers.EJItemFocusListener;
import org.entirej.framework.core.renderers.eventhandlers.EJItemValueChangedListener;
import org.entirej.framework.core.renderers.eventhandlers.EJNewRecordFocusedListener;

public interface EJFXFormToolbar extends EJBlockFocusedListener, EJItemValueChangedListener, EJItemFocusListener, EJNewRecordFocusedListener,
        EJFormEventListener
{
    /**
     * Returns the form to which this toolbar belongs
     * 
     * @return The form to which this toolbar belongs
     */
    public EJInternalForm getForm();

    /**
     * Return the {@link Control} component for this toolbar
     * 
     * @return The {@link Control} component
     */
    public Node getComponent();

    public Node createComponent();

    /**
     * Disables this toolbar
     */
    public void disable();

    /**
     * Returns the current focused item
     * 
     * @return The currently focused item or <code>null</code> if no item is
     *         currently focused
     */
    public EJScreenItemController getFocusedItem();

    /**
     * Instructs the toolbar to synchronise itself according to the focused
     * block
     * 
     * @param focusedBlock
     *            The block to synchronise with
     */
    public void synchronize(EJBlockController focusedBlock);
}

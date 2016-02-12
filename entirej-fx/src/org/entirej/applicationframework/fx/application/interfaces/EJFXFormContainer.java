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

import java.util.Collection;

import org.entirej.framework.core.data.controllers.EJPopupFormController;
import org.entirej.framework.core.internal.EJInternalForm;

public interface EJFXFormContainer
{

    /**
     * Adds a form to this container
     * <p>
     * Once the application container has received the form chosen event, it
     * will instruct EntireJ to open the form. Once the form is opened the
     * application controller will instruct this container to display the chosen
     * form
     * 
     * @param form
     *            The form that is to be added to this container
     */
    public EJInternalForm addForm(EJInternalForm form);

    /**
     * Used to open a specific form as a popup
     * <p>
     * A popup form is a normal form that will be opened in a modal window or as
     * part of the current form. The modal form normally has a direct connection
     * to this form and may receive or return values to or from the calling form
     * 
     * @param popupFormController
     *            The controller holding all required values to open the popup
     *            form
     */
    public void openPopupForm(EJPopupFormController popupController);

    /**
     * Called after a popup form has been closed
     */
    public void popupFormClosed();

    /**
     * Returns all forms that are currently opened within this container
     * 
     * @return A collection containing all opened forms contained within this
     *         container
     */
    public Collection<EJInternalForm> getAllForms();

    /**
     * Used to check if this container contains a form with the given name
     * 
     * @param formName
     *            The name of the form to search for
     * @return <code>true</code> if there is a form within this container with
     *         the specified name, otherwise <code>false</code>
     */
    public boolean containsForm(String formName);

    /**
     * Returns the current active form
     * <p>
     * The active form is the form that currently has focus. When the user
     * chooses a different form then the active form will be replaced
     * 
     * @return The form that is currently active
     */
    public EJInternalForm getActiveForm();

    /**
     * Instructs this container to close the given form
     * 
     * @param form
     *            The form that is to be closed
     */
    public void closeForm(EJInternalForm form);

    /**
     * {@link EJFXFormSelectedListener}'s will be notified when the user chooses
     * a different form within the MDI
     * 
     * @param selectionListener
     *            The selection listener to add
     */
    public void addFormSelectedListener(EJFXFormSelectedListener selectionListener);

    /**
     * Removes the given {@link EJFXFormSelectedListener} from this container
     * <p>
     * {@link EJFXFormSelectedListener}'s will be notified when the user chooses
     * a different form within the MDI
     * 
     * @param selectionListener
     *            The selection listener to add
     */
    public void removeFormSelectedListener(EJFXFormSelectedListener selectionListener);

    public EJInternalForm switchToForm(String key);

    public void updateFormTitle(EJInternalForm form);
}

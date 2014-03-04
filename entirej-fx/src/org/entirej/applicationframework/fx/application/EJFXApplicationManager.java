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
package org.entirej.applicationframework.fx.application;

import java.util.Locale;

import javafx.stage.Stage;

import org.entirej.applicationframework.fx.application.interfaces.EJFXFormContainer;
import org.entirej.framework.core.EJFrameworkManager;
import org.entirej.framework.core.EJManagedFrameworkConnection;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.EJParameterList;
import org.entirej.framework.core.EJTranslatorHelper;
import org.entirej.framework.core.data.controllers.EJApplicationLevelParameter;
import org.entirej.framework.core.data.controllers.EJInternalQuestion;
import org.entirej.framework.core.data.controllers.EJPopupFormController;
import org.entirej.framework.core.data.controllers.EJQuestion;
import org.entirej.framework.core.interfaces.EJApplicationManager;
import org.entirej.framework.core.interfaces.EJMessenger;
import org.entirej.framework.core.internal.EJInternalForm;

public class EJFXApplicationManager implements EJApplicationManager
{

    private EJFrameworkManager       frameworkManager;
    private Stage                    primaryStage;
    private EJFXApplicationContainer applicationContainer;
    private EJFXMessenger            messenger;

    public EJFXApplicationManager()
    {
        messenger = new EJFXMessenger(this);
    }

    public EJFXApplicationContainer getApplicationContainer()
    {
        return applicationContainer;
    }

    @Override
    public EJManagedFrameworkConnection getConnection()
    {
        return frameworkManager.getConnection();
    }

    public EJFXFormContainer getFormContainer()
    {
        return applicationContainer.getFormContainer();
    }

    public EJInternalForm getActiveForm()
    {
        if (applicationContainer == null)
        {
            return null;
        }

        return applicationContainer.getActiveForm();
    }

    public EJInternalForm getForm(String formName)
    {

        return applicationContainer.getForm(formName);
    }

    public void removeFormFromContainer(EJInternalForm form)
    {
        if (applicationContainer == null)
        {
            return;
        }

        applicationContainer.remove(form);
    }

    public int getOpenedFormCount()
    {
        if (applicationContainer == null)
        {
            return 0;
        }

        return applicationContainer.getOpenFormCount();
    }

    public boolean isFormOpened(String formName)
    {
        if (applicationContainer == null)
        {
            return false;
        }

        return applicationContainer.isFormOpened(formName);
    }

    public void addFormToContainer(EJInternalForm form, boolean blocking)
    {
        if (applicationContainer == null)
        {
            throw new IllegalStateException("Unable to open a form until the application has been built");
        }
        applicationContainer.add(form);
    }

    public void openPopupForm(EJPopupFormController popupController)
    {
        if (applicationContainer.getFormContainer() != null)
        {
            applicationContainer.getFormContainer().openPopupForm(popupController);
        }
    }

    public void popupFormClosed()
    {
        if (applicationContainer.getFormContainer() != null)
        {
            applicationContainer.getFormContainer().popupFormClosed();
        }
    }

    public EJInternalForm switchToForm(String key)
    {
        if (applicationContainer.getFormContainer() != null)
        {
            return applicationContainer.getFormContainer().switchToForm(key);
        }
        return null;
    }

    @Override
    public EJApplicationLevelParameter getApplicationLevelParameter(String valueName)
    {
        return frameworkManager.getApplicationLevelParameter(valueName);
    }

    @Override
    public void setApplicationLevelParameter(String valueName, Object value)
    {
        frameworkManager.setApplicationLevelParameter(valueName, value);
    }

    public void changeLocale(Locale locale)
    {
        frameworkManager.changeLocale(locale);
    }

    @Override
    public Locale getCurrentLocale()
    {
        return frameworkManager.getCurrentLocale();
    }

    @Override
    public EJTranslatorHelper getTranslatorHelper()
    {
        return frameworkManager.getTranslatorHelper();
    }

    @Override
    public void handleMessage(EJMessage message)
    {
        messenger.handleMessage(message);
    }

    @Override
    public void handleException(Exception exception)
    {
        messenger.handleException(exception);
    }

    @Override
    public void handleException(Exception exception, boolean showUserMessage)
    {
        messenger.handleException(exception, showUserMessage);
    }

    @Override
    public void askQuestion(EJQuestion question)
    {
        messenger.askQuestion(question);
    }

    @Override
    public void askInternalQuestion(EJInternalQuestion question)
    {
        messenger.askInternalQuestion(question);
    }

    public Stage getPrimaryStage()
    {
        return primaryStage;
    }

    public void buildApplication(EJFXApplicationContainer container, Stage primaryStage)
    {
        if (container == null)
        {
            throw new NullPointerException("The ApplicationContainer cannot bu null");
        }
        this.primaryStage = primaryStage;
        applicationContainer = container;
        applicationContainer.buildApplication(this, primaryStage);
    }

    @Override
    public EJMessenger getApplicationMessenger()
    {
        return messenger;
    }

    @Override
    public EJFrameworkManager getFrameworkManager()
    {
        return frameworkManager;
    }

    @Override
    public void setFrameworkManager(EJFrameworkManager frameworkManager)
    {
        this.frameworkManager = frameworkManager;

    }

    @Override
    public void openForm(String formName, EJParameterList parameterList, boolean blocking)
    {
        frameworkManager.openForm(formName, parameterList, blocking);
        
    }

    @Override
    public void openForm(String formName, EJParameterList parameterList)
    {
        frameworkManager.openForm(formName, parameterList);
        
    }

    @Override
    public void openForm(String formName)
    {
        frameworkManager.openForm(formName);
        
    }
}

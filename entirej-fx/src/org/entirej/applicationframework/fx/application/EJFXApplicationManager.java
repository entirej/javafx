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

import java.awt.Desktop;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import javafx.stage.Stage;

import org.entirej.applicationframework.fx.application.components.EJFXMenuComponent;
import org.entirej.applicationframework.fx.application.interfaces.EJFXFormContainer;
import org.entirej.framework.core.EJFrameworkManager;
import org.entirej.framework.core.EJManagedFrameworkConnection;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.EJParameterList;
import org.entirej.framework.core.EJTranslatorHelper;
import org.entirej.framework.core.data.controllers.EJApplicationLevelParameter;
import org.entirej.framework.core.data.controllers.EJEmbeddedFormController;
import org.entirej.framework.core.data.controllers.EJFormParameter;
import org.entirej.framework.core.data.controllers.EJInternalQuestion;
import org.entirej.framework.core.data.controllers.EJPopupFormController;
import org.entirej.framework.core.data.controllers.EJQuestion;
import org.entirej.framework.core.interfaces.EJApplicationManager;
import org.entirej.framework.core.interfaces.EJMessenger;
import org.entirej.framework.core.internal.EJInternalForm;
import org.entirej.framework.core.properties.EJCoreProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.report.EJReport;
import org.entirej.framework.report.EJReportFrameworkInitialiser;
import org.entirej.framework.report.EJReportFrameworkManager;
import org.entirej.framework.report.EJReportParameterList;
import org.entirej.framework.report.data.controllers.EJReportParameter;
import org.entirej.framework.report.interfaces.EJReportRunner;

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
    
    @Override
    public Collection<EJInternalForm> getOpenedForms()
    {
        if (applicationContainer == null)
        {
            return Collections.emptyList();
        }

        return applicationContainer.getOpenForms();
    }
    
    @Override
    public void updateFormTitle(EJInternalForm form)
    {
        if (applicationContainer == null)
        {
            return ;
        }

         applicationContainer.updateFormTitle(form);
        
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
        EJFrameworkExtensionProperties definedProperties = EJCoreProperties.getInstance().getApplicationDefinedProperties();

        if (definedProperties != null)
        {

            String menuConfigID = definedProperties.getStringProperty("APPLICATION_MENU");

            if (menuConfigID != null && menuConfigID.length() > 0)
            {
                new EJFXMenuComponent().buildMenuProperties(this, primaryStage, menuConfigID);

            }

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

    public void openEmbeddedForm(EJEmbeddedFormController embeddedController)
    {
        embeddedController.getCallingForm().getRenderer().openEmbeddedForm(embeddedController);
    }

    public void closeEmbeddedForm(EJEmbeddedFormController embeddedController)
    {
        embeddedController.getCallingForm().getRenderer().closeEmbeddedForm(embeddedController);
    }

    @Override
    public void runReport(String reportName)
    {
        runReport(reportName, null);

    }

    @Override
    public void runReport(String reportName, EJParameterList parameterList)
    {
        if (reportManager == null)
        {
            reportManager = EJReportFrameworkInitialiser.initialiseFramework("report.ejprop");
        }
        EJReport report;
        if(parameterList==null)
        {
             report = reportManager.createReport(reportName);
        }
        else
        {
            
            
            EJReportParameterList list = new EJReportParameterList();
            
            Collection<EJFormParameter> allParameters = parameterList.getAllParameters();
            for (EJFormParameter parameter : allParameters)
            {
                EJReportParameter reportParameter = new EJReportParameter(parameter.getName(), parameter.getDataType());
                reportParameter.setValue(parameter.getValue());
                
                list.addParameter(reportParameter);
            }
            report = reportManager.createReport(reportName,list);
        }

        EJReportRunner reportRunner = reportManager.createReportRunner();
        String output = reportRunner.runReport(report);

        try
        {
            Desktop.getDesktop().open(new File(output));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    @Override
    public String generateReport(String reportName)
    {
        return generateReport(reportName, null);
        
    }
    
    @Override
    public String generateReport(String reportName, EJParameterList parameterList)
    {
        if (reportManager == null)
        {
            reportManager = EJReportFrameworkInitialiser.initialiseFramework("report.ejprop");
        }
        EJReport report;
        if(parameterList==null)
        {
            report = reportManager.createReport(reportName);
        }
        else
        {
            
            
            EJReportParameterList list = new EJReportParameterList();
            
            Collection<EJFormParameter> allParameters = parameterList.getAllParameters();
            for (EJFormParameter parameter : allParameters)
            {
                EJReportParameter reportParameter = new EJReportParameter(parameter.getName(), parameter.getDataType());
                reportParameter.setValue(parameter.getValue());
                
                list.addParameter(reportParameter);
            }
            report = reportManager.createReport(reportName,list);
        }
        
        EJReportRunner reportRunner = reportManager.createReportRunner();
        String output = reportRunner.runReport(report);
        return output;
        
    }

    private EJReportFrameworkManager reportManager;

}

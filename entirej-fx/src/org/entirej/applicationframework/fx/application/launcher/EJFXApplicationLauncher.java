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
package org.entirej.applicationframework.fx.application.launcher;

import javafx.application.Application;
import javafx.scene.SceneBuilder;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import org.entirej.applicationframework.fx.application.EJFXApplicationContainer;
import org.entirej.applicationframework.fx.application.EJFXApplicationManager;
import org.entirej.applicationframework.fx.notifications.EJFXNotifierDialog;
import org.entirej.applicationframework.fx.utils.EJFXImageRetriever;
import org.entirej.applicationframework.fx.utils.EJFXVisualAttributeUtils;
import org.entirej.framework.core.EJFrameworkHelper;
import org.entirej.framework.core.EJFrameworkInitialiser;
import org.entirej.framework.core.properties.EJCoreLayoutContainer;
import org.entirej.framework.core.properties.EJCoreProperties;

public class EJFXApplicationLauncher extends Application
{

    @Override
    public void start(Stage primaryStage)
    {

        EJFXNotifierDialog.Notifier.setNotificationOwner(primaryStage);
        EJFXApplicationManager applicationManager = null;

        if (this.getClass().getClassLoader().getResource("application.ejprop") != null)
        {
            applicationManager = (EJFXApplicationManager) EJFrameworkInitialiser.initialiseFramework("application.ejprop");
        }
        else if (this.getClass().getClassLoader().getResource("EntireJApplication.properties") != null)
        {

            applicationManager = (EJFXApplicationManager) EJFrameworkInitialiser.initialiseFramework("EntireJApplication.properties");
        }
        else
        {
            throw new RuntimeException("application.ejprop not found");
        }

        EJCoreLayoutContainer layoutContainer = EJCoreProperties.getInstance().getLayoutContainer();
        primaryStage.setTitle(layoutContainer.getTitle());
        primaryStage.setWidth(layoutContainer.getWidth());
        primaryStage.setHeight(layoutContainer.getHeight());

        primaryStage.setScene(SceneBuilder.create().root(new BorderPane()).width(primaryStage.getWidth()).height(primaryStage.getHeight()).build());
        primaryStage.getScene().getStylesheets().add(getDefaultStyle());
        primaryStage.getScene().getStylesheets().add("style/calendar-style.css");
        primaryStage.getScene().getStylesheets().add(getVADefaultStyle());
        String vacss = EJFXVisualAttributeUtils.INSTANCE.buildVACSS(EJCoreProperties.getInstance());
        if (vacss != null)
        {
            primaryStage.getScene().getStylesheets().add(vacss);
        }
        String customStyle = getCustomStyle();
        if (customStyle != null)
        {
            primaryStage.getScene().getStylesheets().add(customStyle);
        }
        preApplicationBuild(applicationManager);

        // /||||||||||||||||||||||||||||||||||||||||||||||||

        EJFXApplicationContainer container = new EJFXApplicationContainer(layoutContainer);
        applicationManager.buildApplication(container, primaryStage);
        // |||||||||||||||||||||||||||||||||||||||||||||||||
        postApplicationBuild(applicationManager);
        primaryStage.getIcons().add(EJFXImageRetriever.get(getIcon()));
        primaryStage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
    }

    protected String getDefaultStyle()
    {
        return "style/default-style.css";
    }

    protected String getVADefaultStyle()
    {
        return "style/default-va-style.css";
    }

    protected String getIcon()
    {
        return "icons/EJ.png";
    }

    protected String getCustomStyle()
    {
        return null;
    }

    public void preApplicationBuild(EJFrameworkHelper frameworkHelper)
    {
    }

    public void postApplicationBuild(EJFrameworkHelper frameworkHelper)
    {
    }
}

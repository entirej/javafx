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

import java.awt.Dimension;
import java.awt.Toolkit;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.SceneBuilder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import org.entirej.applicationframework.fx.application.EJFXApplicationContainer;
import org.entirej.applicationframework.fx.application.EJFXApplicationManager;
import org.entirej.applicationframework.fx.notifications.EJFXNotifierDialog;
import org.entirej.applicationframework.fx.utils.EJFXImageRetriever;
import org.entirej.applicationframework.fx.utils.EJFXVisualAttributeUtils;
import org.entirej.framework.core.EJFrameworkHelper;
import org.entirej.framework.core.EJFrameworkInitialiser;
import org.entirej.framework.core.interfaces.EJException;
import org.entirej.framework.core.properties.EJCoreLayoutContainer;
import org.entirej.framework.core.properties.EJCoreProperties;

public class EJFXApplicationLauncher extends Application
{

    @Override
    public void start(final Stage primaryStage)
    {

        if (System.getProperty("os.name").toLowerCase().contains("mac"))
        {
            System.setProperty("apple.laf.useScreenMenuBar", "true");

            com.apple.eawt.Application application = com.apple.eawt.Application.getApplication();

            application.setDockIconImage(Toolkit.getDefaultToolkit().getImage(EJFXApplicationLauncher.class.getClassLoader().getResource(getIcon())));

        }
        final Stage splashStage = new Stage();
        loadSplash(splashStage);

        new Thread(new Runnable()
        {

            @Override
            public void run()
            {

                loadApp(primaryStage, splashStage);

            }
        }).start();

    }

    private void loadSplash(Stage initStage)
    {
        Pane splashLayout;

        ImageView splash = new ImageView(new Image(getSplash()));
        splashLayout = new VBox();
        splashLayout.getChildren().addAll(splash);

        splashLayout.setEffect(new javafx.scene.effect.Blend());
        Scene splashScene = new Scene(splashLayout);
        splashScene.setFill(null);
        initStage.initStyle(StageStyle.TRANSPARENT);
        initStage.setScene(splashScene);
        initStage.centerOnScreen();
        initStage.show();

    }

    private void loadApp(final Stage primaryStage, final Stage splashStage)
    {
        Platform.runLater(new Runnable()
        {

            @Override
            public void run()
            {
                EJFXNotifierDialog.Notifier.setNotificationOwner(primaryStage);
            }
        });
        final EJFXApplicationManager applicationManager;

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

        final EJCoreLayoutContainer layoutContainer = EJCoreProperties.getInstance().getLayoutContainer();
    
        primaryStage.setTitle(layoutContainer.getTitle());

        primaryStage.setWidth(layoutContainer.getWidth());
        primaryStage.setHeight(layoutContainer.getHeight());
        final String vacss = EJFXVisualAttributeUtils.INSTANCE.buildVACSS(EJCoreProperties.getInstance());
        Platform.runLater(new Runnable()
        {

            @Override
            public void run()
            {
                primaryStage.setScene(SceneBuilder.create().root(new BorderPane()).width(primaryStage.getWidth()).height(primaryStage.getHeight()).build());
                primaryStage.getScene().getStylesheets().add(getDefaultStyle());
                primaryStage.getScene().getStylesheets().add("style/calendar-style.css");
                primaryStage.getScene().getStylesheets().add(getVADefaultStyle());

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

                final EJFXApplicationContainer container = new EJFXApplicationContainer(layoutContainer);
                applicationManager.buildApplication(container, primaryStage);
                // |||||||||||||||||||||||||||||||||||||||||||||||||
                postApplicationBuild(applicationManager);
                primaryStage.getIcons().add(EJFXImageRetriever.get(getIcon()));
                splashStage.close();
                primaryStage.show();

                primaryStage.getScene().getWindow().setOnCloseRequest(new EventHandler<WindowEvent>()
                {
                    public void handle(WindowEvent ev)
                    {

                        try
                        {
                            container.closeALlForms();
                        }
                        catch (Throwable e)
                        {
                            if (e instanceof EJException)
                            {
                                EJException exception = (EJException) e;
                                applicationManager.handleMessage(exception.getFrameworkMessage());
                            }
                            e.printStackTrace();
                            ev.consume();
                        }

                    }
                });
            }
        });

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

    protected String getSplash()
    {
        return "icons/ENTIREJ_BIG.png";
    }

    protected Dimension getSplashSize()
    {
        return new Dimension(200, 56);
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

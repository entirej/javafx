/*******************************************************************************
 * Copyright 2013 CRESOFT AG
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
 *     CRESOFT AG - initial API and implementation
 ******************************************************************************/
package org.entirej.applicationframework.fx.application.components;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.entirej.applicationframework.fx.application.EJFXApplicationManager;
import org.entirej.applicationframework.fx.application.interfaces.EJFXAppComponentRenderer;
import org.entirej.applicationframework.fx.application.interfaces.EJFXApplicationComponent;
import org.entirej.applicationframework.fx.application.interfaces.EJFXFormChosenListener;
import org.entirej.applicationframework.fx.utils.EJFXImageRetriever;
import org.entirej.framework.core.EJActionProcessorException;
import org.entirej.framework.core.EJApplicationException;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.EJMessageFactory;
import org.entirej.framework.core.actionprocessor.interfaces.EJMenuActionProcessor;
import org.entirej.framework.core.data.controllers.EJTranslationController;
import org.entirej.framework.core.enumerations.EJFrameworkMessage;
import org.entirej.framework.core.internal.EJInternalForm;
import org.entirej.framework.core.properties.EJCoreMenuContainer;
import org.entirej.framework.core.properties.EJCoreMenuLeafActionProperties;
import org.entirej.framework.core.properties.EJCoreMenuLeafContainer;
import org.entirej.framework.core.properties.EJCoreMenuLeafFormProperties;
import org.entirej.framework.core.properties.EJCoreMenuLeafProperties;
import org.entirej.framework.core.properties.EJCoreMenuLeafSpacerProperties;
import org.entirej.framework.core.properties.EJCoreMenuProperties;
import org.entirej.framework.core.properties.EJCoreProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;

public class EJFXMenuComponent 
{
    private EJMenuActionProcessor  actionProcessor = null;
    private EJFXApplicationManager _appManager;
    private String                 menuId          = null;

   

    
    public MenuBar createComponent()
    {
        
        MenuBar menuBar = new MenuBar();

        
        if (menuId != null)
        {
            EJTranslationController translationController = _appManager.getFrameworkManager().getTranslationController();
            EJCoreMenuContainer menuContainer = EJCoreProperties.getInstance().getMenuContainer();
            EJCoreMenuProperties root = menuContainer.getMenuProperties(menuId);
            if (root != null)
            {
                if (root.getActionProcessorClassName() != null && root.getActionProcessorClassName().length() > 0)
                {
                    try
                    {
                        Class<?> processorClass = Class.forName(root.getActionProcessorClassName());
                        try
                        {
                            Object processorObject = processorClass.newInstance();
                            if (processorObject instanceof EJMenuActionProcessor)
                            {
                                actionProcessor = (EJMenuActionProcessor) processorObject;
                            }
                            else
                            {
                                throw new EJApplicationException(EJMessageFactory.getInstance().createMessage(EJFrameworkMessage.INVALID_ACTION_PROCESSOR_NAME,
                                        processorClass.getName(), "EJMenuActionProcessor"));
                            }
                        }
                        catch (InstantiationException e)
                        {
                            throw new EJApplicationException(EJMessageFactory.getInstance().createMessage(EJFrameworkMessage.UNABLE_TO_CREATE_ACTION_PROCESSOR,
                                    processorClass.getName()), e);
                        }
                        catch (IllegalAccessException e)
                        {
                            throw new EJApplicationException(EJMessageFactory.getInstance().createMessage(EJFrameworkMessage.UNABLE_TO_CREATE_ACTION_PROCESSOR,
                                    processorClass.getName()), e);
                        }
                    }
                    catch (ClassNotFoundException e)
                    {
                        throw new EJApplicationException(EJMessageFactory.getInstance().createMessage(EJFrameworkMessage.INVALID_ACTION_PROCESSOR_FOR_MENU,
                                root.getActionProcessorClassName()));
                    }
                }

                translationController.translateMenuProperties(root);
                List<EJCoreMenuLeafProperties> leaves = root.getLeaves();
                Menu rootSubmenu = new Menu(root.getName());
                menuBar.getMenus().add(rootSubmenu);
                for (EJCoreMenuLeafProperties leafProperties : leaves)
                {
                    if (leafProperties instanceof EJCoreMenuLeafContainer)
                    {
                        EJCoreMenuLeafContainer container = (EJCoreMenuLeafContainer) leafProperties;
                        Menu subMenu = new Menu(leafProperties.getDisplayName());
                        
                        menuBar.getMenus().add(subMenu);
                        if (leafProperties.getIconName() != null && !leafProperties.getIconName().trim().isEmpty())
                        {
                            subMenu.setGraphic(new ImageView(EJFXImageRetriever.get(leafProperties.getIconName())));
                        }

                        for (EJCoreMenuLeafProperties subLeafProperties : container.getLeaves())
                        {
                            addEJCoreMenuLeafProperties(subMenu.getItems(), subLeafProperties);
                        }
                        continue;
                    }
                    if (leafProperties instanceof EJCoreMenuLeafSpacerProperties)
                    {
                        rootSubmenu.getItems().add(new SeparatorMenuItem());
                        continue;
                    }
                    if (leafProperties instanceof EJCoreMenuLeafFormProperties)
                    {
                        EJCoreMenuLeafFormProperties formLeaf = (EJCoreMenuLeafFormProperties) leafProperties;
                        MenuItem subMenu = new MenuItem(leafProperties.getDisplayName());
                        rootSubmenu.getItems().add(subMenu);
                        if (leafProperties.getIconName() != null && !leafProperties.getIconName().trim().isEmpty())
                        {
                            subMenu.setGraphic(new ImageView(EJFXImageRetriever.get(leafProperties.getIconName())));
                        }
                        final String actionCommand = formLeaf.getFormName();
                        subMenu.setOnAction(new EventHandler<ActionEvent>()
                                {
                            public void handle(ActionEvent t) 
                            {
                                _appManager.getFrameworkManager().openForm(actionCommand, null, false);
                            }
                        });    
                       
                        continue;
                    }
                    if (leafProperties instanceof EJCoreMenuLeafActionProperties)
                    {
                        EJCoreMenuLeafActionProperties action = (EJCoreMenuLeafActionProperties) leafProperties;
                        MenuItem subMenu = new MenuItem(leafProperties.getDisplayName());
                        rootSubmenu.getItems().add(subMenu);
                        if (leafProperties.getIconName() != null && !leafProperties.getIconName().trim().isEmpty())
                        {
                            subMenu.setGraphic(new ImageView(EJFXImageRetriever.get(leafProperties.getIconName())));
                        }
                        if (actionProcessor != null)
                        {
                            final String actionCommand = action.getMenuAction();
                            subMenu.setOnAction(new EventHandler<ActionEvent>()
                                    {
                                public void handle(ActionEvent t) 
                                {
                                    try
                                    {
                                        actionProcessor.executeActionCommand(actionCommand);
                                    }
                                    catch (EJActionProcessorException e)
                                    {
                                        _appManager.getApplicationMessenger().handleException(e, true);
                                    }
                                }
                            }); 
                           
                        }
                        continue;
                    }
                }

                if(rootSubmenu.getItems().isEmpty())
                {
                    menuBar.getMenus().remove(rootSubmenu);
                }
                return menuBar;
            }

        }

        return null;
    }

    private void addEJCoreMenuLeafProperties(ObservableList<MenuItem> observableList, EJCoreMenuLeafProperties leafProperties)
    {

        if (leafProperties instanceof EJCoreMenuLeafContainer)
        {
            EJCoreMenuLeafContainer container = (EJCoreMenuLeafContainer) leafProperties;
            Menu subMenu = new Menu(leafProperties.getDisplayName());
            
            observableList.add(subMenu);
            if (leafProperties.getIconName() != null && !leafProperties.getIconName().trim().isEmpty())
            {
                subMenu.setGraphic(new ImageView(EJFXImageRetriever.get(leafProperties.getIconName())));
            }

            for (EJCoreMenuLeafProperties subLeafProperties : container.getLeaves())
            {
                addEJCoreMenuLeafProperties(subMenu.getItems(), subLeafProperties);
            }
            return;
        }
        if (leafProperties instanceof EJCoreMenuLeafSpacerProperties)
        {
            observableList.add(new SeparatorMenuItem());
            return;
        }
        if (leafProperties instanceof EJCoreMenuLeafFormProperties)
        {
            EJCoreMenuLeafFormProperties formLeaf = (EJCoreMenuLeafFormProperties) leafProperties;
            MenuItem subMenu = new MenuItem(leafProperties.getDisplayName());
            observableList.add(subMenu);
            if (leafProperties.getIconName() != null && !leafProperties.getIconName().trim().isEmpty())
            {
                subMenu.setGraphic(new ImageView(EJFXImageRetriever.get(leafProperties.getIconName())));
            }
            final String actionCommand = formLeaf.getFormName();
            subMenu.setOnAction(new EventHandler<ActionEvent>()
                    {
                public void handle(ActionEvent t) 
                {
                    _appManager.getFrameworkManager().openForm(actionCommand, null, false);
                }
            });    
           
            return;
        }
        if (leafProperties instanceof EJCoreMenuLeafActionProperties)
        {
            EJCoreMenuLeafActionProperties action = (EJCoreMenuLeafActionProperties) leafProperties;
            MenuItem subMenu = new MenuItem(leafProperties.getDisplayName());
            observableList.add(subMenu);
            if (leafProperties.getIconName() != null && !leafProperties.getIconName().trim().isEmpty())
            {
                subMenu.setGraphic(new ImageView(EJFXImageRetriever.get(leafProperties.getIconName())));
            }
            if (actionProcessor != null)
            {
                final String actionCommand = action.getMenuAction();
                subMenu.setOnAction(new EventHandler<ActionEvent>()
                        {
                    public void handle(ActionEvent t) 
                    {
                        try
                        {
                            actionProcessor.executeActionCommand(actionCommand);
                        }
                        catch (EJActionProcessorException e)
                        {
                            _appManager.getApplicationMessenger().handleException(e, true);
                        }
                    }
                }); 
               
            }
            return;
        }
    }

    

    public  void buildMenuProperties(EJFXApplicationManager applicationManager, Stage primaryStage, String menuConfigID)
    {
       _appManager = applicationManager;
       menuId = menuConfigID;
       MenuBar menuBar = createComponent();
       if(menuBar!=null)
       {
           menuBar.setUseSystemMenuBar(true);
           ((BorderPane) primaryStage.getScene().getRoot()).setTop( menuBar);
          
       }
     
    }

}

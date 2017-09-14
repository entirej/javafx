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
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import org.entirej.applicationframework.fx.application.EJFXApplicationManager;
import org.entirej.applicationframework.fx.application.interfaces.EJFXAppComponentRenderer;
import org.entirej.applicationframework.fx.application.interfaces.EJFXApplicationComponent;
import org.entirej.applicationframework.fx.application.interfaces.EJFXFormChosenListener;
import org.entirej.applicationframework.fx.utils.EJFXImageRetriever;
import org.entirej.framework.core.EJActionProcessorException;
import org.entirej.framework.core.EJApplicationException;
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

public class EJFXMenuTreeComponent implements EJFXApplicationComponent, EJFXAppComponentRenderer
{
    private EJMenuActionProcessor  actionProcessor = null;
    private EJFXApplicationManager _appManager;
    private TreeView<String>       menuControl;
    private String                 menuId          = null;
    public static final String     MENU_GROUP      = "MENU_GROUP";

    private Map<Object, Runnable>  actionItems     = new WeakHashMap<>();

    @Override
    public void fireFormOpened(EJInternalForm openedForm)
    {
        // no impl

    }

    @Override
    public void fireFormClosed(EJInternalForm closedForm)
    {
        // no impl
    }

    @Override
    public void fireFormSelected(EJInternalForm selectedForm)
    {
        // no impl

    }

    @Override
    public void addFormChosenListener(EJFXFormChosenListener formChosenListener)
    {
        // no impl

    }

    @Override
    public void removeFormChosenListener(EJFXFormChosenListener formChosenListener)
    {
        // no impl

    }

    @Override
    public Node createComponent()
    {
        TreeItem<String> rootItem = new TreeItem<>("ROOT");
        menuControl = new TreeView<>(rootItem);
        menuControl.setOnMouseReleased(new EventHandler<MouseEvent>()
        {

            @Override
            public void handle(MouseEvent e)
            {
                if (e.getClickCount() > 1)
                {
                    TreeItem<String> selectedItem = menuControl.getSelectionModel().getSelectedItem();
                    if (selectedItem != null && actionItems.containsKey(selectedItem))
                    {
                        Platform.runLater(actionItems.get(selectedItem));
                    }
                }

            }
        });
        menuControl.setOnKeyPressed(new EventHandler<KeyEvent>()
        {

            @Override
            public void handle(KeyEvent event)
            {
                if (event.getCode() == KeyCode.ENTER)
                {
                    TreeItem<String> selectedItem = menuControl.getSelectionModel().getSelectedItem();
                    if (selectedItem != null && actionItems.containsKey(selectedItem))
                    {
                        Platform.runLater(actionItems.get(selectedItem));
                    }
                }

            }
        });
        menuControl.showRootProperty().set(false);
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
                for (EJCoreMenuLeafProperties leafProperties : leaves)
                {
                    addEJCoreMenuLeafProperties(rootItem, leafProperties, true);
                }

                return menuControl;
            }

        }

        return menuControl;
    }

    private void addEJCoreMenuLeafProperties(TreeItem<String> parent, EJCoreMenuLeafProperties leafProperties, boolean expand)
    {

        if (leafProperties instanceof EJCoreMenuLeafContainer)
        {
            EJCoreMenuLeafContainer container = (EJCoreMenuLeafContainer) leafProperties;
            TreeItem<String> subMenu = new TreeItem<>(leafProperties.getDisplayName());
            subMenu.setExpanded(expand);
            parent.getChildren().add(subMenu);
            if (leafProperties.getIconName() != null && !leafProperties.getIconName().trim().isEmpty())
            {
                subMenu.setGraphic(new ImageView(EJFXImageRetriever.get(leafProperties.getIconName())));
            }

            for (EJCoreMenuLeafProperties subLeafProperties : container.getLeaves())
            {
                addEJCoreMenuLeafProperties(subMenu, subLeafProperties, false);
            }
            return;
        }
        if (leafProperties instanceof EJCoreMenuLeafSpacerProperties)
        {
            // parent.addSeparatorItem();
            return;
        }
        if (leafProperties instanceof EJCoreMenuLeafFormProperties)
        {
            EJCoreMenuLeafFormProperties formLeaf = (EJCoreMenuLeafFormProperties) leafProperties;
            TreeItem<String> subMenu = new TreeItem<>(leafProperties.getDisplayName());
            parent.getChildren().add(subMenu);
            if (leafProperties.getIconName() != null && !leafProperties.getIconName().trim().isEmpty())
            {
                subMenu.setGraphic(new ImageView(EJFXImageRetriever.get(leafProperties.getIconName())));
            }
            final String actionCommand = formLeaf.getFormName();
            actionItems.put(subMenu, new Runnable()
            {

                @Override
                public void run()
                {
                    _appManager.getFrameworkManager().openForm(actionCommand, null, false);

                }
            });
            return;
        }
        if (leafProperties instanceof EJCoreMenuLeafActionProperties)
        {
            EJCoreMenuLeafActionProperties action = (EJCoreMenuLeafActionProperties) leafProperties;
            TreeItem<String> subMenu = new TreeItem<>(leafProperties.getDisplayName());
            parent.getChildren().add(subMenu);
            if (leafProperties.getIconName() != null && !leafProperties.getIconName().trim().isEmpty())
            {
                subMenu.setGraphic(new ImageView(EJFXImageRetriever.get(leafProperties.getIconName())));
            }
            if (actionProcessor != null)
            {
                final String actionCommand = action.getMenuAction();
                actionItems.put(subMenu, new Runnable()
                {

                    @Override
                    public void run()
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

    @Override
    public Node getGuiComponent()
    {
        return menuControl;
    }

    @Override
    public Node createContainer(EJFXApplicationManager manager, EJFrameworkExtensionProperties rendererprop)
    {
        _appManager = manager;
        if (rendererprop != null)
        {
            menuId = rendererprop.getStringProperty(MENU_GROUP);
        }
        return createComponent();
    }

}

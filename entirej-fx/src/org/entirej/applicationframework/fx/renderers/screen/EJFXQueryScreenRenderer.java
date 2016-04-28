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
package org.entirej.applicationframework.fx.renderers.screen;

import java.util.Collection;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Screen;
import javafx.stage.WindowEvent;

import org.entirej.applicationframework.fx.application.EJFXApplicationManager;
import org.entirej.applicationframework.fx.application.form.containers.AbstractDialog;
import org.entirej.applicationframework.fx.renderers.items.ItemTextChangeNotifier;
import org.entirej.applicationframework.fx.renderers.items.ItemTextChangeNotifier.TextChangeListener;
import org.entirej.applicationframework.fx.renderers.screen.definition.interfaces.EJFXScreenRendererDefinitionProperties;
import org.entirej.framework.core.EJApplicationException;
import org.entirej.framework.core.EJFrameworkManager;
import org.entirej.framework.core.EJLovBlock;
import org.entirej.framework.core.EJQueryBlock;
import org.entirej.framework.core.EJScreenItem;
import org.entirej.framework.core.data.EJDataItem;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.data.controllers.EJBlockController;
import org.entirej.framework.core.data.controllers.EJLovController;
import org.entirej.framework.core.enumerations.EJManagedScreenProperty;
import org.entirej.framework.core.enumerations.EJRecordType;
import org.entirej.framework.core.enumerations.EJScreenType;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.internal.EJInternalBlock;
import org.entirej.framework.core.properties.EJCoreQueryScreenItemProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJBlockProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;
import org.entirej.framework.core.renderers.EJManagedItemRendererWrapper;
import org.entirej.framework.core.renderers.eventhandlers.EJScreenItemValueChangedListener;
import org.entirej.framework.core.renderers.interfaces.EJItemRenderer;
import org.entirej.framework.core.renderers.interfaces.EJQueryScreenRenderer;
import org.entirej.framework.core.renderers.registry.EJBlockItemRendererRegister;
import org.entirej.framework.core.renderers.registry.EJQueryScreenItemRendererRegister;
import org.entirej.framework.core.service.EJQueryCriteria;
import org.entirej.framework.core.service.EJRestrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EJFXQueryScreenRenderer extends EJFXAbstractScreenRenderer implements EJQueryScreenRenderer, EJScreenItemValueChangedListener
{
    private final int                         QUERY_OK_ACTION_COMMAND     = 0;
    private final int                         QUERY_CANCEL_ACTION_COMMAND = 2;
    private final int                         QUERY_CLEAR_ACTION_COMMAND  = 4;
    private EJBlockController                 _block;
    private AbstractDialog                    _queryDialog;
    private EJQueryScreenItemRendererRegister _itemRegister;
    private EJFrameworkManager                _frameworkManager;

    final Logger                              logger                      = LoggerFactory.getLogger(EJFXQueryScreenRenderer.class);

    public void refreshQueryScreenRendererProperty(String propertyName)
    {
    }

    public EJQueryScreenItemRendererRegister getItemRegister()
    {
        return _itemRegister;
    }

    @Override
    public EJScreenItemController getItem(String itemName)
    {
        return _block.getScreenItem(EJScreenType.QUERY, itemName);
    }

    public void refreshItemProperty(EJCoreQueryScreenItemProperties itemProperties, EJManagedScreenProperty managedItemProperty)
    {
        EJManagedItemRendererWrapper rendererForItem = _itemRegister.getManagedItemRendererForItem(itemProperties.getReferencedItemName());
        if (rendererForItem == null)
            return;
        switch (managedItemProperty)
        {
            case VISIBLE:
                rendererForItem.setVisible(itemProperties.isVisible());
                break;
            case EDIT_ALLOWED:
                rendererForItem.setEditAllowed(itemProperties.isEditAllowed());
                break;
            case MANDATORY:
                rendererForItem.setMandatory(itemProperties.isMandatory());
                break;
            case LABEL:
                rendererForItem.setLabel(itemProperties.getLabel());
                break;
            case HINT:
                rendererForItem.setHint(itemProperties.getHint());
                break;
        }
    }

    public Object getGuiComponent()
    {
        return _queryDialog;
    }

    public void initialiseRenderer(EJBlockController block)
    {
        _block = block;

        _frameworkManager = block.getFrameworkManager();
        _itemRegister = new EJQueryScreenItemRendererRegister(block);
    }

    public void initialiseRenderer(EJLovController controller)
    {
        _block = controller.getBlock().getBlockController();
        _itemRegister = new EJQueryScreenItemRendererRegister(controller);
        _frameworkManager = controller.getFrameworkManager();
        setupQueryScreen();
    }

    public void open(EJDataRecord queryRecord)
    {
        _itemRegister.resetRegister();
        setupQueryScreen();
        _itemRegister.register(queryRecord);
        _itemRegister.initialiseRegisteredRenderers();
        _queryDialog.centreLocation();

        _queryDialog.validate();
        setFoucsItemRenderer();

        _queryDialog.show();
    }

    public void close()
    {

        _queryDialog.close();
        _queryDialog = null;
    }

    public EJDataRecord getQueryRecord()
    {
        return _itemRegister.getRegisteredRecord();
    }

    public void refreshAfterChange(EJDataRecord record)
    {
        _itemRegister.refreshAfterChange(record);
    }

    public void synchronize()
    {
    }

    EJFXApplicationManager getFXManager()
    {
        return (EJFXApplicationManager) _frameworkManager.getApplicationManager();
    }

    private void setupQueryScreen()
    {

        // Setup pane for query window
        EJFrameworkExtensionProperties rendererProperties = _block.getProperties().getQueryScreenRendererProperties();

        String title = rendererProperties.getStringProperty(EJFXScreenRendererDefinitionProperties.TITLE);
        int width = rendererProperties.getIntProperty(EJFXScreenRendererDefinitionProperties.WIDTH, 300);
        int height = rendererProperties.getIntProperty(EJFXScreenRendererDefinitionProperties.HEIGHT, 500);
        boolean maximize = rendererProperties.getBooleanProperty(EJFXScreenRendererDefinitionProperties.MAXIMIZE, false);
        final int numCols = rendererProperties.getIntProperty(EJFXScreenRendererDefinitionProperties.NUM_COLS, 1);
        final String queryButtonLabel = rendererProperties.getStringProperty(EJFXScreenRendererDefinitionProperties.EXECUTE_BUTTON_TEXT);
        final String cancelButtonLabel = rendererProperties.getStringProperty(EJFXScreenRendererDefinitionProperties.CANCEL_BUTTON_TEXT);
        final String clearButtonLabel = rendererProperties.getStringProperty(EJFXScreenRendererDefinitionProperties.CLEAR_BUTTON_TEXT);

        _queryDialog = new AbstractDialog(getFXManager().getPrimaryStage())
        {
            private static final long serialVersionUID = -4685316941898120169L;

            @Override
            public Node createBody()
            {
                GridPane _mainPane = new GridPane();
                _mainPane.setPadding(new Insets(5, 5, 5, 5));
                EJBlockProperties blockProperties = _block.getProperties();
                addAllItemGroups(blockProperties.getScreenItemGroupContainer(EJScreenType.QUERY), _mainPane, numCols, EJScreenType.QUERY);

                _block.addItemValueChangedListener(EJFXQueryScreenRenderer.this);
                ItemTextChangeNotifier.TextChangeListener changeListener = new TextChangeListener()
                {

                    @Override
                    public void changed()
                    {
                        validate();

                    }
                };
                Collection<EJManagedItemRendererWrapper> registeredRenderers = _itemRegister.getRegisteredRenderers();
                for (EJManagedItemRendererWrapper ejManagedItemRendererWrapper : registeredRenderers)
                {
                    if (ejManagedItemRendererWrapper.getUnmanagedRenderer() instanceof ItemTextChangeNotifier)
                    {
                        ((ItemTextChangeNotifier) ejManagedItemRendererWrapper.getUnmanagedRenderer()).addListener(changeListener);
                    }
                }

                return _mainPane;
            }

            public void validate()
            {

                Button button = getButton(QUERY_OK_ACTION_COMMAND);
                if (button == null)
                    return;
                Collection<EJScreenItemController> allScreenItems = _block.getAllScreenItems(EJScreenType.QUERY);
                for (EJScreenItemController ejScreenItemController : allScreenItems)
                {
                    if (!ejScreenItemController.getManagedItemRenderer().isValid())
                    {
                        button.setDisable(true);
                        return;
                    }
                }
                button.setDisable(false);
            }

            @Override
            protected void createButtonsForButtonBar()
            {
                createButton(QUERY_OK_ACTION_COMMAND, queryButtonLabel == null ? "Query" : queryButtonLabel).setDefaultButton(true);
                ;
                createButton(QUERY_CLEAR_ACTION_COMMAND, clearButtonLabel == null ? "Clear" : clearButtonLabel);
                createButton(QUERY_CANCEL_ACTION_COMMAND, cancelButtonLabel == null ? "Cancel" : cancelButtonLabel).setCancelButton(true);

            }

            @Override
            protected void buttonPressed(int buttonId)
            {
                try
                {
                    switch (buttonId)
                    {
                        case QUERY_OK_ACTION_COMMAND:
                        {
                            EJQueryBlock b = new EJLovBlock(_block.getBlock());
                            EJQueryCriteria queryCriteria = new EJQueryCriteria(b);
                            EJDataRecord record = getQueryRecord();
                            for (EJDataItem item : record.getAllItems())
                            {
                                boolean serviceItem = item.isBlockServiceItem();
                                
                                if (item.getValue() != null)
                                {
                                    if (item.getProperties().getDataTypeClass().isAssignableFrom(String.class))
                                    {
                                        String value = (String) item.getValue();
                                        if (value.contains("%"))
                                        {
                                            queryCriteria.add(EJRestrictions.like(item.getName(),serviceItem, item.getValue()));
                                        }
                                        else
                                        {
                                            queryCriteria.add(EJRestrictions.equals(item.getName(),serviceItem, item.getValue()));
                                        }
                                    }
                                    else
                                    {
                                        queryCriteria.add(EJRestrictions.equals(item.getName(),serviceItem, item.getValue()));
                                    }
                                }
                            }
                            try
                            {
                                _block.executeQuery(queryCriteria);
                            }
                            catch (EJApplicationException e)
                            {
                                setButtonEnable(buttonId, false);
                                throw e;
                            }
                            close();
                            break;
                        }
                        case QUERY_CLEAR_ACTION_COMMAND:
                        {
                            _itemRegister.clearRegisteredValues();
                            _itemRegister.register(_block.createRecord(EJRecordType.QUERY));
                            break;
                        }
                        case QUERY_CANCEL_ACTION_COMMAND:
                        {
                            close();
                            break;
                        }
                    }
                }
                catch (EJApplicationException e)
                {
                    logger.trace(e.getMessage());
                    _frameworkManager.handleException(e);
                    return;
                }
                super.buttonPressed(buttonId);

            }
        };

        _queryDialog.setOnCloseRequest(new EventHandler<WindowEvent>()
        {

            @Override
            public void handle(WindowEvent event)
            {
                _block.removeItemValueChangedListener(EJFXQueryScreenRenderer.this);
                _block.setRendererFocus(true);

            }
        });
        _queryDialog.create(width + 80, height + 100);// add
        // dialog
        // border
        // offsets
        _queryDialog.setTitle(title != null ? title : "");

        if(maximize)
        {
            _queryDialog.setMaximized(true);
        }
    }

    protected EJInternalBlock getBlock()
    {
        return _block.getBlock();
    }

    protected void registerRendererForItem(EJItemRenderer renderer, EJScreenItemController item)
    {
        _itemRegister.registerRendererForItem(renderer, item);
    }

    protected EJFrameworkExtensionProperties getItemRendererPropertiesForItem(EJScreenItemProperties item)
    {
        return ((EJCoreQueryScreenItemProperties) item).getQueryScreenRendererProperties();
    }

    public void setFocusToItem(EJScreenItem item)
    {

        EJManagedItemRendererWrapper renderer = _itemRegister.getManagedItemRendererForItem(item.getName());
        if (renderer != null)
        {
            renderer.gainFocus();
        }
    }

    @Override
    public void screenItemValueChanged(EJScreenItemController arg0, EJItemRenderer arg1,Object old,Object newval)
    {
        if (_queryDialog != null)
            _queryDialog.validate();

    }

}

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
import org.entirej.framework.core.EJScreenItem;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.data.controllers.EJEditableBlockController;
import org.entirej.framework.core.enumerations.EJManagedScreenProperty;
import org.entirej.framework.core.enumerations.EJScreenType;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.internal.EJInternalEditableBlock;
import org.entirej.framework.core.properties.EJCoreInsertScreenItemProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJBlockProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;
import org.entirej.framework.core.renderers.EJManagedItemRendererWrapper;
import org.entirej.framework.core.renderers.eventhandlers.EJScreenItemValueChangedListener;
import org.entirej.framework.core.renderers.interfaces.EJInsertScreenRenderer;
import org.entirej.framework.core.renderers.interfaces.EJItemRenderer;
import org.entirej.framework.core.renderers.registry.EJBlockItemRendererRegister;
import org.entirej.framework.core.renderers.registry.EJInsertScreenItemRendererRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EJFXInsertScreenRenderer extends EJFXAbstractScreenRenderer implements EJInsertScreenRenderer, EJScreenItemValueChangedListener
{
    private final int                          INSERT_OK_ACTION_COMMAND     = 0;
    private final int                          INSERT_CANCEL_ACTION_COMMAND = -1;

    private EJEditableBlockController          _block;
    private AbstractDialog                     _insertDialog;
    private EJInsertScreenItemRendererRegister _itemRegister;
    private EJFrameworkManager                 _frameworkManager;

    final Logger                               logger                       = LoggerFactory.getLogger(EJFXInsertScreenRenderer.class);

    public void refreshInsertScreenRendererProperty(String propertyName)
    {
    }

    public EJInsertScreenItemRendererRegister getItemRegister()
    {
        return _itemRegister;
    }

    @Override
    public EJScreenItemController getItem(String itemName)
    {
        return _block.getScreenItem(EJScreenType.INSERT, itemName);
    }

    public void refreshItemProperty(EJCoreInsertScreenItemProperties itemProperties, EJManagedScreenProperty managedItemProperty)
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
            default:
                // do nothing
        }
    }

    public Object getGuiComponent()
    {
        return _insertDialog;
    }

    public void initialiseRenderer(EJEditableBlockController block)
    {
        _block = block;
        _itemRegister = new EJInsertScreenItemRendererRegister(block);
        _frameworkManager = block.getFrameworkManager();
    }

    public void open(EJDataRecord record)
    {
        _itemRegister.resetRegister();
        setupInsertScreen();

        _itemRegister.register(record);
        _insertDialog.centreLocation();
        _insertDialog.validate();
        setFoucsItemRenderer();
        _insertDialog.show();

    }

    public void close()
    {
        _insertDialog.close();
        _insertDialog = null;
    }

    public EJDataRecord getInsertRecord()
    {
        return _itemRegister.getRegisteredRecord();
    }

    public void synchronize()
    {
    }

    public void refreshAfterChange(EJDataRecord record)
    {
        _itemRegister.refreshAfterChange(record);
    }

    EJFXApplicationManager getFXManager()
    {
        return (EJFXApplicationManager) _frameworkManager.getApplicationManager();
    }

    private void setupInsertScreen()
    {
        // Setup pane for Insert window
        EJFrameworkExtensionProperties rendererProperties = _block.getProperties().getInsertScreenRendererProperties();

        String title = rendererProperties.getStringProperty(EJFXScreenRendererDefinitionProperties.TITLE);
        int width = rendererProperties.getIntProperty(EJFXScreenRendererDefinitionProperties.WIDTH, 300);
        int height = rendererProperties.getIntProperty(EJFXScreenRendererDefinitionProperties.HEIGHT, 500);
        boolean maximize = rendererProperties.getBooleanProperty(EJFXScreenRendererDefinitionProperties.MAXIMIZE, false);
        final int numCols = rendererProperties.getIntProperty(EJFXScreenRendererDefinitionProperties.NUM_COLS, 1);
        final String insertButtonLabel = rendererProperties.getStringProperty(EJFXScreenRendererDefinitionProperties.EXECUTE_BUTTON_TEXT);
        final String cancelButtonLabel = rendererProperties.getStringProperty(EJFXScreenRendererDefinitionProperties.CANCEL_BUTTON_TEXT);

        EJFrameworkExtensionProperties extraButtonsGroup = rendererProperties.getPropertyGroup(EJFXScreenRendererDefinitionProperties.EXTRA_BUTTONS_GROUP);

        final String button1Label = extraButtonsGroup.getStringProperty(EJFXScreenRendererDefinitionProperties.EXTRA_BUTTON_ONE_LABEL);
        final String button1Command = extraButtonsGroup.getStringProperty(EJFXScreenRendererDefinitionProperties.EXTRA_BUTTON_ONE_COMMAND);
        final String button2Label = extraButtonsGroup.getStringProperty(EJFXScreenRendererDefinitionProperties.EXTRA_BUTTON_TWO_LABEL);
        final String button2Command = extraButtonsGroup.getStringProperty(EJFXScreenRendererDefinitionProperties.EXTRA_BUTTON_TWO_COMMAND);
        final String button3Label = extraButtonsGroup.getStringProperty(EJFXScreenRendererDefinitionProperties.EXTRA_BUTTON_THREE_LABEL);
        final String button3Command = extraButtonsGroup.getStringProperty(EJFXScreenRendererDefinitionProperties.EXTRA_BUTTON_THREE_COMMAND);
        final String button4Label = extraButtonsGroup.getStringProperty(EJFXScreenRendererDefinitionProperties.EXTRA_BUTTON_FOUR_LABEL);
        final String button4Command = extraButtonsGroup.getStringProperty(EJFXScreenRendererDefinitionProperties.EXTRA_BUTTON_FOUR_COMMAND);
        final String button5Label = extraButtonsGroup.getStringProperty(EJFXScreenRendererDefinitionProperties.EXTRA_BUTTON_FIVE_LABEL);
        final String button5Command = extraButtonsGroup.getStringProperty(EJFXScreenRendererDefinitionProperties.EXTRA_BUTTON_FIVE_COMMAND);

        final int ID_BUTTON_1 = 1;
        final int ID_BUTTON_2 = 2;
        final int ID_BUTTON_3 = 3;
        final int ID_BUTTON_4 = 4;
        final int ID_BUTTON_5 = 5;

        _insertDialog = new AbstractDialog(getFXManager().getPrimaryStage())
        {
            private static final long serialVersionUID = -4685316941898120169L;

            @Override
            public Node createBody()
            {
                GridPane _mainPane = new GridPane();
                _mainPane.setPadding(new Insets(5, 5, 5, 5));
                EJBlockProperties blockProperties = _block.getProperties();
                addAllItemGroups(blockProperties.getScreenItemGroupContainer(EJScreenType.INSERT), _mainPane, numCols, EJScreenType.INSERT);
                _block.addItemValueChangedListener(EJFXInsertScreenRenderer.this);

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
                Button button = getButton(INSERT_OK_ACTION_COMMAND);
                if (button == null)
                    return;
                Collection<EJScreenItemController> allScreenItems = _block.getAllScreenItems(EJScreenType.INSERT);
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
                // Add the buttons in reverse order, as they will be added from
                // left to
                // right
                addExtraButton(button5Label, ID_BUTTON_5);
                addExtraButton(button4Label, ID_BUTTON_4);
                addExtraButton(button3Label, ID_BUTTON_3);
                addExtraButton(button2Label, ID_BUTTON_2);
                addExtraButton(button1Label, ID_BUTTON_1);
                createButton(INSERT_OK_ACTION_COMMAND, insertButtonLabel == null ? "Insert" : insertButtonLabel).setDefaultButton(true);
                createButton(INSERT_CANCEL_ACTION_COMMAND, cancelButtonLabel == null ? "Cancel" : cancelButtonLabel).setCancelButton(true);
            }

            private void addExtraButton(String label, int id)
            {
                if (label == null)
                {
                    return;
                }
                createButton(id, label);

            }

            @Override
            public void canceled()
            {
                _block.insertCancelled();
            }

            @Override
            protected void buttonPressed(int buttonId)
            {
                try
                {
                    switch (buttonId)
                    {
                        case INSERT_OK_ACTION_COMMAND:
                        {

                            EJDataRecord newRecord = getInsertRecord();

                            try
                            {
                                _block.getBlock().insertRecord(newRecord);
                                if (_block.getInsertScreenDisplayProperties().getBooleanProperty(
                                        EJFXScreenRendererDefinitionProperties.SAVE_FORM_AFTER_EXECUTE, false))
                                {
                                    _block.getBlock().getForm().saveChanges();
                                }

                            }
                            catch (EJApplicationException e)
                            {

                                setButtonEnable(buttonId, false);
                                throw e;
                            }
                            close();
                            break;
                        }
                        case INSERT_CANCEL_ACTION_COMMAND:
                        {
                            _block.updateCancelled();
                            close();
                            break;
                        }
                        case ID_BUTTON_1:
                        {
                            _block.executeActionCommand(button1Command, EJScreenType.UPDATE);

                            break;
                        }
                        case ID_BUTTON_2:
                        {
                            _block.executeActionCommand(button2Command, EJScreenType.UPDATE);

                            break;
                        }
                        case ID_BUTTON_3:
                        {
                            _block.executeActionCommand(button3Command, EJScreenType.UPDATE);

                            break;
                        }
                        case ID_BUTTON_4:
                        {
                            _block.executeActionCommand(button4Command, EJScreenType.UPDATE);

                            break;
                        }
                        case ID_BUTTON_5:
                        {
                            _block.executeActionCommand(button5Command, EJScreenType.UPDATE);

                            break;
                        }

                        default:
                            _block.updateCancelled();

                            break;

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
        _insertDialog.setOnCloseRequest(new EventHandler<WindowEvent>()
        {

            @Override
            public void handle(WindowEvent event)
            {
                _block.removeItemValueChangedListener(EJFXInsertScreenRenderer.this);
                _block.setRendererFocus(true);

            }
        });
        _insertDialog.create(width + 80, height + 100);// add
        // dialog
        // border
        // offsets
        _insertDialog.setTitle(title != null ? title : "");
        
        if(maximize)
        {
            _insertDialog.setMaximized(true);
        }
    }

    protected EJInternalEditableBlock getBlock()
    {
        return _block.getBlock();
    }

    protected void registerRendererForItem(EJItemRenderer renderer, EJScreenItemController item)
    {
        _itemRegister.registerRendererForItem(renderer, item);
    }

    protected EJFrameworkExtensionProperties getItemRendererPropertiesForItem(EJScreenItemProperties item)
    {
        return ((EJCoreInsertScreenItemProperties) item).getInsertScreenRendererProperties();
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
    public boolean screenItemValueChanged(EJScreenItemController arg0, EJItemRenderer arg1,Object newval)
    {
        if (_insertDialog != null)
        {
            _insertDialog.validate();
        }
        return  false;
    }

}

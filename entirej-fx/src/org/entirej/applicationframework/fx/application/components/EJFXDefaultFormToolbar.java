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

import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;

import org.entirej.applicationframework.fx.application.EJFXApplicationManager;
import org.entirej.applicationframework.fx.application.components.actions.EJFXAction;
import org.entirej.applicationframework.fx.application.components.actions.EJFXDeleteAction;
import org.entirej.applicationframework.fx.application.components.actions.EJFXInsertAction;
import org.entirej.applicationframework.fx.application.components.actions.EJFXNextPageAction;
import org.entirej.applicationframework.fx.application.components.actions.EJFXNextRecordAction;
import org.entirej.applicationframework.fx.application.components.actions.EJFXPreviousPageAction;
import org.entirej.applicationframework.fx.application.components.actions.EJFXPreviousRecordAction;
import org.entirej.applicationframework.fx.application.components.actions.EJFXQueryAction;
import org.entirej.applicationframework.fx.application.components.actions.EJFXSaveAction;
import org.entirej.applicationframework.fx.application.components.actions.EJFXUpdateAction;
import org.entirej.applicationframework.fx.application.interfaces.EJFXAppComponentRenderer;
import org.entirej.applicationframework.fx.application.interfaces.EJFXApplicationComponent;
import org.entirej.applicationframework.fx.application.interfaces.EJFXFormChosenListener;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.data.controllers.EJBlockController;
import org.entirej.framework.core.data.controllers.EJFormController;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.internal.EJInternalForm;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.renderers.eventhandlers.EJItemFocusedEvent;
import org.entirej.framework.core.renderers.interfaces.EJItemRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EJFXDefaultFormToolbar implements EJFXFormToolbar, EJFXAppComponentRenderer, EJFXApplicationComponent
{
    final Logger                   logger = LoggerFactory.getLogger(EJFXDefaultFormToolbar.class);

    private EJItemFocusedEvent     _itemFocusedEvent;
    private ToolBar                _toolBar;

    private EJFXAction             _toolbarQueryAction;
    private EJFXAction             _toolbarPreviousRecordAction;
    private EJFXAction             _toolbarNextRecordAction;
    private EJFXAction             _toolbarPreviousPageAction;
    private EJFXAction             _toolbarNextPageAction;
    private EJFXAction             _toolbarInsertAction;
    private EJFXAction             _toolbarUpdateAction;
    private EJFXAction             _toolbarDeleteAction;
    private EJFXAction             _toolbarSaveAction;
    private EJFXApplicationManager manager;

    /**
     * Return the {@link CoolBar} component for this toolbar
     * 
     * @return The {@link CoolBar} component
     */
    public ToolBar getComponent()
    {
        if (_toolBar == null)
            throw new IllegalAccessError("Call createCoolbar() before access getCoolbarComponent()");

        return _toolBar;
    }

    public EJInternalForm getForm()
    {
        return (manager != null && manager.getFormContainer() != null) ? manager.getFormContainer().getActiveForm() : null;
    }

    public void blockFocusLost(EJBlockController focused)
    {
        synchronize(focused);
    }

    public void blockFocusGained(EJBlockController focused)
    {
        synchronize(focused);
    }

    public EJScreenItemController getFocusedItem()
    {
        if (_itemFocusedEvent != null)
        {
            return _itemFocusedEvent.getItem();
        }
        else
        {
            return null;
        }

    }

   

    public void focusedGained(EJDataRecord focusedRecord)
    {
        if (focusedRecord != null)
        {
            synchronize(focusedRecord.getBlock().getBlockController());
        }
    }

    public void synchronize(EJBlockController focusedBlock)
    {
        logger.trace("START synchronize");
        _toolbarQueryAction.synchronize(focusedBlock);
        _toolbarPreviousRecordAction.synchronize(focusedBlock);
        _toolbarNextRecordAction.synchronize(focusedBlock);
        _toolbarNextPageAction.synchronize(focusedBlock);
        _toolbarPreviousPageAction.synchronize(focusedBlock);
        _toolbarInsertAction.synchronize(focusedBlock);
        _toolbarUpdateAction.synchronize(focusedBlock);
        _toolbarDeleteAction.synchronize(focusedBlock);
        _toolbarSaveAction.synchronize(focusedBlock);
        logger.trace("END synchronize");
    }

    public void focusGained(EJItemFocusedEvent itemFocusedEvent)
    {
        _itemFocusedEvent = itemFocusedEvent;

        if (itemFocusedEvent == null || itemFocusedEvent.getItem().getBlock() == null)
        {
            synchronize(null);
        }
        else
        {
            synchronize(itemFocusedEvent.getItem().getBlock().getBlockController());
        }
    }

    public void focusLost(EJItemFocusedEvent itemFocusedEvent)
    {
        _itemFocusedEvent = null;
        if (itemFocusedEvent == null || itemFocusedEvent.getItem().getBlock() == null)
        {
            synchronize(null);
        }
        else
        {
            synchronize(itemFocusedEvent.getItem().getBlock().getBlockController());
        }
    }

    public void disable()
    {
        _toolbarQueryAction.setEnabled(false);
        _toolbarPreviousRecordAction.setEnabled(false);
        _toolbarNextRecordAction.setEnabled(false);
        _toolbarNextPageAction.setEnabled(false);
        _toolbarPreviousPageAction.setEnabled(false);
        _toolbarInsertAction.setEnabled(false);
        _toolbarUpdateAction.setEnabled(false);
        _toolbarDeleteAction.setEnabled(false);
        _toolbarSaveAction.setEnabled(false);
    }

    private void initialise()
    {
        _toolbarQueryAction = new EJFXQueryAction(this);
        _toolbarPreviousRecordAction = new EJFXPreviousRecordAction(this);
        _toolbarNextRecordAction = new EJFXNextRecordAction(this);
        _toolbarNextPageAction = new EJFXNextPageAction(this);
        _toolbarPreviousPageAction = new EJFXPreviousPageAction(this);
        _toolbarInsertAction = new EJFXInsertAction(this);
        _toolbarUpdateAction = new EJFXUpdateAction(this);
        _toolbarDeleteAction = new EJFXDeleteAction(this);
        _toolbarSaveAction = new EJFXSaveAction(this);

    }

    protected EJFXAction[] getActions()
    {
        return new EJFXAction[] { _toolbarQueryAction, null, _toolbarPreviousRecordAction, _toolbarNextRecordAction, null, _toolbarPreviousPageAction,
                _toolbarNextPageAction, null, _toolbarInsertAction, _toolbarUpdateAction, _toolbarSaveAction, null, _toolbarDeleteAction };

    }

    @Override
    public Node createComponent()
    {
        _toolBar = new ToolBar();
        _toolBar.getStyleClass().add("ej-toolbar");
        addItems();
        return _toolBar;

    }

    protected void addItems()
    {
        initialise();

        EJFXAction[] actions = getActions();
        for (final EJFXAction action : actions)
        {
            if (action == null)
            {
                _toolBar.getItems().add(new Separator());
                continue;
            }
            _toolBar.getItems().add(action);
            action.setGraphic(new ImageView(action.getImage()));
            action.getStyleClass().add("ej-toolbar-item");
        }

        disable();
    }

    @Override
    public Node getGuiComponent()
    {
        return getComponent();
    }

    @Override
    public Node createContainer(EJFXApplicationManager manager, EJFrameworkExtensionProperties rendererprop)
    {
        this.manager = manager;
        return createComponent();
    }

    @Override
    public void fireFormSelected(EJInternalForm selectedForm)
    {
        synchronize(selectedForm != null && selectedForm.getFocusedBlock() != null ? selectedForm.getFocusedBlock().getBlockController() : null);

    }

    @Override
    public void fireFormOpened(EJInternalForm openedForm)
    {
        openedForm.addFormEventListener(this);
        openedForm.addBlockFocusedListener(this);
        openedForm.addItemValueChangedListener(this);
        openedForm.addItemFocusListener(this);
        openedForm.addNewRecordFocusedListener(this);
        synchronize(openedForm != null && openedForm.getFocusedBlock() != null ? openedForm.getFocusedBlock().getBlockController() : null);

    }

    @Override
    public void fireFormClosed(EJInternalForm closedForm)
    {
        closedForm.removeFormEventListener(this);
        closedForm.removeBlockFocusedListener(this);
        closedForm.removeItemFocusListener(this);
        closedForm.removeItemValueChangedListener(this);
        closedForm.removeNewRecordFocusedListener(this);
        EJInternalForm form = getForm();
        synchronize(form != null && closedForm.getFocusedBlock() != null ? form.getFocusedBlock().getBlockController() : null);

    }

    @Override
    public void addFormChosenListener(EJFXFormChosenListener formChosenListener)
    {
        // ignore

    }

    @Override
    public void removeFormChosenListener(EJFXFormChosenListener formChosenListener)
    {
        // ignore

    }

    @Override
    public void formSaved(EJFormController savedForm)
    {
        synchronize(savedForm.getFocusedBlockController());

    }

    @Override
    public void formCleared(EJFormController clearForm)
    {

        synchronize(clearForm.getFocusedBlockController());

    }

    

    @Override
    public boolean screenItemValueChanged(EJScreenItemController item, EJItemRenderer changedRenderer, Object newValue)
    {
        synchronize(item.getBlock().getBlockController());
        return  false;
    }
}

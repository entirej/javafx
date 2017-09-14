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
package org.entirej.applicationframework.fx.application.form.containers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;

import org.entirej.applicationframework.fx.application.EJFXApplicationManager;
import org.entirej.applicationframework.fx.application.interfaces.EJFXAppComponentRenderer;
import org.entirej.applicationframework.fx.application.interfaces.EJFXFormContainer;
import org.entirej.applicationframework.fx.application.interfaces.EJFXFormSelectedListener;
import org.entirej.applicationframework.fx.renderers.form.EJFXFormRenderer;
import org.entirej.framework.core.data.controllers.EJPopupFormController;
import org.entirej.framework.core.internal.EJInternalForm;
import org.entirej.framework.core.properties.EJCoreFormProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;

public class EJFXTabPaneFormContainer implements EJFXFormContainer, EJFXAppComponentRenderer
{
    private EJFXApplicationManager         _manager;
    private TabPane                        _folder;
    private EJFXFormPopUp                  _formPopup;
    private Map<EJInternalForm, Tab>       _tabPages              = new HashMap<EJInternalForm, Tab>();
    private List<EJFXFormSelectedListener> _formSelectedListeners = new ArrayList<EJFXFormSelectedListener>(1);

    @Override
    public Node createContainer(EJFXApplicationManager manager, EJFrameworkExtensionProperties rendererprop)
    {
        _manager = manager;
        return createContainer();
    }

    @Override
    public Node getGuiComponent()
    {
        return _folder;
    }

    protected EJInternalForm getFormByTab(Tab tabItem)
    {
        Set<Entry<EJInternalForm, Tab>> entries = _tabPages.entrySet();
        for (Entry<EJInternalForm, Tab> entry : entries)
        {
            if (entry.getValue().equals(tabItem))
                return entry.getKey();
        }
        return null;
    }

    public Node createContainer()
    {

        _folder = new TabPane();
        _folder.setTabClosingPolicy(TabClosingPolicy.SELECTED_TAB);
        _folder.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>()
        {

            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue)
            {
                EJInternalForm form = getFormByTab(newValue);

                if (form != null)
                {

                    for (EJFXFormSelectedListener listener : _formSelectedListeners)
                    {
                        listener.fireFormSelected(form);
                    }
                    form.focusGained();
                }

            }
        });

        return _folder;
    }

    @Override
    public EJInternalForm addForm(final EJInternalForm form)
    {
        Tab tabItem = new Tab();
        _tabPages.put(form, tabItem);
        tabItem.setOnClosed(new EventHandler<Event>()
        {

            @Override
            public void handle(Event event)
            {

                try
                {
                    form.close();
                }
                catch (Exception e)
                {
                    form.getFrameworkManager().getApplicationManager().handleException(e);
                }

            }
        });
        EJFXFormRenderer renderer = ((EJFXFormRenderer) form.getRenderer());

        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(5, 5, 5, 5));
        Node node = renderer.createControl();
        if (node instanceof Region)
        {
            ((Region) node).setPadding(new Insets(0, 0, 0, 0));
            ((Region) node).setMinSize(form.getProperties().getFormWidth(), form.getProperties().getFormHeight());
        }
        if (node instanceof Control)
        {
            ((Control) node).setMinSize(form.getProperties().getFormWidth(), form.getProperties().getFormHeight());
        }
        borderPane.setCenter(node);
        final ScrollPane scrollComposite = new ScrollPane();

        scrollComposite.setContent(borderPane);
        scrollComposite.setFitToHeight(true);
        scrollComposite.setFitToWidth(true);
        // scrollComposite.setPrefSize(form.getProperties().getFormWidth(),
        // form.getProperties().getFormHeight());

        final EJCoreFormProperties coreFormProperties = form.getProperties();
        tabItem.setText((coreFormProperties.getTitle() == null) ? coreFormProperties.getName() : coreFormProperties.getTitle());

        tabItem.setContent(scrollComposite);
        _folder.getTabs().add(tabItem);
        _folder.getSelectionModel().select(tabItem);
        renderer.gainInitialFocus();
        return form;
    }

    @Override
    public void openPopupForm(EJPopupFormController popupController)
    {
        _formPopup = new EJFXFormPopUp(_manager.getPrimaryStage(), popupController);
        _formPopup.showForm();
    }

    @Override
    public void popupFormClosed()
    {
        if (_formPopup != null)
        {
            _formPopup.close();
            _formPopup = null;
        }

    }

    @Override
    public Collection<EJInternalForm> getAllForms()
    {
        return new ArrayList<EJInternalForm>(_tabPages.keySet());
    }

    @Override
    public boolean containsForm(String formName)
    {
        Collection<EJInternalForm> opendForms = getAllForms();
        for (EJInternalForm form : opendForms)
        {
            if (form.getProperties().getName().equalsIgnoreCase(formName))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public EJInternalForm getActiveForm()
    {
        if (_folder != null)
        {
            Tab selection = _folder.getSelectionModel().getSelectedItem();
            if (selection != null)
            {
                return getFormByTab(selection);
            }
        }
        return null;
    }

    @Override
    public void closeForm(EJInternalForm form)
    {
        Tab tabItem = _tabPages.get(form);
        if (tabItem != null)
        {

            _tabPages.remove(form);
            _folder.getTabs().remove(tabItem);
        }

    }
    
    @Override
    public void updateFormTitle(EJInternalForm form)
    {
        Tab tabItem = _tabPages.get(form);
        if (tabItem != null)
        {

            tabItem.setText(form.getProperties().getTitle());
        }
        
    }

    public boolean closeAllForms()
    {
        Collection<EJInternalForm> opendForms = getAllForms();
        for (EJInternalForm form : opendForms)
        {
            closeForm(form);
        }

        return true;
    }

    @Override
    public void addFormSelectedListener(EJFXFormSelectedListener selectionListener)
    {
        _formSelectedListeners.add(selectionListener);

    }

    @Override
    public void removeFormSelectedListener(EJFXFormSelectedListener selectionListener)
    {
        _formSelectedListeners.remove(selectionListener);

    }

    public EJInternalForm switchToForm(String key)
    {
        for (EJInternalForm form : _tabPages.keySet())
        {
            if (form.getProperties().getName().equalsIgnoreCase(key))
            {
                EJFXFormRenderer renderer = ((EJFXFormRenderer) form.getRenderer());

                _folder.getSelectionModel().select(_tabPages.get(form));
                renderer.gainInitialFocus();
                return form;
            }
        }
        return null;
    }
    
    @Override
    public void switchToForm(EJInternalForm aform)
    {
        for (EJInternalForm form : _tabPages.keySet())
        {
            if (form.equals(form))
            {
                EJFXFormRenderer renderer = ((EJFXFormRenderer) form.getRenderer());

                _folder.getSelectionModel().select(_tabPages.get(form));
                renderer.gainInitialFocus();
               break;
            }
        }
        
    }

}

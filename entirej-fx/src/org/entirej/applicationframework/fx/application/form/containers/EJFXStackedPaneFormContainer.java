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
package org.entirej.applicationframework.fx.application.form.containers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javafx.scene.Node;

import org.entirej.applicationframework.fx.application.EJFXApplicationManager;
import org.entirej.applicationframework.fx.application.interfaces.EJFXAppComponentRenderer;
import org.entirej.applicationframework.fx.application.interfaces.EJFXFormContainer;
import org.entirej.applicationframework.fx.application.interfaces.EJFXFormSelectedListener;
import org.entirej.applicationframework.fx.layout.EJFXEntireJStackedPane;
import org.entirej.applicationframework.fx.renderers.form.EJFXFormRenderer;
import org.entirej.framework.core.data.controllers.EJPopupFormController;
import org.entirej.framework.core.internal.EJInternalForm;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;

public class EJFXStackedPaneFormContainer implements EJFXFormContainer, EJFXAppComponentRenderer
{
    private EJFXApplicationManager         _manager;
    private EJFXEntireJStackedPane         _stackPane;
    private EJFXFormPopUp                  _formPopup;
    private Map<EJInternalForm, String>    _stackedPages          = new HashMap<EJInternalForm, String>();
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
        return _stackPane;
    }

    protected EJInternalForm getFormByPage(String key)
    {
        Set<Entry<EJInternalForm, String>> entries = _stackedPages.entrySet();
        for (Entry<EJInternalForm, String> entry : entries)
        {
            if (entry.getValue().equals(key))
                return entry.getKey();
        }
        return null;
    }

    public Node createContainer()
    {

        _stackPane = new EJFXEntireJStackedPane();

        return _stackPane;
    }

    @Override
    public EJInternalForm addForm(EJInternalForm form)
    {
        String name = form.getFormController().getEJForm().getName();
        EJInternalForm formByPage = getFormByPage(name);
        if (formByPage != null)
        {
            _stackPane.showPane(name);
            EJFXFormRenderer renderer = ((EJFXFormRenderer) formByPage.getRenderer());
            renderer.gainInitialFocus();

            formByPage.focusGained();
            for (EJFXFormSelectedListener listener : _formSelectedListeners)
            {
                listener.fireFormSelected(formByPage);
            }
            return formByPage;
        }
        _stackedPages.put(form, name);

        EJFXFormRenderer renderer = ((EJFXFormRenderer) form.getRenderer());
        _stackPane.add(name, renderer.createControl());
        _stackPane.showPane(name);
        renderer.gainInitialFocus();

        form.focusGained();
        for (EJFXFormSelectedListener listener : _formSelectedListeners)
        {
            listener.fireFormSelected(form);
        }
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
        return new ArrayList<EJInternalForm>(_stackedPages.keySet());
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
        if (_stackPane != null)
        {
            return getFormByPage(_stackPane.getActiveControlKey());
        }
        return null;
    }

    @Override
    public void closeForm(EJInternalForm form)
    {
        String tabItem = _stackedPages.get(form);
        if (tabItem != null)
        {
            _stackPane.remove(tabItem);
            _stackedPages.remove(form);
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

    @Override
    public EJInternalForm switchToForm(String key)
    {
        for (EJInternalForm form : _stackedPages.keySet())
        {
            if (form.getProperties().getName().equalsIgnoreCase(key))
            {
                EJFXFormRenderer renderer = ((EJFXFormRenderer) form.getRenderer());

                _stackPane.showPane(_stackedPages.get(form));
                renderer.gainInitialFocus();

                form.focusGained();
                for (EJFXFormSelectedListener listener : _formSelectedListeners)
                {
                    listener.fireFormSelected(form);
                }
                return form;
            }
        }
        return null;
    }

}

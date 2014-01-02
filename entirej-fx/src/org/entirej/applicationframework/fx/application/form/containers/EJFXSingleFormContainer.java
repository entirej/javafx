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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;

import org.entirej.applicationframework.fx.application.EJFXApplicationManager;
import org.entirej.applicationframework.fx.application.interfaces.EJFXAppComponentRenderer;
import org.entirej.applicationframework.fx.renderers.interfaces.EJFXAppFormRenderer;
import org.entirej.framework.core.internal.EJInternalForm;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;

public class EJFXSingleFormContainer implements EJFXAppComponentRenderer
{

    public static final String FORM_ID = "FORM_ID";

    private Node               control;
    EJInternalForm             form;

    @Override
    public Node getGuiComponent()
    {

        return control;
    }

    public EJInternalForm getForm()
    {
        return form;
    }

    @Override
    public Node createContainer(final EJFXApplicationManager manager, EJFrameworkExtensionProperties rendererprop)
    {
        String formid = null;
        if (rendererprop != null)
        {
            formid = rendererprop.getStringProperty(FORM_ID);
        }

        if (formid != null)
        {
            try
            {
                form = manager.getFrameworkManager().createInternalForm(formid, null);
                if (form != null)
                {
                    BorderPane borderPane = new BorderPane();
                    borderPane.setPadding(new Insets(5, 5, 5, 5));
                    EJFXAppFormRenderer renderer = ((EJFXAppFormRenderer) form.getRenderer());
                    Node node = renderer.createControl();
                    if (node instanceof Region)
                    {
                        ((Region) node).setPadding(new Insets(0, 0, 0, 0));
                    }
                    borderPane.setCenter(node);
                    control = borderPane;
                    node.focusedProperty().addListener(new ChangeListener<Boolean>()
                    {
                        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
                        {
                            if (newValue.booleanValue())
                            {
                                form.focusGained();
                            }
                        }
                    });
                    return control;
                }
            }
            catch (Exception e)
            {

                manager.getApplicationMessenger().handleException(e, true);
            }
        }

        Label label = new Label();
        label.setText("Form did not found ID#:" + (formid != null ? formid : "<null>"));
        control = label;
        return control;
    }
}

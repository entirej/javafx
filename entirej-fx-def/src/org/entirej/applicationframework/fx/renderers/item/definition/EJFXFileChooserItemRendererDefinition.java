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
package org.entirej.applicationframework.fx.renderers.item.definition;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.entirej.framework.core.properties.definitions.EJPropertyDefinitionType;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinition;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionGroup;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionListener;
import org.entirej.framework.dev.properties.EJDevPropertyDefinition;
import org.entirej.framework.dev.properties.EJDevPropertyDefinitionGroup;
import org.entirej.framework.dev.properties.interfaces.EJDevScreenItemDisplayProperties;
import org.entirej.framework.dev.renderer.definition.EJDevItemRendererDefinitionControl;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevItemRendererDefinition;

public class EJFXFileChooserItemRendererDefinition implements EJDevItemRendererDefinition
{
    private static final String PROPERTY_HIDE_BORDER = "HIDE_BORDER";

    private static final String PROPERTY_TYPE        = "TYPE";
    private static final String PROPERTY_TYPE_FILE   = "FILE";
    private static final String PROPERTY_TYPE_DIRS   = "DIRS";

    public EJFXFileChooserItemRendererDefinition()
    {
    }

    @Override
    public String getRendererClassName()
    {
        return "org.entirej.applicationframework.fx.renderers.items.EJFXFileChooserItemRenderer";
    }

    @Override
    public boolean canExecuteActionCommand()
    {
        return false;
    }

    @Override
    public void loadValidValuesForProperty(EJFrameworkExtensionProperties frameworkExtensionProperties, EJPropertyDefinition propertyDefinition)
    {
        // no impl
    }

    @Override
    public void propertyChanged(EJPropertyDefinitionListener propertyDefListener, EJFrameworkExtensionProperties properties, String propertyName)
    {
        // no impl
    }

    @Override
    public EJPropertyDefinitionGroup getItemPropertyDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("File Chooser Item Renderer");

        EJDevPropertyDefinition hideBorder = new EJDevPropertyDefinition(PROPERTY_HIDE_BORDER, EJPropertyDefinitionType.BOOLEAN);
        hideBorder.setLabel("Hide Border");
        hideBorder.setDescription("Indicates if the border of the button should be hidden, this is usefull when adding pictures to a button");
        hideBorder.setDefaultValue("false");

        EJDevPropertyDefinition pic = new EJDevPropertyDefinition("PICTURE", EJPropertyDefinitionType.PROJECT_FILE);
        pic.setLabel("Picture");
        pic.setDescription("Choose an image file from you project to display on the button");

        EJDevPropertyDefinition type = new EJDevPropertyDefinition(PROPERTY_TYPE, EJPropertyDefinitionType.STRING);
        type.setLabel("Selection Type");

        type.addValidValue(PROPERTY_TYPE_FILE, "File");
        type.addValidValue(PROPERTY_TYPE_DIRS, "Dir");
        type.setDefaultValue(PROPERTY_TYPE_FILE);

        mainGroup.addPropertyDefinition(type);
        mainGroup.addPropertyDefinition(hideBorder);
        mainGroup.addPropertyDefinition(pic);

        return mainGroup;
    }

    @Override
    public EJDevItemRendererDefinitionControl getItemControl(EJDevScreenItemDisplayProperties itemDisplayProperties, Composite parent, FormToolkit toolkit)
    {

        Composite body = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.marginLeft = 0;
        layout.marginRight = 0;
        body.setLayout(layout);
        Text text = new Text(body, SWT.BORDER);
        {
            GridData gridData = new GridData();
            gridData.horizontalAlignment = GridData.FILL;
            gridData.grabExcessHorizontalSpace = true;
            text.setLayoutData(gridData);
            text.setEditable(false);
        }
        Button button = new Button(body, SWT.FLAT);
        if (itemDisplayProperties.getLabel() != null && itemDisplayProperties.getLabel().trim().length() > 0)
        {
            button.setText("Browse");
        }

        {
            GridData gridData = new GridData();
            gridData.horizontalAlignment = GridData.FILL;
            button.setLayoutData(gridData);
        }

        final EJDevItemRendererDefinitionControl control = new EJDevItemRendererDefinitionControl(itemDisplayProperties, body, true);

        
        text.addMouseListener(new MouseAdapter()
        {

            @Override
            public void mouseDown(MouseEvent e)
            {
                control.fireFocusGained();
            }

        });
        
        return control;
    }

    @Override
    public Control getLabelControl(EJDevScreenItemDisplayProperties itemProperties, Composite parent, FormToolkit toolkit)
    {
        String labelText = itemProperties.getLabel();
        Label label = new Label(parent, SWT.NULL);
        label.setText(labelText == null ? "" : labelText);
        return label;
    }

    @Override
    public boolean isReadOnly()
    {
        return false;
    }
}

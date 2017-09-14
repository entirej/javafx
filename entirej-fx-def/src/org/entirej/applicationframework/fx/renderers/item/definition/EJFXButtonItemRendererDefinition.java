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
package org.entirej.applicationframework.fx.renderers.item.definition;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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

public class EJFXButtonItemRendererDefinition implements EJDevItemRendererDefinition
{
    private static final String PROPERTY_HIDE_BORDER      = "HIDE_BORDER";
    private static final String PROPERTY_ALIGNMENT        = "ALIGNMENT";
    private static final String PROPERTY_ALIGNMENT_LEFT   = "LEFT";
    private static final String PROPERTY_ALIGNMENT_RIGHT  = "RIGHT";
    private static final String PROPERTY_ALIGNMENT_CENTER = "CENTER";

    public EJFXButtonItemRendererDefinition()
    {
    }

    @Override
    public String getRendererClassName()
    {
        return "org.entirej.applicationframework.fx.renderers.items.EJFXButtonItemRenderer";
    }

    @Override
    public boolean canExecuteActionCommand()
    {
        return true;
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
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("Button Item Renderer");

        EJDevPropertyDefinition textAllignment = new EJDevPropertyDefinition(PROPERTY_ALIGNMENT, EJPropertyDefinitionType.STRING);
        textAllignment.setLabel("Alignment");
        textAllignment.setDescription("The alignment of the buttons label or image");
        textAllignment.addValidValue(PROPERTY_ALIGNMENT_LEFT, "Left");
        textAllignment.addValidValue(PROPERTY_ALIGNMENT_RIGHT, "Right");
        textAllignment.addValidValue(PROPERTY_ALIGNMENT_CENTER, "Center");
        textAllignment.setDefaultValue(PROPERTY_ALIGNMENT_CENTER);

        EJDevPropertyDefinition hideBorder = new EJDevPropertyDefinition(PROPERTY_HIDE_BORDER, EJPropertyDefinitionType.BOOLEAN);
        hideBorder.setLabel("Hide Border");
        hideBorder.setDescription("Indicates if the border of the button should be hidden, this is usefull when adding pictures to a button");
        hideBorder.setDefaultValue("false");

        EJDevPropertyDefinition pic = new EJDevPropertyDefinition("PICTURE", EJPropertyDefinitionType.PROJECT_FILE);
        pic.setLabel("Picture");
        pic.setDescription("Choose an image file from you project to display on the button");
        
        mainGroup.addPropertyDefinition(textAllignment);
        mainGroup.addPropertyDefinition(hideBorder);
        mainGroup.addPropertyDefinition(pic);

        return mainGroup;
    }

    @Override
    public EJDevItemRendererDefinitionControl getItemControl(EJDevScreenItemDisplayProperties itemDisplayProperties, Composite parent, FormToolkit toolkit)
    {
        Button button = new Button(parent, SWT.FLAT);
        if (itemDisplayProperties.getLabel() != null && itemDisplayProperties.getLabel().trim().length() > 0)
        {
            button.setText(itemDisplayProperties.getLabel());
        }
        EJDevItemRendererDefinitionControl control = new EJDevItemRendererDefinitionControl(itemDisplayProperties, button, false);

        return control;
    }

    @Override
    public Control getLabelControl(EJDevScreenItemDisplayProperties itemDisplayProperties, Composite parent, FormToolkit toolkit)
    {
        // A button implements no label
        return null;
    }

    @Override
    public boolean isReadOnly()
    {
        return true;
    }
}

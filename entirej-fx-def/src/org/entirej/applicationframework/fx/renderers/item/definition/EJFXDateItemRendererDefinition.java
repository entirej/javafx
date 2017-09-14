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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.entirej.framework.core.properties.definitions.EJPropertyDefinitionType;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinition;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionListener;
import org.entirej.framework.dev.properties.EJDevPropertyDefinition;
import org.entirej.framework.dev.properties.EJDevPropertyDefinitionGroup;
import org.entirej.framework.dev.properties.interfaces.EJDevScreenItemDisplayProperties;
import org.entirej.framework.dev.renderer.definition.EJDevItemRendererDefinitionControl;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevItemRendererDefinition;

public class EJFXDateItemRendererDefinition implements EJDevItemRendererDefinition
{
    public static final String PROPERTY_FORMAT                = "FORMAT";
    public static final String PROPERTY_ALIGNMENT             = "ALIGNMENT";
    public static final String PROPERTY_ALIGNMENT_LEFT        = "LEFT";
    public static final String PROPERTY_ALIGNMENT_RIGHT       = "RIGHT";
    public static final String PROPERTY_ALIGNMENT_CENTER      = "CENTER";
    public static final String PROPERTY_DISPLAY_VAUE_AS_LABEL = "DISPLAY_VALUE_AS_LABEL";

    public EJFXDateItemRendererDefinition()
    {
    }

    @Override
    public String getRendererClassName()
    {
        return "org.entirej.applicationframework.fx.renderers.items.EJFXDateItemRenderer";
    }

    @Override
    public boolean canExecuteActionCommand()
    {
        return false;
    }

    @Override
    public void propertyChanged(EJPropertyDefinitionListener propertyDefListener, EJFrameworkExtensionProperties properties, String propertyName)
    {
        // no impl
    }

    @Override
    public void loadValidValuesForProperty(EJFrameworkExtensionProperties frameworkExtensionProperties, EJPropertyDefinition propertyDefinition)
    {
        if (propertyDefinition == null)
        {
            return;
        }

        if (propertyDefinition.getName().equals(PROPERTY_ALIGNMENT))
        {
            propertyDefinition.addValidValue(PROPERTY_ALIGNMENT_LEFT, "Left");
            propertyDefinition.addValidValue(PROPERTY_ALIGNMENT_RIGHT, "Right");
            propertyDefinition.addValidValue(PROPERTY_ALIGNMENT_CENTER, "Center");
        }
    }

    @Override
    public EJDevPropertyDefinitionGroup getItemPropertyDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("Date Item Renderer");
        mainGroup.setDescription("The Date Item Renderer is used to select data values from a calendar popup or by entering the date manually");
        
        EJDevPropertyDefinition textAllignment = new EJDevPropertyDefinition(PROPERTY_ALIGNMENT, EJPropertyDefinitionType.STRING);
        textAllignment.setLabel("Alignment");
        textAllignment.setDescription("The alignment of the text displayed within this item");
        textAllignment.setLoadValidValuesDynamically(true);
        textAllignment.setNotifyWhenChanged(true);

        EJDevPropertyDefinition format = new EJDevPropertyDefinition(PROPERTY_FORMAT, EJPropertyDefinitionType.STRING);
        format.setLabel("Format");
        format.setDefaultValue("dd-MM-yyyy");
        format.setDescription("The format  specified must be a valid format for the Java SimpleDateFormat class. \nFormat strings can contain multiple formats to allow partial input of dates: \nThe multipleformats are separated by the '|' character. eg: 'dd.MM.yy|dd.MM|dd' would allow \nthe user to enter either the full dd.mm.yy format or the dd.MM format or just dd. \nMonth and year of incomplete date entries are complemented with the current month and year.");

        EJDevPropertyDefinition displayValueAsLabel = new EJDevPropertyDefinition(PROPERTY_DISPLAY_VAUE_AS_LABEL, EJPropertyDefinitionType.BOOLEAN);
        displayValueAsLabel.setLabel("Display value as label");
        displayValueAsLabel.setDefaultValue("false");
        displayValueAsLabel.setDescription("Indicates if this item should be displayed as a label. Items displayed as labels cannot be modified by the user.");

        EJDevPropertyDefinition selectOnFocus = new EJDevPropertyDefinition(EJFXTextItemRendererDefinition.PROPERTY_SELECT_ON_FOCUS, EJPropertyDefinitionType.BOOLEAN);
        selectOnFocus.setLabel("Select on focus");
        selectOnFocus.setDescription("Indicates if this item should select text on focus");
        selectOnFocus.setDefaultValue("false");
        
        mainGroup.addPropertyDefinition(textAllignment);
        mainGroup.addPropertyDefinition(format);
        mainGroup.addPropertyDefinition(displayValueAsLabel);
        mainGroup.addPropertyDefinition(selectOnFocus);

        return mainGroup;
    }

    @Override
    public EJDevItemRendererDefinitionControl getItemControl(EJDevScreenItemDisplayProperties itemProperties, Composite parent, FormToolkit toolkit)
    {
        Text text = toolkit.createText(parent, "");
        return new EJDevItemRendererDefinitionControl(itemProperties, text);
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

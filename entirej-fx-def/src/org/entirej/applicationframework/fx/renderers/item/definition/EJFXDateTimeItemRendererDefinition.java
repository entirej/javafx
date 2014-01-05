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


public class EJFXDateTimeItemRendererDefinition implements EJDevItemRendererDefinition
{
    public static final String PROPERTY_TYPE                  = "TYPE";
    public static final String PROPERTY_TYPE_DATE             = "DATE";
    public static final String PROPERTY_TYPE_TIME             = "TIME";
    public static final String PROPERTY_TYPE_CALENDAR         = "CALENDAR";

    public static final String PROPERTY_DETAILS               = "DETAILS";
    public static final String PROPERTY_DETAILS_SHORT         = "SHORT";
    public static final String PROPERTY_DETAILS_MEDIUM        = "MEDIUM";
    public static final String PROPERTY_DETAILS_LONG          = "LONG";
    public static final String PROPERTY_DROP_DOWN             = "PROPERTY_DROP_DOWN";

    public static final String PROPERTY_DISPLAY_VAUE_AS_LABEL = "DISPLAY_VALUE_AS_LABEL";

    public EJFXDateTimeItemRendererDefinition()
    {
    }

    @Override
    public String getRendererClassName()
    {
        return "org.entirej.applicationframework.fx.renderers.items.EJFXDateTimeItemRenderer";
    }

    public boolean canExecuteActionCommand()
    {
        return false;
    }

    public void propertyChanged(EJPropertyDefinitionListener propertyDefListener, EJFrameworkExtensionProperties properties, String propertyName)
    {
        // no impl
    }

    public void loadValidValuesForProperty(EJFrameworkExtensionProperties frameworkExtensionProperties, EJPropertyDefinition propertyDefinition)
    {
        if (propertyDefinition == null)
        {
            return;
        }

        if (propertyDefinition.getName().equals(PROPERTY_TYPE))
        {
            propertyDefinition.addValidValue(PROPERTY_TYPE_DATE, "Date");
            propertyDefinition.addValidValue(PROPERTY_TYPE_TIME, "Time");
            propertyDefinition.addValidValue(PROPERTY_TYPE_CALENDAR, "Calendar");
        }
        if (propertyDefinition.getName().equals(PROPERTY_DETAILS))
        {
            propertyDefinition.addValidValue(PROPERTY_DETAILS_SHORT, "Short");
            propertyDefinition.addValidValue(PROPERTY_DETAILS_MEDIUM, "Medium");
            propertyDefinition.addValidValue(PROPERTY_DETAILS_LONG, "Long");
        }
    }

    public EJDevPropertyDefinitionGroup getItemPropertyDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("Date Item Renderer");

        EJDevPropertyDefinition type = new EJDevPropertyDefinition(PROPERTY_TYPE, EJPropertyDefinitionType.STRING);
        type.setLabel("Type");
        type.setDescription("Item Dispaly type");
        type.setLoadValidValuesDynamically(true);
        type.setNotifyWhenChanged(true);
        type.setDefaultValue(PROPERTY_TYPE_DATE);

        EJDevPropertyDefinition details = new EJDevPropertyDefinition(PROPERTY_DETAILS, EJPropertyDefinitionType.STRING);
        details.setLabel("Details");
        details.setDescription("Item Details");
        details.setLoadValidValuesDynamically(true);
        details.setNotifyWhenChanged(true);
        details.setDefaultValue(PROPERTY_DETAILS_MEDIUM);

        EJDevPropertyDefinition dropDown = new EJDevPropertyDefinition(PROPERTY_DROP_DOWN, EJPropertyDefinitionType.BOOLEAN);
        dropDown.setLabel("Drop Down");
        dropDown.setDefaultValue("false");
        dropDown.setDescription("Indicates if this item should be displayed selection drop down.");
        EJDevPropertyDefinition displayValueAsLabel = new EJDevPropertyDefinition(PROPERTY_DISPLAY_VAUE_AS_LABEL, EJPropertyDefinitionType.BOOLEAN);
        displayValueAsLabel.setLabel("Display value as label");
        displayValueAsLabel.setDefaultValue("false");
        displayValueAsLabel.setDescription("Indicates if this item should be displayed as a label. Items displayed as labels cannot be modified by the user.");

        mainGroup.addPropertyDefinition(type);
        mainGroup.addPropertyDefinition(details);
        mainGroup.addPropertyDefinition(dropDown);
        mainGroup.addPropertyDefinition(displayValueAsLabel);

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

    public boolean isReadOnly()
    {
        return false;
    }

}

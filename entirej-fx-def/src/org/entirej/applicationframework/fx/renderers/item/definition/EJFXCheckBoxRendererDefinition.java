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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

public class EJFXCheckBoxRendererDefinition implements EJDevItemRendererDefinition
{
    public static final String CHECKED_VALUE       = "CHECKED_VALUE";
    public static final String UNCHECKED_VALUE     = "UNCHECKED_VALUE";
    public static final String DEFAULT_VALUE       = "DEFAULT_VALUE";
    public static final String OTHER_VALUE_MAPPING = "OTHER_VALUE_MAPPING";
    public static final String TRI_STATE           = "TRI_STATE";
    public static final String CHECKED             = "CHECKED";
    public static final String UNCHECKED           = "UNCHECKED";

    public EJFXCheckBoxRendererDefinition()
    {
    }

    @Override
    public String getRendererClassName()
    {
        return "org.entirej.applicationframework.fx.renderers.items.EJFXCheckBoxItemRenderer";
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
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("Check Box Renderer");

        EJDevPropertyDefinition checkedValue = new EJDevPropertyDefinition(EJFXCheckBoxRendererDefinition.CHECKED_VALUE, EJPropertyDefinitionType.STRING);
        checkedValue.setLabel("Value when checked");
        checkedValue.setDescription("The check box will be ticked if the underlying item has a value correspinding to this value. This value must be convertable to the data type of the item that this renderer has been assigned to");

        EJDevPropertyDefinition uncheckedValue = new EJDevPropertyDefinition(EJFXCheckBoxRendererDefinition.UNCHECKED_VALUE, EJPropertyDefinitionType.STRING);
        uncheckedValue.setLabel("Value when unchecked");
        uncheckedValue.setDescription("The check box will be unticked if the underlying item has a value correspinding to this value. This value must be convertable to the data type of the item that this renderer has been assigned to");

        EJDevPropertyDefinition initialValue = new EJDevPropertyDefinition(EJFXCheckBoxRendererDefinition.DEFAULT_VALUE, EJPropertyDefinitionType.STRING);
        initialValue.setLabel("Initial Check Box state");
        initialValue.setDescription("This is the default for the check box. If the initial state is set to Checked, then the item will contain the value according to the Value When Checked property");
        initialValue.addValidValue(EJFXCheckBoxRendererDefinition.CHECKED, "Checked");
        initialValue.addValidValue(EJFXCheckBoxRendererDefinition.UNCHECKED, "Unchecked");
        initialValue.setDefaultValue(EJFXCheckBoxRendererDefinition.CHECKED);

        EJDevPropertyDefinition otherValueMapping = new EJDevPropertyDefinition(EJFXCheckBoxRendererDefinition.OTHER_VALUE_MAPPING,
                EJPropertyDefinitionType.STRING);
        otherValueMapping.setLabel("Check Box mapping of other values");
        otherValueMapping
        .setDescription("Indicates if the check box should be checked or unchecked if the underlying item contains a value that is different than the checked or unchecked value");
        otherValueMapping.addValidValue(EJFXCheckBoxRendererDefinition.CHECKED, "Checked");
        otherValueMapping.addValidValue(EJFXCheckBoxRendererDefinition.UNCHECKED, "Unchecked");
        otherValueMapping.setDefaultValue(EJFXCheckBoxRendererDefinition.CHECKED);
        otherValueMapping.setMandatory(true);

        EJDevPropertyDefinition triState = new EJDevPropertyDefinition(EJFXCheckBoxRendererDefinition.TRI_STATE, EJPropertyDefinitionType.BOOLEAN);
        triState.setLabel("Tristate on Query Screen");
        triState.setDescription("Check boxes contain either a checked or unchecked state, i.e.  True or False. However, when searching for data, you may require values with either the checked or unchecked state. To be able to search for both Checked and Unchecked values, se this property to true. This will give you a Tri-State check box on the query screen.");
        triState.setDefaultValue("false");

        mainGroup.addPropertyDefinition(checkedValue);
        mainGroup.addPropertyDefinition(uncheckedValue);
        mainGroup.addPropertyDefinition(initialValue);
        mainGroup.addPropertyDefinition(otherValueMapping);
        mainGroup.addPropertyDefinition(triState);

        return mainGroup;
    }

    @Override
    public EJDevItemRendererDefinitionControl getItemControl(EJDevScreenItemDisplayProperties itemDisplayProperties, Composite parent, FormToolkit toolkit)
    {
        String label = itemDisplayProperties.getLabel();
        Button cb = toolkit.createButton(parent, label, SWT.CHECK);

        final EJDevItemRendererDefinitionControl control = new EJDevItemRendererDefinitionControl(itemDisplayProperties, cb, false);
        cb.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                control.fireFocusGained();
            }
        });

        return control;

    }

    @Override
    public Control getLabelControl(EJDevScreenItemDisplayProperties itemDisplayProperties, Composite parent, FormToolkit toolkit)
    {
        return null;
    }

    @Override
    public boolean isReadOnly()
    {
        return false;
    }

}

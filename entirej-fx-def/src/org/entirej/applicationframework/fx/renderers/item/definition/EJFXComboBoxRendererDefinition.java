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

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.entirej.framework.core.properties.definitions.EJPropertyDefinitionType;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinition;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionGroup;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionListener;
import org.entirej.framework.dev.properties.EJDevPropertyDefinition;
import org.entirej.framework.dev.properties.EJDevPropertyDefinitionGroup;
import org.entirej.framework.dev.properties.EJDevPropertyDefinitionList;
import org.entirej.framework.dev.properties.interfaces.EJDevScreenItemDisplayProperties;
import org.entirej.framework.dev.renderer.definition.EJDevItemRendererDefinitionControl;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevItemRendererDefinition;

public class EJFXComboBoxRendererDefinition implements EJDevItemRendererDefinition
{
    public static final String LOV_DEFINITION_NAME = "LOVDEFINITION";
    public static final String DISPLAY_COLUMNS     = "DISPLAY_COLUMNS";
    public static final String COLUMN_NAME         = "COLUMN";
    public static final String COLUMN_WIDTH        = "WIDTH";
    public static final String COLUMN_FORMAT       = "FORMAT";
    public static final String COLUMN_DISPLAYED    = "DISPLAYED";
    public static final String COLUMN_RETURN_ITEM  = "RETURN_ITEM";
    public static final String VISIBLE_ITEM_COUNT  = "VISIBLE_ITEM_COUNT";
    public static final String INITIALIES_LOV      = "INITIALIES_LOV";
    public EJFXComboBoxRendererDefinition()
    {
    }

    @Override
    public String getRendererClassName()
    {
        return "org.entirej.applicationframework.fx.renderers.items.EJFXComboItemRenderer";
    }

    @Override
    public boolean canExecuteActionCommand()
    {
        return true;
    }

    @Override
    public void loadValidValuesForProperty(EJFrameworkExtensionProperties frameworkExtensionProperties, EJPropertyDefinition propertyDefinition)
    {
        if (COLUMN_NAME.equals(propertyDefinition.getName()))
        {
            String lovDefItemName = frameworkExtensionProperties.getStringProperty(LOV_DEFINITION_NAME);

            if (lovDefItemName != null)
            {
                String lovDefName = lovDefItemName.substring(0, lovDefItemName.indexOf('.'));

                Iterator<String> items = frameworkExtensionProperties.getFormProperties().getLovDefinitionItemNames(lovDefName).iterator();
                while (items.hasNext())
                {
                    String value = items.next();
                    propertyDefinition.addValidValue(value, value);
                }
            }
        }

    }

    @Override
    public void propertyChanged(EJPropertyDefinitionListener propertyDefListener, EJFrameworkExtensionProperties properties, String propertyName)
    {
        if (LOV_DEFINITION_NAME.equals(propertyName))
        {
            properties.getPropertyList(DISPLAY_COLUMNS).removeAllEntries();
        }
    }

    @Override
    public EJPropertyDefinitionGroup getItemPropertyDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("Combo Box Renderer");
        mainGroup.setDescription("Combo Boxes contain a list of data which is retreved from an Lov Definition. To give your combo boxes static values, create a service tha returns the static data, create an Lov Definition based on this service and then assign the Lov Definition to the Combo Box");
        
        EJDevPropertyDefinition lovDefName = new EJDevPropertyDefinition(LOV_DEFINITION_NAME, EJPropertyDefinitionType.LOV_DEFINITION_WITH_ITEMS);
        lovDefName.setLabel("Lov Definition Item Name");
        lovDefName.setDescription("The name of the Lov definition that will provide the data that will be displayed within the Combo Box");
        lovDefName.setNotifyWhenChanged(true);

        EJDevPropertyDefinitionList list = new EJDevPropertyDefinitionList(DISPLAY_COLUMNS, "Display Columns");
        list.setDescription("A Combo Box has a label which the user can see as the selected value and a return value which is the actual value of the item. You can provide a list of columns whos column values will be concatenated together and displayed to the user");
        
        EJDevPropertyDefinition lovItemName = new EJDevPropertyDefinition(COLUMN_NAME, EJPropertyDefinitionType.STRING);
        lovItemName.setLabel("Item");
        lovItemName.setDescription("The item to display in the Combo Box value");
        lovItemName.setLoadValidValuesDynamically(true);
        lovItemName.setMandatory(true);

        EJDevPropertyDefinition lovDisplayItem = new EJDevPropertyDefinition(COLUMN_DISPLAYED, EJPropertyDefinitionType.BOOLEAN);
        lovDisplayItem.setLabel("Displayed");
        lovDisplayItem.setDescription("Indicates if the items value will displayed in the combo list or just used for mapping of values");
        lovDisplayItem.setDefaultValue("true");

        EJDevPropertyDefinition lovItemFormat = new EJDevPropertyDefinition(COLUMN_FORMAT, EJPropertyDefinitionType.STRING);
        lovItemFormat.setLabel("Datatype Format");
        lovItemFormat.setDescription("You can provide a default formatting option for the items value before it is displayed in the Combo Box. This is most important for Numbers and Dates. EntireJ uses the standard java.text.DecimalFormat and java.text.SimpleDataFormat options (##0.#####E0, yyyy.MM.dd");

        EJDevPropertyDefinition returnItem = new EJDevPropertyDefinition(COLUMN_RETURN_ITEM, EJPropertyDefinitionType.BLOCK_ITEM);
        returnItem.setLabel("Return Item");
        returnItem.setDescription("This value will be set to the given value of the specified item when the user chooses a value from the Combo Box");

        list.addPropertyDefinition(lovItemName);
        list.addPropertyDefinition(lovDisplayItem);
        list.addPropertyDefinition(returnItem);
        list.addPropertyDefinition(lovItemFormat);

        EJDevPropertyDefinition visibleItemCount = new EJDevPropertyDefinition(VISIBLE_ITEM_COUNT, EJPropertyDefinitionType.INTEGER);
        visibleItemCount.setLabel("Visible Item Count");
        visibleItemCount.setDescription("Indicates how many values should be displayed when the combo box is opened");
        visibleItemCount.setMandatory(false);
        visibleItemCount.setDefaultValue("10");
        visibleItemCount.setNotifyWhenChanged(true);
        
        
        EJDevPropertyDefinition initialiseLov = new EJDevPropertyDefinition(INITIALIES_LOV, EJPropertyDefinitionType.BOOLEAN);
        initialiseLov.setLabel("Populate on creation");
        initialiseLov.setDescription("Because Combo Boxes are based upon lov definitions, they need to make a query to be created. Thsi could take time dependin on how many combo boxes you are displaying. You can set the Populate On Creation to false to delay the population of the Combo Box until either the items gets set to a value in the action processor or you request that the item renderer be refreshed");
        initialiseLov.setDefaultValue("true");

        mainGroup.addPropertyDefinition(lovDefName);
        mainGroup.addPropertyDefinition(initialiseLov);
        mainGroup.addPropertyDefinitionList(list);
        mainGroup.addPropertyDefinition(visibleItemCount);

        return mainGroup;
    }

    @Override
    public EJDevItemRendererDefinitionControl getItemControl(EJDevScreenItemDisplayProperties itemProperties, Composite parent, FormToolkit toolkit)
    {
        Combo combo = new Combo(parent, SWT.DROP_DOWN);
        combo.add("Value 1");
        combo.add("Value 2");
        combo.add("Value 3");

        return new EJDevItemRendererDefinitionControl(itemProperties, combo);
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

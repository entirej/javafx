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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.entirej.framework.core.extensions.properties.EJCoreFrameworkExtensionPropertyList;
import org.entirej.framework.core.properties.definitions.EJPropertyDefinitionType;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionPropertyListEntry;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinition;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionGroup;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionListener;
import org.entirej.framework.dev.properties.EJDevPropertyDefinition;
import org.entirej.framework.dev.properties.EJDevPropertyDefinitionGroup;
import org.entirej.framework.dev.properties.EJDevPropertyDefinitionList;
import org.entirej.framework.dev.properties.interfaces.EJDevScreenItemDisplayProperties;
import org.entirej.framework.dev.renderer.definition.EJDevItemRendererDefinitionControl;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevItemRendererDefinition;

public class EJFXRadioGroupItemRendererDefinition implements EJDevItemRendererDefinition
{
    public static final String SHOW_BORDER            = "SHOW_BORDER";
    public static final String DEFAULT_BUTTON         = "DEFAULT_BUTTON";
    public static final String ORIENTATION            = "ORIENTATION";
    public static final String ORIENTATION_VERTICAL   = "VERTICAL";
    public static final String ORIENTATION_HORIZONTAL = "HORIZONTAL";

    public static final String RADIO_BUTTONS          = "RADIO_BUTTONS";
    public static final String NAME                   = "NAME";
    public static final String LABEL                  = "LABEL";
    public static final String VALUE                  = "VALUE";

    private Composite          group                  = null;

    @Override
    public String getRendererClassName()
    {
        return "org.entirej.applicationframework.fx.renderers.items.EJFXRadioGroupItemRenderer";
    }

    @Override
    public EJDevItemRendererDefinitionControl getItemControl(EJDevScreenItemDisplayProperties itemDisplayProperties, Composite parent, FormToolkit toolkit)
    {
        String orientation = itemDisplayProperties.getBlockItemDisplayProperties().getItemRendererProperties().getStringProperty(ORIENTATION);
        boolean showBorder = itemDisplayProperties.getBlockItemDisplayProperties().getItemRendererProperties().getBooleanProperty(SHOW_BORDER, true);

        final List<Button> buttons = new ArrayList<Button>();

        group = new Composite(parent, SWT.SHADOW_NONE)
        {
            @Override
            public void addMouseListener(MouseListener listener)
            {

                super.addMouseListener(listener);
                for (Button button : buttons)
                {
                    button.addMouseListener(listener);
                }
            }

        };
        Composite buttonParent = group;
        if (showBorder)
        {
            group.setLayout(new FillLayout());
            buttonParent = new Group(group, SWT.NONE);
            if (itemDisplayProperties.getLabel() != null)
            {
                ((Group) buttonParent).setText(itemDisplayProperties.getLabel());
            }
        }

        if (itemDisplayProperties.getHint() != null)
        {
            buttonParent.setToolTipText(itemDisplayProperties.getHint());
        }

        if (ORIENTATION_HORIZONTAL.equals(orientation))
        {
            buttonParent.setLayout(new RowLayout(SWT.HORIZONTAL));
        }
        else
        {
            buttonParent.setLayout(new RowLayout(SWT.VERTICAL));
        }

        EJCoreFrameworkExtensionPropertyList radionButtons = itemDisplayProperties.getBlockItemDisplayProperties().getItemRendererProperties()
                .getPropertyList(RADIO_BUTTONS);
        for (EJFrameworkExtensionPropertyListEntry entry : radionButtons.getAllListEntries())
        {
            Button button = new Button(buttonParent, SWT.RADIO);
            button.setText(entry.getProperty(LABEL));

            buttons.add(button);

        }

        return new EJDevItemRendererDefinitionControl(itemDisplayProperties, group, false);
    }

    @Override
    public Control getLabelControl(EJDevScreenItemDisplayProperties arg0, Composite arg1, FormToolkit arg2)
    {
        return null;
    }

    @Override
    public boolean canExecuteActionCommand()
    {
        return true;
    }

    @Override
    public EJPropertyDefinitionGroup getItemPropertyDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("Radio Group Renderer");
        mainGroup.setDescription("Radio Groups offer the users a single choice from various options. The radio group can be displayed in wither horizontal format where the choices are alligned next to each other or vertically where they will appear in a list on beneath the other");
        
        EJDevPropertyDefinition showBorder = new EJDevPropertyDefinition(SHOW_BORDER, EJPropertyDefinitionType.BOOLEAN);
        showBorder.setLabel("Show Border");
        showBorder.setDescription("If chosen, a border will be displayed around the radio group");

        EJDevPropertyDefinition orientation = new EJDevPropertyDefinition(ORIENTATION, EJPropertyDefinitionType.STRING);
        orientation.setLabel("Orientation");
        orientation.addValidValue(ORIENTATION_HORIZONTAL, "Horizontal");
        orientation.addValidValue(ORIENTATION_VERTICAL, "Vertical");
        orientation.setMandatory(true);
        orientation.setDescription("Indicates how the radio group will be displayed. Horizontal = in a row, Vertical = in a column");

        EJDevPropertyDefinition defaultValue = new EJDevPropertyDefinition(DEFAULT_BUTTON, EJPropertyDefinitionType.STRING);
        defaultValue.setLabel("Default Button");
        defaultValue.setDescription("The combo box group should normally have one of the items selected as default. Supply the name of the default button here");

        EJDevPropertyDefinitionList list = new EJDevPropertyDefinitionList(RADIO_BUTTONS, "Radio Buttons");

        EJDevPropertyDefinition radioButtonName = new EJDevPropertyDefinition(NAME, EJPropertyDefinitionType.STRING);
        radioButtonName.setLabel("Name");
        radioButtonName.setDescription("The name of the actual button. This is important as you can access these buttons within the action processor");
        radioButtonName.setMandatory(true);

        EJDevPropertyDefinition radioButtonLabel = new EJDevPropertyDefinition(LABEL, EJPropertyDefinitionType.STRING);
        radioButtonLabel.setLabel("Label");
        radioButtonLabel.setMandatory(true);
        radioButtonLabel.setDescription("This value will be displayed to the user or translated and then displayed");
        radioButtonLabel.setMultilingual(true);

        EJDevPropertyDefinition radioButtonValue = new EJDevPropertyDefinition(VALUE, EJPropertyDefinitionType.STRING);
        radioButtonValue.setLabel("Value");
        radioButtonValue
                .setDescription("This is the value of the radio button. The value must be convertable to the data type defined for the item. If the value is left empty, the value will bu null");

        list.addPropertyDefinition(radioButtonName);
        list.addPropertyDefinition(radioButtonLabel);
        list.addPropertyDefinition(radioButtonValue);

        mainGroup.addPropertyDefinition(showBorder);
        mainGroup.addPropertyDefinition(orientation);

        mainGroup.addPropertyDefinition(defaultValue);
        mainGroup.addPropertyDefinitionList(list);

        return mainGroup;
    }

    @Override
    public void loadValidValuesForProperty(EJFrameworkExtensionProperties iframeworkextensionproperties, EJPropertyDefinition ipropertydefinition)
    {
        // no impl
    }

    @Override
    public void propertyChanged(EJPropertyDefinitionListener ipropertydefinitionlistener, EJFrameworkExtensionProperties iframeworkextensionproperties, String s)
    {
        // no impl

    }

    @Override
    public boolean isReadOnly()
    {
        return false;
    }

}


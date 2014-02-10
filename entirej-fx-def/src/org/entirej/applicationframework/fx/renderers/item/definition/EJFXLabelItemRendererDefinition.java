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
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.entirej.framework.core.properties.definitions.EJPropertyDefinitionType;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinition;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionGroup;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionListener;
import org.entirej.framework.dev.properties.EJDevPropertyDefinition;
import org.entirej.framework.dev.properties.EJDevPropertyDefinitionGroup;
import org.entirej.framework.dev.properties.interfaces.EJDevBlockItemDisplayProperties;
import org.entirej.framework.dev.properties.interfaces.EJDevScreenItemDisplayProperties;
import org.entirej.framework.dev.renderer.definition.EJDevItemRendererDefinitionControl;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevItemRendererDefinition;

public class EJFXLabelItemRendererDefinition implements EJDevItemRendererDefinition
{
    public static final String PROPERTY_CASE                 = "CASE";
    public static final String PROPERTY_CASE_UPPER           = "UPPER";
    public static final String PROPERTY_CASE_LOWER           = "LOWER";
    public static final String PROPERTY_CASE_MIXED           = "MIXED";
    public static final String PROPERTY_ALIGNMENT            = "ALIGNMENT";
    public static final String PROPERTY_ALIGNMENT_LEFT       = "LEFT";
    public static final String PROPERTY_ALIGNMENT_RIGHT      = "RIGHT";
    public static final String PROPERTY_ALIGNMENT_CENTER     = "CENTER";
    public static final String PROPERTY_PICTURE              = "PICTURE";
    public static final String PROPERTY_DISPLAY_AS_HYPERLINK = "DISPLAY_AS_HYPERLINK";
    public static final String PROPERTY_TEXT_WRAP            = "WRAP";

    public EJFXLabelItemRendererDefinition()
    {
    }

    @Override
    public String getRendererClassName()
    {
        return "org.entirej.applicationframework.fx.renderers.items.EJFXLabelItemRenderer";
    }

    @Override
    public boolean canExecuteActionCommand()
    {
        return true;
    }

    @Override
    public void propertyChanged(EJPropertyDefinitionListener propertyDefListener, EJFrameworkExtensionProperties properties, String propertyName)
    {
        // no impl
    }

    @Override
    public void loadValidValuesForProperty(EJFrameworkExtensionProperties frameworkExtensionProperties, EJPropertyDefinition propertyDefinition)
    {
        // no impl
    }

    @Override
    public EJPropertyDefinitionGroup getItemPropertyDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("Label Item Renderer");
        mainGroup.setDescription("This renderer should be used if you require dynamic labels for your block items, or be able to place a label anywhere on the screen");
        
        EJDevPropertyDefinition displayAsHyperlink = new EJDevPropertyDefinition(PROPERTY_DISPLAY_AS_HYPERLINK, EJPropertyDefinitionType.BOOLEAN);
        displayAsHyperlink.setLabel("Display as Hyperlink");
        displayAsHyperlink
                .setDescription("If this property is set, the Label will display its content as a Hyperlink, which, if pressed, will fire the labels Action Command");

        EJDevPropertyDefinition textCase = new EJDevPropertyDefinition(PROPERTY_CASE, EJPropertyDefinitionType.STRING);
        textCase.setLabel("Case");
        textCase.setDescription("The character case for the text displayed within this label");
        textCase.addValidValue(PROPERTY_CASE_UPPER, "Upper");
        textCase.addValidValue(PROPERTY_CASE_LOWER, "Lower");
        textCase.addValidValue(PROPERTY_CASE_MIXED, "Mixed");
        textCase.setDefaultValue(PROPERTY_CASE_MIXED);

        EJDevPropertyDefinition textAlignment = new EJDevPropertyDefinition(PROPERTY_ALIGNMENT, EJPropertyDefinitionType.STRING);
        textAlignment.setLabel("Alignment");
        textAlignment.setDescription("The alignment of the text displayed within this label");
        textAlignment.addValidValue(PROPERTY_ALIGNMENT_LEFT, "Left");
        textAlignment.addValidValue(PROPERTY_ALIGNMENT_RIGHT, "Right");
        textAlignment.addValidValue(PROPERTY_ALIGNMENT_CENTER, "Center");
        textAlignment.setDefaultValue(PROPERTY_ALIGNMENT_RIGHT);

        EJDevPropertyDefinition textWrap = new EJDevPropertyDefinition(PROPERTY_TEXT_WRAP, EJPropertyDefinitionType.BOOLEAN);
        textWrap.setLabel("Wrap Text");
        textWrap.setDescription("If this property is set, the Label Wraps the text");

        EJDevPropertyDefinition pic = new EJDevPropertyDefinition(PROPERTY_PICTURE, EJPropertyDefinitionType.PROJECT_FILE);
        pic.setLabel("Picture");
        pic.setDescription("It is possible to display an image in a label renderer. See also: Image Item Renderer");
        
        mainGroup.addPropertyDefinition(displayAsHyperlink);
        mainGroup.addPropertyDefinition(textCase);
        mainGroup.addPropertyDefinition(textAlignment);
        mainGroup.addPropertyDefinition(textWrap);
        mainGroup.addPropertyDefinition(pic);

        return mainGroup;
    }

    @Override
    public EJDevItemRendererDefinitionControl getItemControl(EJDevScreenItemDisplayProperties itemProperties, Composite parent, FormToolkit toolkit)
    {
        Label text = new Label(parent, SWT.NULL);

        text.setText(itemProperties.getLabel());
        
        EJDevBlockItemDisplayProperties blockItemDisplayProperties = itemProperties.getBlockItemDisplayProperties();
        if(blockItemDisplayProperties != null)
        {
            EJFrameworkExtensionProperties itemRendererProperties = blockItemDisplayProperties.getItemRendererProperties();
            
            if(itemRendererProperties!=null)
            {
                String labelAlignment  = itemRendererProperties.getStringProperty(PROPERTY_ALIGNMENT);
                if (PROPERTY_ALIGNMENT_LEFT.equals(labelAlignment))
                {
                    text.setAlignment(SWT.LEFT);
                }
                else if (PROPERTY_ALIGNMENT_RIGHT.equals(labelAlignment))
                {
                    text.setAlignment(SWT.RIGHT);
                }
                else if (PROPERTY_ALIGNMENT_CENTER.equals(labelAlignment))
                {
                    text.setAlignment(SWT.CENTER);
                }
            }
        }
        

        return new EJDevItemRendererDefinitionControl(itemProperties, text);
    }

    /**
     * Used to return the label widget for this item
     * <p>
     * If the widget does not display a label, then this method should do
     * nothing and <code>null</code> should be returned
     * 
     * @param parent
     *            The <code>Composite</code> upon which this widgets label will
     *            be displayed
     * @param screemDisplayProperties
     *            The display properties of this item
     * @param formToolkit
     *            The toolkit to use for the creation of the label widget
     * @return The label widget or <code>null</code> if this item displays no
     *         label
     */
    @Override
    public Control getLabelControl(EJDevScreenItemDisplayProperties itemProperties, Composite parent, FormToolkit toolkit)
    {
        return null;
    }

    @Override
    public boolean isReadOnly()
    {
        return true;
    }
}

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

import static org.entirej.applicationframework.fx.renderers.item.definition.EJFXTextItemRendererDefinition.PROPERTY_ALIGNMENT;
import static org.entirej.applicationframework.fx.renderers.item.definition.EJFXTextItemRendererDefinition.PROPERTY_ALIGNMENT_CENTER;
import static org.entirej.applicationframework.fx.renderers.item.definition.EJFXTextItemRendererDefinition.PROPERTY_ALIGNMENT_LEFT;
import static org.entirej.applicationframework.fx.renderers.item.definition.EJFXTextItemRendererDefinition.PROPERTY_ALIGNMENT_RIGHT;
import static org.entirej.applicationframework.fx.renderers.item.definition.EJFXTextItemRendererDefinition.PROPERTY_CASE;
import static org.entirej.applicationframework.fx.renderers.item.definition.EJFXTextItemRendererDefinition.PROPERTY_CASE_LOWER;
import static org.entirej.applicationframework.fx.renderers.item.definition.EJFXTextItemRendererDefinition.PROPERTY_CASE_MIXED;
import static org.entirej.applicationframework.fx.renderers.item.definition.EJFXTextItemRendererDefinition.PROPERTY_CASE_UPPER;
import static org.entirej.applicationframework.fx.renderers.item.definition.EJFXTextItemRendererDefinition.PROPERTY_DISPLAY_VAUE_AS_LABEL;
import static org.entirej.applicationframework.fx.renderers.item.definition.EJFXTextItemRendererDefinition.PROPERTY_MAXLENGTH;
import static org.entirej.applicationframework.fx.renderers.item.definition.EJFXTextItemRendererDefinition.PROPERTY_WRAP;

import org.eclipse.swt.SWT;
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

public class EJFXTextAreaRendererDefinition implements EJDevItemRendererDefinition
{

    public EJFXTextAreaRendererDefinition()
    {
    }

    @Override
    public String getRendererClassName()
    {
        return "org.entirej.applicationframework.fx.renderers.items.EJFXTextAreaRenderer";
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
        // no impl
    }

    @Override
    public EJPropertyDefinitionGroup getItemPropertyDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("Text Area Renderer");

        EJDevPropertyDefinition maxLength = new EJDevPropertyDefinition(PROPERTY_MAXLENGTH, EJPropertyDefinitionType.INTEGER);
        maxLength.setLabel("Maximum Length");
        maxLength.setDescription("The maximum amount of characters allowed within this item");

        EJDevPropertyDefinition textCase = new EJDevPropertyDefinition(PROPERTY_CASE, EJPropertyDefinitionType.STRING);
        textCase.setLabel("Case");
        textCase.setDescription("The character case for the text displayed within this item");
        textCase.addValidValue(PROPERTY_CASE_UPPER, "Upper");
        textCase.addValidValue(PROPERTY_CASE_LOWER, "Lower");
        textCase.addValidValue(PROPERTY_CASE_MIXED, "Mixed");

        EJDevPropertyDefinition textAlignment = new EJDevPropertyDefinition(PROPERTY_ALIGNMENT, EJPropertyDefinitionType.STRING);
        textAlignment.setLabel("Alignment");
        textAlignment.setDescription("The alignment of the text displayed within this item");
        textAlignment.addValidValue(PROPERTY_ALIGNMENT_LEFT, "Left");
        textAlignment.addValidValue(PROPERTY_ALIGNMENT_RIGHT, "Right");
        textAlignment.addValidValue(PROPERTY_ALIGNMENT_CENTER, "Center");
        textAlignment.setDefaultValue("Left");

        EJDevPropertyDefinition displayValueAsLabel = new EJDevPropertyDefinition(PROPERTY_DISPLAY_VAUE_AS_LABEL, EJPropertyDefinitionType.BOOLEAN);
        displayValueAsLabel.setLabel("Display value as label");
        displayValueAsLabel.setDefaultValue("false");
        displayValueAsLabel.setDescription("Indicates if this item should be displayed as a label. Items displayed as labels cannot be modified by the user.");

        EJDevPropertyDefinition wrapText = new EJDevPropertyDefinition(PROPERTY_WRAP, EJPropertyDefinitionType.BOOLEAN);
        wrapText.setLabel("Wrap Text");
        wrapText.setDefaultValue("true");
        wrapText.setDescription("Indicates if this item text should be wraped and should should display horizontal scroll or not.");

        mainGroup.addPropertyDefinition(maxLength);
        mainGroup.addPropertyDefinition(textCase);
        mainGroup.addPropertyDefinition(textAlignment);
        mainGroup.addPropertyDefinition(displayValueAsLabel);
        mainGroup.addPropertyDefinition(wrapText);

        return mainGroup;

    }

    @Override
    public EJDevItemRendererDefinitionControl getItemControl(EJDevScreenItemDisplayProperties itemProperties, Composite parent, FormToolkit toolkit)
    {
        Text text = toolkit.createText(parent, "", SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
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

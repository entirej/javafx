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

public class EJFXImageItemRendererDefinition implements EJDevItemRendererDefinition
{

    public static final String PROPERTY_ALIGNMENT        = "ALIGNMENT";
    public static final String PROPERTY_ALIGNMENT_LEFT   = "LEFT";
    public static final String PROPERTY_ALIGNMENT_RIGHT  = "RIGHT";
    public static final String PROPERTY_ALIGNMENT_CENTER = "CENTER";
    public static final String PROPERTY_IMAGE            = "IMAGE";

    public EJFXImageItemRendererDefinition()
    {
    }

    @Override
    public String getRendererClassName()
    {
        return "org.entirej.applicationframework.fx.renderers.items.EJFXImageItemRenderer";
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
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("Image Item Renderer");
        mainGroup.setDescription("The image item will display an image that is returned from your data source for the item. The item would contain either the name of an image within your project or provide a byte array containing the image data");
        
        EJDevPropertyDefinition pic = new EJDevPropertyDefinition(PROPERTY_IMAGE, EJPropertyDefinitionType.PROJECT_FILE);
        pic.setLabel("Default Image");
        pic.setDescription("The default image to display when this item is displayed and no image has yet been selected from the blocks data");
        
        
        EJDevPropertyDefinition alignment = new EJDevPropertyDefinition(PROPERTY_ALIGNMENT, EJPropertyDefinitionType.STRING);
        alignment.setLabel("Alignment");
        alignment.setDescription("The alignment of the image displayed within this label");
        alignment.addValidValue(PROPERTY_ALIGNMENT_LEFT, "Left");
        alignment.addValidValue(PROPERTY_ALIGNMENT_RIGHT, "Right");
        alignment.addValidValue(PROPERTY_ALIGNMENT_CENTER, "Center");
        alignment.setDefaultValue(PROPERTY_ALIGNMENT_CENTER);
        
        mainGroup.addPropertyDefinition(pic);
        mainGroup.addPropertyDefinition(alignment);

        return mainGroup;
    }

    @Override
    public EJDevItemRendererDefinitionControl getItemControl(EJDevScreenItemDisplayProperties itemProperties, Composite parent, FormToolkit toolkit)
    {
        Text text = new Text(parent, SWT.NULL);
        text.setText("IMAGE");
        text.setEditable(false);

        EJDevItemRendererDefinitionControl definitionControl = new EJDevItemRendererDefinitionControl(itemProperties, text);
        definitionControl.setUseFontDimensions(false);
        return definitionControl;
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

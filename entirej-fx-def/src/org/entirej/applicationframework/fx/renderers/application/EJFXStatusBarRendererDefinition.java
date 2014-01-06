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
package org.entirej.applicationframework.fx.renderers.application;

import org.entirej.applicationframework.fx.renderers.block.definition.interfaces.EJFXSingleRecordBlockDefinitionProperties;
import org.entirej.framework.core.properties.definitions.EJPropertyDefinitionType;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinition;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionGroup;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionListener;
import org.entirej.framework.dev.properties.EJDevPropertyDefinition;
import org.entirej.framework.dev.properties.EJDevPropertyDefinitionGroup;
import org.entirej.framework.dev.properties.EJDevPropertyDefinitionList;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevAppComponentRendererDefinition;

public class EJFXStatusBarRendererDefinition implements EJDevAppComponentRendererDefinition
{

    public static final String SECTIONS                  = "SECTIONS";
    public static final String EXPAND_X                  = "EXPAND_X";
    public static final String PARAMETER                 = "PARAMETER";
    public static final String WIDTH                     = "WIDTH";
    public static final String VISUAL_ATTRIBUTE_PROPERTY = "VISUAL_ATTRIBUTE";

    public static final String PROPERTY_ALIGNMENT        = "ALIGNMENT";
    public static final String PROPERTY_ALIGNMENT_LEFT   = "LEFT";
    public static final String PROPERTY_ALIGNMENT_RIGHT  = "RIGHT";
    public static final String PROPERTY_ALIGNMENT_CENTER = "CENTER";

    @Override
    public EJPropertyDefinitionGroup getComponentPropertyDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("STATUSBARCONFIG");
        mainGroup.setLabel("StatusBar Configuration");

        EJDevPropertyDefinitionList list = new EJDevPropertyDefinitionList(SECTIONS, "Sections");
        // TODO: write Section description

        EJDevPropertyDefinition paramater = new EJDevPropertyDefinition(PARAMETER, EJPropertyDefinitionType.STRING);// TODO:
                                                                                                                    // add
                                                                                                                    // parameter
                                                                                                                    // selection
                                                                                                                    // support
        paramater.setLabel("Paramater");
        paramater.setDescription("Application Paramater that this section represent.");// TODO:
                                                                                       // fix
                                                                                       // this
                                                                                       // description

        EJDevPropertyDefinition visualAttribute = new EJDevPropertyDefinition(VISUAL_ATTRIBUTE_PROPERTY,
                EJPropertyDefinitionType.VISUAL_ATTRIBUTE);
        visualAttribute.setLabel("Visual Attribute");
        visualAttribute.setDescription("The visual attribute that should be applied to this item");
        visualAttribute.setMandatory(false);

        EJDevPropertyDefinition displayedWidth = new EJDevPropertyDefinition(WIDTH, EJPropertyDefinitionType.INTEGER);
        displayedWidth.setLabel("Displayed Width");
        displayedWidth
                .setDescription("Indicates the width of this section. If no value or zero has been entered, the width of the item will be relevent to its contents");
        displayedWidth.setNotifyWhenChanged(true);

        EJDevPropertyDefinition expandHorizontally = new EJDevPropertyDefinition(EXPAND_X, EJPropertyDefinitionType.BOOLEAN);
        expandHorizontally.setLabel("Expand Horizontally");
        expandHorizontally.setDescription("Indicates if this section should expand horizontally when the canvas is stretched");
        expandHorizontally.setDefaultValue("true");

        EJDevPropertyDefinition textAlignment = new EJDevPropertyDefinition(PROPERTY_ALIGNMENT, EJPropertyDefinitionType.STRING);
        textAlignment.setLabel("Alignment");
        textAlignment.setDescription("The alignment of the text within the section");
        textAlignment.addValidValue(PROPERTY_ALIGNMENT_LEFT, "Left");
        textAlignment.addValidValue(PROPERTY_ALIGNMENT_RIGHT, "Right");
        textAlignment.addValidValue(PROPERTY_ALIGNMENT_CENTER, "Center");
        textAlignment.setDefaultValue(PROPERTY_ALIGNMENT_LEFT);

        list.addPropertyDefinition(paramater);
        list.addPropertyDefinition(displayedWidth);
        list.addPropertyDefinition(expandHorizontally);
        list.addPropertyDefinition(textAlignment);
        list.addPropertyDefinition(visualAttribute);

        mainGroup.addPropertyDefinitionList(list);
        return mainGroup;
    }

    @Override
    public String getRendererClassName()
    {
        return "org.entirej.applicationframework.fx.application.components.EJFXStatusBar";
    }

    @Override
    public void loadValidValuesForProperty(EJFrameworkExtensionProperties arg0, EJPropertyDefinition arg1)
    {

    }

    @Override
    public void propertyChanged(EJPropertyDefinitionListener arg0, EJFrameworkExtensionProperties arg1, String arg2)
    {

    }

}

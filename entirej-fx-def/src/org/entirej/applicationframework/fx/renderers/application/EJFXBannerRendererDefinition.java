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
package org.entirej.applicationframework.fx.renderers.application;

import org.entirej.framework.core.properties.definitions.EJPropertyDefinitionType;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinition;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionGroup;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionListener;
import org.entirej.framework.dev.properties.EJDevPropertyDefinition;
import org.entirej.framework.dev.properties.EJDevPropertyDefinitionGroup;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevAppComponentRendererDefinition;

public class EJFXBannerRendererDefinition implements EJDevAppComponentRendererDefinition
{

    public static final String IMAGE_PATH = "IMAGE_PATH";
    public static final String AUTO_SCALE = "SCALE";
    
    public static final String PROPERTY_ALIGNMENT        = "ALIGNMENT";
    public static final String PROPERTY_ALIGNMENT_LEFT   = "LEFT";
    public static final String PROPERTY_ALIGNMENT_RIGHT  = "RIGHT";
    public static final String PROPERTY_ALIGNMENT_CENTER = "CENTER";

    @Override
    public EJPropertyDefinitionGroup getComponentPropertyDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("IMAGECONFIG");
        mainGroup.setLabel("Image Configuration");

        EJDevPropertyDefinition menuId = new EJDevPropertyDefinition(IMAGE_PATH, EJPropertyDefinitionType.PROJECT_FILE);
        menuId.setLabel("Image");
        menuId.setDescription("The image that will be displayed within the banner");

        EJDevPropertyDefinition autoScale = new EJDevPropertyDefinition(AUTO_SCALE, EJPropertyDefinitionType.BOOLEAN);
        autoScale.setLabel("Auto Scale");
        autoScale.setDescription("Set this property to scale the image to the size of the banner");
        autoScale.setDefaultValue(String.valueOf(true));
        
        EJDevPropertyDefinition imageAlignment = new EJDevPropertyDefinition(PROPERTY_ALIGNMENT, EJPropertyDefinitionType.STRING);
        imageAlignment.setLabel("Alignment");
        imageAlignment.setDescription("The alignment of the image within the banner");
        imageAlignment.addValidValue(PROPERTY_ALIGNMENT_LEFT, "Left");
        imageAlignment.addValidValue(PROPERTY_ALIGNMENT_RIGHT, "Right");
        imageAlignment.addValidValue(PROPERTY_ALIGNMENT_CENTER, "Center");
        imageAlignment.setDefaultValue(PROPERTY_ALIGNMENT_LEFT);
        
        
        mainGroup.addPropertyDefinition(menuId);
        mainGroup.addPropertyDefinition(imageAlignment);
        mainGroup.addPropertyDefinition(autoScale);
        return mainGroup;
    }

    @Override
    public String getRendererClassName()
    {
        return "org.entirej.applicationframework.fx.application.components.EJFXBanner";
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

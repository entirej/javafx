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

import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinition;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionGroup;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionListener;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevAppComponentRendererDefinition;

public class EJFXStackedFormContainerRendererDefinition implements EJDevAppComponentRendererDefinition
{

    @Override
    public EJPropertyDefinitionGroup getComponentPropertyDefinitionGroup()
    {
        return null;
    }

    @Override
    public String getRendererClassName()
    {
        return "org.entirej.applicationframework.fx.application.form.containers.EJFXStackedPaneFormContainer";
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

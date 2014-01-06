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
package org.entirej.applicationframework.fx.application.components;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import org.entirej.applicationframework.fx.application.EJFXApplicationManager;
import org.entirej.applicationframework.fx.application.interfaces.EJFXAppComponentRenderer;
import org.entirej.applicationframework.fx.renderers.items.definition.interfaces.EJFXComboBoxRendererDefinitionProperties;
import org.entirej.applicationframework.fx.renderers.items.definition.interfaces.EJFXLabelItemRendererDefinitionProperties;
import org.entirej.applicationframework.fx.utils.EJFXImageRetriever;
import org.entirej.applicationframework.fx.utils.EJFXVisualAttributeUtils;
import org.entirej.framework.core.data.controllers.EJApplicationLevelParameter;
import org.entirej.framework.core.data.controllers.EJApplicationLevelParameter.ParameterChangedListener;
import org.entirej.framework.core.properties.EJCoreProperties;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionPropertyList;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionPropertyListEntry;

public class EJFXStatusBar implements EJFXAppComponentRenderer
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
    protected HBox       pane;

    @Override
    public Object getGuiComponent()
    {
        return pane;
    }

    @Override
    public Node createContainer(EJFXApplicationManager manager, EJFrameworkExtensionProperties rendererprop)
    {
        pane = new HBox(5);
        
        final EJFrameworkExtensionPropertyList propertyList = rendererprop.getPropertyList(SECTIONS);

        if (propertyList == null)
        {
            return pane;
        }
        
        for (EJFrameworkExtensionPropertyListEntry entry : propertyList.getAllListEntries())
        {
            final Label section = new Label();
            pane.getChildren().add(section);
            
            String paramName = entry.getProperty(PARAMETER);
            if(paramName!=null && paramName.length()>0)
            {
                
                final EJApplicationLevelParameter applicationLevelParameter = manager.getApplicationLevelParameter(paramName);
                if(applicationLevelParameter!=null)
                {
                    Object value = applicationLevelParameter.getValue();
                    section.setText(value==null?"":value.toString());
                    applicationLevelParameter.addParameterChangedListener(new ParameterChangedListener()
                    {
                        
                        @Override
                        public void parameterChanged(String parameterName, Object oldValue, Object newValue)
                        {
                            section.setText(newValue==null?"":newValue.toString());
                            
                        }
                    });
                }
                
            }
            
            boolean expand = Boolean.valueOf(entry.getProperty(EXPAND_X));
            if(expand)
            {
                HBox.setHgrow(section, Priority.ALWAYS);
                section.setMaxWidth(Double.MAX_VALUE);
            }
            
            String width = entry.getProperty(WIDTH);
            
            if(width!=null && width.length()>0)
            {
                try
                {
                    section.setPrefWidth(Integer.parseInt(width));
                }
                catch(Exception ex)
                {
                    //ignore
                }
                
            }
            
            section.setAlignment(getComponentStyle(entry.getProperty(PROPERTY_ALIGNMENT), Pos.CENTER_LEFT));
            
            //set VA 
            
            String visualAttribute = entry.getProperty(VISUAL_ATTRIBUTE_PROPERTY);
            if(visualAttribute!=null && visualAttribute.length()>0)
            {
                EJCoreVisualAttributeProperties va = EJCoreProperties.getInstance().getVisualAttributesContainer()
                        .getVisualAttributeProperties(visualAttribute);
                if (va != null)
                {
                    String css = EJFXVisualAttributeUtils.INSTANCE.toCSS(va);
                    if (css != null )
                    {
                        
                            section.getStyleClass().add( css);

                    }
                }
            }
        }

        return pane;
    }

    
    protected Pos getComponentStyle(String alignmentProperty, Pos style)
    {
        if (alignmentProperty != null && alignmentProperty.trim().length() > 0)
        {
            if (alignmentProperty.equals(PROPERTY_ALIGNMENT_LEFT))
            {
                return Pos.CENTER_LEFT;
            }
            else if (alignmentProperty.equals(PROPERTY_ALIGNMENT_RIGHT))
            {
                return Pos.CENTER_RIGHT;
            }
            else if (alignmentProperty.equals(PROPERTY_ALIGNMENT_CENTER))
            {
                return Pos.CENTER;
            }
        }
        return style;
    }
}

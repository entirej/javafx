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
package org.entirej.applicationframework.fx.application.components;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

import org.entirej.applicationframework.fx.application.EJFXApplicationManager;
import org.entirej.applicationframework.fx.application.interfaces.EJFXAppComponentRenderer;
import org.entirej.applicationframework.fx.utils.EJFXImageRetriever;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;

public class EJFXBanner implements EJFXAppComponentRenderer
{
    public static final String IMAGE_PATH                = "IMAGE_PATH";
    public static final String AUTO_SCALE                = "SCALE";

    public static final String PROPERTY_ALIGNMENT        = "ALIGNMENT";
    public static final String PROPERTY_ALIGNMENT_LEFT   = "LEFT";
    public static final String PROPERTY_ALIGNMENT_RIGHT  = "RIGHT";
    public static final String PROPERTY_ALIGNMENT_CENTER = "CENTER";
    protected BorderPane       pane;

    @Override
    public Object getGuiComponent()
    {
        return pane;
    }

    @Override
    public Node createContainer(EJFXApplicationManager manager, EJFrameworkExtensionProperties rendererprop)
    {
        pane = new BorderPane();
        final ImageView icon = new ImageView();

        String imagePath = null;
        String alignmentProperty = null;
        if (rendererprop != null)
        {
            imagePath = rendererprop.getStringProperty(IMAGE_PATH);

            if (rendererprop.getBooleanProperty(AUTO_SCALE, true))
            {
                pane.widthProperty().addListener(new ChangeListener<Number>()
                {

                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
                    {
                        if (pane.getWidth() > 0)
                        {
                            icon.setFitWidth(pane.getWidth());
                        }
                        else
                        {
                            icon.setFitWidth(0);
                        }

                    }
                });
                pane.heightProperty().addListener(new ChangeListener<Number>()
                {

                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
                    {
                        if (pane.getHeight() > 0)
                        {
                            icon.setFitHeight(pane.getHeight());
                        }
                        else
                        {
                            icon.setFitHeight(0);
                        }

                    }
                });
                icon.setPreserveRatio(true);
                icon.setSmooth(true);
                icon.setCache(true);
            }
            alignmentProperty = rendererprop.getStringProperty(PROPERTY_ALIGNMENT);

        }
        if (alignmentProperty != null && alignmentProperty.trim().length() > 0)
        {
            if (alignmentProperty.equals(EJFXBanner.PROPERTY_ALIGNMENT_LEFT))
            {
                pane.setLeft(icon);
            }
            else if (alignmentProperty.equals(EJFXBanner.PROPERTY_ALIGNMENT_RIGHT))
            {
                pane.setRight(icon);
            }
            else if (alignmentProperty.equals(EJFXBanner.PROPERTY_ALIGNMENT_CENTER))
            {
                 pane.setCenter(icon);
            }
        }
        else
        {
            pane.setCenter(icon);
        }
        if (imagePath != null)
        {
            try
            {
                icon.setImage(EJFXImageRetriever.get(imagePath));
            }
            catch (Exception e)
            {
                manager.getFrameworkManager().handleException(e);
            }
        }

        return pane;
    }

}

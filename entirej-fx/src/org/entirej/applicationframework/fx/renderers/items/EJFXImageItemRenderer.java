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
package org.entirej.applicationframework.fx.renderers.items;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

import org.entirej.applicationframework.fx.application.components.EJFXBanner;
import org.entirej.applicationframework.fx.renderers.interfaces.EJFXAppItemRenderer;
import org.entirej.applicationframework.fx.utils.EJFXImageRetriever;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.EJMessageFactory;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.enumerations.EJFrameworkMessage;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJItemProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;

public class EJFXImageItemRenderer implements EJFXAppItemRenderer
{

    protected EJFrameworkExtensionProperties  _rendererProps;
    protected EJScreenItemController          _item;
    protected EJScreenItemProperties          _screenItemProperties;
    protected EJItemProperties                _itemProperties;
    protected String                          _registeredItemName;
    protected ImageView                       _labelField;

    protected EJCoreVisualAttributeProperties _visualAttributeProperties;
    protected EJCoreVisualAttributeProperties _initialVAProperties;
    protected VALUE_CASE                      _valueCase                = VALUE_CASE.DEFAULT;
    private Image                             defaultImage;
    private Image                             currentImage;
    protected Object                          baseValue;

    public static final String                PROPERTY_ALIGNMENT        = "ALIGNMENT";
    public static final String                PROPERTY_ALIGNMENT_LEFT   = "LEFT";
    public static final String                PROPERTY_ALIGNMENT_RIGHT  = "RIGHT";
    public static final String                PROPERTY_ALIGNMENT_CENTER = "CENTER";

    public boolean useFontDimensions()
    {
        return false;
    }

    public void refreshItemRendererProperty(String propertyName)
    {
    }

    public void refreshItemRenderer()
    {

    }

    public void initialise(EJScreenItemController item, EJScreenItemProperties screenItemProperties)
    {

        _item = item;
        _itemProperties = _item.getReferencedItemProperties();
        _screenItemProperties = screenItemProperties;
        _rendererProps = _itemProperties.getItemRendererProperties();

    }

    @Override
    public void setLabel(String label)
    {
        // ignore
    }

    @Override
    public void setHint(String hint)
    {
        // ignore

    }

    public void enableLovActivation(boolean activate)
    {

    }

    public EJScreenItemController getItem()
    {
        return _item;
    }

    public EJItemProperties getItemProperties()
    {
        return _itemProperties;
    }

    public Node getGuiComponent()
    {

        return _labelField.getParent();
    }

    public Label getGuiComponentLabel()
    {
        return null;
    }

    public void clearValue()
    {
        baseValue = null;
        if ((_labelField) != null)
        {

            _labelField.setImage(defaultImage);
        }

    }

    public String getRegisteredItemName()
    {
        return _registeredItemName;
    }

    public Object getValue()
    {

        return baseValue;
    }

    public boolean isEditAllowed()
    {

        return false;
    }

    public boolean isVisible()
    {

        if ((_labelField != null))
            return _labelField.isVisible();

        return false;
    }

    public boolean isValid()
    {
        return true;

    }

    public void gainFocus()
    {

        if (_labelField != null)
            _labelField.requestFocus();

    }

    public void setEditAllowed(boolean editAllowed)
    {
        // ignore
    }

    public void setInitialValue(Object value)
    {

        setValue(value);

    }

    public void setRegisteredItemName(String name)
    {
        _registeredItemName = name;
    }

    public void setValue(Object value)
    {
        baseValue = value;
        if (currentImage != null)
        {
            currentImage = null;
        }
        if (_labelField != null)
        {
            if (value == null)
                _labelField.setImage(defaultImage);
            else
            {
                if (value instanceof String)
                {
                    try
                    {
                        currentImage = new Image((new URL((String)value)).toExternalForm());
                    }
                    catch (MalformedURLException e)
                    {
                        EJMessage message = EJMessageFactory.getInstance().createMessage(EJFrameworkMessage.INVALID_DATA_TYPE_FOR_ITEM, _item.getName(),
                                "String should follow URL Spec", (String)value);
                        throw new IllegalArgumentException(message.getMessage());
                    }
                }
                if (value instanceof URL)
                {
                    currentImage = new Image(((URL) value).toExternalForm());
                }
                else if (value instanceof byte[])
                {
                    currentImage = new Image(new ByteArrayInputStream((byte[]) value));
                }
                else
                {
                    EJMessage message = EJMessageFactory.getInstance().createMessage(EJFrameworkMessage.INVALID_DATA_TYPE_FOR_ITEM, _item.getName(),
                            "URL or byte[]", value.getClass().getName());
                    throw new IllegalArgumentException(message.getMessage());
                }
                _labelField.setImage(currentImage);
            }

        }

    }

    public void setVisible(boolean visible)
    {

        if (_labelField != null)
            _labelField.setVisible(visible);
    }

    public void setMandatory(boolean mandatory)
    {

    }

    public boolean isMandatory()
    {
        return false;
    }

    public boolean valueEqualsTo(Object value)
    {
        return value.equals(this.getValue());
    }

    public void validationErrorOccurred(boolean error)
    {
        // ignore
    }

    @Override
    public void setVisualAttribute(EJCoreVisualAttributeProperties visualAttributeProperties)
    {
        // ignore
    }

    @Override
    public EJCoreVisualAttributeProperties getVisualAttributeProperties()
    {
        return _visualAttributeProperties;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("LabelItem:\n");
        buffer.append("  RegisteredItemName: ");
        buffer.append(_registeredItemName);
        buffer.append("\n");
        buffer.append("  Label: ");
        buffer.append(_labelField);
        buffer.append("  GUI Component: ");
        buffer.append(_labelField);

        return buffer.toString();
    }

    @Override
    public void setInitialVisualAttribute(EJCoreVisualAttributeProperties va)
    {
        this._initialVAProperties = va;
        setVisualAttribute(va);

    }

    public boolean isReadOnly()
    {
        return true;
    }

    public boolean canExecuteActionCommand()
    {
        return false;
    }

    @Override
    public Node createComponent()
    {
        String PROPERTY_IMAGE = "IMAGE";
        final BorderPane pane = new BorderPane();
        String alignmentProperty = _rendererProps.getStringProperty(PROPERTY_ALIGNMENT);
        _labelField = new ImageView();

        String pictureName = _rendererProps.getStringProperty(PROPERTY_IMAGE);

        if (pictureName != null && pictureName.length() > 0)
        {
            if (pictureName != null && pictureName.trim().length() > 0)
            {
                defaultImage = EJFXImageRetriever.get(pictureName);
            }

        }

        _labelField.setUserData(_item.getReferencedItemProperties().getName());
        _labelField.focusedProperty().addListener(new ChangeListener<Boolean>()
        {
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                if (newValue.booleanValue())
                {
                    _item.itemFocusGained();

                }
                else
                {
                    _item.itemFocusLost();

                }
            }
        });
        _labelField.setImage(defaultImage);
        if (alignmentProperty != null && alignmentProperty.trim().length() > 0)
        {
            if (alignmentProperty.equals(EJFXBanner.PROPERTY_ALIGNMENT_LEFT))
            {
                pane.setLeft(_labelField);
            }
            else if (alignmentProperty.equals(EJFXBanner.PROPERTY_ALIGNMENT_RIGHT))
            {
                pane.setRight(_labelField);
            }
            else if (alignmentProperty.equals(EJFXBanner.PROPERTY_ALIGNMENT_CENTER))
            {
                 pane.setCenter(_labelField);
            }
        }
        else
        {
            pane.setCenter(_labelField);
        }
        
        return pane;
    }

    @Override
    public Label createLable()
    {
        return null;
    }

    @Override
    public TableColumn<EJDataRecord, EJDataRecord> createColumnProvider(EJScreenItemProperties item, EJScreenItemController controller)
    {
        return null;
    }

    @Override
    public Comparator<EJDataRecord> getColumnSorter(EJScreenItemProperties itemProps, EJScreenItemController item)
    {
        // TODO Auto-generated method stub
        return null;
    }
}

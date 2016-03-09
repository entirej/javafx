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

import java.util.Comparator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

import org.entirej.applicationframework.fx.renderers.interfaces.EJFXAppItemRenderer;
import org.entirej.applicationframework.fx.renderers.items.definition.interfaces.EJFXButtonItemRendererDefinitionProperties;
import org.entirej.applicationframework.fx.utils.EJFXImageRetriever;
import org.entirej.applicationframework.fx.utils.EJFXVisualAttributeUtils;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJItemProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;

public class EJFXButtonItemRenderer implements EJFXAppItemRenderer
{
    protected EJFrameworkExtensionProperties  _rendererProps;
    protected EJScreenItemController          _item;
    protected EJItemProperties                _itemProperties;
    protected EJScreenItemProperties          _screenItemProperties;
    private String                            _registeredItemName;
    protected boolean                         activeEvent = true;
    protected Button                          _button;
    private boolean                           _isValid    = true;
    protected String                          _vaCSSName;

    private EJCoreVisualAttributeProperties   _visualAttributeProperties;
    protected EJCoreVisualAttributeProperties _initialVAProperties;

    public void refreshItemRendererProperty(String propertyName)
    {
    }

    public void validationErrorOccurred(boolean error)
    {

    }

    public void refreshItemRenderer()
    {

    }
    
    
    public String getDisplayValue()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean useFontDimensions()
    {
        return false;
    }

    protected boolean controlState(Control control)
    {
        return control != null;

    }

    public void initialise(EJScreenItemController item, EJScreenItemProperties screenItemProperties)
    {
        _registeredItemName = item.getReferencedItemProperties().getName();
        _item = item;
        _itemProperties = _item.getReferencedItemProperties();
        _screenItemProperties = item.getProperties();
        _rendererProps = _itemProperties.getItemRendererProperties();

    }

    @Override
    public void setLabel(String label)
    {
        if (controlState(_button))
            _button.setText(label == null ? "" : label);
    }

    @Override
    public void setHint(String hint)
    {
        if (controlState(_button))
        {
            _button.setTooltip(hint == null || hint.trim().length() == 0 ? null : new Tooltip(hint));
        }

    }

    public EJScreenItemController getItem()
    {
        return _item;
    }

    public EJItemProperties getItemProperties()
    {
        return _itemProperties;
    }

    public Control getGuiComponent()
    {
        return _button;
    }

    public Label getGuiComponentLabel()
    {
        return null;
    }

    public void clearValue()
    {
    }

    public String getRegisteredItemName()
    {
        return _registeredItemName;
    }

    public Object getValue()
    {

        return null;

    }

    public boolean isEditAllowed()
    {
        if (controlState(_button))

            return !_button.isDisable();

        return false;
    }

    public boolean isVisible()
    {

        if (controlState(_button))
            return _button.isVisible();

        return false;
    }

    public boolean isMandatory()
    {
        return false;
    }

    public boolean isValid()
    {
        if (_isValid)

            return true;

        else

            return false;

    }

    public void gainFocus()
    {
        if (controlState(_button))
            _button.requestFocus();
    }

    public void setEditAllowed(boolean editAllowed)
    {
        if (controlState(_button))
            _button.setDisable(!editAllowed);
    }

    public void setInitialValue(Object value)
    {
    }

    public void setRegisteredItemName(String name)
    {
        _registeredItemName = name;
    }

    public void setValue(Object value)
    {

    }

    public void setVisible(boolean visible)
    {
        if (controlState(_button))
            _button.setVisible(visible);
    }

    public void setMandatory(boolean mandatory)
    {
    }

    public void enableLovActivation(boolean activate)
    {
    }

    public boolean valueEqualsTo(Object value)
    {
        return false;
    }

    @Override
    public void setVisualAttribute(EJCoreVisualAttributeProperties va)
    {
        _visualAttributeProperties = va;

        if (va == null)
        {
            if (_vaCSSName != null)
            {

                if (_button != null)
                    _button.getStyleClass().remove(_vaCSSName);
            }
            if (_initialVAProperties == null || EJCoreVisualAttributeProperties.UNSPECIFIED.equals(_initialVAProperties.getBackgroundRGB()))
            {
                _button.getStyleClass().remove("va-button-bg");
            }
        }
        else
        {
            String css = EJFXVisualAttributeUtils.INSTANCE.toCSS(va);
            if (css != null && !css.equals(_vaCSSName))
            {
                if (_button != null)
                    _button.getStyleClass().add(_vaCSSName = css);

            }
            if (_initialVAProperties == null || EJCoreVisualAttributeProperties.UNSPECIFIED.equals(_initialVAProperties.getBackgroundRGB()))
            {
                if (!EJCoreVisualAttributeProperties.UNSPECIFIED.equals(va.getBackgroundRGB()))
                {
                    _button.getStyleClass().add("va-button-bg");
                }
            }
        }

    }

    @Override
    public EJCoreVisualAttributeProperties getVisualAttributeProperties()
    {
        return _visualAttributeProperties;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("ButtonItem:\n");
        buffer.append("  RegisteredItemName: ");
        buffer.append(_registeredItemName);
        buffer.append("\n");
        buffer.append("  Button: ");
        buffer.append(_button);
        buffer.append("  Label: ");
        buffer.append("null");

        return buffer.toString();
    }

    protected Pos getComponentStyle(String alignmentProperty, Pos style)
    {
        if (alignmentProperty != null && alignmentProperty.trim().length() > 0)
        {
            if (alignmentProperty.equals(EJFXButtonItemRendererDefinitionProperties.PROPERTY_ALIGNMENT_LEFT))
            {
                return Pos.CENTER_LEFT;
            }
            else if (alignmentProperty.equals(EJFXButtonItemRendererDefinitionProperties.PROPERTY_ALIGNMENT_RIGHT))
            {
                return Pos.CENTER_RIGHT;
            }
            else if (alignmentProperty.equals(EJFXButtonItemRendererDefinitionProperties.PROPERTY_ALIGNMENT_CENTER))
            {
                return Pos.CENTER;
            }
        }
        return style;
    }

    @Override
    public void setInitialVisualAttribute(EJCoreVisualAttributeProperties va)
    {
        this._initialVAProperties = va;
        if (_button != null && va != null)
        {
            _button.getStyleClass().add(EJFXVisualAttributeUtils.INSTANCE.toCSS(va));
            if (!EJCoreVisualAttributeProperties.UNSPECIFIED.equals(va.getBackgroundRGB()))
            {
                _button.getStyleClass().add("va-button-bg");
            }
        }

    }

    public boolean isReadOnly()
    {
        return true;
    }

    @Override
    public Node createComponent()
    {
        String pictureName = _itemProperties.getItemRendererProperties().getStringProperty(EJFXButtonItemRendererDefinitionProperties.PROPERTY_PICTURE);
        String alignmentProperty = _rendererProps.getStringProperty(EJFXButtonItemRendererDefinitionProperties.PROPERTY_ALIGNMENT);

        String hint = _screenItemProperties.getHint();
        String label = _screenItemProperties.getLabel();

        _button = new Button();
        _button.setAlignment(getComponentStyle(alignmentProperty, _button.getAlignment()));

        setHint(hint);
        setLabel(label);
        if (pictureName != null && pictureName.trim().length() > 0)
        {
            _button.setGraphic(new ImageView(EJFXImageRetriever.get(pictureName)));
        }
        boolean hideBorder = _itemProperties.getItemRendererProperties().getBooleanProperty(EJFXButtonItemRendererDefinitionProperties.PROPERTY_HIDE_BORDER,
                false);
        if (hideBorder)
        {
            // FIXME : hide via CSS
        }
        _button.setUserData(_item.getReferencedItemProperties().getName());
        _button.focusedProperty().addListener(new ChangeListener<Boolean>()
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
        _button.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
                if (activeEvent)
                    _item.executeActionCommand();
            }
        });
        return _button;
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

        return null;
    }

}

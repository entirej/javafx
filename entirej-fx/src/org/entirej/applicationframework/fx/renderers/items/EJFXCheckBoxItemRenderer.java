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
/**
 * 
 */
package org.entirej.applicationframework.fx.renderers.items;

import java.math.BigDecimal;
import java.util.Comparator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import org.entirej.applicationframework.fx.renderers.interfaces.EJFXAppItemRenderer;
import org.entirej.applicationframework.fx.renderers.items.EJFXTextItemRenderer.VACell;
import org.entirej.applicationframework.fx.renderers.items.definition.interfaces.EJFXCheckBoxRendererDefinitionProperties;
import org.entirej.applicationframework.fx.utils.EJFXImageRetriever;
import org.entirej.applicationframework.fx.utils.EJFXVisualAttributeUtils;
import org.entirej.framework.core.EJApplicationException;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.enumerations.EJScreenType;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJItemProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;

public class EJFXCheckBoxItemRenderer implements EJFXAppItemRenderer
{

    private Object                            _checkedValue;
    private Object                            _uncheckedValue;
    private boolean                           _otherValueMappingValue;
    private boolean                           _useTriStateChaeckBox = false;
    protected EJFrameworkExtensionProperties  _rendererProps;
    protected EJScreenItemController          _item;
    protected EJItemProperties                _itemProperties;
    protected EJScreenItemProperties          _screenItemProperties;
    private String                            _registeredItemName;
    protected boolean                         activeEvent           = true;
    protected CheckBox                        _button;
    protected HBox                            _host;
    private boolean                           _isValid              = true;
    private boolean                           _mandatory;

    protected Object                          baseValue;

    private EJCoreVisualAttributeProperties   _visualAttributeProperties;
    protected String                          _vaCSSName;
    protected EJCoreVisualAttributeProperties _initialVAProperties;
    private String                            _defaultValue;

    @Override
    public void initialise(EJScreenItemController item, EJScreenItemProperties screenItemProperties)
    {
        _registeredItemName = item.getReferencedItemProperties().getName();
        _item = item;
        _itemProperties = _item.getReferencedItemProperties();
        _screenItemProperties = item.getProperties();
        _rendererProps = _itemProperties.getItemRendererProperties();

        final String checkedValue = _rendererProps.getStringProperty(EJFXCheckBoxRendererDefinitionProperties.CHECKED_VALUE);
        final String uncheckedValue = _rendererProps.getStringProperty(EJFXCheckBoxRendererDefinitionProperties.UNCHECKED_VALUE);
        final String otherValueMapping = _rendererProps.getStringProperty(EJFXCheckBoxRendererDefinitionProperties.OTHER_VALUE_MAPPING);

        if (checkedValue == null || uncheckedValue == null)
        {
            throw new EJApplicationException(new EJMessage("A checked and unchecked value must be specified for the Check Box Renderer for item: "
                    + _itemProperties.getBlockName() + "." + _rendererProps.getName()));
        }

        // set 3 state checkBox
        if (_item.getScreenType() == EJScreenType.QUERY)
        {
            _useTriStateChaeckBox = _rendererProps.getBooleanProperty(EJFXCheckBoxRendererDefinitionProperties.TRI_STATE, false);
        }

        _checkedValue = getValueAsObject(_itemProperties.getDataTypeClass(), checkedValue);
        _uncheckedValue = getValueAsObject(_itemProperties.getDataTypeClass(), uncheckedValue);

        _otherValueMappingValue = EJFXCheckBoxRendererDefinitionProperties.CHECKED.equals(otherValueMapping);

        _defaultValue = _itemProperties.getItemRendererProperties().getStringProperty(EJFXCheckBoxRendererDefinitionProperties.DEFAULT_VALUE);
    }

    protected boolean controlState(Control control)
    {
        return control != null;

    }

    @Override
    public void clearValue()
    {
        baseValue = null;
        try
        {
            activeEvent = false;
            if (controlState(_button))
                if (_useTriStateChaeckBox && (_defaultValue == null || "".equals(_defaultValue)))
                {
                    _button.setIndeterminate(true);
                    _button.setSelected(true);
                }
                else
                {
                    _button.setIndeterminate(false);
                    if (_uncheckedValue != null && _uncheckedValue.equals(_defaultValue))
                    {
                        _button.setSelected(false);
                    }
                    else
                    {
                        _button.setSelected(true);
                    }
                }

        }
        finally
        {
            activeEvent = true;
        }
    }
    public String getDisplayValue()
    {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Object getValue()
    {
        if (controlState(_button))
        {
            if (_useTriStateChaeckBox && _button.isIndeterminate())
            {
                return null;
            }
            else
            {
                if (_button.isSelected())
                {
                    return baseValue = _checkedValue;
                }
                else
                {
                    return baseValue = _uncheckedValue;
                }
            }
        }

        return baseValue;
    }

    @Override
    public void setValue(Object value)
    {
        baseValue = value;
        if (controlState(_button))
        {
            try
            {

                activeEvent = false;
                if (_useTriStateChaeckBox && value == null)
                {
                    _button.setIndeterminate(true);
                    _button.setSelected(true);
                }
                else
                {
                    _button.setIndeterminate(false);
                    if (_checkedValue.equals(value))
                    {

                        _button.setSelected(true);
                    }
                    else if (_uncheckedValue.equals(value))
                    {
                        _button.setSelected(false);
                    }
                    else
                    {
                        _button.setSelected(_otherValueMappingValue);
                    }
                }
            }
            finally
            {
                activeEvent = true;
            }
        }
    }

    @Override
    public void setInitialValue(Object value)
    {
        try
        {
            activeEvent = false;
            if (controlState(_button))
            {

                if (_useTriStateChaeckBox && (value == null))
                {
                    _button.setIndeterminate(true);
                    _button.setSelected(true);
                }
                else
                {
                    _button.setIndeterminate(false);
                    if (_checkedValue.equals(value))
                    {
                        _button.setSelected(true);
                    }
                    else if (_uncheckedValue.equals(value))
                    {
                        _button.setSelected(false);
                    }
                    else
                    {

                        if (value != null && !"".equals(value))
                        {
                            _button.setSelected(_otherValueMappingValue);
                        }
                        else
                        {
                            if (_useTriStateChaeckBox && (_defaultValue == null || "".equals(_defaultValue)))
                            {
                                _button.setIndeterminate(true);
                                _button.setSelected(true);
                            }
                            else
                            {
                                _button.setSelected(EJFXCheckBoxRendererDefinitionProperties.CHECKED.equals(_defaultValue));
                            }

                        }
                    }
                }
            }
        }
        finally
        {
            activeEvent = true;
        }
    }

    private Object getValueAsObject(Class<?> datatypeClassName, String value)
    {
        if (datatypeClassName.getName().equals(Integer.class.getName()))
        {
            return Integer.parseInt(value);
        }
        else if (datatypeClassName.getName().equals(String.class.getName()))
        {
            return (value);
        }
        else if (datatypeClassName.getName().equals(Float.class.getName()))
        {
            return Float.parseFloat(value);
        }
        else if (datatypeClassName.getName().equals(Long.class.getName()))
        {
            return Long.parseLong(value);
        }
        else if (datatypeClassName.getName().equals(Double.class.getName()))
        {
            return Double.parseDouble(value);
        }
        else if (datatypeClassName.getName().equals(Boolean.class.getName()))
        {
            return Boolean.parseBoolean(value);
        }
        else if (datatypeClassName.getName().equals(BigDecimal.class.getName()))
        {
            return new BigDecimal(value);
        }
        else if (datatypeClassName.getName().equals(Number.class.getName()))
        {
            return Double.parseDouble(value);
        }

        return value;

    }

    @Override
    public boolean isMandatory()
    {
        return _mandatory;
    }

    @Override
    public void setMandatory(boolean mandatory)
    {
        _mandatory = mandatory;
    }

    @Override
    public boolean valueEqualsTo(Object value)
    {
        return value.equals(this.getValue());
    }

    @Override
    public boolean isValid()
    {
        // if (_isValid)
        // {
        // if (_mandatory && getValue() == null)
        // {
        // return false;
        // }
        // else
        // {
        // return true;
        // }
        // }
        // else
        // {
        // return false;
        // }
        return _isValid;
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

    public Node getGuiComponent()
    {
        return _host;
    }

    public Label getGuiComponentLabel()
    {
        return null;
    }

    public String getRegisteredItemName()
    {
        return _registeredItemName;
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

    public void setRegisteredItemName(String name)
    {
        _registeredItemName = name;
    }

    public void setVisible(boolean visible)
    {
        if (controlState(_button))
            _button.setVisible(visible);
    }

    public void enableLovActivation(boolean activate)
    {
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
        }
        else
        {
            String css = EJFXVisualAttributeUtils.INSTANCE.toCSS(va);
            if (css != null && !css.equals(_vaCSSName))
            {
                if (_button != null)
                    _button.getStyleClass().add(_vaCSSName = css);

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
        buffer.append("CheckBoxItem:\n");
        buffer.append("  RegisteredItemName: ");
        buffer.append(_registeredItemName);
        buffer.append("\n");
        buffer.append("  CheckBox: ");
        buffer.append(_button);
        buffer.append("  Label: ");
        buffer.append("null");

        return buffer.toString();
    }

    @Override
    public Node createComponent()
    {
        final String label = _screenItemProperties.getLabel();
        final String hint = _screenItemProperties.getHint();

        _button = new CheckBox(label);
        _host = new HBox();
        _host.getChildren().add(_button);
        _button.setAllowIndeterminate(_useTriStateChaeckBox);
        _button.selectedProperty().addListener(new ChangeListener<Boolean>()
        {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                if (activeEvent)
                {
                    Object old = null;
                    Object newVal = null;
                    if (newValue)
                    {
                        old = _uncheckedValue;
                        newVal =  _checkedValue;
                    }
                    else
                    {
                        old = _checkedValue;
                        newVal =  _uncheckedValue;
                    }
                    _item.itemValueChaged(newVal);

                    _item.executeActionCommand();
                }

            }
        });

        if (label != null && label.trim().length() > 0)
        {
            _button.setText(label);
        }
        setHint(hint);
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

        setInitialValue(baseValue);
        return _host;
    }

    @Override
    public Label createLable()
    {
        return null;
    }

    @Override
    public void setInitialVisualAttribute(EJCoreVisualAttributeProperties va)
    {
        this._initialVAProperties = va;
        if (_button != null && va != null)
        {
            _button.getStyleClass().add(EJFXVisualAttributeUtils.INSTANCE.toCSS(va));
        }

    }

    @Override
    public TableColumn<EJDataRecord, EJDataRecord> createColumnProvider(final EJScreenItemProperties item, EJScreenItemController controller)
    {
        TableColumn<EJDataRecord, EJDataRecord> column = new TableColumn<>(item.getLabel());
        column.setEditable(false);

        EJItemProperties itemProperties = controller.getReferencedItemProperties();
        EJFrameworkExtensionProperties itemRendererProperties = itemProperties.getItemRendererProperties();
        final Object checkedValue = getValueAsObject(itemProperties.getDataTypeClass(),
                itemRendererProperties.getStringProperty(EJFXCheckBoxRendererDefinitionProperties.CHECKED_VALUE));
        final Object uncheckedValue = getValueAsObject(itemProperties.getDataTypeClass(),
                itemRendererProperties.getStringProperty(EJFXCheckBoxRendererDefinitionProperties.UNCHECKED_VALUE));
        final boolean otherValueMappingValue = EJFXCheckBoxRendererDefinitionProperties.CHECKED.equals(itemRendererProperties
                .getStringProperty(EJFXCheckBoxRendererDefinitionProperties.OTHER_VALUE_MAPPING));

        final boolean defaultState = EJFXCheckBoxRendererDefinitionProperties.CHECKED.equals(otherValueMappingValue);
        final Image checkSelected = EJFXImageRetriever.get(EJFXImageRetriever.IMG_CHECK_SELECTED);
        final Image checkUnSelected = EJFXImageRetriever.get(EJFXImageRetriever.IMG_CHECK_UNSELECTED);
        Callback<TableColumn<EJDataRecord, EJDataRecord>, TableCell<EJDataRecord, EJDataRecord>> checkboxCellFactory = new Callback<TableColumn<EJDataRecord, EJDataRecord>, TableCell<EJDataRecord, EJDataRecord>>()
        {

            @Override
            public TableCell<EJDataRecord, EJDataRecord> call(TableColumn<EJDataRecord, EJDataRecord> p)
            {

                return new VACell(item.getReferencedItemName())
                {

                    @Override
                    public void updateItem(EJDataRecord value, boolean empty)
                    {

                        if (!empty && value != null)
                        {
                            setText(null);
                            setGraphic(new ImageView(getImage(value)));
                        }
                        else
                        {
                            setText(null);
                            setGraphic(null);
                        }
                    }

                    public Image getImage(EJDataRecord record)
                    {
                        if (record != null)
                        {

                            Object value = record.getValue(_registeredItemName);
                            if (value != null)
                            {
                                if (value.equals(checkedValue))
                                {
                                    return checkSelected;
                                }
                                else if (value.equals(uncheckedValue))
                                {
                                    return checkUnSelected;
                                }
                                else
                                {
                                    if (otherValueMappingValue)
                                    {
                                        return checkSelected;
                                    }
                                    else
                                    {
                                        return checkUnSelected;
                                    }
                                }
                            }
                            else
                            {
                                if (defaultState)
                                {
                                    return checkSelected;
                                }
                                else
                                {
                                    return checkUnSelected;
                                }
                            }
                        }
                        return checkUnSelected;
                    }
                };
            }
        };
        column.setCellFactory(checkboxCellFactory);
        return column;
    }

    class CheckboxCell extends TableCell<EJDataRecord, EJDataRecord>
    {
        CheckBox checkbox;

        @Override
        protected void updateItem(EJDataRecord value, boolean arg1)
        {

            paintCell(value);
        }

        private void paintCell(EJDataRecord value)
        {
            if (value != null)
            {
                if (checkbox == null)
                {
                    checkbox = new CheckBox();
                }
                updateValue(value.getValue(_registeredItemName));
                setText(null);
                setGraphic(checkbox);
            }
            else
            {
                setText(null);
                setGraphic(null);
            }
        }

        private void updateValue(Object value)
        {
            if (controlState(checkbox))
            {

                if (_checkedValue.equals(value))
                {

                    checkbox.setSelected(true);
                }
                else if (_uncheckedValue.equals(value))
                {
                    checkbox.setSelected(false);
                }
                else
                {
                    checkbox.setSelected(_otherValueMappingValue);
                }

            }
        }
    }

    @Override
    public Comparator<EJDataRecord> getColumnSorter(EJScreenItemProperties itemProps, EJScreenItemController item)
    {
        return new Comparator<EJDataRecord>()
        {

            @Override
            public int compare(EJDataRecord v1, EJDataRecord v2)
            {
                Object value1 = v1 != null ? v1.getValue(_registeredItemName) : null;
                Object value2 = v2 != null ? v2.getValue(_registeredItemName) : null;
                if (value1 == null && value2 == null)
                {
                    return 0;
                }
                if (value1 == null && value2 != null)
                {
                    return -1;
                }
                if (value1 != null && value2 == null)
                {
                    return 1;
                }
                if (value1 instanceof Comparable)
                {
                    @SuppressWarnings("unchecked")
                    Comparable<Object> comparable = (Comparable<Object>) value1;
                    return comparable.compareTo(value2);
                }
                return 0;
            }
        };
    }

    public void refreshItemRendererProperty(String propertyName)
    {
    }

    public void validationErrorOccurred(boolean error)
    {

    }
    
    @Override
    public void setMessage(EJMessage message)
    {
       
    }
    
    @Override
    public void clearMessage()
    {
        
        
    }

    public void refreshItemRenderer()
    {

    }

    public boolean useFontDimensions()
    {
        return false;
    }

    public boolean isReadOnly()
    {
        return false;
    }
}

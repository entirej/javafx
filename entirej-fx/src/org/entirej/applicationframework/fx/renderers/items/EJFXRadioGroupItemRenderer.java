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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import org.entirej.applicationframework.fx.renderers.interfaces.EJFXAppItemRenderer;
import org.entirej.applicationframework.fx.renderers.items.EJFXTextItemRenderer.VACell;
import org.entirej.applicationframework.fx.renderers.items.definition.interfaces.EJFXCheckBoxRendererDefinitionProperties;
import org.entirej.applicationframework.fx.renderers.items.definition.interfaces.EJFXRadioButtonItemRendererDefinitionProperties;
import org.entirej.applicationframework.fx.utils.EJFXImageRetriever;
import org.entirej.framework.core.EJApplicationException;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionPropertyList;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionPropertyListEntry;
import org.entirej.framework.core.properties.interfaces.EJItemProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;

public class EJFXRadioGroupItemRenderer implements EJFXAppItemRenderer
{

    private Map<String, RadioButtonValue>     _radioButtons;
    protected AbstractActionNode<Parent>      _actionControl;
    protected EJFrameworkExtensionProperties  _rendererProps;
    protected EJScreenItemController          _item;
    protected EJScreenItemProperties          _screenItemProperties;
    protected EJItemProperties                _itemProperties;
    protected String                          _registeredItemName;
    protected boolean                         _mandatory;
    private boolean                           _isValid              = true;
    private RadioButtonSelection              _radioButtonSelection = new RadioButtonSelection();

    protected EJCoreVisualAttributeProperties _visualAttributeProperties;
    protected EJCoreVisualAttributeProperties _initialVAProperties;
    protected Object                          baseValue;

    public boolean useFontDimensions()
    {
        return false;
    }

    public void refreshItemRenderer()
    {

    }

    
    
    @Override
    public String getDisplayValue()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public void clearValue()
    {
        baseValue = null;
        try
        {
            removeListeners();

        }
        finally
        {
            addListeners();
        }

    }

    private void addListeners()
    {
        for (RadioButtonValue buttonValue : _radioButtons.values())
        {
            RadioButton button = buttonValue.getButton();

            button.selectedProperty().addListener(_radioButtonSelection);
            button.focusedProperty().addListener(new ChangeListener<Boolean>()
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
        }
    }

    private void removeListeners()
    {
        for (RadioButtonValue buttonValue : _radioButtons.values())
        {
            RadioButton button = buttonValue.getButton();
            button.selectedProperty().removeListener(_radioButtonSelection);

        }
    }

    @Override
    public void enableLovActivation(boolean arg0)
    {

    }

    @Override
    public void gainFocus()
    {
        if (_actionControl != null)
            for (RadioButtonValue buttonValue : _radioButtons.values())
            {
                RadioButton button = buttonValue.getButton();
                if (button.selectedProperty().get())
                {
                    button.requestFocus();
                    break;
                }
            }

    }

    @Override
    public EJScreenItemController getItem()
    {
        return _item;
    }

    @Override
    public String getRegisteredItemName()
    {
        return _registeredItemName;
    }

    @Override
    public Object getValue()
    {
        if (_actionControl == null)
            return baseValue;

        for (RadioButtonValue buttonValue : _radioButtons.values())
        {
            RadioButton button = buttonValue.getButton();

            if (button.isSelected())
                return baseValue = buttonValue.getValue();
        }

        return baseValue;
    }

    @Override
    public void initialise(EJScreenItemController item, EJScreenItemProperties screenItemProperties)
    {
        _item = item;
        _itemProperties = _item.getReferencedItemProperties();
        _screenItemProperties = screenItemProperties;
        _rendererProps = _itemProperties.getItemRendererProperties();

        _radioButtons = new HashMap<String, RadioButtonValue>();

    }

    @Override
    public boolean isEditAllowed()
    {
        if (_actionControl != null)
            return !_actionControl.isDisabled();
        return false;
    }

    @Override
    public boolean isMandatory()
    {

        return _mandatory;
    }

    @Override
    public boolean isValid()
    {

        if (_isValid)
        {
            if (_mandatory && getValue() == null)
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean isVisible()
    {
        if (_actionControl != null)
        {
            return _actionControl.isVisible();
        }

        return false;
    }

    @Override
    public void refreshItemRendererProperty(String propertyName)
    {

    }

    @Override
    public void setEditAllowed(boolean editAllowed)
    {
        if (_actionControl != null)
            _actionControl.setDisable(!editAllowed);
    }

    @Override
    public void setHint(String hint)
    {
        if (_actionControl != null)
        {
            for (RadioButtonValue buttonValue : _radioButtons.values())
            {
                RadioButton button = buttonValue.getButton();

                button.setTooltip(hint == null ? null : new Tooltip(hint));
            }
        }

    }

    @Override
    public void setInitialValue(Object value)
    {
        try
        {
            removeListeners();
            for (RadioButtonValue buttonValue : _radioButtons.values())
            {
                if (buttonValue.getValue().equals(value))
                {
                    buttonValue.getButton().setSelected(true);

                }
                else
                    buttonValue.getButton().setSelected(false);
            }

        }
        finally
        {
            addListeners();
            setMandatoryBorder(_mandatory);
        }

    }

    @Override
    public void setLabel(String label)
    {
        // FIXME
        // if (controlState(_radioGroup) && (_radioGroup instanceof Group))
        // ((Group) _radioGroup).setText(label == null ? "" : label);
    }

    protected void setMandatoryBorder(boolean req)
    {
        if (_actionControl != null)
            _actionControl.setShowMandatory(req && getValue() == null);
    }

    @Override
    public void setMandatory(boolean mandatory)
    {
        _mandatory = mandatory;
        setMandatoryBorder(mandatory);

    }

    @Override
    public void setRegisteredItemName(String name)
    {
        _registeredItemName = name;

    }

    @Override
    public void setValue(Object value)
    {
        baseValue = value;
        if (_actionControl != null)
        {

            for (RadioButtonValue buttonValue : _radioButtons.values())
            {
                if (buttonValue.getValue().equals(value))
                    buttonValue.getButton().setSelected(true);
                else
                    buttonValue.getButton().setSelected(false);
            }

            setMandatoryBorder(_mandatory);
        }

    }

    @Override
    public void setVisible(boolean visible)
    {
        if (_actionControl != null)
        {
            _actionControl.setVisible(visible);

        }
    }

    @Override
    public void setVisualAttribute(EJCoreVisualAttributeProperties visualAttributeProperties)
    {
        _visualAttributeProperties = visualAttributeProperties != null ? visualAttributeProperties : _initialVAProperties;

        for (RadioButtonValue buttonValue : _radioButtons.values())
        {
            // FIXME Button button = buttonValue.getButton();
            // if (button == null || button.isDisposed())
            // continue;
            // refreshBackground(button);
            // refreshForeground(button);
            // refreshFont(button);
        }

    }

    @Override
    public EJCoreVisualAttributeProperties getVisualAttributeProperties()
    {
        return _visualAttributeProperties;
    }

    @Override
    public void validationErrorOccurred(boolean error)
    {
        if (_actionControl != null)
        {
            _actionControl.setErrorDescriptionText(null);
            _actionControl.setShowError(error);
        }

    }

    @Override
    public boolean valueEqualsTo(Object value)
    {
        return value.equals(this.getValue());

    }

    @Override
    public Node getGuiComponent()
    {

        return _actionControl;
    }

    @Override
    public Label getGuiComponentLabel()
    {

        return null;
    }

    protected class RadioButtonValue
    {

        private RadioButton button;
        private Object      value;

        /**
         * @param button
         * @param value
         */
        public RadioButtonValue(RadioButton button, Object value)
        {
            this.button = button;
            this.value = value;
        }

        public RadioButton getButton()
        {
            return button;
        }

        public Object getValue()
        {
            return value;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((button == null) ? 0 : button.hashCode());
            return result;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            RadioButtonValue other = (RadioButtonValue) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (button == null)
            {
                if (other.button != null)
                    return false;
            }
            else if (!button.equals(other.button))
                return false;
            return true;
        }

        private EJFXRadioGroupItemRenderer getOuterType()
        {
            return EJFXRadioGroupItemRenderer.this;
        }

    }

    private Object getValueAsObject(Class<?> dataType, String value)
    {
        try
        {
            Constructor<?> constructor = dataType.getConstructor(String.class);
            Object val = constructor.newInstance(value);
            return val;
        }
        catch (SecurityException e)
        {
            throw new EJApplicationException("Unable to find a constructor with a String parameter for the data type: " + dataType.getName(), e);
        }
        catch (NoSuchMethodException e)
        {
            throw new EJApplicationException("Unable to find a constructor with a String parameter for the data type: " + dataType.getName(), e);
        }
        catch (IllegalArgumentException e)
        {
            throw new EJApplicationException("Unable create a new data type: " + dataType.getName() + ". With a single string parameter of: " + value);
        }
        catch (InstantiationException e)
        {
            throw new EJApplicationException("Unable create a new data type: " + dataType.getName() + ". With a single string parameter of: " + value);
        }
        catch (IllegalAccessException e)
        {
            throw new EJApplicationException("Unable create a new data type: " + dataType.getName() + ". With a single string parameter of: " + value);
        }
        catch (InvocationTargetException e)
        {
            throw new EJApplicationException("Unable create a new data type: " + dataType.getName() + ". With a single string parameter of: " + value);
        }
    }

    private class RadioButtonSelection implements ChangeListener<Boolean>
    {

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
        {
            if (newValue)
            {
                valueChanged();
            }

        }

    }

    public void valueChanged()
    {
         Object old = baseValue;
        _item.itemValueChaged(old,getValue());
        _item.executeActionCommand();
        setMandatoryBorder(_mandatory);
    }

    @Override
    public void setInitialVisualAttribute(EJCoreVisualAttributeProperties va)
    {
        this._initialVAProperties = va;
        setVisualAttribute(va);

    }

    public boolean isReadOnly()
    {
        return false;
    }

    @Override
    public Node createComponent()
    {
        final String hint = _screenItemProperties.getHint();
        // int style = SWT.NO_FOCUS | (_displayFrame ? SWT.SHADOW_ETCHED_IN :
        // SWT.NONE);
        //
        // if (_displayFrame)
        // {
        // _radioGroup = new Group(composite, style);
        // if (_screenItemProperties.getLabel() != null)
        // ((Group) _radioGroup).setText(_screenItemProperties.getLabel());
        //
        // composite = _radioGroup;
        // }
        final Parent _radioGroup;
        if (EJFXRadioButtonItemRendererDefinitionProperties.PROPERTY_ORIENTATION_VERTICAL.equals(_rendererProps
                .getStringProperty(EJFXRadioButtonItemRendererDefinitionProperties.PROPERTY_ORIENTATION)))
        {
            _radioGroup = new VBox();
        }
        else
        {
            _radioGroup = new HBox(5);
        }

        _actionControl = new AbstractActionNode<Parent>()
        {

            @Override
            public Parent createNode()
            {
                return _radioGroup;
            }

            @Override
            public Control createCustomActionLabel()
            {

                return null;
            }

            @Override
            public Control createActionLabel()
            {

                return null;
            }
        };
        _actionControl.setUserData(_item.getReferencedItemProperties().getName());

        ToggleGroup group = new ToggleGroup();
        EJFrameworkExtensionPropertyList radioButtons = _rendererProps.getPropertyList(EJFXRadioButtonItemRendererDefinitionProperties.PROPERTY_RADIO_BUTTONS);
        for (EJFrameworkExtensionPropertyListEntry listEntry : radioButtons.getAllListEntries())
        {
            Object value = getValueAsObject(_item.getReferencedItemProperties().getDataTypeClass(),
                    listEntry.getProperty(EJFXRadioButtonItemRendererDefinitionProperties.PROPERTY_VALUE));
            RadioButton button = new RadioButton();
            if (_radioGroup instanceof HBox)
            {
                ((HBox) _radioGroup).getChildren().add(button);
            }
            else if (_radioGroup instanceof VBox)
            {
                ((VBox) _radioGroup).getChildren().add(button);
            }
            // Store the button and the button values for future reference
            _radioButtons.put(listEntry.getProperty(EJFXRadioButtonItemRendererDefinitionProperties.PROPERTY_NAME), new RadioButtonValue(button, value));

            // Set the button properties
            button.setUserData(listEntry.getProperty(EJFXRadioButtonItemRendererDefinitionProperties.PROPERTY_NAME));
            button.setText(_item.getForm().translateText(listEntry.getProperty(EJFXRadioButtonItemRendererDefinitionProperties.PROPERTY_LABEL)));

            // button.setActionCommand(screenItemProperties.getActionCommand());

            if (_rendererProps.getStringProperty(EJFXRadioButtonItemRendererDefinitionProperties.PROPERTY_DEFAULT_BUTTON) != null)
            {
                if (_rendererProps.getStringProperty(EJFXRadioButtonItemRendererDefinitionProperties.PROPERTY_DEFAULT_BUTTON).equals(
                        listEntry.getProperty(EJFXRadioButtonItemRendererDefinitionProperties.PROPERTY_NAME)))
                {
                    button.setSelected(true);
                }
                else
                {
                    button.setSelected(false);
                }
            }

            button.setToggleGroup(group);

        }

        _actionControl.setMandatoryDescriptionText(_screenItemProperties.getLabel() == null || _screenItemProperties.getLabel().isEmpty() ? "Required Item"
                : String.format("%s is required", _screenItemProperties.getLabel()));
        if (_isValid)
            _actionControl.setShowError(false);
        _actionControl.setShowMandatory(false);
        // setInitialValue(baseValue);
        setHint(hint);
        addListeners();
        return _actionControl;
    }

    @Override
    public Label createLable()
    {
        // TODO Auto-generated method stub
        return null;
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

    @Override
    public TableColumn<EJDataRecord, EJDataRecord> createColumnProvider(final EJScreenItemProperties item, EJScreenItemController controller)
    {
        TableColumn<EJDataRecord, EJDataRecord> column = new TableColumn<>(item.getLabel());
        column.setEditable(false);

        EJItemProperties itemProperties = controller.getReferencedItemProperties();
        EJFrameworkExtensionProperties itemRendererProperties = itemProperties.getItemRendererProperties();

        EJFrameworkExtensionPropertyList radioButtons = itemRendererProperties
                .getPropertyList(EJFXRadioButtonItemRendererDefinitionProperties.PROPERTY_RADIO_BUTTONS);
        final Map<Object, String> valueMap = new HashMap<>();
        for (EJFrameworkExtensionPropertyListEntry listEntry : radioButtons.getAllListEntries())
        {
            Object value = getValueAsObject(_item.getReferencedItemProperties().getDataTypeClass(),
                    listEntry.getProperty(EJFXRadioButtonItemRendererDefinitionProperties.PROPERTY_VALUE));

            String name = listEntry.getProperty(EJFXRadioButtonItemRendererDefinitionProperties.PROPERTY_LABEL);
            valueMap.put(value, name);
        }

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
                            setText(valueMap.get(value.getValue(_registeredItemName)));
                        }
                        else
                        {
                            setText(null);
                        }
                    }

                };
            }
        };
        column.setCellFactory(checkboxCellFactory);
        return column;
    }
}

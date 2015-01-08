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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

import org.entirej.applicationframework.fx.renderers.interfaces.EJFXAppItemRenderer;
import org.entirej.applicationframework.fx.renderers.items.EJFXTextItemRenderer.VACell;
import org.entirej.applicationframework.fx.renderers.items.definition.interfaces.EJFXComboBoxRendererDefinitionProperties;
import org.entirej.applicationframework.fx.renderers.screen.EJFXAbstractScreenRenderer;
import org.entirej.applicationframework.fx.utils.EJFXImageRetriever;
import org.entirej.applicationframework.fx.utils.EJFXVisualAttributeUtils;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.EJMessageFactory;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.data.controllers.EJBlockController;
import org.entirej.framework.core.data.controllers.EJItemLovController;
import org.entirej.framework.core.data.controllers.EJLovController;
import org.entirej.framework.core.enumerations.EJFrameworkMessage;
import org.entirej.framework.core.enumerations.EJLovDisplayReason;
import org.entirej.framework.core.enumerations.EJScreenType;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.properties.EJCoreItemProperties;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionPropertyList;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionPropertyListEntry;
import org.entirej.framework.core.properties.interfaces.EJItemProperties;
import org.entirej.framework.core.properties.interfaces.EJLovDefinitionProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;
import org.entirej.framework.core.renderers.registry.EJBlockItemRendererRegister;

public class EJFXComboItemRenderer implements EJFXAppItemRenderer, ItemTextChangeNotifier
{
    private List<TextChangeListener>                      changeListeners = new ArrayList<ItemTextChangeNotifier.TextChangeListener>(1);
    protected EJFrameworkExtensionProperties              _rendererProps;
    protected EJScreenItemController                      _item;
    protected EJScreenItemProperties                      _screenItemProperties;
    protected EJItemProperties                            _itemProperties;
    protected String                                      _registeredItemName;
    protected AbstractActionNode<ComboBox<ComboBoxValue>> _actionControl;
    protected ComboBox<ComboBoxValue>                     _comboField;
    protected boolean                                     activeEvent     = true;
    protected Label                                       _label;
    protected boolean                                     _isValid        = true;
    protected boolean                                     _mandatory;
    protected int                                         _maxLength;
    private int                                           _visibleItemCount;
    private List<ComboBoxValue>                           _comboValues;

    private static final String                           CSS_VA_TEXT_BG  = "va-text-bg";
    protected String                                      _vaCSSName;
    protected EJCoreVisualAttributeProperties             _visualAttributeProperties;
    protected EJCoreVisualAttributeProperties             _initialVAProperties;

    protected Object                                      baseValue;

    protected boolean                                     _lovActivated;

    protected boolean                                     _lovInitialied;

    public boolean useFontDimensions()
    {
        return true;
    }

    @Override
    public void clearValue()
    {
        baseValue = null;
        try
        {
            activeEvent = false;
            if ((_comboField) != null)
                _comboField.setValue(null);

        }
        finally
        {
            activeEvent = true;
        }

    }

    @Override
    public void enableLovActivation(boolean activate)
    {
        _lovActivated = activate;
        if ((_actionControl) != null)
            _actionControl.setActionVisible(activate && isEditAllowed());
    }

    @Override
    public void gainFocus()
    {
        if ((_comboField) != null)
            _comboField.requestFocus();

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

    private ComboBoxValue getComboBoxValue()
    {
        if ((_comboField) != null)
        {

            return _comboField.getValue();
        }

        return null;
    }

    @Override
    public Object getValue()
    {

        ComboBoxValue value = getComboBoxValue();
        if (value != null)
        {
            return baseValue = value.getItemValue();
        }

        return null;
    }

    @Override
    public void initialise(EJScreenItemController item, EJScreenItemProperties screenItemProperties)
    {
        _registeredItemName = item.getReferencedItemProperties().getName();
        _item = item;
        _itemProperties = _item.getReferencedItemProperties();
        _screenItemProperties = screenItemProperties;
        _rendererProps = _itemProperties.getItemRendererProperties();
        _visibleItemCount = _rendererProps.getIntProperty(EJFXComboBoxRendererDefinitionProperties.VISIBLE_ITEM_COUNT, 0);

        _comboValues = new ArrayList<>();

        if (_rendererProps.getBooleanProperty(EJFXComboBoxRendererDefinitionProperties.INITIALIES_LOV, true))
        {
            fillComboBox();
        }

    }

    private void verifyLOVState()
    {
        if (!_lovInitialied)
        {
            fillComboBox();
            refreshCombo();
        }
    }

    private void refreshCombo()
    {
        if (_comboField != null && baseValue != null)
        {
            try
            {
                activeEvent = false;
                _comboField.getItems().clear();
                _comboField.getItems().addAll(_comboValues);
                setValue(baseValue);
            }
            finally
            {
                activeEvent = true;
            }
        }
    }

    private void fillComboBox()
    {
        // Initialise both the field and the values.
        _lovInitialied = true;
        _comboValues.clear();
        String lovDefName = _rendererProps.getStringProperty(EJFXComboBoxRendererDefinitionProperties.LOV_DEFINITION_NAME);

        if (lovDefName == null || lovDefName.trim().length() == 0)
        {
            return;
        }

        String defName = lovDefName;
        String defItemName = "";
        if (lovDefName.indexOf('.') != -1)
        {
            defName = lovDefName.substring(0, lovDefName.indexOf('.'));
            defItemName = lovDefName.substring(lovDefName.indexOf('.') + 1);
        }
        else
        {
            EJMessage message = new EJMessage("No LovDefinition item has been chosen for the ComboBox renderer properties on item: "
                    + _itemProperties.getName());
            _item.getForm().getFrameworkManager().getApplicationManager().getApplicationMessenger().handleMessage(message);
            return;
        }
        if (_item.getBlock().getProperties().isReferenceBlock())
        {
            defName = String.format("%s.%s", _item.getBlock().getProperties().getReferencedBlockName(), defName);
        }
        EJLovDefinitionProperties lovDef = _item.getForm().getProperties().getLovDefinitionProperties(defName);

        if (lovDef == null)
        {
            return;
        }

        EJLovController lovController = _item.getForm().getLovController(defName);
        if (lovController == null)
        {
            return;
        }
        try
        {
            lovController.executeQuery(new EJItemLovController(_item.getBlock().getBlockController().getFormController(),
                    _item, ((EJCoreItemProperties)_itemProperties).getLovMappingPropertiesOnUpdate()));

            if (!_item.getProperties().isMandatory())
            {
                ComboBoxValue emptyValue = new ComboBoxValue(null, defItemName);
                _comboValues.add(emptyValue);
            }

            Collection<EJDataRecord> records = lovController.getRecords();
            for (EJDataRecord ejDataRecord : records)
            {
                if (!ejDataRecord.containsItem(defItemName))
                {
                    EJMessage message = new EJMessage("The item name '" + defItemName
                            + "', does not exist within the lov definitions underlying block. Lov Definition: " + defName);
                    _item.getForm().getFrameworkManager().getApplicationManager().getApplicationMessenger().handleMessage(message);
                    return;
                }

                ComboBoxValue comboValue = new ComboBoxValue(ejDataRecord, defItemName);

                _comboValues.add(comboValue);

            }

        }
        catch (Exception e)
        {
            _item.getForm().getFrameworkManager().getApplicationManager().getApplicationMessenger().handleException(e, true);

        }

    }

    @Override
    public boolean isEditAllowed()
    {
        if ((_comboField) != null)
        {
            return !_comboField.isDisable();
        }
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
        if ((_comboField) != null)
        {
            return _comboField.isVisible();
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
        if ((_comboField) != null)
            _comboField.setDisable(!editAllowed);
        setMandatoryBorder(editAllowed && _mandatory);

        if (_actionControl != null)
            _actionControl.setActionVisible(isLovActivated() && (editAllowed));
    }

    public boolean isLovActivated()
    {
        return _lovActivated;
    }

    @Override
    public void setHint(String hint)
    {
        if ((_comboField) != null)
            _comboField.setTooltip(hint == null || hint.trim().length() == 0 ? null : new Tooltip(hint));

    }

    @Override
    public void setInitialValue(Object value)
    {

        setValue(value);

    }

    @Override
    public void setLabel(String label)
    {
        if ((_label) != null)
            _label.setText(label == null ? "" : label);

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
    public void refreshItemRenderer()
    {
        fillComboBox();
        refreshCombo();
    }

    @Override
    public void setValue(Object value)
    {
        baseValue = value;
        if (!_lovInitialied && value != null)
        {
            verifyLOVState();
            return;
        }
        if ((_comboField) != null)
        {
            try
            {
                activeEvent = false;
                if (value != null)
                {

                    if (_comboValues != null && !_comboValues.isEmpty())
                    {
                        ComboBoxValue boxValue = null;

                        for (ComboBoxValue val : _comboValues)
                        {
                            if (val.getItemValue() == null && value == null)
                            {
                                boxValue = val;
                                break;
                            }

                            if (val.getItemValue() == null)
                            {
                                continue;
                            }

                            if (!val.getItemValue().getClass().isAssignableFrom(value.getClass()))
                            {
                                EJMessage message = EJMessageFactory.getInstance().createMessage(EJFrameworkMessage.INVALID_DATA_TYPE_FOR_ITEM,
                                        _item.getName(), val.getItemValue().getClass().getName(), value.getClass().getName());
                                throw new IllegalArgumentException(message.getMessage());
                            }

                            if (val.getItemValue().equals(value))
                            {
                                boxValue = val;
                                break;
                            }

                        }

                        if (boxValue != null)
                        {
                            if (value.equals(boxValue.getItemValue()))
                            {
                                _comboField.setValue(boxValue);

                            }
                            else if (boxValue.getItemValue() == null)
                            {
                                _comboField.setValue(null);
                            }
                        }
                    }

                }
                else
                {
                    _comboField.setValue(null);
                }
            }
            finally
            {
                activeEvent = true;
            }

            setMandatoryBorder(_mandatory);
        }

    }

    @Override
    public void setVisible(boolean visible)
    {
        if ((_comboField) != null)
            _comboField.setVisible(visible);

        if ((_label) != null)
            _label.setVisible(visible);
    }

    @Override
    public EJCoreVisualAttributeProperties getVisualAttributeProperties()
    {
        return _visualAttributeProperties;
    }

    @Override
    public void validationErrorOccurred(boolean error)
    {

        // _isValid=error;
        _actionControl.setErrorDescriptionText(null);
        _actionControl.setShowError(error);

        fireTextChange();

    }

    @Override
    public boolean valueEqualsTo(Object value)
    {
        return value.equals(this.getValue());
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("ComboItem:\n");
        buffer.append("  RegisteredItemName: ");
        buffer.append(_registeredItemName);
        buffer.append("\n");
        buffer.append("  Combo: ");
        buffer.append(_comboField);
        buffer.append("  Label: ");
        buffer.append(_label);
        buffer.append("  GUI Component: ");
        buffer.append(_comboField);

        return buffer.toString();
    }

    @Override
    public Node getGuiComponent()
    {

        return _actionControl;
    }

    @Override
    public Label getGuiComponentLabel()
    {

        return _label;
    }

    protected void setMandatoryBorder(boolean req)
    {
        if (_actionControl != null)
            _actionControl.setShowMandatory(req && getValue() == null);
    }

    @Override
    public void setVisualAttribute(EJCoreVisualAttributeProperties va)
    {

        Node node = _actionControl != null ? _actionControl.getNode() : null;

        if (va == null)
        {
            if (_vaCSSName != null)
            {

                if (node != null)
                    node.getStyleClass().remove(_vaCSSName);
            }
            if (_initialVAProperties == null || EJCoreVisualAttributeProperties.UNSPECIFIED.equals(_initialVAProperties.getBackgroundRGB()))
            {
                node.getStyleClass().remove(CSS_VA_TEXT_BG);
            }
        }
        else
        {
            String css = EJFXVisualAttributeUtils.INSTANCE.toCSS(va);
            if (css != null && !css.equals(_vaCSSName))
            {

                if (node != null)
                    node.getStyleClass().add(_vaCSSName = css);

            }

            if (_initialVAProperties == null || EJCoreVisualAttributeProperties.UNSPECIFIED.equals(_initialVAProperties.getBackgroundRGB()))
            {
                if (!EJCoreVisualAttributeProperties.UNSPECIFIED.equals(va.getBackgroundRGB()))
                {
                    node.getStyleClass().add(CSS_VA_TEXT_BG);
                }
            }
        }

    }

    class ComboBoxValue
    {
        private String                  _valueLabel;
        private Object                  _itemValue;

        private HashMap<String, Object> _returnItemValues = new HashMap<String, Object>();

        public ComboBoxValue(EJDataRecord record, String lovItemName)
        {
            constructStringValue(record, lovItemName);
        }

        private void constructStringValue(EJDataRecord record, String lovItemName)
        {
            if (record == null)
            {
                _itemValue = null;
            }
            else
            {
                _itemValue = record.getValue(lovItemName);
            }

            final EJFrameworkExtensionPropertyList propertyList = _rendererProps.getPropertyList(EJFXComboBoxRendererDefinitionProperties.DISPLAY_COLUMNS);

            if (propertyList == null)
            {
                return;
            }

            StringBuffer buffer = new StringBuffer();
            boolean multi = false;
            for (EJFrameworkExtensionPropertyListEntry entry : propertyList.getAllListEntries())
            {
                String format = entry.getProperty(EJFXComboBoxRendererDefinitionProperties.COLUMN_FORMAT);
                boolean display = Boolean.valueOf(entry.getProperty(EJFXComboBoxRendererDefinitionProperties.COLUMN_DISPLAYED));
                String returnItem = entry.getProperty(EJFXComboBoxRendererDefinitionProperties.COLUMN_RETURN_ITEM);

                // If I have a null record, then I need to initialize all my
                // return items to null when this value is chosen
                if (record == null)
                {
                    _returnItemValues.put(returnItem, null);
                    _valueLabel = "";
                    continue;
                }

                Object val = record.getValue(entry.getProperty(EJFXComboBoxRendererDefinitionProperties.COLUMN_NAME));
                
                if(returnItem!=null && !returnItem.isEmpty())
                    _returnItemValues.put(returnItem, val);

                if (display && val !=null)
                {
                    if (multi)
                    {
                        buffer.append(" - ");
                    }

                    buffer.append(getFormattedString(val, format));
                    multi = true;
                }

            }

            _valueLabel = buffer.toString();

        }

        public String toString()
        {
            return getItemValueAsString();
        }

        public void populateReturnItems(EJBlockController controller, EJScreenType screenType)
        {
            for (String itemName : _returnItemValues.keySet())
            {

                if (itemName == null || itemName.length() == 0)
                    continue;

                EJScreenItemController itemController = controller.getScreenItem(screenType, itemName);
                if (itemController != null)
                {
                    itemController.getItemRenderer().setValue(_returnItemValues.get(itemName));

                    // Was a scrfeen item, so no need to go to the record...
                    continue;
                }

                EJFXAbstractScreenRenderer abstractScreenRenderer = null;
                switch (screenType)
                {
                    case MAIN:
                        EJDataRecord record = controller.getFocusedRecord();
                        if (record == null)
                        {
                            break;
                        }

                        if (record.containsItem(itemName))
                        {
                            record.setValue(itemName, _returnItemValues.get(itemName));
                        }
                        break;
                    case INSERT:
                        abstractScreenRenderer = (EJFXAbstractScreenRenderer) controller.getManagedInsertScreenRenderer().getUnmanagedRenderer();
                        break;
                    case QUERY:
                        abstractScreenRenderer = (EJFXAbstractScreenRenderer) controller.getManagedQueryScreenRenderer().getUnmanagedRenderer();
                        break;
                    case UPDATE:
                        abstractScreenRenderer = (EJFXAbstractScreenRenderer) controller.getManagedUpdateScreenRenderer().getUnmanagedRenderer();
                        break;
                }
                if (abstractScreenRenderer != null)
                {
                    EJBlockItemRendererRegister itemRegister = abstractScreenRenderer.getItemRegister();
                    itemRegister.setItemValueNoValidate(screenType, itemName, _returnItemValues.get(itemName));
                }

            }
        }

        public String getItemValueAsString()
        {
            return _valueLabel;
        }

        public Object getItemValue()
        {
            return _itemValue;
        }
    }

    private String getFormattedString(Object value, String format)
    {
        Locale defaultLocale = _item.getForm().getFrameworkManager().getCurrentLocale();
        if (defaultLocale == null)
        {
            defaultLocale = Locale.UK;
        }

        if (value instanceof BigDecimal)
        {
            DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(defaultLocale);

            if (format == null || format.trim().length() == 0)
            {
                format = "##################.00";
            }
            DecimalFormat decimalFormat = new DecimalFormat("", formatSymbols);
            return decimalFormat.format(value);
        }
        else if (value instanceof Date)
        {
            if (format == null || format.trim().length() == 0)
            {
                format = "dd.MM.yyyy HH:mm:ss";
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat(format, defaultLocale);

            return dateFormat.format(value);
        }
        else
        {
            return value.toString();
        }
    }

    public void valuedChanged()
    {

        _item.itemValueChaged();

        setMandatoryBorder(_mandatory);
    }

    @Override
    public void setInitialVisualAttribute(EJCoreVisualAttributeProperties va)
    {
        this._initialVAProperties = va;
        Node node = _actionControl != null ? _actionControl.getNode() : null;

        if (node != null && va != null)
        {
            if (!EJCoreVisualAttributeProperties.UNSPECIFIED.equals(va.getBackgroundRGB()))
            {
                node.getStyleClass().add(CSS_VA_TEXT_BG);
            }
            node.getStyleClass().add(EJFXVisualAttributeUtils.INSTANCE.toCSS(va));

        }

    }

    public boolean isReadOnly()
    {
        return false;
    }

    @Override
    public void addListener(TextChangeListener listener)
    {
        changeListeners.add(listener);

    }

    @Override
    public void removeListener(TextChangeListener listener)
    {
        changeListeners.remove(listener);

    }

    protected void fireTextChange()
    {
        for (TextChangeListener listener : new ArrayList<TextChangeListener>(changeListeners))
        {
            listener.changed();
        }
    }

    @Override
    public Node createComponent()
    {

        _actionControl = new AbstractActionNode<ComboBox<ComboBoxValue>>()
        {

            @Override
            public ComboBox<ComboBoxValue> createNode()
            {

                String hint = _screenItemProperties.getHint();

                _comboField = new ComboBox<>();
                _comboField.setMaxWidth(Double.MAX_VALUE);
                setHint(hint);
                _comboField.setUserData(_item.getReferencedItemProperties().getName());
                if (_visibleItemCount > 5)
                {
                    _comboField.setVisibleRowCount(_visibleItemCount);
                }
                _comboField.focusedProperty().addListener(new ChangeListener<Boolean>()
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
                _comboField.valueProperty().addListener(new ChangeListener<ComboBoxValue>()
                {
                    @Override
                    public void changed(ObservableValue ov, ComboBoxValue t, ComboBoxValue t1)
                    {
                        if (activeEvent)
                        {
                            _isValid = true;
                            ComboBoxValue value = getComboBoxValue();
                            if (value != null)
                            {
                                value.populateReturnItems(_item.getBlock().getBlockController(), _item.getScreenType());
                            }

                            _item.itemValueChaged();

                            setMandatoryBorder(_mandatory);
                            // fire action command
                            _item.executeActionCommand();
                        }

                    }
                });

                return _comboField;
            }

            @Override
            public Control createActionLabel()
            {
                Label label = new Label();
                label.setGraphic(new ImageView(EJFXImageRetriever.get(EJFXImageRetriever.IMG_FIND_LOV)));
                label.focusedProperty().addListener(new ChangeListener<Boolean>()
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
                label.setOnMouseReleased(new EventHandler<Event>()
                {

                    @Override
                    public void handle(Event event)
                    {
                        _item.getItemLovController().displayLov(EJLovDisplayReason.LOV);

                    }
                });

                getNode().setOnKeyReleased(new EventHandler<KeyEvent>()
                {

                    @Override
                    public void handle(KeyEvent event)
                    {
                        if ( event.isShiftDown() && event.getCode() == KeyCode.DOWN && isLovActivated())
                        {
                            _item.getItemLovController().displayLov(EJLovDisplayReason.LOV);
                        }

                    }
                });

                return label;
            }

            @Override
            public Control createCustomActionLabel()
            {
                // ignore
                return null;
            }

        };
        _comboField.getItems().addAll(_comboValues);

        _actionControl.setMandatoryDescriptionText(_screenItemProperties.getLabel() == null || _screenItemProperties.getLabel().isEmpty() ? "Required Item"
                : String.format("%s is required", _screenItemProperties.getLabel()));
        if (_isValid)
            _actionControl.setShowError(false);
        _actionControl.setShowMandatory(false);
        setInitialValue(baseValue);
        return _actionControl;
    }

    @Override
    public Label createLable()
    {

        _label = new Label();
        _label.setText(_screenItemProperties.getLabel() == null ? "" : _screenItemProperties.getLabel());
        return _label;
    }

    @Override
    public TableColumn<EJDataRecord, EJDataRecord> createColumnProvider(EJScreenItemProperties item, EJScreenItemController controller)
    {
        if (!_lovInitialied)
        {
            fillComboBox();
        }
        TableColumn<EJDataRecord, EJDataRecord> column = new TableColumn<>(item.getLabel());

        Callback<TableColumn<EJDataRecord, EJDataRecord>, TableCell<EJDataRecord, EJDataRecord>> checkboxCellFactory = new Callback<TableColumn<EJDataRecord, EJDataRecord>, TableCell<EJDataRecord, EJDataRecord>>()
        {

            @Override
            public TableCell<EJDataRecord, EJDataRecord> call(TableColumn<EJDataRecord, EJDataRecord> p)
            {
                return createVACell(p);
            }
        };
        column.setCellFactory(checkboxCellFactory);

        return column;
    }

    protected TableCell<EJDataRecord, EJDataRecord> createVACell(TableColumn<EJDataRecord, EJDataRecord> p)
    {
        return new VACell(_registeredItemName)
        {

            @Override
            public String getText(EJDataRecord record)
            {
                Object value = record.getValue(_registeredItemName);

                if (value != null && _comboValues != null && !_comboValues.isEmpty())
                {
                    ComboBoxValue boxValue = null;

                    for (ComboBoxValue val : _comboValues)
                    {
                        if (val.getItemValue() == null && value == null)
                        {
                            boxValue = val;
                            break;
                        }

                        if (val.getItemValue() == null)
                        {
                            continue;
                        }

                        if (val.getItemValue().equals(value))
                        {
                            boxValue = val;
                            break;
                        }

                    }

                    if (boxValue != null)
                    {
                        return boxValue.toString();
                    }
                }
                return null;
            }

            protected void paintCellCSS(EJDataRecord value)
            {
                getStyleClass().remove(CSS_VA_CELL_BG);
                if (value != null)
                {
                    if (_initialVAProperties != null)
                    {

                        if (!EJCoreVisualAttributeProperties.UNSPECIFIED.equals(_initialVAProperties.getBackgroundRGB()))
                            getStyleClass().add(CSS_VA_CELL_BG);

                        getStyleClass().add(EJFXVisualAttributeUtils.INSTANCE.toCSS(_initialVAProperties));
                    }
                    EJCoreVisualAttributeProperties attributes = getAttributes(value);
                    if (attributes != null)
                    {

                        if (_initialVAProperties == null || EJCoreVisualAttributeProperties.UNSPECIFIED.equals(_initialVAProperties.getBackgroundRGB()))
                        {
                            if (!EJCoreVisualAttributeProperties.UNSPECIFIED.equals(attributes.getBackgroundRGB()))
                            {
                                getStyleClass().add(CSS_VA_CELL_BG);
                            }
                        }
                        getStyleClass().add(EJFXVisualAttributeUtils.INSTANCE.toCSS(attributes));
                    }

                }
            }
        };
    }

    protected EJCoreVisualAttributeProperties getAttributes(EJDataRecord record)
    {
        EJCoreVisualAttributeProperties properties = null;

        properties = record.getItem(_registeredItemName).getVisualAttribute();

        if (properties == null)
            properties = _visualAttributeProperties;

        return properties;
    }

    @Override
    public Comparator<EJDataRecord> getColumnSorter(EJScreenItemProperties itemProps, EJScreenItemController item)
    {
        // TODO Auto-generated method stub
        return null;
    }

}

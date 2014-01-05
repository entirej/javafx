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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import jfxtras.labs.scene.control.CalendarPicker;
import jfxtras.labs.scene.control.CalendarTextField;
import jfxtras.labs.scene.control.CalendarTimeTextField;

import org.entirej.applicationframework.fx.renderers.interfaces.EJFXAppItemRenderer;
import org.entirej.applicationframework.fx.renderers.items.definition.interfaces.EJFXDateTimeItemRendererDefinitionProperties;
import org.entirej.applicationframework.fx.renderers.items.definition.interfaces.EJFXTextItemRendererDefinitionProperties;
import org.entirej.applicationframework.fx.utils.EJFXImageRetriever;
import org.entirej.applicationframework.fx.utils.EJFXVisualAttributeUtils;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.EJMessageFactory;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.enumerations.EJFrameworkMessage;
import org.entirej.framework.core.enumerations.EJLovDisplayReason;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJItemProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;

public class EJFXDateTimeItemRenderer implements EJFXAppItemRenderer
{
    private DateFormat                        _format;

    private static final String               CSS_VA_TEXT_BG = "va-text-bg";
    protected EJFrameworkExtensionProperties  _rendererProps;
    protected EJScreenItemController          _item;
    protected EJScreenItemProperties          _screenItemProperties;
    protected EJItemProperties                _itemProperties;
    protected String                          _registeredItemName;
    protected boolean                         _mandatory;
    protected boolean                         _displayValueAsLabel;

    protected AbstractActionNode<Control>     _actionControl;
    protected Label                           _valueLabel;
    protected Label                           _label;
    protected boolean                         _lovActivated;

    protected boolean                         _valueChanged;

    protected EJCoreVisualAttributeProperties _visualAttributeProperties;
    protected String                          _vaCSSName;
    protected EJCoreVisualAttributeProperties _initialVAProperties;

    protected boolean                         _isValid       = true;

    protected Object                          baseValue;

    private DataTimeAdapter                   adapter;

    enum DATE_TIME_TYPE
    {
        CALENDAR, DATE, TIME
    };

    class DataTimeAdapter
    {

        final DATE_TIME_TYPE type;

         Control        node;

        DataTimeAdapter(DATE_TIME_TYPE type, DateFormat format)
        {
            this.type = type;
            switch (type)
            {
                case CALENDAR:
                {
                    CalendarPicker calendarPicker = new CalendarPicker();
                    node = calendarPicker;
                }
                    break;
                    
                case DATE:
                {
                    CalendarTextField calendarTextField = new CalendarTextField(); 
                    calendarTextField.dateFormatProperty().set(format);
                    node = calendarTextField;
                }
                    break;
                case TIME:
                {
                    CalendarTimeTextField timeTextField = new CalendarTimeTextField();
                    timeTextField.dateFormatProperty().set(format);
                    node = timeTextField;
                }
                break;
            }
        }

        Calendar toCalendar(Date date)
        {
            Locale defaultLocale = _item.getForm().getFrameworkManager().getCurrentLocale();
            if (defaultLocale == null)
            {
                defaultLocale = Locale.getDefault();
            }
            Calendar instance = Calendar.getInstance(defaultLocale);
            if(date!=null)
            {
                instance.setTime(date);
            }
            return instance;
        }
        
        void setValue(Date date)
        {
            switch (type)
            {
                case CALENDAR:
                {
                    CalendarPicker calendarPicker = (CalendarPicker) node;
                    calendarPicker.calendarProperty().set(toCalendar(date));
                     break;
                }
                case DATE:
                {
                    CalendarTextField calendarTextField = (CalendarTextField) node;
                    calendarTextField.calendarProperty().set(toCalendar(date));
                    break;
                }
                case TIME:
                {
                    CalendarTimeTextField timeTextField = (CalendarTimeTextField) node;
                    timeTextField.calendarProperty().set(toCalendar(date));
                    break;
                }
            }
        }

        Date getDate()
        {

            switch (type)
            {
                case CALENDAR:
                {
                    CalendarPicker calendarPicker = (CalendarPicker) node;
                    Calendar value = calendarPicker.calendarProperty().getValue();
                    return value!=null ? value.getTime():null;
                }
                case DATE:
                {
                    CalendarTextField calendarTextField = (CalendarTextField) node;
                    Calendar value = calendarTextField.calendarProperty().getValue();
                    return value!=null ? value.getTime():null;
                }
                case TIME:
                {
                    CalendarTimeTextField timeTextField = (CalendarTimeTextField) node;
                    Calendar value = timeTextField.calendarProperty().getValue();
                    return value!=null ? value.getTime():null;
                }
            }
            
            return null;
        }

        Control getControl()
        {
            return node;
        }

        public void clear()
        {
            switch (type)
            {
                case CALENDAR:
                {
                    CalendarPicker calendarPicker = (CalendarPicker) node;
                    calendarPicker.calendarProperty().set(null);
                     break;
                }
                case DATE:
                {
                    CalendarTextField calendarTextField = (CalendarTextField) node;
                    calendarTextField.calendarProperty().set(null);
                    break;
                }
                case TIME:
                {
                    CalendarTimeTextField timeTextField = (CalendarTimeTextField) node;
                    timeTextField.calendarProperty().set(null);
                    break;
                }
            }

        }

        public boolean isEditable()
        {
            switch (type)
            {
                case CALENDAR:
                {
                    CalendarPicker calendarPicker = (CalendarPicker) node;
                    return !calendarPicker.isDisable();
                }
                case DATE:
                {
                    CalendarTextField calendarTextField = (CalendarTextField) node;
                    return !calendarTextField.isDisable();
                }
                case TIME:
                {
                    CalendarTimeTextField timeTextField = (CalendarTimeTextField) node;
                    return timeTextField.isDisable();
                }
            }
            return false;
        }

        public void setEditable(boolean editAllowed)
        {
            switch (type)
            {
                case CALENDAR:
                {
                    CalendarPicker calendarPicker = (CalendarPicker) node;
                    calendarPicker.setDisable(!editAllowed);
                     break;
                }
                case DATE:
                {
                    CalendarTextField calendarTextField = (CalendarTextField) node;
                    calendarTextField.setDisable(!editAllowed);
                    break;
                }
                case TIME:
                {
                    CalendarTimeTextField timeTextField = (CalendarTimeTextField) node;
                    timeTextField.setDisable(!editAllowed);
                    break;
                }
            }

        }

    }

    protected boolean controlState()
    {
        return _actionControl != null;

    }

    @Override
    public void clearValue()
    {
        baseValue = null;
        if (_displayValueAsLabel)
        {
            if (_valueLabel != null)
                _valueLabel.setText("");
        }

        if (controlState())
            adapter.clear();

    }

    @Override
    public void enableLovActivation(boolean activate)
    {
        _lovActivated = activate;
        if (_displayValueAsLabel)
        {
            return;
        }
        if (controlState())
            _actionControl.setActionVisible(activate && (isEditAllowed()));

    }

    @Override
    public void gainFocus()
    {
        if (_displayValueAsLabel)
        {
            if (_valueLabel != null)
                _valueLabel.requestFocus();
        }
        else
        {
            if (controlState())
                _actionControl.getNode().requestFocus();
        }

    }

    @Override
    public EJScreenItemController getItem()
    {
        return _item;
    }

    public EJItemProperties getItemProperties()
    {
        return _itemProperties;
    }

    @Override
    public String getRegisteredItemName()
    {
        return _registeredItemName;
    }

    @Override
    public EJCoreVisualAttributeProperties getVisualAttributeProperties()
    {
        return _visualAttributeProperties;
    }

    public boolean isLovActivated()
    {
        return _lovActivated;
    }

    @Override
    public boolean isEditAllowed()
    {
        if (controlState())
        {
            return adapter.isEditable();
        }

        return false;
    }

    @Override
    public boolean isMandatory()
    {
        return _mandatory;
    }

    @Override
    public boolean isReadOnly()
    {
        return false;
    }

    @Override
    public boolean isValid()
    {
        if (_displayValueAsLabel)
        {
            return true;
        }

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
        if (_displayValueAsLabel)
        {
            if ((_valueLabel != null))
                return _valueLabel.isVisible();
        }
        else
        {
            if (controlState())
                return _actionControl.isVisible();
        }

        return false;
    }

    @Override
    public void refreshItemRenderer()
    {
        // ignore

    }

    @Override
    public void refreshItemRendererProperty(String arg0)
    {
        // ignore

    }

    @Override
    public void setHint(String hint)
    {
        if (hint == null || hint.trim().length() == 0)
        {
            if (controlState())
                _actionControl.getNode().setTooltip(null);
            if (_valueLabel != null)
                _valueLabel.setTooltip(null);
        }
        else
        {
            if (controlState())
                _actionControl.getNode().setTooltip(new Tooltip(hint));
            if (_valueLabel != null)
                _valueLabel.setTooltip(new Tooltip(hint));
        }

    }

    @Override
    public void setInitialValue(Object value)
    {

        setValue(value);

    }

    @Override
    public void setLabel(String label)
    {
        if (_label != null)
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
    public void setVisible(boolean visible)
    {
        if (_displayValueAsLabel)
        {
            if (_valueLabel != null)
                _valueLabel.setVisible(visible);
        }
        else
        {
            if (controlState())
                _actionControl.setVisible(visible);
        }

        if (_label != null)
            _label.setVisible(visible);

    }

    @Override
    public void setVisualAttribute(EJCoreVisualAttributeProperties va)
    {

        Node node = controlState() ? _actionControl.getNode() : _valueLabel;

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

    @Override
    public void validationErrorOccurred(boolean error)
    {
        if (_displayValueAsLabel)
        {
            return;
        }

        // _isValid=error;
        _actionControl.setErrorDescriptionText(null);
        _actionControl.setShowError(error);

    }

    @Override
    public boolean valueEqualsTo(Object value)
    {
        return value.equals(this.getValue());
    }

    @Override
    public Node getGuiComponent()
    {
        if (_displayValueAsLabel)
        {
            return _valueLabel;
        }
        return _actionControl;
    }

    @Override
    public Label getGuiComponentLabel()
    {
        return _label;
    }

    protected Pos getComponentStyle(String alignmentProperty, Pos style)
    {
        if (alignmentProperty != null && alignmentProperty.trim().length() > 0)
        {
            if (alignmentProperty.equals(EJFXTextItemRendererDefinitionProperties.PROPERTY_ALIGNMENT_LEFT))
            {
                return Pos.CENTER_LEFT;
            }
            else if (alignmentProperty.equals(EJFXTextItemRendererDefinitionProperties.PROPERTY_ALIGNMENT_RIGHT))
            {
                return Pos.CENTER_RIGHT;
            }
            else if (alignmentProperty.equals(EJFXTextItemRendererDefinitionProperties.PROPERTY_ALIGNMENT_CENTER))
            {
                return Pos.CENTER;
            }
        }
        return style != null ? style : Pos.CENTER_LEFT;
    }

    @Override
    public Node createComponent()
    {

        String hint = _screenItemProperties.getHint();

        if (_displayValueAsLabel)
        {
            String alignmentProperty = _rendererProps.getStringProperty(EJFXTextItemRendererDefinitionProperties.PROPERTY_ALIGNMENT);
            _valueLabel = newVlaueLabel();
            _valueLabel.setUserData(_itemProperties.getName());
            _valueLabel.setAlignment(getComponentStyle(alignmentProperty, _valueLabel.getAlignment()));
            setHint(hint);
            setInitialValue(baseValue);
            return _valueLabel;
        }
        else
        {
            _actionControl = new AbstractActionNode<Control>()
            {

                @Override
                public Control createNode()
                {

                    String type = _rendererProps.getStringProperty(EJFXDateTimeItemRendererDefinitionProperties.PROPERTY_TYPE);
                    if (EJFXDateTimeItemRendererDefinitionProperties.PROPERTY_TYPE_CALENDAR.equals(type))
                    {
                        adapter = new DataTimeAdapter(DATE_TIME_TYPE.CALENDAR, _format);
                    }
                    else if (EJFXDateTimeItemRendererDefinitionProperties.PROPERTY_TYPE_TIME.equals(type))
                    {
                        adapter = new DataTimeAdapter(DATE_TIME_TYPE.TIME, _format);
                    }
                    else
                    {
                        adapter = new DataTimeAdapter(DATE_TIME_TYPE.DATE, _format);
                    }

                    if (_rendererProps.getBooleanProperty(EJFXDateTimeItemRendererDefinitionProperties.PROPERTY_DROP_DOWN, false))
                    {
                        // FIXME:
                    }

                    adapter.getControl().focusedProperty().addListener(new ChangeListener<Boolean>()
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
                                if (_valueChanged)
                                {
                                    _valueChanged = false;
                                    _item.itemValueChaged();
                                    setMandatoryBorder(_mandatory);

                                }

                            }
                        }
                    });

                    return adapter.getControl();
                }

                @Override
                public Control createCustomActionLabel()
                {
                    return null;
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
                                if (_valueChanged)
                                {
                                    _valueChanged = false;
                                    _item.itemValueChaged();
                                    setMandatoryBorder(_mandatory);

                                }

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
                            if (event.isShiftDown() && event.getCode() == KeyCode.DOWN)
                            {
                                _item.getItemLovController().displayLov(EJLovDisplayReason.LOV);
                            }
                            else if (event.getCode() == KeyCode.ENTER)
                            {
                                if (_valueChanged)
                                {
                                    _valueChanged = false;
                                    _item.itemValueChaged();
                                    setMandatoryBorder(_mandatory);
                                }
                            }

                        }
                    });

                    return label;
                }
            };
            setHint(hint);

            _actionControl.setMandatoryDescriptionText(_screenItemProperties.getLabel() == null || _screenItemProperties.getLabel().isEmpty() ? "Required Item"
                    : String.format("%s is required", _screenItemProperties.getLabel()));
            if (_isValid)
                _actionControl.setShowError(false);
            _actionControl.setShowMandatory(false);
            setInitialValue(baseValue);
            return _actionControl;
        }

    }

    @Override
    public Label createLable()
    {
        _label = new Label();
        _label.setText(_screenItemProperties.getLabel() == null ? "" : _screenItemProperties.getLabel());
        return _label;
    }

    protected Label newVlaueLabel()
    {
        return new Label();
    }

    @Override
    public boolean useFontDimensions()
    {
        return true;
    }

    @Override
    public void setInitialVisualAttribute(EJCoreVisualAttributeProperties va)
    {
        this._initialVAProperties = va;
        Node node = controlState() ? _actionControl.getNode() : _valueLabel;

        if (node != null && va != null)
        {
            if (!EJCoreVisualAttributeProperties.UNSPECIFIED.equals(va.getBackgroundRGB()))
            {
                node.getStyleClass().add(CSS_VA_TEXT_BG);
            }
            node.getStyleClass().add(EJFXVisualAttributeUtils.INSTANCE.toCSS(va));

        }

    }

    @Override
    public TableColumn<EJDataRecord, EJDataRecord> createColumnProvider(EJScreenItemProperties item, EJScreenItemController controller)
    {
        TableColumn<EJDataRecord, EJDataRecord> column = new TableColumn<>(item.getLabel());
        String alignmentProperty = _rendererProps.getStringProperty(EJFXTextItemRendererDefinitionProperties.PROPERTY_ALIGNMENT);
        Pos pos = getComponentStyle(alignmentProperty, null);
        final String alignmentCSS;
        switch (pos)
        {
            case CENTER:
                alignmentCSS = "-fx-alignment:center;";
                break;
            case CENTER_RIGHT:
                alignmentCSS = "-fx-alignment:center-right;";
                break;
            case CENTER_LEFT:
                alignmentCSS = "-fx-alignment:center-left ;";
                break;

            default:
                alignmentCSS = null;
                break;
        }

        Callback<TableColumn<EJDataRecord, EJDataRecord>, TableCell<EJDataRecord, EJDataRecord>> checkboxCellFactory = new Callback<TableColumn<EJDataRecord, EJDataRecord>, TableCell<EJDataRecord, EJDataRecord>>()
        {

            @Override
            public TableCell<EJDataRecord, EJDataRecord> call(TableColumn<EJDataRecord, EJDataRecord> p)
            {

                TableCell<EJDataRecord, EJDataRecord> cell = createVACell(p);

                if (alignmentCSS != null)
                {
                    cell.setStyle(alignmentCSS);
                }

                return cell;
            }
        };
        column.setCellFactory(checkboxCellFactory);

        return column;
    }

    protected EJCoreVisualAttributeProperties getAttributes(EJDataRecord record)
    {
        EJCoreVisualAttributeProperties properties = null;

        properties = record.getItem(_registeredItemName).getVisualAttribute();

        if (properties == null)
            properties = _visualAttributeProperties;

        return properties;
    }

    public static class VACell extends TableCell<EJDataRecord, EJDataRecord>
    {

        protected final String        _registeredItemName;

        protected static final String CSS_VA_CELL_BG = "va-cell-bg";

        @Override
        public void updateItem(EJDataRecord value, boolean empty)
        {

            paintCell(value);
            paintCellCSS(value);
        }

        public VACell(String _registeredItemName)
        {
            this._registeredItemName = _registeredItemName;
        }

        protected void paintCell(EJDataRecord value)
        {

            if (value != null)
            {
                setText(getText(value));
            }
            else
            {
                setText(null);
                setGraphic(null);
            }

        }

        public String getText(EJDataRecord value)
        {
            Object object = value.getValue(_registeredItemName);
            return (object != null ? object.toString() : null);
        }

        protected void paintCellCSS(EJDataRecord value)
        {

        }
    }

    @Override
    public Comparator<EJDataRecord> getColumnSorter(EJScreenItemProperties itemProps, EJScreenItemController item)
    {
        return new Comparator<EJDataRecord>()
        {

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

    protected void setMandatoryBorder(boolean req)
    {
        if (_displayValueAsLabel)
        {
            return;
        }
        _actionControl.setShowMandatory(req && getValue() == null);

    }

    public void valueChanged()
    {
        if (!_actionControl.getNode().isFocused())
        {
            _item.itemValueChaged();
        }
        else
        {
            _valueChanged = true;
        }
        setMandatoryBorder(_mandatory);
    }

    @Override
    public void setValue(Object value)
    {

        if (value != null && !Date.class.isAssignableFrom(value.getClass()))
        {
            EJMessage message = EJMessageFactory.getInstance().createMessage(EJFrameworkMessage.INVALID_DATA_TYPE_FOR_ITEM, _item.getName(),
                    Date.class.getName(), value.getClass().getName());
            throw new IllegalArgumentException(message.getMessage());
        }
        baseValue = value;
        if (_displayValueAsLabel)
        {
            if ((_valueLabel) != null)
            {
                _valueLabel.setText(value != null ? _format.format(value) : "");
            }
        }
        else
        {

            if (controlState())
            {
                adapter.setValue((Date) value);
                setMandatoryBorder(_mandatory);
            }
        }

    }

    @Override
    public void initialise(EJScreenItemController item, EJScreenItemProperties screenItemProperties)
    {
        _registeredItemName = item.getReferencedItemProperties().getName();
        _item = item;
        _itemProperties = _item.getReferencedItemProperties();
        _screenItemProperties = screenItemProperties;
        _rendererProps = _itemProperties.getItemRendererProperties();
        _displayValueAsLabel = _rendererProps.getBooleanProperty(EJFXTextItemRendererDefinitionProperties.PROPERTY_DISPLAY_VAUE_AS_LABEL, false);

        _format = createDateFormat(_item);
        if (_actionControl != null)
            _actionControl.setCustomActionVisible(isEditAllowed());

    }

    protected static DateFormat createDateFormat(EJScreenItemController item)
    {
        DateFormat dateFormat;
        Locale defaultLocale = item.getForm().getFrameworkManager().getCurrentLocale();
        if (defaultLocale == null)
        {
            defaultLocale = Locale.getDefault();
        }

        EJFrameworkExtensionProperties rendererProps = item.getReferencedItemProperties().getItemRendererProperties();
        String format = rendererProps.getStringProperty(EJFXDateTimeItemRendererDefinitionProperties.PROPERTY_DETAILS);
        boolean istime = EJFXDateTimeItemRendererDefinitionProperties.PROPERTY_TYPE_TIME.equals(rendererProps
                .getStringProperty(EJFXDateTimeItemRendererDefinitionProperties.PROPERTY_TYPE));
        if (EJFXDateTimeItemRendererDefinitionProperties.PROPERTY_DETAILS_LONG.equals(format))
        {
            dateFormat = istime ? DateFormat.getTimeInstance(DateFormat.LONG, defaultLocale) : DateFormat.getDateInstance(DateFormat.LONG, defaultLocale);
        }
        else if (EJFXDateTimeItemRendererDefinitionProperties.PROPERTY_DETAILS_MEDIUM.equals(format))
        {
            dateFormat = istime ? DateFormat.getTimeInstance(DateFormat.MEDIUM, defaultLocale) : DateFormat.getDateInstance(DateFormat.MEDIUM, defaultLocale);
        }
        else if (EJFXDateTimeItemRendererDefinitionProperties.PROPERTY_DETAILS_SHORT.equals(format))
        {
            dateFormat = istime ? DateFormat.getTimeInstance(DateFormat.SHORT, defaultLocale) : DateFormat.getDateInstance(DateFormat.SHORT, defaultLocale);
        }
        else
        {
            dateFormat = new SimpleDateFormat();
        }

        return dateFormat;
    }

    @Override
    public Date getValue()
    {
        if (_displayValueAsLabel || !controlState())
            return (Date) baseValue;

        Date value = null;

        value = adapter.getDate();

        // convert to correct type if need
        if (value != null && !_itemProperties.getDataTypeClassName().equals(Date.class.getName()))
        {
            String dataTypeClass = _itemProperties.getDataTypeClassName();
            if (dataTypeClass.equals("java.sql.Date"))
                value = new java.sql.Date(value.getTime());
            else if (dataTypeClass.equals("java.sql.Time"))
                value = new java.sql.Time(value.getTime());
            else if (dataTypeClass.equals("java.sql.Timestamp"))
                value = new java.sql.Timestamp(value.getTime());
        }

        baseValue = value;

        return value;
    }

    protected TableCell<EJDataRecord, EJDataRecord> createVACell(TableColumn<EJDataRecord, EJDataRecord> p)
    {
        final DateFormat format = createDateFormat(_item);
        return new VACell(_registeredItemName)
        {

            @Override
            public String getText(EJDataRecord value)
            {
                Object v = value.getValue(_registeredItemName);
                if (v != null && v instanceof Date)
                    return format.format(v);
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

    public void setEditAllowed(boolean editAllowed)
    {
        if (_displayValueAsLabel)
        {
            return;
        }
        if (controlState())
            adapter.setEditable(editAllowed);

        setMandatoryBorder(editAllowed && _mandatory);

        if (controlState())
            _actionControl.setActionVisible(isLovActivated() && (editAllowed));
    }

}

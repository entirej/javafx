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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.stage.Popup;

import org.entirej.applicationframework.fx.controls.calendar.CalendarView;
import org.entirej.applicationframework.fx.renderers.items.definition.interfaces.EJFXTextItemRendererDefinitionProperties;
import org.entirej.applicationframework.fx.utils.EJFXImageRetriever;
import org.entirej.applicationframework.fx.utils.EJFXVisualAttributeUtils;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.EJMessageFactory;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.enumerations.EJFrameworkMessage;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;

public class EJFXDateItemRenderer extends EJFXTextItemRenderer
{

    private MultiDateFormater _dateFormat;

    @Override
    protected TextField newTextField()
    {
        String alignmentProperty = _rendererProps.getStringProperty(EJFXTextItemRendererDefinitionProperties.PROPERTY_ALIGNMENT);
        final TextField _textField = new TextField()
        {
            @Override
            public void replaceText(int start, int end, String text)
            {
                super.replaceText(start, end, (text));
            }

            @Override
            public void replaceSelection(String text)
            {
                super.replaceSelection((text));
            }

        };
        _textField.setAlignment(getComponentStyle(alignmentProperty, Pos.CENTER_LEFT));

        _textField.textProperty().addListener(new ChangeListener<String>()
        {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
            {
                if (newValue != null && newValue.trim().length() > 0)
                    try
                    {
                        _dateFormat.parse(newValue);
                    }
                    catch (ParseException e)
                    {
                        _rendererProps.getStringProperty(EJFXTextItemRendererDefinitionProperties.PROPERTY_FORMAT);

                        _actionControl.setErrorDescriptionText(String.format("Invalid Date format. Should be %s ",
                                _rendererProps.getStringProperty(EJFXTextItemRendererDefinitionProperties.PROPERTY_FORMAT)));
                        _actionControl.setShowError(true,AbstractActionNode.ErrorIconType.ERROR);
                    }

            }

        });
        if (_rendererProps != null && _rendererProps.getBooleanProperty(EJFXTextItemRendererDefinitionProperties.PROPERTY_SELECT_ON_FOCUS, false))
        {
            _textField.focusedProperty().addListener(new ChangeListener<Boolean>()
            {

                @Override
                public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean newValue)
                {
                    if (newValue.booleanValue())
                    {
                        _textField.selectAll();

                    }
                    
                }
            });
            
        }
        _textField.focusedProperty().addListener(new ChangeListener<Boolean>()
        {
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                if (!newValue.booleanValue())
                {
                    try
                    {
                        modifyListener.enable = false;
                        Object value = getValue();
                        if (value != null)
                            _textField.setText(_dateFormat.format(value));
                        else
                            _textField.setText("");
                    }
                    finally
                    {
                        modifyListener.enable = true;
                    }

                }
            }
        });

        String format = _rendererProps.getStringProperty(EJFXTextItemRendererDefinitionProperties.PROPERTY_FORMAT);
        if (format == null || format.length() == 0)
        {
            format = "dd.MM.yyyy";
        }
        _textField.setPromptText(format);

        return _textField;
    }

    @Override
    public void setValue(Object value)
    {
        try
        {

            modifyListener.enable = false;
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
                    _valueLabel.setText(value != null ? _dateFormat.format(value) : "");
                }
            }
            else
            {

                if (controlState())
                {
                    _actionControl.getNode().setText(value != null ? _dateFormat.format(value) : "");
                    setMandatoryBorder(_mandatory);
                }
            }
        }
        finally
        {
            modifyListener.enable = true;
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

        _dateFormat = createDateFormat();
        if (_actionControl != null)
            _actionControl.setCustomActionVisible(isEditAllowed());

    }

    protected MultiDateFormater createDateFormat()
    {
        MultiDateFormater dateFormat;
        Locale defaultLocale = _item.getForm().getFrameworkManager().getCurrentLocale();
        if (defaultLocale == null)
        {
            defaultLocale = Locale.getDefault();
        }

        EJFrameworkExtensionProperties rendererProps = _item.getReferencedItemProperties().getItemRendererProperties();
        String format = rendererProps.getStringProperty(EJFXTextItemRendererDefinitionProperties.PROPERTY_FORMAT);
        if (format == null || format.length() == 0)
        {
            format = "dd.MM.yyyy";
            dateFormat = new MultiDateFormater(new SimpleDateFormat(format, defaultLocale));
        }
        else
        {
            String[] split = format.split("\\|");
            SimpleDateFormat[] formats = new SimpleDateFormat[split.length];
            for (int i = 0; i < split.length; i++)
            {
                formats[i] = new SimpleDateFormat(split[i], defaultLocale);
            }
            dateFormat = new MultiDateFormater(formats);
        }

        return dateFormat;
    }

    @Override
    public Date getValue()
    {
        if (_displayValueAsLabel || !controlState())
            return (Date) baseValue;

        Date value = null;
        try
        {
            if (_actionControl.getNode().getText() != null)
                value = (_dateFormat.parse(_actionControl.getNode().getText()));

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
        }
        catch (ParseException e)
        {
            // ignore error
        }

        baseValue = value;

        return value;
    }

    @Override
    protected TableCell<EJDataRecord, EJDataRecord> createVACell(TableColumn<EJDataRecord, EJDataRecord> p)
    {
        final MultiDateFormater format = createDateFormat();
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

            String VA_CSS=null;
            protected void paintCellCSS(EJDataRecord value)
            {
                getStyleClass().remove(CSS_VA_CELL_BG);
                if(VA_CSS!=null)
                {
                    getStyleClass().remove(VA_CSS);
                }
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
                        getStyleClass().add(VA_CSS=EJFXVisualAttributeUtils.INSTANCE.toCSS(attributes));
                    }

                }
            }
        };
    }

    @Override
    public void setEditAllowed(boolean editAllowed)
    {
        super.setEditAllowed(editAllowed);
        if (controlState())
            _actionControl.setCustomActionVisible(editAllowed);
    }

    @Override
    public Control createCustomButtonControl()
    {
        final Label label = new Label();
        label.setGraphic(new ImageView(EJFXImageRetriever.get(EJFXImageRetriever.IMG_DATE_SELECTION)));
        label.setContentDisplay(ContentDisplay.CENTER);
        label.setAlignment(Pos.CENTER);
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
                        _item.itemValueChaged(getValue());
                        _oldVal = null;
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
                Locale defaultLocale = _item.getForm().getFrameworkManager().getCurrentLocale();
                if (defaultLocale == null)
                {
                    defaultLocale = Locale.getDefault();
                }
                final CalendarView calendarView = new CalendarView(defaultLocale);
                calendarView.setEffect(new DropShadow());
                Date currentDate = getValue();
                if (currentDate != null)
                {
                    calendarView.currentDateProperty().set(currentDate);
                }
                final Popup popup = new Popup();
                popup.setAutoHide(true);
                popup.setHideOnEscape(true);
                popup.setAutoFix(true);
                popup.getContent().add(calendarView);
                calendarView.selectedDateProperty().addListener(new InvalidationListener()
                {
                    @Override
                    public void invalidated(Observable observable)
                    {
                        setValue(calendarView.selectedDateProperty().get());
                        _item.gainFocus();
                        fireTextChange();
                        popup.hide();
                    }
                });
                Bounds calendarBounds = calendarView.getBoundsInLocal();
                Bounds bounds = label.localToScene(label.getBoundsInLocal());

                double posX = calendarBounds.getMinX() + bounds.getMinX() + label.getScene().getX() + label.getScene().getWindow().getX();
                double posY = calendarBounds.getMinY() + bounds.getHeight() + bounds.getMinY() + label.getScene().getY() + label.getScene().getWindow().getY();

                popup.show(label, posX, posY);
            }
        });

        return label;
    }

    private static class MultiDateFormater
    {

        private final SimpleDateFormat[] formats;

        public MultiDateFormater(SimpleDateFormat... formats)
        {
            this.formats = formats;

        }

        public Date parse(String text) throws ParseException
        {

            for (SimpleDateFormat format : formats)
            {
                try
                {
                    Date parse = format.parse(text);
                    String pattern = format.toPattern();
                    boolean fixday = !pattern.contains("d");
                    boolean fixmMonth = !pattern.contains("M");
                    boolean fixmYear = !pattern.contains("y");

                    if (fixday || fixmMonth || fixmYear)
                    {
                        Date currentDate = new Date();
                        Calendar calendar = format.getCalendar();

                        if (fixmYear)
                        {
                            calendar.setTime(currentDate);
                            int year = calendar.get(Calendar.YEAR);

                            calendar.setTime(parse);
                            calendar.set(Calendar.YEAR, year);
                            parse = calendar.getTime();
                        }
                        if (fixmMonth)
                        {
                            calendar.setTime(currentDate);
                            int month = calendar.get(Calendar.MONTH);

                            calendar.setTime(parse);
                            calendar.set(Calendar.MONTH, month);
                            parse = calendar.getTime();
                        }
                        if (fixday)
                        {
                            calendar.setTime(currentDate);
                            int day = calendar.get(Calendar.DATE);

                            calendar.setTime(parse);
                            calendar.set(Calendar.DATE, day);
                            parse = calendar.getTime();
                        }

                    }

                    return parse;
                }
                catch (ParseException e)
                {
                    // ignore try next
                }
            }
            throw new ParseException(text, 0);

        }

        public String format(Object value)
        {
            for (SimpleDateFormat format : formats)
            {
                return format.format(value);
            }
            return "";
        }

    }
}

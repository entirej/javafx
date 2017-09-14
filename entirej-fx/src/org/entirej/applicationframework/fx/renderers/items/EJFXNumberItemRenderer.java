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
package org.entirej.applicationframework.fx.renderers.items;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;

import org.entirej.applicationframework.fx.renderers.items.definition.interfaces.EJFXTextItemRendererDefinitionProperties;
import org.entirej.applicationframework.fx.utils.EJFXVisualAttributeUtils;
import org.entirej.framework.core.EJApplicationException;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.EJMessageFactory;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.enumerations.EJFrameworkMessage;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;

public class EJFXNumberItemRenderer extends EJFXTextItemRenderer
{

    private DecimalFormat _decimalFormatter;
    private NUMBER_TYPE   _numberType;

    public enum NUMBER_TYPE
    {
        NUMBER, INTEGER, FLOAT, BIG_DECIMAL, DOUBLE, LONG
    };

    protected Label newVlaueLabel()
    {
        Label label = new Label();
        label.setAlignment(Pos.CENTER_RIGHT);
        return label;
    }

    @Override
    protected Pos getComponentStyle(String alignmentProperty, Pos style)
    {
        return super.getComponentStyle(alignmentProperty, style != null ? style : Pos.CENTER_RIGHT);
    }

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
        _textField.setAlignment(getComponentStyle(alignmentProperty, Pos.CENTER_RIGHT));

        _textField.textProperty().addListener(new ChangeListener<String>()
        {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
            {
                if (newValue != null && newValue.trim().length() > 0)
                    try
                    {
                        _decimalFormatter.parse(newValue);
                    }
                    catch (ParseException e)
                    {
                        _rendererProps.getStringProperty(EJFXTextItemRendererDefinitionProperties.PROPERTY_FORMAT);

                        _actionControl.setErrorDescriptionText(String.format("Invalid Number format. Should be %s ",
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
                            _textField.setText(_decimalFormatter.format(value));
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

        return _textField;
    }

    @Override
    public void setValue(Object value)
    {
        try
        {

            modifyListener.enable = false;
            if (value != null && !Number.class.isAssignableFrom(value.getClass()))
            {
                EJMessage message = EJMessageFactory.getInstance().createMessage(EJFrameworkMessage.INVALID_DATA_TYPE_FOR_ITEM, _item.getName(),
                        Number.class.getName(), value.getClass().getName());
                throw new IllegalArgumentException(message.getMessage());
            }
            baseValue = value;
            if (_displayValueAsLabel)
            {
                if (_valueLabel != null)
                {

                    _valueLabel.setText(value != null ? _decimalFormatter.format(value) : "");
                }
            }
            else
            {

                if (controlState())
                {

                    if (value != null)
                    {
                        if (_maxLength > 0 && value.toString().length() > _maxLength)
                        {
                            EJMessage message = new EJMessage("The value for item, " + _item.getReferencedItemProperties().getBlockName() + "."
                                    + _item.getReferencedItemProperties().getName() + " is too long for its field definition.");
                            throw new EJApplicationException(message);
                        }
                    }

                    _actionControl.getNode().setText(value != null ? _decimalFormatter.format(value) : "");
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
        super.initialise(item, screenItemProperties);

        _numberType = getNumberType();

        _decimalFormatter = createFormatter(_numberType);

    }

    private NUMBER_TYPE getNumberType()
    {
        final String datatypeClassName = _item.getReferencedItemProperties().getDataTypeClassName();
        NUMBER_TYPE numberType;
        if (datatypeClassName.equals(Integer.class.getName()))
        {
            numberType = NUMBER_TYPE.INTEGER;
        }
        else if (datatypeClassName.equals(Float.class.getName()))
        {
            numberType = NUMBER_TYPE.FLOAT;
        }
        else if (datatypeClassName.endsWith(Long.class.getName()))
        {
            numberType = NUMBER_TYPE.LONG;
        }
        else if (datatypeClassName.endsWith(Double.class.getName()))
        {
            numberType = NUMBER_TYPE.DOUBLE;
        }
        else
        {

            numberType = NUMBER_TYPE.BIG_DECIMAL;
        }
        return numberType;
    }

    private DecimalFormat createFormatter(NUMBER_TYPE numberType)
    {

        DecimalFormat _decimalFormatter = null;
        EJFrameworkExtensionProperties _rendererProps = _item.getReferencedItemProperties().getItemRendererProperties();
        Locale defaultLocale = _item.getForm().getFrameworkManager().getCurrentLocale();
        if (defaultLocale == null)
        {
            defaultLocale = Locale.getDefault();
        }

        String format = _rendererProps.getStringProperty(EJFXTextItemRendererDefinitionProperties.PROPERTY_FORMAT);

        if (format == null || format.length() == 0)
        {

            _decimalFormatter = (DecimalFormat) NumberFormat.getNumberInstance(defaultLocale);
        }
        else
        {
            DecimalFormatSymbols dfs = new DecimalFormatSymbols(defaultLocale);
            _decimalFormatter = new DecimalFormat(format, dfs);
            switch (numberType)
            {

                case INTEGER:
                case LONG:

                    _decimalFormatter.setGroupingUsed(true);
                    _decimalFormatter.setParseIntegerOnly(true);
                    _decimalFormatter.setParseBigDecimal(false);
                    break;

                default:

                    char seperator = dfs.getDecimalSeparator();
                    if (format.indexOf(seperator) != -1)
                    {
                        _decimalFormatter.setGroupingUsed(true);
                    }
                    _decimalFormatter.setParseIntegerOnly(false);
                    _decimalFormatter.setParseBigDecimal(true);
                    break;
            }
        }
        return _decimalFormatter;
    }

    public Object getValue()
    {
        if (_displayValueAsLabel)
            return baseValue;
        if (!controlState())
        {
            return baseValue;
        }

        Number value = null;
        try
        {
            value = (Number) _decimalFormatter.parse(_actionControl.getNode().getText());
        }
        catch (ParseException e)
        {
            // ignore error
        }

        if (value == null)
            return baseValue = value;
        try
        {
            switch (_numberType)
            {
                case INTEGER:
                    return value.intValue();
                case LONG:
                    return value.longValue();
                case FLOAT:
                    return value.floatValue();
                case DOUBLE:
                    return value.doubleValue();
                case BIG_DECIMAL:
                    return new BigDecimal(value.toString());
                default:
                    break;

            }
        }
        catch (NumberFormatException nfe)
        {
            nfe.printStackTrace();
        }

        return baseValue = value;
    }

    @Override
    protected TableCell<EJDataRecord, EJDataRecord> createVACell(TableColumn<EJDataRecord, EJDataRecord> p)
    {
        NUMBER_TYPE numberType = getNumberType();
        final DecimalFormat format = createFormatter(numberType);
        return new VACell(_registeredItemName)
        {

            @Override
            public String getText(EJDataRecord value)
            {
                Object v = value.getValue(_registeredItemName);
                if (v != null && v instanceof Number)
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

}

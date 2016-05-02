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
import javafx.scene.control.Control;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import org.entirej.applicationframework.fx.renderers.interfaces.EJFXAppItemRenderer;
import org.entirej.applicationframework.fx.renderers.items.EJFXTextItemRenderer.VACell;
import org.entirej.applicationframework.fx.renderers.items.definition.interfaces.EJFXLabelItemRendererDefinitionProperties;
import org.entirej.applicationframework.fx.renderers.items.definition.interfaces.EJFXTextItemRendererDefinitionProperties;
import org.entirej.applicationframework.fx.utils.EJFXImageRetriever;
import org.entirej.applicationframework.fx.utils.EJFXVisualAttributeUtils;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJItemProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;

public class EJFXLabelItemRenderer implements EJFXAppItemRenderer
{

    protected EJFrameworkExtensionProperties  _rendererProps;
    protected EJScreenItemController          _item;
    protected EJScreenItemProperties          _screenItemProperties;
    protected EJItemProperties                _itemProperties;
    protected String                          _registeredItemName;
    protected FXComponentAdapter              _labelField;

    protected EJCoreVisualAttributeProperties _visualAttributeProperties;
    protected EJCoreVisualAttributeProperties _initialVAProperties;
    protected VALUE_CASE                      _valueCase         = VALUE_CASE.DEFAULT;
    protected String                          _vaCSSName;
    private boolean                           displayAsHyperlink = false;

    protected Object                          baseValue;

    protected boolean controlState(Control control)
    {
        return control != null;

    }

    public boolean useFontDimensions()
    {
        return true;
    }

    public void refreshItemRendererProperty(String propertyName)
    {
    }
    public String getDisplayValue()
    {
        // TODO Auto-generated method stub
        return null;
    }
    public void refreshItemRenderer()
    {

    }

    public void initialise(EJScreenItemController item, EJScreenItemProperties screenItemProperties)
    {
        _registeredItemName = item.getReferencedItemProperties().getName();
        _item = item;
        _itemProperties = _item.getReferencedItemProperties();
        _screenItemProperties = screenItemProperties;
        _rendererProps = _itemProperties.getItemRendererProperties();

        final String caseProperty = _rendererProps.getStringProperty(EJFXLabelItemRendererDefinitionProperties.PROPERTY_CASE);
        if (caseProperty != null && caseProperty.trim().length() > 0)
        {

            if (caseProperty.equals(EJFXLabelItemRendererDefinitionProperties.PROPERTY_CASE_LOWER))
            {
                _valueCase = VALUE_CASE.LOWER;
            }
            else if (caseProperty.equals(EJFXLabelItemRendererDefinitionProperties.PROPERTY_CASE_UPPER))
            {
                _valueCase = VALUE_CASE.UPPER;
            }

        }
    }

    @Override
    public void setLabel(String label)
    {
        if (_labelField != null && controlState(_labelField.getControl()))
            _labelField.setText(label == null ? "" : label);
    }

    @Override
    public void setHint(String hint)
    {
        if (_labelField != null && controlState(_labelField.getControl()))
        {
            _labelField.getControl().setTooltip(hint == null || hint.trim().length() == 0 ? null : new Tooltip(hint));
        }

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

    public Control getGuiComponent()
    {

        return _labelField.getControl();
    }

    public Label getGuiComponentLabel()
    {
        return null;
    }

    public void clearValue()
    {
        baseValue = null;
        if (_labelField != null && controlState(_labelField.getControl()))
        {

            _labelField.setText(_item.getForm().translateText(_screenItemProperties.getLabel()));
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
        if (_labelField != null && controlState(_labelField.getControl()))
        {
            return !_labelField.getControl().isDisabled();
        }
        return false;
    }

    public boolean isVisible()
    {

        if (_labelField != null && controlState(_labelField.getControl()))
            return _labelField.getControl().isVisible();

        return false;
    }

    public boolean isValid()
    {
        return true;

    }

    public void gainFocus()
    {

        if (_labelField != null && controlState(_labelField.getControl()))
            _labelField.getControl().requestFocus();

    }

    public void setEditAllowed(boolean editAllowed)
    {
        if (_labelField != null && controlState(_labelField.getControl()))
        {
            _labelField.getControl().setDisable(!editAllowed);
        }
    }

    public void setInitialValue(Object value)
    {

        setValue(value);

    }

    public void setRegisteredItemName(String name)
    {
        _registeredItemName = name;
    }

    private String toCaseValue(String string)
    {
        switch (_valueCase)
        {
            case LOWER:
                string = string.toLowerCase();
                break;
            case UPPER:
                string = string.toUpperCase();
                break;
            default:
                break;
        }

        return string;

    }

    public void setValue(Object value)
    {
        baseValue = value;
        if (_labelField != null && controlState(_labelField.getControl()))
        {
            if (value == null)
                value = _item.getForm().translateText(_screenItemProperties.getLabel());

            _labelField.setText(value == null ? "" : toCaseValue(value.toString()));

        }

    }

    public void setVisible(boolean visible)
    {

        if (_labelField != null)
            _labelField.getControl().setVisible(visible);
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
    public void setMessage(EJMessage message)
    {
        // TODO Auto-generated method stub
        
    }
    @Override
    public void clearMessage()
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void setVisualAttribute(EJCoreVisualAttributeProperties va)
    {
        _visualAttributeProperties = va;

        if (va == null)
        {
            if (_vaCSSName != null)
            {

                if (_labelField != null)
                    _labelField.getControl().getStyleClass().remove(_vaCSSName);
            }
        }
        else
        {
            String css = EJFXVisualAttributeUtils.INSTANCE.toCSS(va);
            if (css != null && !css.equals(_vaCSSName))
            {
                if (_labelField != null)
                    _labelField.getControl().getStyleClass().add(_vaCSSName = css);

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

    protected Pos getComponentStyle(String alignmentProperty, Pos style)
    {
        if (alignmentProperty != null && alignmentProperty.trim().length() > 0)
        {
            if (alignmentProperty.equals(EJFXLabelItemRendererDefinitionProperties.PROPERTY_ALIGNMENT_LEFT))
            {
                return Pos.CENTER_LEFT;
            }
            else if (alignmentProperty.equals(EJFXLabelItemRendererDefinitionProperties.PROPERTY_ALIGNMENT_RIGHT))
            {
                return Pos.CENTER_RIGHT;
            }
            else if (alignmentProperty.equals(EJFXLabelItemRendererDefinitionProperties.PROPERTY_ALIGNMENT_CENTER))
            {
                return Pos.CENTER;
            }
        }
        return style;
    }

    private static interface FXComponentAdapter
    {

        Control getControl();

        String getText();

        void setText(String string);
    }

    @Override
    public void setInitialVisualAttribute(EJCoreVisualAttributeProperties va)
    {
        this._initialVAProperties = va;
        if (_labelField != null && va != null)
        {
            _labelField.getControl().getStyleClass().add(EJFXVisualAttributeUtils.INSTANCE.toCSS(va));
        }

    }

    public boolean isReadOnly()
    {
        return true;
    }

    @Override
    public Node createComponent()
    {

        String alignmentProperty = _rendererProps.getStringProperty(EJFXLabelItemRendererDefinitionProperties.PROPERTY_ALIGNMENT);

        boolean textWrapProperty = _rendererProps.getBooleanProperty(EJFXLabelItemRendererDefinitionProperties.PROPERTY_TEXT_WRAP, false);
        String hint = _screenItemProperties.getHint();

        displayAsHyperlink = _rendererProps.getBooleanProperty(EJFXLabelItemRendererDefinitionProperties.PROPERTY_DISPLAY_AS_HYPERLINK, false);
        final String label = _screenItemProperties.getLabel();
        if (!displayAsHyperlink)
        {
            final Label labelField = new Label();
            labelField.setAlignment(getComponentStyle(alignmentProperty, labelField.getAlignment()));
            labelField.setWrapText(textWrapProperty);
            String pictureName = _rendererProps.getStringProperty(EJFXLabelItemRendererDefinitionProperties.PROPERTY_PICTURE);

            if (pictureName != null && pictureName.length() > 0)
            {
                if (pictureName != null && pictureName.trim().length() > 0)
                {
                    labelField.setGraphic(new ImageView(EJFXImageRetriever.get(pictureName)));
                }

            }

            _labelField = new FXComponentAdapter()
            {

                @Override
                public void setText(String text)
                {
                    if (controlState(labelField))
                        labelField.setText(text);
                }

                @Override
                public String getText()
                {
                    return labelField.getText();
                }

                @Override
                public Control getControl()
                {
                    return labelField;
                }

            };

        }
        else
        {
            final Hyperlink linkField = new Hyperlink();
            linkField.setAlignment(getComponentStyle(alignmentProperty, linkField.getAlignment()));
            linkField.setWrapText(textWrapProperty);
            _labelField = new FXComponentAdapter()
            {
                String value;

                @Override
                public void setText(String text)
                {
                    if (controlState(linkField))
                        linkField.setText(value = text);
                }

                @Override
                public String getText()
                {
                    return value;
                }

                @Override
                public Control getControl()
                {
                    return linkField;
                }

            };
            linkField.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent e)
                {
                    linkField.setVisited(false);
                    _item.executeActionCommand();
                }
            });

        }

        _labelField.setText(label != null ? label : "");
        setHint(hint);
        _labelField.getControl().setUserData(_item.getReferencedItemProperties().getName());
        _labelField.getControl().focusedProperty().addListener(new ChangeListener<Boolean>()
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

        return _labelField.getControl();
    }

    @Override
    public Label createLable()
    {
        // ignore
        return null;
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

    protected TableCell<EJDataRecord, EJDataRecord> createVACell(TableColumn<EJDataRecord, EJDataRecord> p)
    {
        return new VACell(_registeredItemName)
        {

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

}

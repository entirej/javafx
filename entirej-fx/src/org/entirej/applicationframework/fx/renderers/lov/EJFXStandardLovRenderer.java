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
package org.entirej.applicationframework.fx.renderers.lov;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import org.entirej.applicationframework.fx.application.EJFXApplicationManager;
import org.entirej.applicationframework.fx.application.form.containers.AbstractDialog;
import org.entirej.applicationframework.fx.renderers.blocks.def.EJFXMultiRecordBlockDefinitionProperties;
import org.entirej.applicationframework.fx.renderers.interfaces.EJFXAppItemRenderer;
import org.entirej.applicationframework.fx.renderers.items.EJFXTextItemRenderer.VACell;
import org.entirej.applicationframework.fx.utils.EJFXAbstractFilteredTable;
import org.entirej.applicationframework.fx.utils.EJFXAbstractFilteredTable.FilteredContentProvider;
import org.entirej.framework.core.EJFrameworkManager;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.data.controllers.EJBlockController;
import org.entirej.framework.core.data.controllers.EJItemLovController;
import org.entirej.framework.core.data.controllers.EJLovController;
import org.entirej.framework.core.enumerations.EJLovDisplayReason;
import org.entirej.framework.core.enumerations.EJManagedScreenProperty;
import org.entirej.framework.core.enumerations.EJScreenType;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.internal.EJInternalBlock;
import org.entirej.framework.core.properties.EJCoreMainScreenItemProperties;
import org.entirej.framework.core.properties.EJCoreProperties;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJItemGroupProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;
import org.entirej.framework.core.renderers.EJManagedItemRendererWrapper;
import org.entirej.framework.core.renderers.interfaces.EJLovRenderer;
import org.entirej.framework.core.renderers.interfaces.EJQueryScreenRenderer;

public class EJFXStandardLovRenderer implements EJLovRenderer
{

    final int                       OK_ACTION_COMMAND     = 1;
    final int                       CANCEL_ACTION_COMMAND = 2;
    private EJItemLovController     _itemToValidate;
    private EJLovDisplayReason      _displayReason;

    protected EJLovController       _lovController;

    private TableView<EJDataRecord> _tableViewer;

    private boolean                 _validate             = true;

    private EJFrameworkManager      _frameworkManager;
    private AbstractDialog          _dialog;

    protected EJInternalBlock       _block;

    private FilteredContentProvider   _contentProvider;
    private List<EJDataRecord>        _tableBaseRecords = new ArrayList<EJDataRecord>();

    public Object getGuiComponent()
    {
        return _dialog;
    }

    public EJQueryScreenRenderer getQueryScreenRenderer()
    {
        return null;
    }

    @Override
    public void refreshLovRendererProperty(String propertyName)
    {
    }

    @Override
    public void refreshItemProperty(String itemName, EJManagedScreenProperty managedItemPropertyType, EJDataRecord record)
    {
    }

    @Override
    public void refreshItemRendererProperty(String itemName, String propertyName)
    {
    }

    @Override
    public void synchronize()
    {
    }

    public void initialiseRenderer(EJLovController lovController)
    {
        this._lovController = lovController;
        _frameworkManager = _lovController.getFrameworkManager();
        _block = _lovController.getBlock();

    }

    protected Node createToolbar()
    {
        return null;
    }

    protected void buildGui()
    {
        int width = _lovController.getDefinitionProperties().getWidth();
        int height = _lovController.getDefinitionProperties().getHeight();

        _dialog = new AbstractDialog(getFXManager().getPrimaryStage())
        {

           

            @Override
            public Node createBody()
            {
                _tableViewer = new TableView<>();
                final EJFXAbstractFilteredTable filterTable = new EJFXAbstractFilteredTable(_tableViewer)
                {

                    @Override
                    protected Node doCreateCustomComponents()
                    {
                        return createToolbar();
                    }
                    
                    @Override
                    public void filter(String filter)
                    {
                        if (_contentProvider != null
                                && (filter == null && _contentProvider.getFilter() != null || !filter.equals(_contentProvider.getFilter())))
                        {
                            _contentProvider.setFilter(filter);
                            _contentProvider.refresh(filter);
                            if (_tableViewer.getSelectionModel().getSelectedItem() == null)
                            {
                                selectRow(0);
                            }
                        }

                    }

                };
                EJFrameworkExtensionProperties rendererProp = _lovController.getDefinitionProperties().getLovRendererProperties();

                if (!rendererProp.getBooleanProperty(EJFXMultiRecordBlockDefinitionProperties.HIDE_TABLE_BORDER, false))
                {
                    // TODO
                }

                if (rendererProp.getBooleanProperty(EJFXMultiRecordBlockDefinitionProperties.ROW_SELECTION_PROPERTY, true))
                {
                    // TODO
                }
                else
                {
                    // TODO
                }

                // TODO
                // table.setLinesVisible(rendererProp.getBooleanProperty(EJFXMultiRecordBlockDefinitionProperties.SHOW_VERTICAL_LINES,
                // true));
                // TODO
                // table.setHeaderVisible(rendererProp.getBooleanProperty(EJFXMultiRecordBlockDefinitionProperties.SHOW_HEADING_PROPERTY,
                // true));

                List<EJItemGroupProperties> allItemGroupProperties = new ArrayList<>(_block.getProperties().getScreenItemGroupContainer(EJScreenType.MAIN)
                        .getAllItemGroupProperties());

                if (allItemGroupProperties.size() > 0)
                {
                    EJItemGroupProperties mainItems = allItemGroupProperties.get(0);
                    allItemGroupProperties.remove(0);

                    addColumns(_tableViewer.getColumns(), mainItems);

                    // ass other sub groups
                    for (EJItemGroupProperties itemGroupProperties : allItemGroupProperties)
                    {
                        TableColumn<EJDataRecord, ?> group = new TableColumn<>(itemGroupProperties.getFrameTitle());
                        _tableViewer.getColumns().add(group);
                        addColumns(group.getColumns(), itemGroupProperties);
                    }

                }

                _contentProvider = new FilteredContentProvider()
                {
                    boolean matchItem(EJDataRecord rec)
                    {
                        if (filter != null && filter.trim().length() > 0)
                        {
                            ObservableList<TableColumn<EJDataRecord, ?>> columns = _tableViewer.getColumns();

                            for (TableColumn filterTextProvider : columns)
                            {
                                Callback<TableColumn<EJDataRecord, ?>, TableCell<EJDataRecord, ?>> cellFactory = (Callback<TableColumn<EJDataRecord, ?>, TableCell<EJDataRecord, ?>>) filterTextProvider
                                        .getCellFactory();
                                Object cell = cellFactory.call(filterTextProvider);

                                if (cell instanceof VACell)
                                {
                                    String text = ((VACell)cell).getText(rec);
                                    if (text != null && text.toLowerCase().contains(filter.toLowerCase()))
                                    {
                                        return true;
                                    }
                                }
                            }
                        }
                        return false;
                    }

                    @Override
                    public void refresh(Object object)
                    {

                        _tableViewer.getItems().clear();

                        _tableBaseRecords.clear();

                        if (object != null && object.equals(filter) && filter.trim().length() > 0)
                        {
                            // filter
                            for (EJDataRecord record : _block.getRecords())
                            {
                                if (matchItem(record))
                                {
                                    _tableBaseRecords.add(record);
                                }
                            }
                        }
                        else
                        {
                            filter = null;
                            if (filterTable != null)
                            {
                                filterTable.clearText();
                            }
                            _tableBaseRecords.addAll(_block.getRecords());
                        }

                        _tableViewer.getItems().addAll(_tableBaseRecords);
                    }
                };
                
                _tableViewer.setOnMouseReleased(new EventHandler<MouseEvent>()
                {

                    @Override
                    public void handle(MouseEvent e)
                    {
                        if (e.getClickCount() > 1)
                        {

                            buttonPressed(OK_ACTION_COMMAND);
                        }

                    }
                });
                _tableViewer.setOnKeyPressed(new EventHandler<KeyEvent>()
                {
                    public void handle(KeyEvent ke)
                    {
                        if (ke.getCode() == KeyCode.ESCAPE)
                        {
                            buttonPressed(CANCEL_ACTION_COMMAND);
                        }
                        else if (ke.getCode() == KeyCode.ENTER)
                        {
                            buttonPressed(OK_ACTION_COMMAND);
                        }
                    }
                });
                _tableViewer.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

                _tableViewer.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<EJDataRecord>()
                {

                    @Override
                    public void changed(ObservableValue<? extends EJDataRecord> ov, EJDataRecord old, EJDataRecord newM)
                    {
                        if (!_validate)
                        {
                            return;
                        }

                        _validate = false;

                        try
                        {
                            EJDataRecord record = getFocusedRecord();
                            if (_lovController.getFocusedRecord() == null || _lovController.getFocusedRecord() != record)
                            {
                                _lovController.newRecordInstance(record);
                            }
                        }
                        finally
                        {
                            _validate = true;
                        }
                    }
                });

                return filterTable;

            }

            @Override
            protected void createButtonsForButtonBar()
            {

                createButton(OK_ACTION_COMMAND, "OK").setDefaultButton(true);
                createButton(CANCEL_ACTION_COMMAND, "Cancel").setCancelButton(true);
            }

            @Override
            public void canceled()
            {
                _lovController.lovCompleted(_itemToValidate, null);
            }

            @Override
            protected void buttonPressed(int buttonId)
            {
                switch (buttonId)
                {
                    case OK_ACTION_COMMAND:
                    {
                        _lovController.lovCompleted(_itemToValidate, _lovController.getFocusedRecord());
                        _dialog.close();
                        break;
                    }
                    case CANCEL_ACTION_COMMAND:
                    {
                        _lovController.lovCompleted(_itemToValidate, null);
                        _dialog.close();
                        break;
                    }

                    default:
                        _lovController.lovCompleted(_itemToValidate, null);

                        break;
                }
                super.buttonPressed(buttonId);

            }
        };
        _dialog.setOnCloseRequest(new EventHandler<WindowEvent>()
        {

            @Override
            public void handle(WindowEvent event)
            {
                _tableViewer = null;
                _dialog = null;

            }
        });
        _dialog.create(width + 80, height + 100);// add
        // dialog
        // border
        // offsets
        _contentProvider.refresh(null);
        selectRow(0);
    }

    public EJLovDisplayReason getDisplayReason()
    {
        return _displayReason;
    }

    @Override
    public EJDataRecord getFocusedRecord()
    {
        EJDataRecord _focusedRecord = null;

        if (_tableViewer != null)
        {
            _focusedRecord = _tableViewer.getSelectionModel().getSelectedItem();
        }
        return _focusedRecord;
    }

    public void enterQuery(EJDataRecord record)
    {
        // No user query is permitted on this standard lov
    }

    @Override
    public void blockCleared()
    {
        if (_tableViewer != null)
            _tableViewer.getItems().clear();

    }

    EJFXApplicationManager getFXManager()
    {
        return (EJFXApplicationManager) _frameworkManager.getApplicationManager();
    }

    @Override
    public void displayLov(EJItemLovController itemToValidate, EJLovDisplayReason displayReason)
    {
        _itemToValidate = itemToValidate;
        _displayReason = displayReason;
        buildGui();
        String title = null;
        if (_itemToValidate.getLovMappingProperties().getLovDisplayName() != null)
        {
            title = (_itemToValidate.getLovMappingProperties().getLovDisplayName());
        }
        _dialog.setTitle(title == null ? "" : title);

        _dialog.setButtonEnable(OK_ACTION_COMMAND, _itemToValidate.getManagedLovItemRenderer().isEditAllowed());
        selectRow(0);
        if (_tableViewer != null)
            _tableViewer.requestFocus();

        _dialog.centreLocation();
        _dialog.show();
    }

    @Override
    public void refreshAfterChange(EJDataRecord record)
    {

        refresh();
    }

    protected void refresh()
    {
        EJDataRecord item = _tableViewer.getSelectionModel().getSelectedItem();

        _tableViewer.getItems().clear();
        _tableViewer.layout();
        _contentProvider.refresh(null);
        if (item != null)
        {
            recordSelected(item);
        }
    }

    public void recordSelected(EJDataRecord record)
    {
        if (_tableViewer != null)
            _tableViewer.getSelectionModel().select(record);
    }

    @Override
    public int getDisplayedRecordCount()
    {
        return _tableBaseRecords.size();
    }

    @Override
    public int getDisplayedRecordNumber(EJDataRecord record)
    {
        return _tableBaseRecords.indexOf(record);
    }

    @Override
    public EJDataRecord getFirstRecord()
    {
        return getRecordAt(0);
    }

    @Override
    public EJDataRecord getLastRecord()
    {
        return getRecordAt(getDisplayedRecordCount() - 1);
    }

    @Override
    public EJDataRecord getRecordAt(int displayedRecordNumber)
    {

        if (displayedRecordNumber > -1 && displayedRecordNumber < getDisplayedRecordCount())
        {

            return _tableBaseRecords.get(displayedRecordNumber);
        }

        return null;
    }

    @Override
    public EJDataRecord getRecordAfter(EJDataRecord record)
    {
        int viewIndex = getDisplayedRecordNumber(record);
        if (-1 < viewIndex)
        {
            return getRecordAt(viewIndex + 1);
        }
        return null;
    }

    @Override
    public EJDataRecord getRecordBefore(EJDataRecord record)
    {
        int viewIndex = getDisplayedRecordNumber(record);
        if (-1 < viewIndex)
        {
            return getRecordAt(viewIndex - 1);
        }
        return null;
    }

    public void selectRow(int selectedRow)
    {

        if (_tableViewer != null && _tableBaseRecords.size() > selectedRow)
        {
            _tableViewer.getSelectionModel().select(_tableBaseRecords.get(selectedRow));

        }
    }

    @Override
    public void executingQuery()
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void queryExecuted()
    {
        try
        {
            _validate = false;
            if (_tableViewer != null && _block != null)
            {
                _tableViewer.getItems().clear();
                _contentProvider.refresh(null);
            }
            selectRow(0);

        }
        finally
        {
            _validate = true;
        }
    }

    void addColumns(ObservableList<TableColumn<EJDataRecord, ?>> columns, EJItemGroupProperties groupProperties)
    {
        Collection<EJScreenItemProperties> itemProperties = groupProperties.getAllItemProperties();

        for (EJScreenItemProperties screenItemProperties : itemProperties)
        {
            EJCoreMainScreenItemProperties mainScreenItemProperties = (EJCoreMainScreenItemProperties) screenItemProperties;

            TableColumn<EJDataRecord, ?> column = createScreenItem(mainScreenItemProperties);
            if (column != null)
                columns.add(column);
        }

        for (EJItemGroupProperties itemGroupProperties : groupProperties.getChildItemGroupContainer().getAllItemGroupProperties())
        {
            TableColumn<EJDataRecord, ?> group = new TableColumn<>(itemGroupProperties.getFrameTitle());
            columns.add(group);
            addColumns(group.getColumns(), itemGroupProperties);
        }
    }

    TableColumn<EJDataRecord, ?> createScreenItem(EJCoreMainScreenItemProperties itemProps)
    {
        if (itemProps.isSpacerItem())
        {

            return null;
        }

        EJScreenItemController item = _block.getScreenItem(EJScreenType.MAIN, itemProps.getReferencedItemName());
        EJManagedItemRendererWrapper renderer = item.getManagedItemRenderer();
        if (renderer != null)
        {
            EJFrameworkExtensionProperties blockProperties = itemProps.getLovRendererRequiredProperties();
            EJFXAppItemRenderer itemRenderer = (EJFXAppItemRenderer) renderer.getUnmanagedRenderer();
            TableColumn<EJDataRecord, EJDataRecord> labelProvider = itemRenderer.createColumnProvider(itemProps, item);
            if (labelProvider != null)
            {

                String labelOrientation = blockProperties.getStringProperty(EJFXMultiRecordBlockDefinitionProperties.COLUMN_ALIGNMENT);
               
                if (labelOrientation != null)
                    switch (labelOrientation)
                    {
                        case EJFXMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_LEFT:
                            labelProvider.getStyleClass().add("ej-column-header-left");
                            break;
                        case EJFXMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_RIGHT:
                            labelProvider.getStyleClass().add("ej-column-header-right");
                            break;
                        case EJFXMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_CENTER:
                            labelProvider.getStyleClass().add("ej-column-header-center");
                            break;

                        default:
                            break;
                    }
                int displayedWidth = blockProperties.getIntProperty(EJFXMultiRecordBlockDefinitionProperties.DISPLAY_WIDTH_PROPERTY, 0);

                // if no width define in block properties use item renderer pref
                // width
                if (displayedWidth == 0)
                {
                    if (itemProps.getLabel() != null)
                        displayedWidth = itemProps.getLabel().length() + 2;// add
                                                                           // offset
                    else
                        displayedWidth = 5;
                }

                String visualAttribute = blockProperties.getStringProperty(EJFXMultiRecordBlockDefinitionProperties.VISUAL_ATTRIBUTE_PROPERTY);

                if (visualAttribute != null)
                {
                    EJCoreVisualAttributeProperties va = EJCoreProperties.getInstance().getVisualAttributesContainer()
                            .getVisualAttributeProperties(visualAttribute);
                    if (va != null)
                        itemRenderer.setInitialVisualAttribute(va);
                }

                labelProvider.setCellValueFactory(new Callback<CellDataFeatures<EJDataRecord, EJDataRecord>, ObservableValue<EJDataRecord>>()
                {
                    public ObservableValue<EJDataRecord> call(final CellDataFeatures<EJDataRecord, EJDataRecord> p)
                    {

                        return new ObservableValue<EJDataRecord>()
                        {

                            @Override
                            public void removeListener(InvalidationListener arg0)
                            {
                                // ignore

                            }

                            @Override
                            public void addListener(InvalidationListener arg0)
                            {
                                // ignore
                            }

                            @Override
                            public void removeListener(ChangeListener<? super EJDataRecord> arg0)
                            {
                                // ignore

                            }

                            @Override
                            public EJDataRecord getValue()
                            {
                                return p.getValue();
                            }

                            @Override
                            public void addListener(ChangeListener<? super EJDataRecord> arg0)
                            {
                                // ignore

                            }
                        };
                    }
                });

                labelProvider.setId(itemProps.getReferencedItemName());
                labelProvider.setText(itemProps.getLabel());
                // labelProvider.setToolTipText(itemProps.getHint());

                labelProvider.setResizable(blockProperties.getBooleanProperty(EJFXMultiRecordBlockDefinitionProperties.ALLOW_COLUMN_RESIZE, true));
                labelProvider.setSortable(blockProperties.getBooleanProperty(EJFXMultiRecordBlockDefinitionProperties.ALLOW_ROW_SORTING, true));
                if (blockProperties.getBooleanProperty(EJFXMultiRecordBlockDefinitionProperties.ALLOW_ROW_SORTING, true))
                {

                    Comparator<EJDataRecord> columnSorter = itemRenderer.getColumnSorter(itemProps, item);
                    if (columnSorter != null)
                        labelProvider.setComparator(columnSorter);
                }

                // ensure that the width property of the table column is in
                // Characters

                final Text text = new Text("A");
                // FIXME FONT of labelProvider
                text.snapshot(null, null);
                double avgCharWidth = text.getLayoutBounds().getWidth()+5;//offset;
                if (avgCharWidth > 0)
                {
                    labelProvider.setPrefWidth((int) (((displayedWidth + 1) * avgCharWidth)));// add
                    // +1
                    // padding
                }
                return labelProvider;
            }

        }
        return null;
    }
    
    
}

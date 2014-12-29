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
package org.entirej.applicationframework.fx.renderers.blocks;

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
import javafx.scene.control.Control;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.util.Callback;

import org.entirej.applicationframework.fx.renderers.blocks.def.EJFXMultiRecordBlockDefinitionProperties;
import org.entirej.applicationframework.fx.renderers.blocks.def.EJFXSingleRecordBlockDefinitionProperties;
import org.entirej.applicationframework.fx.renderers.interfaces.EJFXAppBlockRenderer;
import org.entirej.applicationframework.fx.renderers.interfaces.EJFXAppItemRenderer;
import org.entirej.applicationframework.fx.renderers.items.EJFXTextItemRenderer.VACell;
import org.entirej.applicationframework.fx.renderers.screen.EJFXInsertScreenRenderer;
import org.entirej.applicationframework.fx.renderers.screen.EJFXQueryScreenRenderer;
import org.entirej.applicationframework.fx.renderers.screen.EJFXUpdateScreenRenderer;
import org.entirej.applicationframework.fx.utils.EJFXAbstractFilteredTable;
import org.entirej.applicationframework.fx.utils.EJFXAbstractFilteredTable.FilteredContentProvider;
import org.entirej.framework.core.EJForm;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.data.controllers.EJEditableBlockController;
import org.entirej.framework.core.data.controllers.EJQuestion;
import org.entirej.framework.core.enumerations.EJManagedBlockProperty;
import org.entirej.framework.core.enumerations.EJManagedScreenProperty;
import org.entirej.framework.core.enumerations.EJQuestionButton;
import org.entirej.framework.core.enumerations.EJScreenType;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.internal.EJInternalEditableBlock;
import org.entirej.framework.core.properties.EJCoreMainScreenItemProperties;
import org.entirej.framework.core.properties.EJCoreProperties;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJBlockProperties;
import org.entirej.framework.core.properties.interfaces.EJItemGroupProperties;
import org.entirej.framework.core.properties.interfaces.EJMainScreenProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;
import org.entirej.framework.core.renderers.EJManagedItemRendererWrapper;
import org.entirej.framework.core.renderers.interfaces.EJInsertScreenRenderer;
import org.entirej.framework.core.renderers.interfaces.EJQueryScreenRenderer;
import org.entirej.framework.core.renderers.interfaces.EJUpdateScreenRenderer;

public class EJFXMultiRecordBlockRenderer implements EJFXAppBlockRenderer
{

    private boolean                   _isFocused        = false;
    private EJEditableBlockController _block;
    private TableView<EJDataRecord>   _tableViewer;
    private FilteredContentProvider   _contentProvider;
    private EJFXQueryScreenRenderer   _queryScreenRenderer;
    private EJFXInsertScreenRenderer  _insertScreenRenderer;
    private EJFXUpdateScreenRenderer  _updateScreenRenderer;
    private List<EJDataRecord>        _tableBaseRecords = new ArrayList<EJDataRecord>();

    
    protected void clearFilter()
    {
        if (_contentProvider != null)
        {
            _contentProvider.setFilter(null);
        }
    }
    public final EJInternalEditableBlock getBlock()
    {
        return _block.getBlock();
    }

    public void askToDeleteRecord(EJDataRecord recordToDelete, String msg)
    {
        if (msg == null)
        {
            msg = "Are you sure you want to delete the current record?";
        }
        EJMessage message = new EJMessage(msg);
        EJQuestion question = new EJQuestion(new EJForm(_block.getForm()), "DELETE_RECORD", "Delete", message, "Yes", "No");
        _block.getForm().getMessenger().askQuestion(question);
        if (EJQuestionButton.ONE == (question.getAnswer()))
        {
            _block.getBlock().deleteRecord(recordToDelete);
        }
        _block.setRendererFocus(true);

    }

    @Override
    public void blockCleared()
    {
        if (_tableViewer != null)
        {
            clearFilter();
            _contentProvider.refresh(null);
        }

    }

    @Override
    public void detailBlocksCleared()
    {
        // ignore

    }

    public void enterInsert(EJDataRecord record)
    {
        if (_block.getInsertScreenRenderer() == null)
        {
            EJMessage message = new EJMessage("Please define an Insert Screen Renderer for this form before an insert operation can be performed.");
            _block.getForm().getMessenger().handleMessage(message);
        }
        else
        {
            _block.getInsertScreenRenderer().open(record);
        }
    }

    public void enterQuery(EJDataRecord queryRecord)
    {
        if (_block.getQueryScreenRenderer() == null)
        {
            EJMessage message = new EJMessage("Please define a Query Screen Renderer for this form before a query operation can be performed.");
            _block.getForm().getMessenger().handleMessage(message);
        }
        else
        {
            _block.getQueryScreenRenderer().open(queryRecord);
        }
    }

    public void enterUpdate(EJDataRecord recordToUpdate)
    {
        if (_block.getUpdateScreenRenderer() == null)
        {
            EJMessage message = new EJMessage("Please define an Update Screen Renderer for this form before an update operation can be performed.");
            _block.getForm().getMessenger().handleMessage(message);
        }
        else
        {
            _block.getUpdateScreenRenderer().open(recordToUpdate);
        }
    }

    @Override
    public void gainFocus()
    {
        if (_tableViewer != null)
        {
            _tableViewer.requestFocus();
        }
        setHasFocus(true);

    }

    @Override
    public EJInsertScreenRenderer getInsertScreenRenderer()
    {
        return _insertScreenRenderer;
    }

    @Override
    public EJQueryScreenRenderer getQueryScreenRenderer()
    {
        return _queryScreenRenderer;
    }

    @Override
    public EJUpdateScreenRenderer getUpdateScreenRenderer()
    {
        return _updateScreenRenderer;
    }

    @Override
    public boolean hasFocus()
    {
        return _isFocused;
    }

    @Override
    public void initialiseRenderer(EJEditableBlockController block)
    {
        _block = block;
        _queryScreenRenderer = new EJFXQueryScreenRenderer();
        _insertScreenRenderer = new EJFXInsertScreenRenderer();
        _updateScreenRenderer = new EJFXUpdateScreenRenderer();

    }

    @Override
    public boolean isCurrentRecordDirty()
    {
        return false;
    }

    @Override
    public void queryExecuted()
    {
        
        setInputs();
        selectRow(0);
    }

    public void selectRow(int selectedRow)
    {

        if (_tableViewer != null && _tableBaseRecords.size() > selectedRow)
        {
            _tableViewer.getSelectionModel().select(_tableBaseRecords.get(selectedRow));
        }
    }

    @Override
    public void recordDeleted(int dataBlockRecordNumber)
    {
        EJDataRecord recordAt = getRecordAt(dataBlockRecordNumber > 1 ? dataBlockRecordNumber - 2 : 0);

        if (recordAt == null)

        {
            recordAt = getLastRecord();
        }
        setInputs();

        if (recordAt != null)
            recordSelected(recordAt);

    }

    @Override
    public void recordInserted(EJDataRecord record)
    {
        setInputs();
        recordSelected(record);

    }

    @Override
    public void refreshBlockProperty(EJManagedBlockProperty arg0)
    {
        // ignore

    }

    @Override
    public void refreshBlockRendererProperty(String arg0)
    {
        // ignore

    }

    @Override
    public void setFocusToItem(EJScreenItemController arg0)
    {
        if (_tableViewer == null)
            return;
        _tableViewer.requestFocus();

    }

    @Override
    public void setHasFocus(boolean focus)
    {
        _isFocused = focus;
        if (_isFocused)
        {
            showFocusedBorder(true);
            _block.focusGained();
        }
        else
        {
            showFocusedBorder(false);
            _block.focusLost();
        }

    }

    protected void showFocusedBorder(boolean focused)
    {
        /**
         * FIXME: This needs to be implemented
         */

    }

    @Override
    public void executingQuery()
    {
        // ignore

    }

    @Override
    public int getDisplayedRecordNumber(EJDataRecord record)
    {
        if (record == null)
        {
            return -1;
        }

        return _tableBaseRecords.indexOf(record);
    }

    @Override
    public int getDisplayedRecordCount()
    {
        // Indicates the number of records that are available within the View.
        // the number depends on the filters set on the table!
        return _tableBaseRecords.size();
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

    @Override
    public EJDataRecord getFocusedRecord()
    {
        return (_tableViewer != null) ? _tableViewer.getSelectionModel().getSelectedItem() : null;
    }

    @Override
    public void recordSelected(EJDataRecord record)
    {
        if (_tableViewer != null)
            _tableViewer.getSelectionModel().select(record);

    }

    @Override
    public void refreshAfterChange(EJDataRecord record)
    {

        refresh();
    }

    protected void refresh()
    {
        if (_tableViewer == null)
            return;
        EJDataRecord item = _tableViewer.getSelectionModel().getSelectedItem();

        _tableViewer.getItems().clear();
        _tableViewer.layout();
        clearFilter();
        _contentProvider.refresh(null);
        if (item != null)
        {
            recordSelected(item);
        }
    }

    @Override
    public void refreshItemProperty(String itemName, EJManagedScreenProperty managedItemPropertyType, EJDataRecord record)
    {
        if (EJManagedScreenProperty.ITEM_INSTANCE_VISUAL_ATTRIBUTE.equals(managedItemPropertyType))
        {
            EJScreenItemController item = _block.getScreenItem(EJScreenType.MAIN, itemName);
            if (item != null)
            {
                if (record == null || _tableViewer == null)
                    return;

                EJDataRecord selected = _tableViewer.getSelectionModel().getSelectedItem();

                _tableViewer.getItems().clear();
                _tableViewer.layout();

                _contentProvider.refresh(_contentProvider.getFilter());
                if (selected != null)
                {
                    recordSelected(selected);
                }
            }
        }
        else if (EJManagedScreenProperty.ITEM_INSTANCE_HINT_TEXT.equals(managedItemPropertyType))
        {
            EJScreenItemController item = _block.getScreenItem(EJScreenType.MAIN, itemName);
            if (item != null)
            {
                if (record == null)
                {
                    return;
                }

            }
        }
        else if (EJManagedScreenProperty.SCREEN_ITEM_VISUAL_ATTRIBUTE.equals(managedItemPropertyType))
        {
            EJScreenItemController item = _block.getScreenItem(EJScreenType.MAIN, itemName);
            if (item != null)
            {
                item.getManagedItemRenderer().getUnmanagedRenderer().setVisualAttribute(item.getProperties().getVisualAttributeProperties());

                EJDataRecord selected = _tableViewer.getSelectionModel().getSelectedItem();

                _tableViewer.getItems().clear();
                _tableViewer.layout();

                _contentProvider.refresh(_contentProvider.getFilter());
                if (selected != null)
                {
                    recordSelected(selected);
                }
            }
        }

    }

    @Override
    public void refreshItemRendererProperty(String arg0, String arg1)
    {
        // ignore

    }

    @Override
    public void synchronize()
    {
        // ignore
    }

    @Override
    public Object getGuiComponent()
    {
        return _tableViewer;
    }

    @Override
    public Node createComponent()
    {
        EJBlockProperties blockProperties = _block.getProperties();
        EJMainScreenProperties mainScreenProperties = blockProperties.getMainScreenProperties();
        Node componet = null;
        EJFrameworkExtensionProperties rendererProp = blockProperties.getBlockRendererProperties();

        boolean useFilter = rendererProp.getBooleanProperty(EJFXMultiRecordBlockDefinitionProperties.FILTER, false);

        _tableViewer = new TableView<>();
        Node tableNode = _tableViewer;
        final EJFXAbstractFilteredTable filterTable;
        if (useFilter)
        {
            filterTable = new EJFXAbstractFilteredTable(_tableViewer)
            {

                @Override
                public void filter(String filter)
                {
                    if (_contentProvider != null && (filter == null && _contentProvider.getFilter() != null || !filter.equals(_contentProvider.getFilter())))
                    {
                        _contentProvider.setFilter(filter);
                        _contentProvider.refresh(filter);
                        if(_tableViewer.getSelectionModel().getSelectedItem()==null)
                        {
                            selectRow(0);
                        }
                    }

                }

            };
            tableNode = filterTable;
        }
        else
        {
            filterTable = null;
        }

        EJFrameworkExtensionProperties sectionProperties = null;
        if (rendererProp != null)
        {
            sectionProperties = rendererProp.getPropertyGroup(EJFXSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR);
        }

        if (sectionProperties != null && sectionProperties.getBooleanProperty(EJFXSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_VISIBLE, false))
        {
            String title = sectionProperties.getStringProperty(EJFXSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_TITLE);
            TitledPane t1;
            if (mainScreenProperties.getDisplayFrame())
            {

                String frameTitle = mainScreenProperties.getFrameTitle();

                TitledPane t2 = new TitledPane(frameTitle != null ? frameTitle : "", tableNode);
                t1 = new TitledPane(title != null ? title : "", t2);

            }
            else
            {
                t1 = new TitledPane(title != null ? title : "", tableNode);

            }
            createGridData(mainScreenProperties, t1);
            t1.setCollapsible(sectionProperties.getBooleanProperty(EJFXSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_EXPANDABLE, false));
            if (t1.isCollapsible())
                t1.setExpanded(sectionProperties.getBooleanProperty(EJFXSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_EXPANDED, true));

            componet = (t1);
        }
        else
        {
            if (mainScreenProperties.getDisplayFrame())
            {

                String frameTitle = mainScreenProperties.getFrameTitle();

                TitledPane t2 = new TitledPane(frameTitle != null ? frameTitle : "", tableNode);
                createGridData(mainScreenProperties, t2);
                componet = (t2);

            }
            else
            {
                createGridData(mainScreenProperties, tableNode);
                componet = (tableNode);
            }
        }

        _tableViewer.focusedProperty().addListener(new ChangeListener<Boolean>()
        {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {

                setHasFocus(newValue);
            }

        });

        List<EJItemGroupProperties> allItemGroupProperties = new ArrayList<>(_block.getProperties().getScreenItemGroupContainer(EJScreenType.MAIN)
                .getAllItemGroupProperties());

        if (allItemGroupProperties.size() > 0)
        {
            EJItemGroupProperties mainItems = allItemGroupProperties.get(0);
            allItemGroupProperties.remove(0);

            addColumns(_tableViewer.getColumns(), mainItems);

            // add other sub groups
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
                    for (EJDataRecord record : _block.getBlock().getRecords())
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
                    _tableBaseRecords.addAll(_block.getBlock().getRecords());
                }

                _tableViewer.getItems().addAll(_tableBaseRecords);
            }
        };

        // add double click action
        final String doubleClickActionCommand = rendererProp.getStringProperty(EJFXMultiRecordBlockDefinitionProperties.DOUBLE_CLICK_ACTION_COMMAND);
        if (doubleClickActionCommand != null)
        {
            _tableViewer.setOnMouseReleased(new EventHandler<MouseEvent>()
            {

                @Override
                public void handle(MouseEvent e)
                {
                    if (e.getClickCount() > 1)
                    {
                        _block.executeActionCommand(doubleClickActionCommand, EJScreenType.MAIN);
                    }

                }
            });
        }
        _tableViewer.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        _tableViewer.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<EJDataRecord>()
        {

            @Override
            public void changed(ObservableValue<? extends EJDataRecord> ov, EJDataRecord old, EJDataRecord newM)
            {
                if (newM != null)
                    _block.newRecordInstance(newM);
            }
        });

        return componet;

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
            EJFrameworkExtensionProperties blockProperties = itemProps.getBlockRendererRequiredProperties();
            EJFXAppItemRenderer itemRenderer = (EJFXAppItemRenderer) renderer.getUnmanagedRenderer();
            TableColumn<EJDataRecord, EJDataRecord> labelProvider = itemRenderer.createColumnProvider(itemProps, item);
            if (labelProvider != null)
            {

                String labelOrientation = blockProperties.getStringProperty(EJFXMultiRecordBlockDefinitionProperties.COLUMN_ALIGNMENT);

                if (labelOrientation != null)
                    switch (labelOrientation)
                    {
                        case EJFXMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_LEFT:
                            labelProvider.setStyle("-fx-ej-alignment:CENTER_LEFT");
                            break;
                        case EJFXMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_RIGHT:
                            labelProvider.setStyle("-fx-ej-alignment:CENTER_RIGHT");
                            break;
                        case EJFXMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_CENTER:
                            labelProvider.setStyle("-fx-ej-alignmentt:CENTER");
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
                double avgCharWidth = text.getLayoutBounds().getWidth();
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

    private Node createGridData(EJMainScreenProperties layoutItem, Node node)
    {

        int height = layoutItem.getHeight();
        if (height == 0)
        {
            height = 1;
        }
        int width = layoutItem.getWidth();
        if (width == 0)
        {
            width = 1;
        }
        if (node instanceof Control)
        {
            if (layoutItem.canExpandVertically())
                ((Control) node).setMinHeight(height);
            if (layoutItem.canExpandHorizontally())
                ((Control) node).setMinWidth(width);

            if (height > 0)
                ((Control) node).setPrefHeight(height);

            if (width > 0)
                ((Control) node).setPrefWidth(width);
        }
        else if (node instanceof Region)
        {
            if (layoutItem.canExpandVertically())
                ((Region) node).setMinHeight(height);
            if (layoutItem.canExpandHorizontally())
                ((Region) node).setMinWidth(width);

            if (height > 0)
                ((Region) node).setPrefHeight(height);

            if (width > 0)
                ((Region) node).setPrefWidth(width);
        }

        GridPane.setColumnSpan(node, layoutItem.getHorizontalSpan());
        GridPane.setRowSpan(node, layoutItem.getVerticalSpan());
        if (layoutItem.canExpandVertically())
            GridPane.setVgrow(node, Priority.ALWAYS);
        else
            GridPane.setVgrow(node, Priority.NEVER);
        if (layoutItem.canExpandHorizontally())
            GridPane.setHgrow(node, Priority.ALWAYS);
        else
            GridPane.setHgrow(node, Priority.NEVER);

        if (node instanceof Control)
        {

            ((Control) node).setMaxWidth(layoutItem.canExpandHorizontally()?Double.MAX_VALUE:layoutItem.getWidth());
            ((Control) node).setMaxHeight(layoutItem.canExpandVertically()?Double.MAX_VALUE:layoutItem.getHeight());

        }
        else if (node instanceof Region)
        {

            ((Region) node).setMaxWidth(layoutItem.canExpandHorizontally()?Double.MAX_VALUE:layoutItem.getWidth());
            ((Region) node).setMaxHeight(layoutItem.canExpandVertically()?Double.MAX_VALUE:layoutItem.getHeight());

        }

        return node;

    }

    void setInputs()
    {
        if (_tableViewer != null && _block != null)
        {
            clearFilter();
            _contentProvider.refresh(null);
        }

    }

}

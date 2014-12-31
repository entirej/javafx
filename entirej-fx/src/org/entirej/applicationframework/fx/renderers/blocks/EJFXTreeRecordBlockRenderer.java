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

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Callback;

import org.entirej.applicationframework.fx.renderers.blocks.def.EJFXMultiRecordBlockDefinitionProperties;
import org.entirej.applicationframework.fx.renderers.blocks.def.EJFXSingleRecordBlockDefinitionProperties;
import org.entirej.applicationframework.fx.renderers.blocks.def.EJFXTreeBlockDefinitionProperties;
import org.entirej.applicationframework.fx.renderers.interfaces.EJFXAppBlockRenderer;
import org.entirej.applicationframework.fx.renderers.interfaces.EJFXAppItemRenderer;
import org.entirej.applicationframework.fx.renderers.items.EJFXTextItemRenderer.VACell;
import org.entirej.applicationframework.fx.renderers.screen.EJFXInsertScreenRenderer;
import org.entirej.applicationframework.fx.renderers.screen.EJFXQueryScreenRenderer;
import org.entirej.applicationframework.fx.renderers.screen.EJFXUpdateScreenRenderer;
import org.entirej.applicationframework.fx.utils.EJFXAbstractFilteredTable.FilteredContentProvider;
import org.entirej.applicationframework.fx.utils.EJFXAbstractFilteredTree;
import org.entirej.applicationframework.fx.utils.EJFXVisualAttributeUtils;
import org.entirej.framework.core.EJForm;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.EJRecord;
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

public class EJFXTreeRecordBlockRenderer implements EJFXAppBlockRenderer
{

    private boolean                   _isFocused        = false;
    private EJEditableBlockController _block;
    private TreeView<EJDataRecord>    _tableViewer;
    private FilteredContentProvider   _contentProvider;
    private EJFXQueryScreenRenderer   _queryScreenRenderer;
    private EJFXInsertScreenRenderer  _insertScreenRenderer;
    private EJFXUpdateScreenRenderer  _updateScreenRenderer;
    private List<EJDataRecord>        _tableBaseRecords = new ArrayList<EJDataRecord>();
    private int                       expandLevel       = -1;

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
        expandLevel(expandLevel, _tableViewer.getRoot());
        selectRow(0);
    }

    public void selectRow(int selectedRow)
    {

        if (_tableViewer != null && _tableBaseRecords.size() > selectedRow)
        {
            _tableViewer.getSelectionModel().select(findTreeItem(_tableBaseRecords.get(selectedRow), _tableViewer.getRoot()));
        }
    }

    TreeItem<EJDataRecord> findTreeItem(EJDataRecord rec, TreeItem<EJDataRecord> parent)
    {
        ObservableList<TreeItem<EJDataRecord>> children = parent.getChildren();
        for (TreeItem<EJDataRecord> treeItem : children)
        {
            if (rec.equals(treeItem.getValue()))
            {
                return treeItem;
            }
            TreeItem<EJDataRecord> match = findTreeItem(rec, treeItem);
            if (match != null)
            {
                return match;
            }
        }

        return null;
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
        return (_tableViewer != null && _tableViewer.getSelectionModel().getSelectedItem() != null) ? _tableViewer.getSelectionModel().getSelectedItem()
                .getValue() : null;
    }

    @Override
    public void recordSelected(EJDataRecord record)
    {
        if (_tableViewer != null && record != null)
        {
            _tableViewer.getSelectionModel().select(findTreeItem(record, _tableViewer.getRoot()));
        }
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
        EJDataRecord item = getFocusedRecord();

        _tableViewer.getRoot().getChildren().clear();
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

                EJDataRecord selected = getFocusedRecord();

                _tableViewer.getRoot().getChildren().clear();
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

                EJDataRecord selected = getFocusedRecord();

                _tableViewer.getRoot().getChildren().clear();
                _tableViewer.layout();

                _contentProvider.refresh(_contentProvider.getFilter());
                if (selected != null)
                {
                    recordSelected(selected);
                }
            }
        }

    }

    protected void expandLevel(int level, TreeItem<EJDataRecord> parent)
    {
        if (level > 0 && _tableViewer != null)
        {
            parent.expandedProperty().set(true);
            ObservableList<TreeItem<EJDataRecord>> children = parent.getChildren();
            if ((level - 1) > 0)
            {
                for (TreeItem<EJDataRecord> treeItem : children)
                {
                    expandLevel(level - 1, treeItem);
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
        TreeItem<EJDataRecord> rootItem = new TreeItem<>();
        _tableViewer = new TreeView<>(rootItem);
        Node tableNode = _tableViewer;
        final EJFXAbstractFilteredTree filterTable;
        if (useFilter)
        {
            filterTable = new EJFXAbstractFilteredTree(_tableViewer)
            {

                @Override
                public void filter(String filter)
                {
                    if (_contentProvider != null && (filter == null && _contentProvider.getFilter() != null || !filter.equals(_contentProvider.getFilter())))
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

        final List<TreeNodeTextProvider> columns = new ArrayList<>();
        if (allItemGroupProperties.size() > 0)
        {
            EJItemGroupProperties mainItems = allItemGroupProperties.get(0);
            allItemGroupProperties.remove(0);

            addColumns(columns, mainItems);

        }

        final String pid = rendererProp.getStringProperty(EJFXTreeBlockDefinitionProperties.PARENT_ITEM);
        final String rid = rendererProp.getStringProperty(EJFXTreeBlockDefinitionProperties.RELATION_ITEM);
        final String imageid = rendererProp.getStringProperty(EJFXTreeBlockDefinitionProperties.NODE_IMAGE_ITEM);
        expandLevel = rendererProp.getIntProperty(EJFXTreeBlockDefinitionProperties.NODE_EXPAND_LEVEL, -1);

        final EJCoreVisualAttributeProperties baseVA;
        String visualAttribute = rendererProp.getStringProperty(EJFXTreeBlockDefinitionProperties.VISUAL_ATTRIBUTE_PROPERTY);

        if (visualAttribute != null)
        {
            baseVA = EJCoreProperties.getInstance().getVisualAttributesContainer().getVisualAttributeProperties(visualAttribute);
        }
        else
        {
            baseVA = null;
        }
        final Map<Object, Image> imageMap = new HashMap<>();

        _tableViewer.setShowRoot(false);
        _tableViewer.setCellFactory(new Callback<TreeView<EJDataRecord>, TreeCell<EJDataRecord>>()
        {

            @Override
            public TreeCell<EJDataRecord> call(TreeView<EJDataRecord> arg0)
            {
                return new TreeCell<EJDataRecord>()
                {

                    protected static final String CSS_VA_CELL_BG = "va-cell-bg";

                    @Override
                    public void updateItem(EJDataRecord item, boolean empty)
                    {
                        super.updateItem(item, empty);
                        if (empty)
                        {
                            setText(null);
                            setGraphic(null);
                            paintCellCSS(null);
                        }
                        else
                        {

                            setText(getString());
                            Node imageNode = getImage();
                            if (imageNode != null)
                            {
                                setGraphic(imageNode);
                            }
                            paintCellCSS(getItem());
                        }

                    }

                    void paintCellCSS(EJDataRecord value)
                    {
                        getStyleClass().remove(CSS_VA_CELL_BG);
                        if (value != null)
                        {
                            if (baseVA != null)
                            {

                                if (!EJCoreVisualAttributeProperties.UNSPECIFIED.equals(baseVA.getBackgroundRGB()))
                                    getStyleClass().add(CSS_VA_CELL_BG);

                                getStyleClass().add(EJFXVisualAttributeUtils.INSTANCE.toCSS(baseVA));
                            }
                            EJCoreVisualAttributeProperties attributes = getAttributes(value);
                            if (attributes != null)
                            {

                                if (baseVA == null || EJCoreVisualAttributeProperties.UNSPECIFIED.equals(baseVA.getBackgroundRGB()))
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

                    private EJCoreVisualAttributeProperties getAttributes(Object element)
                    {
                        EJCoreVisualAttributeProperties properties = null;
                        if (pid != null && element instanceof EJDataRecord)
                        {
                            EJDataRecord record = (EJDataRecord) element;
                            properties = record.getItem(pid).getVisualAttribute();
                        }
                        if (properties == null)
                        {
                            properties = baseVA;
                        }
                        return properties;
                    }

                    Node getImage()
                    {
                        if (imageid == null)
                        {
                            return null;
                        }
                        
                        EJDataRecord record = getItem();
                        if (record == null)
                        {
                            return null;
                        }
                        
                        
                        Object iV = record.getValue(imageid);
                        if (iV == null)
                        {
                            return null;
                        }
                        Image image = imageMap.get(iV);
                        if (image != null)
                        {
                            return new ImageView(image);
                        }

                        if (iV instanceof URL)
                        {
                            image = new Image(((URL) iV).toExternalForm());
                        }
                        else if (iV instanceof byte[])
                        {
                            image = new Image(new ByteArrayInputStream((byte[]) iV));
                        }
                        if (image != null)
                        {
                            imageMap.put(iV, image);
                        }
                        return new ImageView(image);

                    }

                    private String getString()
                    {

                        EJDataRecord item = getItem();

                        if (item != null)
                        {
                            StringBuilder builder = new StringBuilder();

                            for (TreeNodeTextProvider filterTextProvider : columns)
                            {

                                String text = filterTextProvider.getText(item);
                                if (text != null)
                                {
                                    builder.append(text);
                                }

                            }
                            return builder.toString();
                        }
                        return "";
                    }
                };
            }
        });

        _contentProvider = new FilteredContentProvider()
        {
            private List<EJDataRecord>              root     = new ArrayList<>();
            private Map<Object, Object>             indexMap = new HashMap<>();
            private Map<Object, List<EJDataRecord>> cmap     = new HashMap<>();

            private List<EJDataRecord>              froot    = new ArrayList<>();
            private Map<Object, List<EJDataRecord>> fcmap    = new HashMap<>();

            boolean matchItem(EJDataRecord rec)
            {
                if (filter != null && filter.trim().length() > 0)
                {

                    for (TreeNodeTextProvider filterTextProvider : columns)
                    {

                        String text = filterTextProvider.getText(rec);
                        if (text != null && text.toLowerCase().contains(filter.toLowerCase()))
                        {
                            return true;
                        }

                    }
                }
                return false;
            }

            @Override
            public void refresh(Object object)
            {

                _tableViewer.getRoot().getChildren().clear();

                _tableBaseRecords.clear();

                if (object != null && object.equals(filter) && filter.trim().length() > 0)
                {
                    // filter
                    froot.clear();
                    fcmap.clear();
                    // filter
                    for (Entry<Object, List<EJDataRecord>> entry : cmap.entrySet())
                    {

                        List<EJDataRecord> values = entry.getValue();
                        List<EJDataRecord> fvalues = new ArrayList<EJDataRecord>(values.size());
                        fcmap.put(entry.getKey(), fvalues);
                        for (EJDataRecord record : values)
                        {
                            if (matchItem(record))
                            {
                                fvalues.add(record);
                            }
                        }
                    }
                    // filter root
                    for (EJDataRecord record : root)
                    {
                        if (matchItem(record))
                        {
                            froot.add(record);
                        }
                        else if (hasChildren(record))
                        {
                            froot.add(record);
                        }
                    }
                    for (EJDataRecord record : froot)
                    {
                        _tableBaseRecords.add(record);
                        addSubRecords(record.getValue(pid), fcmap);
                    }
                }
                else
                {
                    filter = null;
                    if (filterTable != null)
                    {
                        filterTable.clearText();
                    }
                    root.clear();
                    indexMap.clear();
                    froot.clear();
                    cmap.clear();
                    fcmap.clear();

                    imageMap.clear();
                    Collection<EJDataRecord> records = _block.getRecords();
                    for (EJDataRecord record : records)
                    {
                        Object rV = record.getValue(rid);
                        Object pV = record.getValue(pid);
                        
                        if (pid != null)
                        {
                            indexMap.put(pV, record);
                        }
                        if (rV == null)
                        {
                            root.add(record);
                            
                            continue;
                        }
                        List<EJDataRecord> list = cmap.get(rV);
                        if (list == null)
                        {
                            list = new ArrayList<EJDataRecord>();
                            cmap.put(rV, list);
                        }
                        list.add(record);
                    }
                    
                  //child node with no parent need to consider as roots
                    for (Object key : new HashSet<Object>(cmap.keySet()))
                    {
                        if(indexMap.containsKey(key))
                        {
                            continue;
                        }
                        List<EJDataRecord> list = cmap.get(key);
                        cmap.remove(key);
                        for (EJDataRecord record : list)
                        {
                            Object pV = record.getValue(pid);
                            root.add(record);
                            if (pid != null)
                            {
                                indexMap.put(pV, record);
                            }
                        }
                    }
                    for (EJDataRecord record : root)
                    {
                        _tableBaseRecords.add(record);
                        addSubRecords(record.getValue(pid), cmap);
                    }
                }
                _tableViewer.getRoot().getChildren().addAll(createItems(getElements()));
            }

            List<TreeItem<EJDataRecord>> createItems(List<EJDataRecord> records)
            {
                List<TreeItem<EJDataRecord>> items = new ArrayList<>();

                for (EJDataRecord rec : records)
                {
                    items.add(createItem(rec));
                }

                return items;
            }

            TreeItem<EJDataRecord> createItem(EJDataRecord rec)
            {
                TreeItem<EJDataRecord> item = new TreeItem<EJDataRecord>(rec);

                item.getChildren().addAll(createItems(getChildren(rec)));

                return item;
            }

            public List<EJDataRecord> getElements()
            {
                if (filter != null && filter.trim().length() > 0)
                {
                    return froot;
                }
                return root;
            }

            public List<EJDataRecord> getChildren(EJDataRecord arg0)
            {
                Map<Object, List<EJDataRecord>> map = filter != null && filter.trim().length() > 0 ? fcmap : cmap;
                if (arg0 instanceof EJDataRecord)
                {
                    EJDataRecord record = (EJDataRecord) arg0;
                    Object pV = record.getValue(pid);
                    if (pV != null)
                    {
                        List<EJDataRecord> list = map.get(pV);
                        if (list != null)
                        {
                            return list;
                        }
                    }
                }
                return Collections.emptyList();
            }

            public boolean hasChildren(Object arg0)
            {
                if (arg0 instanceof EJDataRecord)
                {
                    Map<Object, List<EJDataRecord>> map = filter != null && filter.trim().length() > 0 ? fcmap : cmap;
                    EJDataRecord record = (EJDataRecord) arg0;
                    Object pV = record.getValue(pid);
                    if (pV != null)
                    {
                        List<EJDataRecord> list = map.get(pV);
                        return list != null && list.size() > 0;

                    }
                }
                return false;
            }

            private void addSubRecords(Object key, Map<Object, List<EJDataRecord>> cmap)
            {
                if (key != null)
                {
                    List<EJDataRecord> list = cmap.get(key);
                    if (list != null)
                    {
                        for (EJDataRecord record : list)
                        {
                            _tableBaseRecords.add(record);
                            addSubRecords(record.getValue(pid), cmap);
                        }
                    }
                }
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

        _tableViewer.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<EJDataRecord>>()
        {

            @Override
            public void changed(ObservableValue<? extends TreeItem<EJDataRecord>> ov, TreeItem<EJDataRecord> old, TreeItem<EJDataRecord> newM)
            {
                if (newM != null)
                    _block.newRecordInstance(newM.getValue());
            }
        });

        return componet;

    }

    void addColumns(List<TreeNodeTextProvider> columns, EJItemGroupProperties groupProperties)
    {
        Collection<EJScreenItemProperties> itemProperties = groupProperties.getAllItemProperties();

        for (EJScreenItemProperties screenItemProperties : itemProperties)
        {
            EJCoreMainScreenItemProperties mainScreenItemProperties = (EJCoreMainScreenItemProperties) screenItemProperties;

            TreeNodeTextProvider column = createScreenItem(mainScreenItemProperties);
            if (column != null)
                columns.add(column);
        }

    }

    static class TreeNodeTextProvider
    {
        private final String      prefix;
        private final String      suffix;
        @SuppressWarnings("rawtypes")
        private final TableColumn provider;

        public TreeNodeTextProvider(String prefix, String suffix, TableColumn<EJDataRecord, ?> provider)
        {
            this.prefix = prefix;
            this.suffix = suffix;
            this.provider = provider;
        }

        public String getText(EJDataRecord object)
        {
            @SuppressWarnings("unchecked")
            Callback<TableColumn<EJDataRecord, ?>, TableCell<EJDataRecord, ?>> cellFactory = (Callback<TableColumn<EJDataRecord, ?>, TableCell<EJDataRecord, ?>>) provider
                    .getCellFactory();
            @SuppressWarnings("unchecked")
            Object cell = cellFactory.call(provider);
            String text = null;
            if (cell instanceof VACell)
            {

                text = ((VACell) cell).getText(object);

            }
            if (text != null && text.length() > 0 && (prefix != null || suffix != null))
            {
                StringBuilder builder = new StringBuilder();
                if (prefix != null)
                {
                    builder.append(prefix);
                }
                builder.append(text);
                if (suffix != null)
                {
                    builder.append(suffix);
                }
                return builder.toString();
            }

            return text;
        }
    }

    TreeNodeTextProvider createScreenItem(EJCoreMainScreenItemProperties itemProps)
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

            return new TreeNodeTextProvider(blockProperties.getStringProperty(EJFXTreeBlockDefinitionProperties.ITEM_PREFIX),
                    blockProperties.getStringProperty(EJFXTreeBlockDefinitionProperties.ITEM_SUFFIX), labelProvider);

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

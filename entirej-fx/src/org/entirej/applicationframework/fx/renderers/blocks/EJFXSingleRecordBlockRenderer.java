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

import java.util.Collection;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import org.entirej.applicationframework.fx.renderers.blocks.def.EJFXSingleRecordBlockDefinitionProperties;
import org.entirej.applicationframework.fx.renderers.interfaces.EJFXAppBlockRenderer;
import org.entirej.applicationframework.fx.renderers.interfaces.EJFXAppItemRenderer;
import org.entirej.applicationframework.fx.renderers.screen.EJFXInsertScreenRenderer;
import org.entirej.applicationframework.fx.renderers.screen.EJFXQueryScreenRenderer;
import org.entirej.applicationframework.fx.renderers.screen.EJFXUpdateScreenRenderer;
import org.entirej.applicationframework.fx.utils.EJUIUtils;
import org.entirej.applicationframework.fx.utils.EJUIUtils.GridLayoutUsage;
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
import org.entirej.framework.core.properties.containers.interfaces.EJItemGroupPropertiesContainer;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJBlockProperties;
import org.entirej.framework.core.properties.interfaces.EJItemGroupProperties;
import org.entirej.framework.core.properties.interfaces.EJMainScreenProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;
import org.entirej.framework.core.renderers.EJManagedItemRendererWrapper;
import org.entirej.framework.core.renderers.interfaces.EJInsertScreenRenderer;
import org.entirej.framework.core.renderers.interfaces.EJQueryScreenRenderer;
import org.entirej.framework.core.renderers.interfaces.EJUpdateScreenRenderer;
import org.entirej.framework.core.renderers.registry.EJMainScreenItemRendererRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EJFXSingleRecordBlockRenderer implements EJFXAppBlockRenderer
{

    final Logger                             logger             = LoggerFactory.getLogger(EJFXSingleRecordBlockRenderer.class);

    private boolean                          _showFocusedBorder = false;
    private EJManagedItemRendererWrapper     _firstNavigationalItem;
    private EJEditableBlockController        _block;
    private EJMainScreenItemRendererRegister _mainItemRegister;

    private boolean                          _isFocused         = false;

    private GridPane                         _mainPane;

    private EJFXQueryScreenRenderer          _queryScreenRenderer;
    private EJFXInsertScreenRenderer         _insertScreenRenderer;
    private EJFXUpdateScreenRenderer         _updateScreenRenderer;

    protected EJInternalEditableBlock getBlock()
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
        logger.trace("START blockCleared");
        _mainItemRegister.clearRegisteredValues();
        logger.trace("END blockCleared");

    }

    @Override
    public void detailBlocksCleared()
    {
        // ignore
    }

    @Override
    public void enterInsert(EJDataRecord record)
    {
        if (getBlock().getInsertScreenRenderer() == null)
        {
            EJMessage message = new EJMessage("Please define an Insert Screen Renderer for this form before an insert operation can be performed.");
            getBlock().getForm().getMessenger().handleMessage(message);
        }
        else
        {
            getBlock().getInsertScreenRenderer().open(record);
        }

    }

    @Override
    public void enterUpdate(EJDataRecord recordToUpdate)
    {
        if (getBlock().getUpdateScreenRenderer() == null)
        {
            EJMessage message = new EJMessage("Please define an Update Screen Renderer for this form before an update operation can be performed.");
            getBlock().getForm().getMessenger().handleMessage(message);
        }
        else
        {
            getBlock().getUpdateScreenRenderer().open(recordToUpdate);
        }
    }

    @Override
    public void gainFocus()
    {
        logger.trace("START gainFocus");
        if (_firstNavigationalItem != null)
        {
            _firstNavigationalItem.gainFocus();
        }
        else
        {
            _mainPane.requestFocus();
        }
        setHasFocus(true);
        logger.trace("END gainFocus");

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
        _mainItemRegister = new EJMainScreenItemRendererRegister(_block);

        _queryScreenRenderer = new EJFXQueryScreenRenderer();
        _insertScreenRenderer = new EJFXInsertScreenRenderer();
        _updateScreenRenderer = new EJFXUpdateScreenRenderer();

    }

    @Override
    public boolean isCurrentRecordDirty()
    {
        return _mainItemRegister.changesMade();
    }

    @Override
    public void queryExecuted()
    {
        if (getFocusedRecord() == null)
        {
            _mainItemRegister.register(getFirstRecord());
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
        recordSelected(recordAt);
    }

    @Override
    public void recordInserted(EJDataRecord record)
    {
        if (record != null)
        {
            logger.trace("START recordInserted");
            _mainItemRegister.register(record);
            // _block.newRecordInstance(record);
            logger.trace("END recordInserted");
        }
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
    public void setFocusToItem(EJScreenItemController item)
    {
        if (item == null)
        {
            return;
        }

        logger.trace("START setFocusToItem. Item: {}", item.getName());

        EJManagedItemRendererWrapper renderer = _mainItemRegister.getManagedItemRendererForItem(item.getProperties().getReferencedItemName());
        if (renderer != null)
        {
            renderer.gainFocus();
        }
        logger.trace("END setFocusToItem");

    }

    protected void setShowFocusedBorder(boolean show)
    {
        _showFocusedBorder = show;
    }

    @Override
    public void setHasFocus(boolean focus)
    {
        logger.trace("START setHasFocus. Focus: {}", focus);
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
        logger.trace("END hasFocus");

    }

    private void showFocusedBorder(boolean focused)
    {
        /**
         * FIXME: This needs to be implemented
         */
    }

    @Override
    public void enterQuery(EJDataRecord record)
    {
        if (getBlock().getQueryScreenRenderer() == null)
        {
            EJMessage message = new EJMessage("Please define a Query Screen Renderer for this form before a query operation can be performed.");
            getBlock().getForm().getMessenger().handleMessage(message);
        }
        else
        {
            getBlock().getQueryScreenRenderer().open(record);
        }

    }

    @Override
    public void executingQuery()
    {
        // ignore

    }

    @Override
    public int getDisplayedRecordCount()
    {
        return _block.getDataBlock().getBlockRecordCount();
    }

    @Override
    public int getDisplayedRecordNumber(EJDataRecord record)
    {
        return _block.getDataBlock().getRecordNumber(record);
    }

    @Override
    public EJDataRecord getFirstRecord()
    {
        return _block.getDataBlock().getRecord(0);
    }

    @Override
    public EJDataRecord getFocusedRecord()
    {
        return _mainItemRegister.getRegisteredRecord();
    }

    @Override
    public EJDataRecord getLastRecord()
    {
        return _block.getDataBlock().getRecord(_block.getBlockRecordCount() - 1);
    }

    @Override
    public EJDataRecord getRecordAfter(EJDataRecord record)
    {
        return _block.getDataBlock().getRecordAfter(record);
    }

    @Override
    public EJDataRecord getRecordAt(int displayedRecordNumber)
    {
        if (displayedRecordNumber > -1 && displayedRecordNumber < getDisplayedRecordCount())
        {

            return _block.getRecord(displayedRecordNumber);
        }
        return null;
    }

    @Override
    public EJDataRecord getRecordBefore(EJDataRecord record)
    {
        return _block.getDataBlock().getRecordBefore(record);
    }

    @Override
    public void recordSelected(EJDataRecord record)
    {
        if (record != null)
        {
            logger.trace("START recordSelected");
            synchronize();

            _mainItemRegister.register(record);
            logger.trace("END recordSelected");
        }
    }

    @Override
    public void refreshAfterChange(EJDataRecord record)
    {
        logger.trace("START refreshAfterChange");
        _mainItemRegister.refreshAfterChange(record);
        logger.trace("END recordUpdated");
    }

    public void refreshItemProperty(String itemName, EJManagedScreenProperty managedItemPropertyType, EJDataRecord record)
    {
        EJManagedItemRendererWrapper itemRenderer = _mainItemRegister.getManagedItemRendererForItem(itemName);

        if (itemRenderer == null)
            return;

        switch (managedItemPropertyType)
        {
            case EDIT_ALLOWED:
                itemRenderer.setEditAllowed((itemRenderer.isReadOnly() || _block.getBlock().getProperties().isControlBlock())
                        && itemRenderer.getItem().getProperties().isEditAllowed());
                break;
            case MANDATORY:
                itemRenderer.setMandatory(itemRenderer.getItem().getProperties().isMandatory());
                break;
            case VISIBLE:
                itemRenderer.setVisible(itemRenderer.getItem().getProperties().isVisible());
                break;
            case HINT:
                itemRenderer.setHint(itemRenderer.getItem().getProperties().getHint());
                break;
            case LABEL:
                itemRenderer.setLabel(itemRenderer.getItem().getProperties().getLabel());
                break;
            case SCREEN_ITEM_VISUAL_ATTRIBUTE:
                itemRenderer.setVisualAttribute(itemRenderer.getItem().getProperties().getVisualAttributeProperties());
                break;
            case ITEM_INSTANCE_VISUAL_ATTRIBUTE:
                if (record == getFocusedRecord())
                {
                    refreshRecordInstanceVA(record);
                }
                break;
            case ITEM_INSTANCE_HINT_TEXT:
                if (record == getFocusedRecord())
                {
                    refreshRecordInstanceHintText(record);
                }
                break;
        }
    }

    public void refreshItemRendererProperty(String itemName, String propertyName)
    {
        _mainItemRegister.getManagedItemRendererForItem(itemName).refreshItemRendererProperty(propertyName);
    }

    @Override
    public void synchronize()
    {
        // implementing this method caused modified values to be overridden by
        // the screen values
    }

    @Override
    public Object getGuiComponent()
    {
        return _mainPane;
    }

    @Override
    public Node createComponent()
    {
        Node componet = null;
        EJBlockProperties blockProperties = _block.getProperties();
        EJMainScreenProperties mainScreenProperties = blockProperties.getMainScreenProperties();
        EJFrameworkExtensionProperties rendererProp = blockProperties.getBlockRendererProperties();

        _mainPane = new GridPane();

        _mainPane.setVgap(1);
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

                TitledPane t2 = new TitledPane(frameTitle != null ? frameTitle : "", _mainPane);
                t1 = new TitledPane(title != null ? title : "", t2);
                componet = t1;

            }
            else
            {
                t1 = new TitledPane(title != null ? title : "", _mainPane);

            }
            createGridData(mainScreenProperties, t1);
            t1.setCollapsible(sectionProperties.getBooleanProperty(EJFXSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_EXPANDABLE, false));
            if (t1.isCollapsible())
                t1.setExpanded(sectionProperties.getBooleanProperty(EJFXSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_EXPANDED, true));

        }
        else
        {
            if (mainScreenProperties.getDisplayFrame() && mainScreenProperties.getFrameTitle() != null
                    && mainScreenProperties.getFrameTitle().trim().length() > 0)
            {

                String frameTitle = mainScreenProperties.getFrameTitle();

                TitledPane t2 = new TitledPane(frameTitle != null ? frameTitle : "", _mainPane);
                createGridData(mainScreenProperties, t2);
                componet = t2;

            }
            else
            {
                createGridData(mainScreenProperties, _mainPane);
                componet = _mainPane;
                if (mainScreenProperties.getDisplayFrame())
                {
                    _mainPane.setStyle("-fx-border-color: -fx-box-border");
                }
            }
        }

        _mainPane.focusedProperty().addListener(new ChangeListener<Boolean>()
        {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {

                setHasFocus(newValue);
            }

        });
        _mainPane.setOnMouseReleased(new EventHandler<MouseEvent>()
        {

            @Override
            public void handle(MouseEvent e)
            {
                setHasFocus(true);

            }
        });
        EJDataRecord registeredRecord = _mainItemRegister.getRegisteredRecord();
        _mainItemRegister.resetRegister();
        // ------
        EJItemGroupPropertiesContainer container = blockProperties.getScreenItemGroupContainer(EJScreenType.MAIN);
        Collection<EJItemGroupProperties> itemGroupProperties = container.getAllItemGroupProperties();
        GridLayoutUsage layoutUsage = EJUIUtils.newGridLayoutUsage(mainScreenProperties.getNumCols());

        for (EJItemGroupProperties ejItemGroupProperties : itemGroupProperties)
        {
            Node node = createItemGroup(ejItemGroupProperties);
            if (node != null)
            {
                createGridData(ejItemGroupProperties, node);
                Integer columnSpan = GridPane.getColumnSpan(node);
                if(columnSpan>layoutUsage.getColLimit())
                {
                    columnSpan = layoutUsage.getColLimit();
                    GridPane.setColumnSpan(node, columnSpan);
                }
                layoutUsage.allocate(columnSpan, GridPane.getRowSpan(node));
                _mainPane.add(node, layoutUsage.getCol(), layoutUsage.getRow());

            }
        }
        EJUIUtils.setConstraints(_mainPane, layoutUsage.getCol(), layoutUsage.getRow());

        _mainItemRegister.clearRegisteredValues();
        if (registeredRecord == null)
        {
            registeredRecord = getFirstRecord();
        }
        if (registeredRecord != null)
        {

            _mainItemRegister.register(registeredRecord);
        }
        return componet;

    }

    public Node[] createScreenItem(EJCoreMainScreenItemProperties itemProps)
    {
        if (itemProps.isSpacerItem())
        {
            Label empty = new Label();
            createBlockItemGridData(null, itemProps.getBlockRendererRequiredProperties(), empty, null);
            return new Node[] { empty };
        }

        EJScreenItemController item = _block.getScreenItem(EJScreenType.MAIN, itemProps.getReferencedItemName());
        EJManagedItemRendererWrapper renderer = item.getManagedItemRenderer();
        if (renderer != null)
        {
            _mainItemRegister.registerRendererForItem(renderer.getUnmanagedRenderer(), item);
            EJFrameworkExtensionProperties blockRequiredItemProperties = itemProps.getBlockRendererRequiredProperties();

            String labelPosition = blockRequiredItemProperties.getStringProperty(EJFXSingleRecordBlockDefinitionProperties.LABEL_POSITION_PROPERTY);
            String labelOrientation = blockRequiredItemProperties.getStringProperty(EJFXSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_PROPERTY);
            String visualAttribute = blockRequiredItemProperties.getStringProperty(EJFXSingleRecordBlockDefinitionProperties.VISUAL_ATTRIBUTE_PROPERTY);

            EJFXAppItemRenderer itemRenderer = (EJFXAppItemRenderer) renderer.getUnmanagedRenderer();
            boolean hasLabel = (itemProps.getLabel() != null && itemProps.getLabel().trim().length() > 0);

            EJScreenItemProperties itemProperties = item.getProperties();
            Node[] nodes = new Node[0];
            if (hasLabel && EJFXSingleRecordBlockDefinitionProperties.LABEL_POSITION_LEFT_PROPERTY.equals(labelPosition))
            {
                nodes = new Node[2];
                nodes[0] = itemRenderer.createLable();
                nodes[1] = itemRenderer.createComponent();
                labletextAliment(itemRenderer.getGuiComponentLabel(), labelOrientation);
            }
            else if (hasLabel && EJFXSingleRecordBlockDefinitionProperties.LABEL_POSITION_RIGHT_PROPERTY.equals(labelPosition))
            {
                nodes = new Node[2];
                nodes[0] = itemRenderer.createComponent();
                nodes[1] = itemRenderer.createLable();
                labletextAliment(itemRenderer.getGuiComponentLabel(), labelOrientation);
            }
            else
            {
                nodes = new Node[1];
                nodes[0] = itemRenderer.createComponent();
            }

            createBlockItemGridData(itemRenderer, blockRequiredItemProperties, itemRenderer.getGuiComponent(), null);

            if (itemRenderer.getGuiComponentLabel() != null)
            {
                createBlockLableGridData(blockRequiredItemProperties, itemRenderer.getGuiComponentLabel());
            }
            if (visualAttribute != null)
            {
                EJCoreVisualAttributeProperties va = EJCoreProperties.getInstance().getVisualAttributesContainer()
                        .getVisualAttributeProperties(visualAttribute);
                if (va != null)
                    itemRenderer.setInitialVisualAttribute(va);
            }
            
            if(item.getProperties().getVisualAttributeProperties()!=null)
            {
                renderer.setVisualAttribute(item.getProperties().getVisualAttributeProperties());
            }

            renderer.setVisible(itemProperties.isVisible());

            renderer.setEditAllowed((itemRenderer.isReadOnly() || (_block.getBlock().getProperties().isControlBlock()) && itemProperties.isEditAllowed()));

            // Add the item to the pane according to its display coordinates.
            renderer.setMandatory(itemProperties.isMandatory());

            renderer.enableLovActivation(itemProperties.getLovMappingName() != null);

            if (_firstNavigationalItem == null)
            {
                if (itemProperties.isVisible() && itemProperties.isEditAllowed())
                {
                    _firstNavigationalItem = renderer;
                }
            }
            return nodes;
        }

        return new Node[0];
    }

    private void labletextAliment(Label label, String labelOrientation)
    {
        if (label == null)
            return;
        // label.setLayoutData(new GridData(GridData.FILL_BOTH));
        if (EJFXSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_LEFT_PROPERTY.equals(labelOrientation))
        {
            label.setAlignment(Pos.CENTER_LEFT);
        }
        else if (EJFXSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_RIGHT_PROPERTY.equals(labelOrientation))
        {
            label.setAlignment(Pos.CENTER_RIGHT);
        }
        else if (EJFXSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_CENTER_PROPERTY.equals(labelOrientation))
        {
            label.setAlignment(Pos.CENTER);
        }
    }

    private Node createItemGroup(EJItemGroupProperties groupProperties)
    {
        Node baseNode = null;
        GridPane groupPane = new GridPane();
        groupPane.setHgap(5);
        groupPane.setVgap(1);
        boolean hasGroup = groupProperties.dispayGroupFrame();

        if (hasGroup && groupProperties.getFrameTitle() != null && groupProperties.getFrameTitle().trim().length() > 0)
        {
            EJFrameworkExtensionProperties rendererProp = groupProperties.getRendererProperties();
            GridPane pane = new GridPane();

            String title = groupProperties.getFrameTitle();
            TitledPane t1 = new TitledPane(title != null ? title : "", groupPane);
            if (rendererProp != null)
            {
                t1.setCollapsible(rendererProp.getBooleanProperty(EJFXSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_EXPANDABLE, false));
                if (t1.isCollapsible())
                    t1.setExpanded(rendererProp.getBooleanProperty(EJFXSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_EXPANDED, true));
            }

            GridPane.setVgrow(t1, Priority.ALWAYS);

            GridPane.setHgrow(t1, Priority.ALWAYS);
            ColumnConstraints column = new ColumnConstraints();
            column.setHgrow(Priority.ALWAYS);
            pane.getColumnConstraints().addAll(column);
            RowConstraints row = new RowConstraints();
            row.setVgrow(Priority.ALWAYS);
            pane.getRowConstraints().add(row);
            t1.setMaxWidth(Double.MAX_VALUE);
            t1.setMaxHeight(Double.MAX_VALUE);
            pane.getChildren().add(t1);
            pane.setPadding(new Insets(5, 5, 5, 5));
            groupPane.setPadding(new Insets(0, 5, 0, 5));
            baseNode = pane;
        }
        else
        {
            baseNode = groupPane;
            groupPane.setPadding(new Insets(5, 5, 5, 5));
            if (hasGroup)
            {
                groupPane.setStyle("-fx-border-color: -fx-box-border");
            }
        }

        GridLayoutUsage layoutUsage = EJUIUtils.newGridLayoutUsage(groupProperties.getNumCols());


        // add items
        Collection<EJScreenItemProperties> itemProperties = groupProperties.getAllItemProperties();
        for (EJScreenItemProperties screenItemProperties : itemProperties)
        {
            Node[] nodes = createScreenItem((EJCoreMainScreenItemProperties) screenItemProperties);
            for (Node node : nodes)
            {
                if (node != null)
                {
                    Integer columnSpan = GridPane.getColumnSpan(node);
                    if(columnSpan>layoutUsage.getColLimit())
                    {
                        columnSpan = layoutUsage.getColLimit();
                        GridPane.setColumnSpan(node, columnSpan);
                    }
                    layoutUsage.allocate(columnSpan, GridPane.getRowSpan(node));
                    groupPane.add(node, layoutUsage.getCol(), layoutUsage.getRow());
                }
                    
            }
        }

        // add sub groups

        Collection<EJItemGroupProperties> itemGroupProperties = groupProperties.getChildItemGroupContainer().getAllItemGroupProperties();

        for (EJItemGroupProperties ejItemGroupProperties : itemGroupProperties)
        {
            Node node = createItemGroup(ejItemGroupProperties);
            if (node != null)
            {
                createGridData(ejItemGroupProperties, node);
                Integer columnSpan = GridPane.getColumnSpan(node);
                if(columnSpan>layoutUsage.getColLimit())
                {
                    columnSpan = layoutUsage.getColLimit();
                    GridPane.setColumnSpan(node, columnSpan);
                }
                layoutUsage.allocate(columnSpan, GridPane.getRowSpan(node));
                groupPane.add(node, layoutUsage.getCol(), layoutUsage.getRow());
                

            }
        }
        EJUIUtils.setConstraints(groupPane, layoutUsage.getCol(), layoutUsage.getRow());

        return baseNode;
    }

    Bounds getCharBound(Font font)
    {
        final Text text = new Text("A");
        if (font != null)
            text.setFont(font);
        text.snapshot(null, null);
        return text.getLayoutBounds();
    }

    private Node createBlockLableGridData(EJFrameworkExtensionProperties blockRequiredItemProperties, Node node)
    {

        GridPane.setRowSpan(node, blockRequiredItemProperties.getIntProperty(EJFXSingleRecordBlockDefinitionProperties.MAIN_YSPAN_PROPERTY, 1));
        GridPane.setColumnSpan(node, 1);
        GridPane.setHgrow(node, Priority.NEVER);
        if (node instanceof Control)
        {

            ((Control) node).setPrefHeight(Control.USE_COMPUTED_SIZE);
            ((Control) node).setMinHeight(Control.USE_COMPUTED_SIZE);
            ((Control) node).setMaxHeight(Control.USE_COMPUTED_SIZE);

            ((Control) node).setPrefWidth(Control.USE_COMPUTED_SIZE);
            ((Control) node).setMinWidth(Control.USE_COMPUTED_SIZE);
            ((Control) node).setMaxWidth(Control.USE_COMPUTED_SIZE);
        }
        if (GridPane.getRowSpan(node) > 1
                || blockRequiredItemProperties.getBooleanProperty(EJFXSingleRecordBlockDefinitionProperties.MAIN_EXPAND_Y_PROPERTY, false))
        {
            GridPane.setMargin(node, new Insets(2, 0, 0, 0));
            GridPane.setValignment(node, VPos.TOP);
        }
        return node;
    }

    private Node createBlockItemGridData(EJFXAppItemRenderer itemRenderer, EJFrameworkExtensionProperties blockRequiredItemProperties, Node node, Font font)
    {

        boolean grabExcessVerticalSpace = blockRequiredItemProperties.getBooleanProperty(EJFXSingleRecordBlockDefinitionProperties.MAIN_EXPAND_Y_PROPERTY,
                false);
        boolean grabExcessHorizontalSpace = blockRequiredItemProperties.getBooleanProperty(EJFXSingleRecordBlockDefinitionProperties.MAIN_EXPAND_X_PROPERTY,
                false);

        int horizontalSpan = blockRequiredItemProperties.getIntProperty(EJFXSingleRecordBlockDefinitionProperties.MAIN_XSPAN_PROPERTY, 1);
        int verticalSpan = blockRequiredItemProperties.getIntProperty(EJFXSingleRecordBlockDefinitionProperties.MAIN_YSPAN_PROPERTY, 1);
        int displayedWidth = blockRequiredItemProperties.getIntProperty(EJFXSingleRecordBlockDefinitionProperties.DISPLAYED_WIDTH_PROPERTY, 0);
        int displayedHeight = blockRequiredItemProperties.getIntProperty(EJFXSingleRecordBlockDefinitionProperties.DISPLAYED_HEIGHT_PROPERTY, 0);

        if (itemRenderer != null && itemRenderer.useFontDimensions())
        {
            Bounds bounds = getCharBound(font);

            if (displayedWidth > 0)
            {

                double avgCharWidth = bounds.getWidth();//offset;
                if (avgCharWidth > 0)
                {
                    displayedWidth = ((int) ((displayedWidth + 3) * avgCharWidth))+5;// add
                                                                                 // //
                                                                                 // padding
                }

            }
            if (displayedHeight > 0)
            {

                double avgCharHeight = bounds.getHeight();
                if (avgCharHeight > 0)
                {
                    displayedHeight = (int) ((displayedHeight + 1) * avgCharHeight);// add
                                                                                    // //
                                                                                    // padding
                }

            }
        }

        if (node instanceof Control)
        {
            if (grabExcessVerticalSpace)
                ((Control) node).setMinHeight(displayedHeight);
            else
                ((Control) node).setMinHeight(Control.USE_COMPUTED_SIZE);
            if (grabExcessHorizontalSpace)
                ((Control) node).setMinWidth(displayedWidth);
            else
                ((Control) node).setMinWidth(Control.USE_COMPUTED_SIZE);

            if (displayedHeight > 0)
                ((Control) node).setPrefHeight(displayedHeight);
            else if (grabExcessVerticalSpace)
                ((Control) node).setPrefHeight(1);

            if (displayedWidth > 0)
                ((Control) node).setPrefWidth(displayedWidth);
            else if (grabExcessHorizontalSpace)
                ((Control) node).setPrefWidth(1);
        }
        else if (node instanceof Region)
        {
            if (grabExcessVerticalSpace)
                ((Region) node).setMinHeight(displayedHeight);
            else
                ((Region) node).setMinHeight(Control.USE_COMPUTED_SIZE);
            if (grabExcessHorizontalSpace)
                ((Region) node).setMinWidth(displayedWidth);
            else
                ((Region) node).setMinWidth(Control.USE_COMPUTED_SIZE);

            if (displayedHeight > 0)
                ((Region) node).setPrefHeight(displayedHeight);
            else if (grabExcessVerticalSpace)
                ((Region) node).setPrefHeight(1);
            if (displayedWidth > 0)
                ((Region) node).setPrefWidth(displayedWidth);
            else if (grabExcessHorizontalSpace)
                ((Region) node).setPrefWidth(1);
        }

        GridPane.setColumnSpan(node, horizontalSpan);
        GridPane.setRowSpan(node, grabExcessVerticalSpace?1:verticalSpan);
        if (grabExcessVerticalSpace)
            GridPane.setVgrow(node, Priority.ALWAYS);
        else
            GridPane.setVgrow(node, Priority.NEVER);
        if (grabExcessHorizontalSpace)
            GridPane.setHgrow(node, Priority.ALWAYS);
        else
            GridPane.setHgrow(node, Priority.NEVER);

        if (node instanceof Control)
        {

            ((Control) node).setMaxWidth(grabExcessHorizontalSpace ? Double.MAX_VALUE : displayedWidth);
            ((Control) node).setMaxHeight(grabExcessVerticalSpace ? Double.MAX_VALUE : displayedHeight);

        }
        else if (node instanceof Region)
        {

            ((Region) node).setMaxWidth(grabExcessHorizontalSpace ? Double.MAX_VALUE : displayedWidth);
            ((Region) node).setMaxHeight(grabExcessVerticalSpace ? Double.MAX_VALUE : displayedHeight);

        }

        return node;
    }

    private Node createGridData(EJMainScreenProperties layoutItem, Node node)
    {

        if (node instanceof Control)
        {
            if (layoutItem.canExpandVertically())
                ((Control) node).setMinHeight(layoutItem.getHeight());
            if (layoutItem.canExpandHorizontally())
                ((Control) node).setMinWidth(layoutItem.getWidth());

            if (layoutItem.getHeight() > 0)
                ((Control) node).setPrefHeight(layoutItem.getHeight());
            else if (layoutItem.canExpandVertically())
                ((Control) node).setPrefHeight(1);
            if (layoutItem.getWidth() > 0)
                ((Control) node).setPrefWidth(layoutItem.getWidth());
            else if (layoutItem.canExpandHorizontally())
                ((Control) node).setPrefWidth(1);
        }
        else if (node instanceof Region)
        {
            if (layoutItem.canExpandVertically())
                ((Region) node).setMinHeight(layoutItem.getHeight());
            if (layoutItem.canExpandHorizontally())
                ((Region) node).setMinWidth(layoutItem.getWidth());

            if (layoutItem.getHeight() > 0)
                ((Region) node).setPrefHeight(layoutItem.getHeight());
            else if (layoutItem.canExpandVertically())
                ((Region) node).setPrefHeight(1);

            if (layoutItem.getWidth() > 0)
                ((Region) node).setPrefWidth(layoutItem.getWidth());
            else if (layoutItem.canExpandHorizontally())
                ((Region) node).setPrefWidth(1);
        }

        GridPane.setColumnSpan(node, layoutItem.getHorizontalSpan());
        GridPane.setRowSpan(node, layoutItem.canExpandVertically()?1:layoutItem.getVerticalSpan());
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

            ((Control) node).setMaxWidth(layoutItem.canExpandHorizontally() ? Double.MAX_VALUE : layoutItem.getWidth());
            ((Control) node).setMaxHeight(layoutItem.canExpandVertically() ? Double.MAX_VALUE : layoutItem.getHeight());

        }
        else if (node instanceof Region)
        {

            ((Region) node).setMaxWidth(layoutItem.canExpandHorizontally() ? Double.MAX_VALUE : layoutItem.getWidth());
            ((Region) node).setMaxHeight(layoutItem.canExpandVertically() ? Double.MAX_VALUE : layoutItem.getHeight());

        }

        return node;

    }

    private Node createGridData(EJItemGroupProperties layoutItem, Node node)
    {

        if (node instanceof Control)
        {
            if (layoutItem.canExpandVertically())
                ((Control) node).setMinHeight(layoutItem.getHeight());
            if (layoutItem.canExpandHorizontally())
                ((Control) node).setMinWidth(layoutItem.getWidth());

            if (layoutItem.getHeight() > 0)
                ((Control) node).setPrefHeight(layoutItem.getHeight());
            else if (layoutItem.canExpandVertically())
                ((Control) node).setPrefHeight(1);
            if (layoutItem.getWidth() > 0)
                ((Control) node).setPrefWidth(layoutItem.getWidth());
            else if (layoutItem.canExpandHorizontally())
                ((Control) node).setPrefWidth(1);
        }
        else if (node instanceof Region)
        {
            if (layoutItem.canExpandVertically())
                ((Region) node).setMinHeight(layoutItem.getHeight());
            if (layoutItem.canExpandHorizontally())
                ((Region) node).setMinWidth(layoutItem.getWidth());

            if (layoutItem.getHeight() > 0)
                ((Region) node).setPrefHeight(layoutItem.getHeight());
            else if (layoutItem.canExpandVertically())
                ((Region) node).setPrefHeight(1);

            if (layoutItem.getWidth() > 0)
                ((Region) node).setPrefWidth(layoutItem.getWidth());
            else if (layoutItem.canExpandHorizontally())
                ((Region) node).setPrefWidth(1);
        }

        GridPane.setColumnSpan(node, layoutItem.getXspan() > 0 ? layoutItem.getXspan() : 1);
        GridPane.setRowSpan(node, layoutItem.canExpandVertically()?1: (layoutItem.getYspan() > 0 ? layoutItem.getYspan() : 1));
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

            ((Control) node).setMaxWidth(layoutItem.canExpandHorizontally() ? Double.MAX_VALUE : layoutItem.getWidth());
            ((Control) node).setMaxHeight(layoutItem.canExpandVertically() ? Double.MAX_VALUE : layoutItem.getHeight());

        }
        else if (node instanceof Region)
        {

            ((Region) node).setMaxWidth(layoutItem.canExpandHorizontally() ? Double.MAX_VALUE : layoutItem.getWidth());
            ((Region) node).setMaxHeight(layoutItem.canExpandVertically() ? Double.MAX_VALUE : layoutItem.getHeight());

        }

        if (layoutItem.getHorizontalAlignment() != null)
        {
            switch (layoutItem.getHorizontalAlignment())
            {
                case CENTER:
                    GridPane.setHalignment(node, HPos.CENTER);
                    GridPane.setHgrow(node, Priority.ALWAYS);
                    break;
                case BEGINNING:
                    GridPane.setHalignment(node, HPos.LEFT);
                    break;
                case END:
                    GridPane.setHalignment(node, HPos.RIGHT);
                    GridPane.setHgrow(node, Priority.ALWAYS);
                    break;

                default:
                    break;
            }
        }
        if (layoutItem.getVerticalAlignment() != null)
        {
            switch (layoutItem.getVerticalAlignment())
            {
                case CENTER:
                    GridPane.setValignment(node, VPos.CENTER);
                    GridPane.setVgrow(node, Priority.ALWAYS);
                    break;
                case BEGINNING:
                    GridPane.setValignment(node, VPos.TOP);
                    break;
                case END:
                    GridPane.setValignment(node, VPos.BOTTOM);
                    GridPane.setVgrow(node, Priority.ALWAYS);
                    break;

                default:
                    break;
            }
        }

        return node;

    }

    private void refreshRecordInstanceVA(EJDataRecord record)
    {
        for (EJManagedItemRendererWrapper wrapper : _mainItemRegister.getRegisteredRenderers())
        {
            // The screen item visual attribute has priority over the record
            // instance va
            if (record.containsItem(wrapper.getRegisteredItemName()) && wrapper.getItem().getProperties().getVisualAttributeProperties() == null)
            {
                if (record.getItem(wrapper.getRegisteredItemName()).getVisualAttribute() != null)
                {
                    wrapper.setVisualAttribute(record.getItem(wrapper.getRegisteredItemName()).getVisualAttribute());
                }
                else
                {
                    if (wrapper.getVisualAttributeProperties() != null)
                    {
                        wrapper.setVisualAttribute(null);
                    }
                }
            }
        }
    }

    private void refreshRecordInstanceHintText(EJDataRecord record)
    {
        for (EJManagedItemRendererWrapper wrapper : _mainItemRegister.getRegisteredRenderers())
        {
            if (record.containsItem(wrapper.getRegisteredItemName()))
            {
                if (record.getItem(wrapper.getRegisteredItemName()).getHint() != null)
                {
                    wrapper.setHint(record.getItem(wrapper.getRegisteredItemName()).getHint());
                }
                else
                {
                    EJScreenItemController screenItem = record.getBlock().getScreenItem(EJScreenType.MAIN, wrapper.getRegisteredItemName());
                    if (screenItem != null)
                    {
                        wrapper.setHint(screenItem.getProperties().getHint());
                    }
                }
            }
        }
    }
}

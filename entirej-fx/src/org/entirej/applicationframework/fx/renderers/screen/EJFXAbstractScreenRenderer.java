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
package org.entirej.applicationframework.fx.renderers.screen;

import java.util.Collection;

import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import org.entirej.applicationframework.fx.renderers.blocks.def.EJFXSingleRecordBlockDefinitionProperties;
import org.entirej.applicationframework.fx.renderers.interfaces.EJFXAppItemRenderer;
import org.entirej.applicationframework.fx.utils.EJUIUtils;
import org.entirej.applicationframework.fx.utils.EJUIUtils.GridLayoutUsage;
import org.entirej.framework.core.enumerations.EJScreenType;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.internal.EJInternalBlock;
import org.entirej.framework.core.properties.EJCoreProperties;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.containers.interfaces.EJItemGroupPropertiesContainer;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJItemGroupProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;
import org.entirej.framework.core.renderers.EJManagedItemRendererWrapper;
import org.entirej.framework.core.renderers.interfaces.EJItemRenderer;
import org.entirej.framework.core.renderers.interfaces.EJRenderer;
import org.entirej.framework.core.renderers.registry.EJBlockItemRendererRegister;

public abstract class EJFXAbstractScreenRenderer implements EJRenderer
{
    private EJManagedItemRendererWrapper _firstNavigationalItem;

    protected abstract EJInternalBlock getBlock();

    public abstract EJBlockItemRendererRegister getItemRegister();

    protected abstract void registerRendererForItem(EJItemRenderer managedRenderer, EJScreenItemController item);

    protected abstract EJFrameworkExtensionProperties getItemRendererPropertiesForItem(EJScreenItemProperties item);

    protected void addAllItemGroups(EJItemGroupPropertiesContainer container, GridPane containerPane, int numCol, EJScreenType screenType)
    {

        Collection<EJItemGroupProperties> itemGroupProperties = container.getAllItemGroupProperties();

        GridLayoutUsage layoutUsage = EJUIUtils.newGridLayoutUsage(numCol);

        for (EJItemGroupProperties ejItemGroupProperties : itemGroupProperties)
        {
            Node node = createItemGroup(ejItemGroupProperties, screenType);
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
                containerPane.add(node, layoutUsage.getCol(), layoutUsage.getRow());

            }
        }
        EJUIUtils.setConstraints(containerPane, layoutUsage.getCol(), layoutUsage.getRow());
    }

    protected void setFoucsItemRenderer()
    {
        if (_firstNavigationalItem != null)
            _firstNavigationalItem.gainFocus();
    }

    public Node[] createScreenItem(EJScreenItemProperties itemProps, EJScreenType screenType)
    {
        if (itemProps.isSpacerItem())
        {
            Label empty = new Label();
            createBlockItemGridData(null, getItemRendererPropertiesForItem(itemProps), empty, null);
            return new Node[] { empty };
        }

        EJScreenItemController item = getBlock().getScreenItem(screenType, itemProps.getReferencedItemName());
        EJManagedItemRendererWrapper renderer = item.getManagedItemRenderer();
        if (renderer != null)
        {
            getItemRegister().registerRendererForItem(renderer.getUnmanagedRenderer(), item);
            EJFrameworkExtensionProperties blockRequiredItemProperties = getItemRendererPropertiesForItem(itemProps);

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

            renderer.setVisible(itemProperties.isVisible());

            renderer.setEditAllowed(itemProperties.isEditAllowed());

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

    private Node createItemGroup(EJItemGroupProperties groupProperties, EJScreenType screenType)
    {
        Node baseNode = null;
        GridPane groupPane = new GridPane();
        boolean hasGroup = groupProperties.dispayGroupFrame();
        groupPane.setHgap(5);
        groupPane.setVgap(1);
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
            else
            {
                t1.setCollapsible(false);
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
            Node[] nodes = createScreenItem(screenItemProperties, screenType);
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
            Node node = createItemGroup(ejItemGroupProperties, screenType);
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
        Bounds layoutBounds = text.getLayoutBounds();
        return layoutBounds;
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

                double avgCharWidth = bounds.getWidth()+5;//offset;
                if (avgCharWidth > 0)
                {
                    displayedWidth = (int) ((displayedWidth + 1) * avgCharWidth);// add
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
                ((Control) node).setPrefHeight(20);
            if (displayedWidth > 0)
                ((Control) node).setPrefWidth(displayedWidth);
            else if (grabExcessHorizontalSpace)
                ((Control) node).setPrefWidth(10);
        }
        else if (node instanceof Region)
        {
            if (grabExcessVerticalSpace)
                ((Region) node).setMinHeight(displayedHeight);
            if (grabExcessHorizontalSpace)
                ((Region) node).setMinWidth(displayedWidth);

            if (displayedHeight > 0)
                ((Region) node).setPrefHeight(displayedHeight);
            else if (grabExcessVerticalSpace)
                ((Region) node).setPrefHeight(20);
            if (displayedWidth > 0)
                ((Region) node).setPrefWidth(displayedWidth);
            else if (grabExcessHorizontalSpace)
                ((Region) node).setPrefWidth(10);
        }

        GridPane.setColumnSpan(node, horizontalSpan);
        
            GridPane.setRowSpan(node,grabExcessVerticalSpace?1: verticalSpan);
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

    private Node createGridData(EJItemGroupProperties layoutItem, Node node)
    {

        if (node instanceof Control)
        {
            if (layoutItem.canExpandVertically())
                ((Control) node).setMinHeight(layoutItem.getHeight());
            else
                ((Control) node).setMinHeight(Control.USE_COMPUTED_SIZE);
            if (layoutItem.canExpandHorizontally())
                ((Control) node).setMinWidth(layoutItem.getWidth());
            else
                ((Control) node).setMinWidth(Control.USE_COMPUTED_SIZE);

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
        GridPane.setRowSpan(node, layoutItem.canExpandVertically()?1:(layoutItem.getYspan() > 0 ? layoutItem.getYspan() : 1));
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

}

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
package org.entirej.applicationframework.fx.renderers.block.definition;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.entirej.applicationframework.fx.renderers.block.definition.interfaces.EJFXMultiRecordBlockDefinitionProperties;
import org.entirej.applicationframework.fx.renderers.block.definition.interfaces.EJFXSingleRecordBlockDefinitionProperties;
import org.entirej.applicationframework.fx.renderers.block.definition.interfaces.EJFXTreeBlockDefinitionProperties;
import org.entirej.applicationframework.fx.renderers.block.definition.interfaces.EJFXTreeTableBlockDefinitionProperties;
import org.entirej.applicationframework.fx.renderers.screen.definition.EJFXInsertScreenRendererDefinition;
import org.entirej.applicationframework.fx.renderers.screen.definition.EJFXQueryScreenRendererDefinition;
import org.entirej.applicationframework.fx.renderers.screen.definition.EJFXUpdateScreenRendererDefinition;
import org.entirej.framework.core.properties.definitions.EJPropertyDefinitionType;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinition;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionGroup;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionListener;
import org.entirej.framework.core.properties.interfaces.EJMainScreenProperties;
import org.entirej.framework.dev.properties.EJDevPropertyDefinition;
import org.entirej.framework.dev.properties.EJDevPropertyDefinitionGroup;
import org.entirej.framework.dev.properties.interfaces.EJDevBlockDisplayProperties;
import org.entirej.framework.dev.properties.interfaces.EJDevItemGroupDisplayProperties;
import org.entirej.framework.dev.properties.interfaces.EJDevMainScreenItemDisplayProperties;
import org.entirej.framework.dev.properties.interfaces.EJDevScreenItemDisplayProperties;
import org.entirej.framework.dev.renderer.definition.EJDevBlockRendererDefinitionControl;
import org.entirej.framework.dev.renderer.definition.EJDevItemRendererDefinitionControl;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevBlockRendererDefinition;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevInsertScreenRendererDefinition;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevQueryScreenRendererDefinition;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevUpdateScreenRendererDefinition;

public class EJFXTreeTableRecordBlockDefinition implements EJDevBlockRendererDefinition
{
    public EJFXTreeTableRecordBlockDefinition()
    {
    }

    @Override
    public String getRendererClassName()
    {
        return "org.entirej.applicationframework.fx.renderers.blocks.EJFXTreeTableRecordBlockRenderer";
    }

    @Override
    public boolean allowSpacerItems()
    {
        return false;
    }

    public void loadValidValuesForProperty(EJFrameworkExtensionProperties frameworkExtensionProperties, EJPropertyDefinition propertyDefinition)
    {
        // no impl
    }

    public void propertyChanged(EJPropertyDefinitionListener propertyDefListener, EJFrameworkExtensionProperties properties, String propertyName)
    {
        // no impl
    }

    public boolean useInsertScreen()
    {
        return true;
    }

    public boolean useQueryScreen()
    {
        return true;
    }

    public boolean useUpdateScreen()
    {
        return true;
    }

    @Override
    public EJDevInsertScreenRendererDefinition getInsertScreenRendererDefinition()
    {
        return new EJFXInsertScreenRendererDefinition();
    }

    @Override
    public EJDevQueryScreenRendererDefinition getQueryScreenRendererDefinition()
    {
        return new EJFXQueryScreenRendererDefinition();
    }

    @Override
    public EJDevUpdateScreenRendererDefinition getUpdateScreenRendererDefinition()
    {
        return new EJFXUpdateScreenRendererDefinition();
    }

    public boolean allowMultipleItemGroupsOnMainScreen()
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.entirej.framework.renderers.IBlockRenderer#
     * getBlockPropertyDefinitionGroup()
     */
    public EJPropertyDefinitionGroup getBlockPropertyDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("Tree-Record Block");

        EJDevPropertyDefinition doubleClickActionCommand = new EJDevPropertyDefinition(EJFXTreeTableBlockDefinitionProperties.DOUBLE_CLICK_ACTION_COMMAND,
                EJPropertyDefinitionType.ACTION_COMMAND);
        doubleClickActionCommand.setLabel("Double Click Action Command");
        doubleClickActionCommand.setDescription("Add an action command that will be sent to the action processor when a user double clicks on this block");

        EJDevPropertyDefinition showTableBorder = new EJDevPropertyDefinition(EJFXTreeTableBlockDefinitionProperties.HIDE_TREE_BORDER,
                EJPropertyDefinitionType.BOOLEAN);
        showTableBorder.setLabel("Hide Tree Border");
        showTableBorder.setDescription("If selected, the renderer will hide the tree standard border");
        showTableBorder.setDefaultValue("false");

        EJDevPropertyDefinition showTableHeader = new EJDevPropertyDefinition(EJFXMultiRecordBlockDefinitionProperties.SHOW_HEADING_PROPERTY,
                EJPropertyDefinitionType.BOOLEAN);
        showTableHeader.setLabel("Show Tree Heading");
        showTableHeader.setDescription("If selected, the heading of the Tree will be displayed, otherwise a Tree will be displayed without a header");
        showTableHeader.setDefaultValue("true");

        EJDevPropertyDefinition filter = new EJDevPropertyDefinition(EJFXTreeBlockDefinitionProperties.FILTER, EJPropertyDefinitionType.BOOLEAN);
        filter.setLabel("Add Filter");
        filter.setDescription("If selected, the renderer will show Filter support");
        filter.setDefaultValue("false");

        EJDevPropertyDefinition showVerticalLines = new EJDevPropertyDefinition(EJFXTreeTableBlockDefinitionProperties.SHOW_VERTICAL_LINES,
                EJPropertyDefinitionType.BOOLEAN);
        showVerticalLines.setLabel("Show Vertical Lines");
        showVerticalLines.setDescription("Inicates if the tree show show vertical lines");
        showVerticalLines.setDefaultValue("true");

        EJDevPropertyDefinition parentItem = new EJDevPropertyDefinition(EJFXTreeTableBlockDefinitionProperties.PARENT_ITEM,
                EJPropertyDefinitionType.BLOCK_ITEM);
        parentItem.setLabel("Parent Item");
        parentItem.setMandatory(true);
        parentItem.setDescription("Parent item is used to match with relation item that build tree hierarchy using records.");

        EJDevPropertyDefinition relationItem = new EJDevPropertyDefinition(EJFXTreeTableBlockDefinitionProperties.RELATION_ITEM,
                EJPropertyDefinitionType.BLOCK_ITEM);
        relationItem.setLabel("Relation Item");
        relationItem.setMandatory(true);
        relationItem.setDescription("Relation item that build tree hierarchy using records.");

        EJDevPropertyDefinition imageItem = new EJDevPropertyDefinition(EJFXTreeTableBlockDefinitionProperties.NODE_IMAGE_ITEM,
                EJPropertyDefinitionType.BLOCK_ITEM);
        imageItem.setLabel("Image Item");
        imageItem.setDescription("item that provide node images [ url / byte array ].");
        EJDevPropertyDefinition expandLevel = new EJDevPropertyDefinition(EJFXTreeTableBlockDefinitionProperties.NODE_EXPAND_LEVEL,
                EJPropertyDefinitionType.INTEGER);
        expandLevel.setLabel("Expand Level");
        // expandLevel.setDescription("item that provide node images [ url / byte array ].");

        mainGroup.addPropertyDefinition(parentItem);
        mainGroup.addPropertyDefinition(relationItem);
        mainGroup.addPropertyDefinition(imageItem);
        mainGroup.addPropertyDefinition(expandLevel);
        mainGroup.addPropertyDefinition(doubleClickActionCommand);
        mainGroup.addPropertyDefinition(showTableBorder);
        mainGroup.addPropertyDefinition(showTableHeader);
        mainGroup.addPropertyDefinition(filter);
        mainGroup.addPropertyDefinition(showVerticalLines);

        EJDevPropertyDefinitionGroup sectionGroup = new EJDevPropertyDefinitionGroup("TITLE_BAR");
        sectionGroup.setLabel("Title Bar");
        EJDevPropertyDefinition showTitleBarVisible = new EJDevPropertyDefinition(EJFXSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_VISIBLE,
                EJPropertyDefinitionType.BOOLEAN);
        showTitleBarVisible.setLabel("Title Bar Visible");
        showTitleBarVisible.setDescription("If selected, the renderer will display section title bar .");
        showTitleBarVisible.setDefaultValue("false");
        EJDevPropertyDefinition title = new EJDevPropertyDefinition(EJFXSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_TITLE,
                EJPropertyDefinitionType.STRING);
        title.setLabel("Title");
        title.setDescription("Title Bar Caption.");

        EJDevPropertyDefinition showTitleBarExpandable = new EJDevPropertyDefinition(EJFXSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_EXPANDABLE,
                EJPropertyDefinitionType.BOOLEAN);
        showTitleBarExpandable.setLabel("Title Bar Expandable");
        showTitleBarExpandable.setDescription("If selected, the renderer will display section title bar with expandable support.");
        showTitleBarExpandable.setDefaultValue("false");

        EJDevPropertyDefinition showTitleBarExpanded = new EJDevPropertyDefinition(EJFXSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_EXPANDED,
                EJPropertyDefinitionType.BOOLEAN);
        showTitleBarExpanded.setLabel("Title Bar Expanded");
        showTitleBarExpanded.setDescription("If selected , the renderer will display section title bar expanded by default.");
        showTitleBarExpanded.setDefaultValue("true");

        sectionGroup.addPropertyDefinition(showTitleBarVisible);
        sectionGroup.addPropertyDefinition(title);
        sectionGroup.addPropertyDefinition(showTitleBarExpandable);
        sectionGroup.addPropertyDefinition(showTitleBarExpanded);
        mainGroup.addSubGroup(sectionGroup);

        return mainGroup;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.entirej.framework.renderers.IBlockRenderer#
     * getRequiredItemPropertiesDefinitionGroup()
     */
    public EJPropertyDefinitionGroup getItemPropertiesDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("Tree-Record Block: Required Item Properties");

        EJDevPropertyDefinition displayedWidth = new EJDevPropertyDefinition(EJFXTreeTableBlockDefinitionProperties.DISPLAY_WIDTH_PROPERTY,
                EJPropertyDefinitionType.INTEGER);
        displayedWidth.setLabel("Displayed Width");
        displayedWidth
                .setDescription("The width (in characters) of this items column within the blocks table.If zero specified system default width will be applied.");

        EJDevPropertyDefinition headerAllignment = new EJDevPropertyDefinition(EJFXTreeTableBlockDefinitionProperties.COLUMN_ALIGNMENT,
                EJPropertyDefinitionType.STRING);
        headerAllignment.setLabel("Column Alignment");
        headerAllignment.setDescription("Indicates the alignment of the column.");
        headerAllignment.setDefaultValue(EJFXTreeTableBlockDefinitionProperties.COLUMN_ALLIGN_LEFT);

        headerAllignment.addValidValue(EJFXTreeTableBlockDefinitionProperties.COLUMN_ALLIGN_LEFT, "Left");
        headerAllignment.addValidValue(EJFXTreeTableBlockDefinitionProperties.COLUMN_ALLIGN_RIGHT, "Right");
        headerAllignment.addValidValue(EJFXTreeTableBlockDefinitionProperties.COLUMN_ALLIGN_CENTER, "Center");

        EJDevPropertyDefinition allowColumnSorting = new EJDevPropertyDefinition(EJFXTreeTableBlockDefinitionProperties.ALLOW_ROW_SORTING,
                EJPropertyDefinitionType.BOOLEAN);
        allowColumnSorting.setLabel("Allow Column Sorting");
        allowColumnSorting.setDescription("If selected, the user will be able to re-order the data within the column by clicking on the column header");
        allowColumnSorting.setDefaultValue("true");

        EJDevPropertyDefinition allowColunmResize = new EJDevPropertyDefinition(EJFXTreeTableBlockDefinitionProperties.ALLOW_COLUMN_RESIZE,
                EJPropertyDefinitionType.BOOLEAN);
        allowColunmResize.setLabel("Allow Resize of Column");
        allowColunmResize.setDescription("If selected, the user will be able to resize the width of the table columns");
        allowColunmResize.setDefaultValue("true");

        EJDevPropertyDefinition allowColunmReorder = new EJDevPropertyDefinition(EJFXTreeTableBlockDefinitionProperties.ALLOW_COLUMN_REORDER,
                EJPropertyDefinitionType.BOOLEAN);
        allowColunmReorder.setLabel("Allow Re-Order of Column");
        allowColunmReorder.setDescription("If selected, the user will be able to change the order of the displayed column");
        allowColunmReorder.setDefaultValue("true");

        EJDevPropertyDefinition visualAttribute = new EJDevPropertyDefinition(EJFXTreeTableBlockDefinitionProperties.VISUAL_ATTRIBUTE_PROPERTY,
                EJPropertyDefinitionType.VISUAL_ATTRIBUTE);
        visualAttribute.setLabel("Visual Attribute");
        visualAttribute.setDescription("The background, foreground and font attributes applied for screen item");
        visualAttribute.setMandatory(false);

        mainGroup.addPropertyDefinition(displayedWidth);
        mainGroup.addPropertyDefinition(headerAllignment);
        /*
         * mainGroup.addPropertyDefinition(columnHeaderVA);
         * 
         * mainGroup.addPropertyDefinition(underlineHeader);
         */
        mainGroup.addPropertyDefinition(allowColumnSorting);
        mainGroup.addPropertyDefinition(allowColunmResize);
        mainGroup.addPropertyDefinition(allowColunmReorder);
        mainGroup.addPropertyDefinition(visualAttribute);

        return mainGroup;
    }

    @Override
    public EJPropertyDefinitionGroup getSpacerItemPropertiesDefinitionGroup()
    {
        // No spacers are available for a multi record block
        return null;
    }

    @Override
    public EJDevBlockRendererDefinitionControl addBlockControlToCanvas(EJMainScreenProperties mainScreenProperties,
            EJDevBlockDisplayProperties blockDisplayProperties, Composite parent, FormToolkit toolkit)
    {
        EJFrameworkExtensionProperties rendererProperties = blockDisplayProperties.getBlockRendererProperties();
        if (blockDisplayProperties != null)
        {
            rendererProperties = rendererProperties.getPropertyGroup(EJFXSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR);
        }
        Composite layoutBody;
        if (rendererProperties != null && rendererProperties.getBooleanProperty(EJFXSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_VISIBLE, false))
        {
            int style = ExpandableComposite.TITLE_BAR;

            if (rendererProperties.getBooleanProperty(EJFXSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_EXPANDABLE, false))
            {
                style = style | ExpandableComposite.TWISTIE;
            }
            if (rendererProperties.getBooleanProperty(EJFXSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_EXPANDED, true))
            {
                style = style | ExpandableComposite.EXPANDED;
            }
            String title = rendererProperties.getStringProperty(EJFXSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_TITLE);
            Section section = toolkit.createSection(parent, style);
            if (title != null)
            {
                section.setText(title);
            }
            section.setFont(parent.getFont());
            section.setForeground(parent.getForeground());
            if (mainScreenProperties.getDisplayFrame())
            {
                layoutBody = new Group(section, SWT.NONE);
                layoutBody.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
                if (mainScreenProperties.getFrameTitle() != null)
                {
                    ((Group) layoutBody).setText(mainScreenProperties.getFrameTitle());
                }
            }
            else
            {
                layoutBody = toolkit.createComposite(section);
            }
            section.setClient(layoutBody);
        }
        else
        {
            if (mainScreenProperties.getDisplayFrame())
            {
                layoutBody = new Group(parent, SWT.NONE);
                if (mainScreenProperties.getFrameTitle() != null)
                {
                    ((Group) layoutBody).setText(mainScreenProperties.getFrameTitle());
                }
            }
            else
            {
                layoutBody = new Composite(parent, SWT.NONE);
            }
        }
        EJFXTreeRendererDefinitionControl control = addTreeTable(blockDisplayProperties, layoutBody, toolkit);

        return control;
    }

    private EJFXTreeRendererDefinitionControl addTreeTable(EJDevBlockDisplayProperties blockDisplayProperties, Composite client, FormToolkit toolkit)
    {
        Map<String, Integer> columnPositions = new HashMap<String, Integer>();

        final ScrolledForm sc = toolkit.createScrolledForm(client);

        GridData scgd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        scgd.grabExcessHorizontalSpace = true;
        scgd.grabExcessVerticalSpace = true;
        sc.setLayoutData(scgd);
        GridLayout gl = new GridLayout();
        gl.marginHeight = gl.marginWidth = 0;
        sc.getBody().setLayout(gl);
        toolkit.adapt(sc);

        sc.getBody().setLayout(new FillLayout());
        Composite tablePanel = sc.getBody();
        EJDevItemGroupDisplayProperties displayProperties = null;
        if (blockDisplayProperties.getMainScreenItemGroupDisplayContainer().getAllItemGroupDisplayProperties().size() > 0)
        {
            displayProperties = blockDisplayProperties.getMainScreenItemGroupDisplayContainer().getAllItemGroupDisplayProperties().iterator().next();
            if (displayProperties.dispayGroupFrame())
            {
                Group group = new Group(tablePanel, SWT.NONE);
                group.setLayout(new FillLayout());
                if (displayProperties.getFrameTitle() != null && displayProperties.getFrameTitle().length() > 0)
                    group.setText(displayProperties.getFrameTitle());
                tablePanel = group;
            }
        }

        Tree table = new Tree(tablePanel, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);

        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        TableLayout tableLayout = new TableLayout();

        // There is only one item group for a flow layout
        TreeViewer _tableViewer = new TreeViewer(table);
        _tableViewer.setContentProvider(new ITreeContentProvider()
        {
            
            @Override
            public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
            {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void dispose()
            {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public boolean hasChildren(Object element)
            {
               
                return true;
            }
            
            @Override
            public Object getParent(Object element)
            {
                
                return null;
            }
            
            @Override
            public Object[] getElements(Object inputElement)
            {
                return new Object[]{new Object(),new Object(),new Object()};
            }
            
            @Override
            public Object[] getChildren(Object parentElement)
            {
                return new Object[]{new Object(),new Object()};
            }
        });
        int itemCount = 0;
        if (displayProperties != null)
            for (final EJDevScreenItemDisplayProperties screenItem : displayProperties.getAllItemDisplayProperties())
            {
                if (!screenItem.isSpacerItem())
                {
                    int width = ((EJDevMainScreenItemDisplayProperties) screenItem).getBlockRendererRequiredProperties().getIntProperty(
                            EJFXTreeTableBlockDefinitionProperties.DISPLAY_WIDTH_PROPERTY, 0);

                    TreeViewerColumn masterColumn = new TreeViewerColumn(_tableViewer, SWT.NONE);
                    masterColumn.getColumn().setData("SCREEN_ITEM", screenItem);
                    masterColumn.getColumn().setText(screenItem.getLabel());
                    masterColumn.getColumn().setWidth(width);
                    String alignment = ((EJDevMainScreenItemDisplayProperties) screenItem).getBlockRendererRequiredProperties().getStringProperty(
                            EJFXTreeTableBlockDefinitionProperties.COLUMN_ALIGNMENT);

                    if (EJFXTreeTableBlockDefinitionProperties.COLUMN_ALLIGN_RIGHT.equals(alignment))
                    {
                        masterColumn.getColumn().setAlignment(SWT.RIGHT);
                    }
                    else if (EJFXTreeTableBlockDefinitionProperties.COLUMN_ALLIGN_CENTER.equals(alignment))
                    {
                        masterColumn.getColumn().setAlignment(SWT.CENTER);
                    }
                    masterColumn.setLabelProvider(new ColumnLabelProvider(){
                        
                        @Override
                        public String getText(Object element)
                        {
                            
                            return screenItem.getReferencedItemName();
                            
                        }
                        
                    });
                    ColumnWeightData colData = new ColumnWeightData(5, 50, true);
                    tableLayout.addColumnData(colData);
                    columnPositions.put(screenItem.getReferencedItemName(), itemCount);
                    itemCount++;
                }
            }

        table.setLayout(tableLayout);
        _tableViewer.setInput(new Object());

        return new EJFXTreeRendererDefinitionControl(blockDisplayProperties, table, columnPositions);
    }
    @Override
    public EJDevItemRendererDefinitionControl getSpacerItemControl(EJDevScreenItemDisplayProperties itemProperties, Composite parent, FormToolkit toolkit)
    {
        return null;
    }

    @Override
    public EJPropertyDefinitionGroup getItemGroupPropertiesDefinitionGroup()
    {
        // TODO Auto-generated method stub
        return null;
    }

}

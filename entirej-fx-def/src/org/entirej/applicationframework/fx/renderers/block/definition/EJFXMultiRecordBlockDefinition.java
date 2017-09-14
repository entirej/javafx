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
package org.entirej.applicationframework.fx.renderers.block.definition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.entirej.applicationframework.fx.renderers.block.definition.interfaces.EJFXMultiRecordBlockDefinitionProperties;
import org.entirej.applicationframework.fx.renderers.block.definition.interfaces.EJFXSingleRecordBlockDefinitionProperties;
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
import org.entirej.framework.dev.renderer.definition.EJDevItemRendererDefinitionControl;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevBlockRendererDefinition;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevInsertScreenRendererDefinition;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevQueryScreenRendererDefinition;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevUpdateScreenRendererDefinition;

public class EJFXMultiRecordBlockDefinition implements EJDevBlockRendererDefinition
{
    public EJFXMultiRecordBlockDefinition()
    {
    }

    @Override
    public String getRendererClassName()
    {
        return "org.entirej.applicationframework.fx.renderers.blocks.EJFXMultiRecordBlockRenderer";
    }

    @Override
    public boolean allowSpacerItems()
    {
        return false;
    }

    @Override
    public void loadValidValuesForProperty(EJFrameworkExtensionProperties frameworkExtensionProperties, EJPropertyDefinition propertyDefinition)
    {
        // no impl
    }

    @Override
    public void propertyChanged(EJPropertyDefinitionListener propertyDefListener, EJFrameworkExtensionProperties properties, String propertyName)
    {
        // no impl
    }

    @Override
    public boolean useInsertScreen()
    {
        return true;
    }

    @Override
    public boolean useQueryScreen()
    {
        return true;
    }

    @Override
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

    @Override
    public boolean allowMultipleItemGroupsOnMainScreen()
    {
        return true;
    }

    @Override
    public EJPropertyDefinitionGroup getBlockPropertyDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("Multi-Record Block");

        EJDevPropertyDefinition doubleClickActionCommand = new EJDevPropertyDefinition(EJFXMultiRecordBlockDefinitionProperties.DOUBLE_CLICK_ACTION_COMMAND,
                EJPropertyDefinitionType.ACTION_COMMAND);
        doubleClickActionCommand.setLabel("Double Click Action Command");
        doubleClickActionCommand.setDescription("Add an action command that will be sent to the action processor when a user double clicks on this block");

        EJDevPropertyDefinition showTableBorder = new EJDevPropertyDefinition(EJFXMultiRecordBlockDefinitionProperties.HIDE_TABLE_BORDER,
                EJPropertyDefinitionType.BOOLEAN);
        showTableBorder.setLabel("Hide Table Border");
        showTableBorder.setDescription("If selected, the renderer will hide the tables standard border");
        showTableBorder.setDefaultValue("false");

        EJDevPropertyDefinition showTableHeader = new EJDevPropertyDefinition(EJFXMultiRecordBlockDefinitionProperties.SHOW_HEADING_PROPERTY,
                EJPropertyDefinitionType.BOOLEAN);
        showTableHeader.setLabel("Show Headings");
        showTableHeader.setDescription("If selected, the cloumn headings of the block will be displayed");
        showTableHeader.setDefaultValue("true");

        EJDevPropertyDefinition allowRowSelection = new EJDevPropertyDefinition(EJFXMultiRecordBlockDefinitionProperties.ROW_SELECTION_PROPERTY,
                EJPropertyDefinitionType.BOOLEAN);
        allowRowSelection.setLabel("Allow Row Selection");
        allowRowSelection.setDescription("Indicates if row selection is allowed for this block");
        allowRowSelection.setDefaultValue("true");

        EJDevPropertyDefinition showVerticalLines = new EJDevPropertyDefinition(EJFXMultiRecordBlockDefinitionProperties.SHOW_VERTICAL_LINES,
                EJPropertyDefinitionType.BOOLEAN);
        showVerticalLines.setLabel("Show Vertical Lines");
        showVerticalLines.setDescription("Indicates if the table should display vertical lines");
        showVerticalLines.setDefaultValue("true");

        EJDevPropertyDefinition filter = new EJDevPropertyDefinition(EJFXMultiRecordBlockDefinitionProperties.FILTER, EJPropertyDefinitionType.BOOLEAN);
        filter.setLabel("Add Filter");
        filter.setDescription("If selected, the renderer will display a filter field above the blocks data. This filter can then be used by users to filter the blocks displayed data");
        filter.setDefaultValue("false");

        mainGroup.addPropertyDefinition(doubleClickActionCommand);
        mainGroup.addPropertyDefinition(showTableBorder);
        mainGroup.addPropertyDefinition(showTableHeader);
        mainGroup.addPropertyDefinition(allowRowSelection);
        mainGroup.addPropertyDefinition(filter);
        mainGroup.addPropertyDefinition(showVerticalLines);

        EJDevPropertyDefinitionGroup sectionGroup = new EJDevPropertyDefinitionGroup("TITLE_BAR");
        sectionGroup.setLabel("Title Bar");
        sectionGroup.setDescription("Title bars are used containers for this block. These can be opened and collapsed according to the client framework implementation. ");
        
        EJDevPropertyDefinition showTitleBarVisible = new EJDevPropertyDefinition(EJFXSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_VISIBLE,
                EJPropertyDefinitionType.BOOLEAN);
        showTitleBarVisible.setLabel("Title Bar Visible");
        showTitleBarVisible.setDescription("Indicates if the title bar should be displayed for this block");
        showTitleBarVisible.setDefaultValue("false");

        EJDevPropertyDefinition title = new EJDevPropertyDefinition(EJFXSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_TITLE,
                EJPropertyDefinitionType.STRING);
        title.setLabel("Title");
        title.setDescription("The title to be displayed on this title bar");

        EJDevPropertyDefinition showTitleBarExpandable = new EJDevPropertyDefinition(EJFXSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_EXPANDABLE,
                EJPropertyDefinitionType.BOOLEAN);
        showTitleBarExpandable.setLabel("Title Bar Expandable");
        showTitleBarExpandable.setDescription("If you are using the Title Bars for your blocks, then it is possible to expand or collapse the block to either show or hide the content. Setting this property will enable the expand functionality for this block");
        showTitleBarExpandable.setDefaultValue("false");

        EJDevPropertyDefinition showTitleBarExpanded = new EJDevPropertyDefinition(EJFXSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_EXPANDED,
                EJPropertyDefinitionType.BOOLEAN);
        showTitleBarExpanded.setLabel("Title Bar Expanded");
        showTitleBarExpanded.setDescription("If selected , this blocks title bar will be expanded by default");
        showTitleBarExpanded.setDefaultValue("true");

        sectionGroup.addPropertyDefinition(showTitleBarVisible);
        sectionGroup.addPropertyDefinition(title);
        sectionGroup.addPropertyDefinition(showTitleBarExpandable);
        sectionGroup.addPropertyDefinition(showTitleBarExpanded);
        mainGroup.addSubGroup(sectionGroup);

        return mainGroup;
    }

    @Override
    public EJPropertyDefinitionGroup getItemPropertiesDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("Multi-Record Block: Required Item Properties");

        EJDevPropertyDefinition isFixedInTable = new EJDevPropertyDefinition(EJFXMultiRecordBlockDefinitionProperties.IS_COLUMN_FIXED,
                EJPropertyDefinitionType.BOOLEAN);
        isFixedInTable.setLabel("Fixed Column");
        isFixedInTable.setDescription("Indicates if this column is fixed to the left part of this block. Fixed columns hold their position even when the user scrolls horizontally to see the hidden columns");
        isFixedInTable.setDefaultValue("false");

        EJDevPropertyDefinition displayedWidth = new EJDevPropertyDefinition(EJFXMultiRecordBlockDefinitionProperties.DISPLAY_WIDTH_PROPERTY,
                EJPropertyDefinitionType.INTEGER);
        displayedWidth.setLabel("Displayed Width");
        displayedWidth
                .setDescription("The width (in characters) of this items column within the block.If zero is specified then the width of the column will be relative to the data it contains and the width of the other columns");

        EJDevPropertyDefinition headerAllignment = new EJDevPropertyDefinition(EJFXMultiRecordBlockDefinitionProperties.COLUMN_ALIGNMENT,
                EJPropertyDefinitionType.STRING);
        headerAllignment.setLabel("Column Alignment");
        headerAllignment.setDescription("Indicates the alignment of the contents within this column.");
        headerAllignment.setDefaultValue(EJFXMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_LEFT);

        headerAllignment.addValidValue(EJFXMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_LEFT, "Left");
        headerAllignment.addValidValue(EJFXMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_RIGHT, "Right");
        headerAllignment.addValidValue(EJFXMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_CENTER, "Center");

        EJDevPropertyDefinition allowColumnSorting = new EJDevPropertyDefinition(EJFXMultiRecordBlockDefinitionProperties.ALLOW_ROW_SORTING,
                EJPropertyDefinitionType.BOOLEAN);
        allowColumnSorting.setLabel("Allow Column Sorting");
        allowColumnSorting.setDescription("If selected, the user will be able to re-order the data within the block by clicking on the column header. Only block contents will be sorted, no new data will be retreived from the datasource");
        allowColumnSorting.setDefaultValue("true");

        EJDevPropertyDefinition allowColunmResize = new EJDevPropertyDefinition(EJFXMultiRecordBlockDefinitionProperties.ALLOW_COLUMN_RESIZE,
                EJPropertyDefinitionType.BOOLEAN);
        allowColunmResize.setLabel("Allow Resizing of Columns");
        allowColunmResize.setDescription("If selected, the user will be able to resize the width of the columns within this block");
        allowColunmResize.setDefaultValue("true");

        EJDevPropertyDefinition allowColunmReorder = new EJDevPropertyDefinition(EJFXMultiRecordBlockDefinitionProperties.ALLOW_COLUMN_REORDER,
                EJPropertyDefinitionType.BOOLEAN);
        allowColunmReorder.setLabel("Allow Re-Positioning of Columns");
        allowColunmReorder.setDescription("If selected, the user will be able to move columns of this block to change their displayed position. The re-positioning will not be saved and the next time the block is displayed, columns will be displayed in their original positions");
        allowColunmReorder.setDefaultValue("true");

        EJDevPropertyDefinition visualAttribute = new EJDevPropertyDefinition(EJFXMultiRecordBlockDefinitionProperties.VISUAL_ATTRIBUTE_PROPERTY,
                EJPropertyDefinitionType.VISUAL_ATTRIBUTE);
        visualAttribute.setLabel("Visual Attribute");
        visualAttribute.setDescription("The column will be displayed using the properties from the chosen visual attribute");
        visualAttribute.setMandatory(false);

        mainGroup.addPropertyDefinition(isFixedInTable);
        mainGroup.addPropertyDefinition(displayedWidth);
        mainGroup.addPropertyDefinition(headerAllignment);

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
    public EJFXTableRendererDefinitionControl addBlockControlToCanvas(EJMainScreenProperties mainScreenProperties,
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

        layoutBody.setLayout(new GridLayout(mainScreenProperties.getNumCols(), false));

        EJFXTableRendererDefinitionControl control = addTable(blockDisplayProperties, layoutBody, toolkit);

        return control;
    }

    private EJFXTableRendererDefinitionControl addTable(EJDevBlockDisplayProperties blockDisplayProperties, Composite client, FormToolkit toolkit)
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

        if (blockDisplayProperties.getMainScreenItemGroupDisplayContainer().getAllItemGroupDisplayProperties().size() > 0)
        {
            EJDevItemGroupDisplayProperties displayProperties = blockDisplayProperties.getMainScreenItemGroupDisplayContainer()
                    .getAllItemGroupDisplayProperties().iterator().next();
            if (displayProperties.dispayGroupFrame())
            {
                Group group = new Group(tablePanel, SWT.NONE);
                group.setLayout(new FillLayout());
                if (displayProperties.getFrameTitle() != null && displayProperties.getFrameTitle().length() > 0)
                {
                    group.setText(displayProperties.getFrameTitle());
                }
                tablePanel = group;
            }
        }

        Table table = new Table(tablePanel, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);

        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        TableLayout tableLayout = new TableLayout();

        // There is only one item group for a flow layout
        List<EJDevItemGroupDisplayProperties> allItemGroupProperties = new ArrayList<EJDevItemGroupDisplayProperties>(blockDisplayProperties
                .getMainScreenItemGroupDisplayContainer().getAllItemGroupDisplayProperties());

        int itemCount = 0;
        if (allItemGroupProperties.size() > 0)
        {
            EJDevItemGroupDisplayProperties mainItems = allItemGroupProperties.get(0);
            allItemGroupProperties.remove(0);
            itemCount = addFields(columnPositions, mainItems, table, tableLayout, 0);

            for (EJDevItemGroupDisplayProperties displayProperties : allItemGroupProperties)
            {
                itemCount = addFields(columnPositions, displayProperties, table, tableLayout, itemCount);
            }

        }

        table.setLayout(tableLayout);

        return new EJFXTableRendererDefinitionControl(blockDisplayProperties, table, columnPositions);
    }

    private int addFields(Map<String, Integer> columnPositions, EJDevItemGroupDisplayProperties displayProperties, Table table, TableLayout tableLayout,
            int itemCount)
    {
        if (displayProperties != null)
        {
            for (EJDevScreenItemDisplayProperties screenItem : displayProperties.getAllItemDisplayProperties())
            {
                if (!screenItem.isSpacerItem())
                {
                    int width = ((EJDevMainScreenItemDisplayProperties) screenItem).getBlockRendererRequiredProperties().getIntProperty(
                            EJFXMultiRecordBlockDefinitionProperties.DISPLAY_WIDTH_PROPERTY, 0);

                    TableColumn masterColumn = new TableColumn(table, SWT.NONE);
                    masterColumn.setData("SCREEN_ITEM", screenItem);
                    masterColumn.setText(screenItem.getLabel());
                    masterColumn.setWidth(width);
                    String alignment = ((EJDevMainScreenItemDisplayProperties) screenItem).getBlockRendererRequiredProperties().getStringProperty(
                            EJFXMultiRecordBlockDefinitionProperties.COLUMN_ALIGNMENT);

                    if (EJFXMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_RIGHT.equals(alignment))
                    {
                        masterColumn.setAlignment(SWT.RIGHT);
                    }
                    else if (EJFXMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_CENTER.equals(alignment))
                    {
                        masterColumn.setAlignment(SWT.CENTER);
                    }
                    ColumnWeightData colData = new ColumnWeightData(5, 50, true);
                    tableLayout.addColumnData(colData);
                    columnPositions.put(screenItem.getReferencedItemName(), itemCount);
                    itemCount++;
                }
            }

            // There is only one item group for a flow layout
            List<EJDevItemGroupDisplayProperties> allItemGroupProperties = new ArrayList<EJDevItemGroupDisplayProperties>(displayProperties
                    .getChildItemGroupContainer().getAllItemGroupDisplayProperties());

            if (allItemGroupProperties.size() > 0)
            {
                EJDevItemGroupDisplayProperties mainItems = allItemGroupProperties.get(0);
                allItemGroupProperties.remove(0);
                itemCount = addFields(columnPositions, mainItems, table, tableLayout, 0);

                for (EJDevItemGroupDisplayProperties subDisplayProperties : allItemGroupProperties)
                {
                    itemCount = addFields(columnPositions, subDisplayProperties, table, tableLayout, itemCount);
                }

            }
        }
        return itemCount;
    }

    @Override
    public EJDevItemRendererDefinitionControl getSpacerItemControl(EJDevScreenItemDisplayProperties itemProperties, Composite parent, FormToolkit toolkit)
    {
        return null;
    }

    @Override
    public EJPropertyDefinitionGroup getItemGroupPropertiesDefinitionGroup()
    {
        return null;
    }
}

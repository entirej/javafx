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

import java.util.Collections;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.entirej.applicationframework.fx.renderers.block.definition.interfaces.EJFXSingleRecordBlockDefinitionProperties;
import org.entirej.applicationframework.fx.renderers.block.definition.interfaces.EJFXTreeBlockDefinitionProperties;
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

public class EJFXTreeRecordBlockDefinition implements EJDevBlockRendererDefinition
{
    public EJFXTreeRecordBlockDefinition()
    {
    }

    @Override
    public String getRendererClassName()
    {
        return "org.entirej.applicationframework.fx.renderers.blocks.EJFXTreeRecordBlockRenderer";
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

    public EJPropertyDefinitionGroup getBlockPropertyDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("Tree-Record Block");

        EJDevPropertyDefinition doubleClickActionCommand = new EJDevPropertyDefinition(EJFXTreeBlockDefinitionProperties.DOUBLE_CLICK_ACTION_COMMAND,
                EJPropertyDefinitionType.ACTION_COMMAND);
        doubleClickActionCommand.setLabel("Double Click Action Command");
        doubleClickActionCommand.setDescription("Add an action command that will be sent to the action processor when a user double clicks on this block");

        EJDevPropertyDefinition showTableBorder = new EJDevPropertyDefinition(EJFXTreeBlockDefinitionProperties.HIDE_TREE_BORDER,
                EJPropertyDefinitionType.BOOLEAN);
        showTableBorder.setLabel("Hide Tree Border");
        showTableBorder.setDescription("If selected, the renderer will hide the tree standard border");
        showTableBorder.setDefaultValue("false");
        EJDevPropertyDefinition filter = new EJDevPropertyDefinition(EJFXTreeBlockDefinitionProperties.FILTER, EJPropertyDefinitionType.BOOLEAN);
        filter.setLabel("Add Filter");
        filter.setDescription("If selected, the renderer will display a filter field above the table. This filter can then be used by users to filter table data");
        filter.setDefaultValue("false");

        EJDevPropertyDefinition parentItem = new EJDevPropertyDefinition(EJFXTreeBlockDefinitionProperties.PARENT_ITEM, EJPropertyDefinitionType.BLOCK_ITEM);
        parentItem.setLabel("Parent Item");
        parentItem.setMandatory(true);
        parentItem.setDescription("A TreeRecord displays records in a tree hierarchy. The hierarchy is made by joining this item to a Relation Item. ");

        EJDevPropertyDefinition relationItem = new EJDevPropertyDefinition(EJFXTreeBlockDefinitionProperties.RELATION_ITEM, EJPropertyDefinitionType.BLOCK_ITEM);
        relationItem.setLabel("Relation Item");
        relationItem.setMandatory(true);
        relationItem.setDescription("Use to join to the Parent Item to create the hierarchy for the data displayed within this block");

        EJDevPropertyDefinition imageItem = new EJDevPropertyDefinition(EJFXTreeBlockDefinitionProperties.NODE_IMAGE_ITEM, EJPropertyDefinitionType.BLOCK_ITEM);
        imageItem.setLabel("Image Item");
        imageItem.setDescription("It is possible to dynamically add an image to the tree node by supplying the path of a picture within your project or by supplying a byteArray within the items value");
        
        EJDevPropertyDefinition expandLevel = new EJDevPropertyDefinition(EJFXTreeBlockDefinitionProperties.NODE_EXPAND_LEVEL,
                EJPropertyDefinitionType.INTEGER);
        expandLevel.setLabel("Expand Level");
        expandLevel.setDescription("Indicates the level to which the tree will be opened by default when the form is opened");

        
        EJDevPropertyDefinition visualAttribute = new EJDevPropertyDefinition(EJFXTreeBlockDefinitionProperties.VISUAL_ATTRIBUTE_PROPERTY,
                EJPropertyDefinitionType.VISUAL_ATTRIBUTE);
        visualAttribute.setLabel("Visual Attribute");
        visualAttribute.setDescription("The background, foreground and font attributes applied for screen item");
        visualAttribute.setMandatory(false);

        mainGroup.addPropertyDefinition(visualAttribute);

        mainGroup.addPropertyDefinition(parentItem);
        mainGroup.addPropertyDefinition(relationItem);
        mainGroup.addPropertyDefinition(imageItem);
        mainGroup.addPropertyDefinition(expandLevel);
        mainGroup.addPropertyDefinition(doubleClickActionCommand);
        mainGroup.addPropertyDefinition(showTableBorder);
        mainGroup.addPropertyDefinition(filter);

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

    public EJPropertyDefinitionGroup getItemPropertiesDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("Tree-Record Block: Required Item Properties");

        EJDevPropertyDefinition prefix = new EJDevPropertyDefinition(EJFXTreeBlockDefinitionProperties.ITEM_PREFIX, EJPropertyDefinitionType.STRING);
        prefix.setLabel("Prefix");
        prefix.setDescription("If you are displaying multiple items as part of the tree, then the Prefix and Suffix properties can be used to seperate the item labels");
        prefix.setMandatory(false);
        EJDevPropertyDefinition suffix = new EJDevPropertyDefinition(EJFXTreeBlockDefinitionProperties.ITEM_SUFFIX, EJPropertyDefinitionType.STRING);
        suffix.setLabel("Suffix");
        prefix.setDescription("If you are displaying multiple items as part of the tree, then the Prefix and Suffix properties can be used to seperate the item labels");
        suffix.setMandatory(false);

        mainGroup.addPropertyDefinition(prefix);
        mainGroup.addPropertyDefinition(suffix);

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
        layoutBody.setLayout(new FillLayout());
        layoutBody.setLayout(new FillLayout());

        EJDevItemGroupDisplayProperties displayProperties = null;
        if (blockDisplayProperties.getMainScreenItemGroupDisplayContainer().getAllItemGroupDisplayProperties().size() > 0)
        {
            displayProperties = blockDisplayProperties.getMainScreenItemGroupDisplayContainer().getAllItemGroupDisplayProperties().iterator().next();
           
        }
        StringBuilder builder = new StringBuilder();
        if (displayProperties != null)
            for (EJDevScreenItemDisplayProperties screenItem : displayProperties.getAllItemDisplayProperties())
            {
                if (!screenItem.isSpacerItem())
                {
                    EJFrameworkExtensionProperties properties = ((EJDevMainScreenItemDisplayProperties) screenItem).getBlockRendererRequiredProperties();
                   String prefix = properties.getStringProperty(EJFXTreeBlockDefinitionProperties.ITEM_PREFIX);
                   if(prefix!=null)
                   {
                       builder.append(prefix);
                   }
                   builder.append(screenItem.getReferencedItemName());
                   String sufix = properties.getStringProperty(EJFXTreeBlockDefinitionProperties.ITEM_SUFFIX);
                   if(sufix!=null)
                   {
                       builder.append(sufix);
                   }
                   
                }
            }
        String tag = builder.toString();
        if(tag.length()==0)
        {
            tag = "<empty>";
        }
        final Tree browser = new Tree (layoutBody, SWT.BORDER);
        for (int i=0; i<4; i++) {
                TreeItem iItem = new TreeItem (browser, 0);
                
                iItem.setText (tag+" " + (i+1));
                for (int j=0; j<4; j++) {
                        TreeItem jItem = new TreeItem (iItem, 0);
                        jItem.setText (tag+" " + (j+1));
                        
                }
        }
        return new EJDevBlockRendererDefinitionControl(blockDisplayProperties, Collections.<EJDevItemRendererDefinitionControl> emptyList());
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

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
/**
 * 
 */
package org.entirej.applicationframework.fx.renderers.block.definition;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.entirej.applicationframework.fx.renderers.block.definition.interfaces.EJFXSingleRecordBlockDefinitionProperties;
import org.entirej.applicationframework.fx.renderers.screen.definition.EJFXInsertScreenRendererDefinition;
import org.entirej.applicationframework.fx.renderers.screen.definition.EJFXQueryScreenRendererDefinition;
import org.entirej.applicationframework.fx.renderers.screen.definition.EJFXUpdateScreenRendererDefinition;
import org.entirej.framework.core.enumerations.EJSeparatorOrientation;
import org.entirej.framework.core.properties.definitions.EJPropertyDefinitionType;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinition;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionGroup;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionListener;
import org.entirej.framework.core.properties.interfaces.EJMainScreenProperties;
import org.entirej.framework.dev.properties.EJDevPropertyDefinition;
import org.entirej.framework.dev.properties.EJDevPropertyDefinitionGroup;
import org.entirej.framework.dev.properties.interfaces.EJDevBlockDisplayProperties;
import org.entirej.framework.dev.properties.interfaces.EJDevScreenItemDisplayProperties;
import org.entirej.framework.dev.renderer.definition.EJDevBlockRendererDefinitionControl;
import org.entirej.framework.dev.renderer.definition.EJDevItemRendererDefinitionControl;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevBlockRendererDefinition;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevInsertScreenRendererDefinition;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevQueryScreenRendererDefinition;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevUpdateScreenRendererDefinition;

public class EJFXSingleRecordBlockDefinition implements EJDevBlockRendererDefinition
{

    public EJFXSingleRecordBlockDefinition()
    {

    }

    @Override
    public boolean allowMultipleItemGroupsOnMainScreen()
    {
        return true;
    }

    @Override
    public boolean allowSpacerItems()
    {
        return true;
    }

    @Override
    public EJPropertyDefinitionGroup getBlockPropertyDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("Single-Record Block");

        EJDevPropertyDefinitionGroup sectionGroup = new EJDevPropertyDefinitionGroup("TITLE_BAR");
        sectionGroup.setLabel("Title Bar");
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
        EJDevPropertyDefinition itemPosition = new EJDevPropertyDefinition(EJFXSingleRecordBlockDefinitionProperties.ITEM_POSITION_PROPERTY,
                EJPropertyDefinitionType.STRING);
        itemPosition.setLabel("Item Orientation");
        itemPosition.setDescription("If the item is fixed in size and smaller than other items within its displayed column, then you can indicate how the item is displayed");
        itemPosition.addValidValue(EJFXSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_LEFT_PROPERTY, "Left");
        itemPosition.addValidValue(EJFXSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_RIGHT_PROPERTY, "Right");
        itemPosition.addValidValue(EJFXSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_CENTER_PROPERTY, "Center");
        itemPosition.setDefaultValue(EJFXSingleRecordBlockDefinitionProperties.LABEL_POSITION_LEFT_PROPERTY);
        itemPosition.setMandatory(true);

        EJDevPropertyDefinition labelPosition = new EJDevPropertyDefinition(EJFXSingleRecordBlockDefinitionProperties.LABEL_POSITION_PROPERTY,
                EJPropertyDefinitionType.STRING);
        labelPosition.setLabel("Label Position");
        labelPosition.setDescription("The position the items label should be displayed i.e. Before or after the item");
        labelPosition.addValidValue(EJFXSingleRecordBlockDefinitionProperties.LABEL_POSITION_LEFT_PROPERTY, "Left");
        labelPosition.addValidValue(EJFXSingleRecordBlockDefinitionProperties.LABEL_POSITION_RIGHT_PROPERTY, "Right");
        labelPosition.setDefaultValue(EJFXSingleRecordBlockDefinitionProperties.LABEL_POSITION_LEFT_PROPERTY);

        EJDevPropertyDefinition labelOrientation = new EJDevPropertyDefinition(EJFXSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_PROPERTY,
                EJPropertyDefinitionType.STRING);
        labelOrientation.setLabel("Label Orientation");
        labelOrientation.setDescription("The orientation of the labels text");
        labelOrientation.addValidValue(EJFXSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_LEFT_PROPERTY, "Left");
        labelOrientation.addValidValue(EJFXSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_RIGHT_PROPERTY, "Right");
        labelOrientation.addValidValue(EJFXSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_CENTER_PROPERTY, "Center");
        labelOrientation.setDefaultValue(EJFXSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_RIGHT_PROPERTY);
        labelOrientation.setMandatory(true);

        EJDevPropertyDefinition initiallyDisplayed = new EJDevPropertyDefinition(EJFXSingleRecordBlockDefinitionProperties.INITIALLY_DISPLAYED_PROPERTY,
                EJPropertyDefinitionType.BOOLEAN);
        initiallyDisplayed.setLabel("Initially Displayed");
        initiallyDisplayed
                .setDescription("Indicates if this item should be displayed to the user when the form starts. This property is effective if the Displayed property has been set true");
        initiallyDisplayed.setDefaultValue("true");
        initiallyDisplayed.setMandatory(true);

        EJDevPropertyDefinition horizontalSpan = new EJDevPropertyDefinition(EJFXSingleRecordBlockDefinitionProperties.MAIN_XSPAN_PROPERTY,
                EJPropertyDefinitionType.INTEGER);
        horizontalSpan.setLabel("Horizontal Span");
        horizontalSpan.setDescription("Indicates how many columns this item should span");
        horizontalSpan.setDefaultValue("1");

        EJDevPropertyDefinition verticalSpan = new EJDevPropertyDefinition(EJFXSingleRecordBlockDefinitionProperties.MAIN_YSPAN_PROPERTY,
                EJPropertyDefinitionType.INTEGER);
        verticalSpan.setLabel("Vertical Span");
        verticalSpan.setDescription("Indicates how many rows this item should span");
        verticalSpan.setDefaultValue("1");

        EJDevPropertyDefinition expandHorizontally = new EJDevPropertyDefinition(EJFXSingleRecordBlockDefinitionProperties.MAIN_EXPAND_X_PROPERTY,
                EJPropertyDefinitionType.BOOLEAN);
        expandHorizontally.setLabel("Expand Horizontally");
        expandHorizontally.setDescription("Indicates if this item should expand horizontally when the canvas is stretched");
        expandHorizontally.setDefaultValue("true");

        EJDevPropertyDefinition expandVertically = new EJDevPropertyDefinition(EJFXSingleRecordBlockDefinitionProperties.MAIN_EXPAND_Y_PROPERTY,
                EJPropertyDefinitionType.BOOLEAN);
        expandVertically.setLabel("Expand Vertically");
        expandVertically.setDescription("Indicates if this item should expand vertically when the canvas is stretched.");
        expandVertically.setDefaultValue("false");

        EJDevPropertyDefinition visualAttribute = new EJDevPropertyDefinition(EJFXSingleRecordBlockDefinitionProperties.VISUAL_ATTRIBUTE_PROPERTY,
                EJPropertyDefinitionType.VISUAL_ATTRIBUTE);
        visualAttribute.setLabel("Visual Attribute");
        visualAttribute.setDescription("The visual attribute that should be applied to this item");
        visualAttribute.setMandatory(false);

        EJDevPropertyDefinition displayedWidth = new EJDevPropertyDefinition(EJFXSingleRecordBlockDefinitionProperties.DISPLAYED_WIDTH_PROPERTY,
                EJPropertyDefinitionType.INTEGER);
        displayedWidth.setLabel("Displayed Width");
        displayedWidth.setDescription("Indicates the width (in characters) of this item. If no value or zero has been entered, the width of the item will be relevent to its contents");
        displayedWidth.setNotifyWhenChanged(true);

        EJDevPropertyDefinition displayedHeight = new EJDevPropertyDefinition(EJFXSingleRecordBlockDefinitionProperties.DISPLAYED_HEIGHT_PROPERTY,
                EJPropertyDefinitionType.INTEGER);
        displayedHeight.setLabel("Displayed Height");
        displayedWidth.setDescription("Indicates the height (in characters) of this item. If no value or zero has been entered, the height of the item will be relevent to its contents");
        displayedHeight.setNotifyWhenChanged(true);

        EJDevPropertyDefinitionGroup mainScreenGroup = new EJDevPropertyDefinitionGroup(
                EJFXSingleRecordBlockDefinitionProperties.MAIN_DISPLAY_COORDINATES_GROUP);
        mainScreenGroup.addPropertyDefinition(itemPosition);
        mainScreenGroup.addPropertyDefinition(labelPosition);
        mainScreenGroup.addPropertyDefinition(labelOrientation);
        mainScreenGroup.addPropertyDefinition(initiallyDisplayed);
        mainScreenGroup.addPropertyDefinition(horizontalSpan);
        mainScreenGroup.addPropertyDefinition(verticalSpan);
        mainScreenGroup.addPropertyDefinition(expandHorizontally);
        mainScreenGroup.addPropertyDefinition(expandVertically);
        mainScreenGroup.addPropertyDefinition(displayedWidth);
        mainScreenGroup.addPropertyDefinition(displayedHeight);
        mainScreenGroup.addPropertyDefinition(visualAttribute);

        return mainScreenGroup;
    }

    @Override
    public EJPropertyDefinitionGroup getSpacerItemPropertiesDefinitionGroup()
    {
        EJDevPropertyDefinition horizontalSpan = new EJDevPropertyDefinition(EJFXSingleRecordBlockDefinitionProperties.MAIN_XSPAN_PROPERTY,
                EJPropertyDefinitionType.INTEGER);
        horizontalSpan.setLabel("Horizontal Span");
        horizontalSpan.setDescription("Indicates how many columns this spacer should span");

        EJDevPropertyDefinition verticalSpan = new EJDevPropertyDefinition(EJFXSingleRecordBlockDefinitionProperties.MAIN_YSPAN_PROPERTY,
                EJPropertyDefinitionType.INTEGER);
        verticalSpan.setLabel("Vertical Span");
        verticalSpan.setDescription("Indicates how many rows this spacer should span");

        EJDevPropertyDefinition expandx = new EJDevPropertyDefinition(EJFXSingleRecordBlockDefinitionProperties.MAIN_EXPAND_X_PROPERTY,
                EJPropertyDefinitionType.BOOLEAN);
        expandx.setLabel("Expand Horizontally");
        expandx.setDescription("Indicates if this spacer should expand horizontally to fill the gap between items before and after this spacer");

        EJDevPropertyDefinition expandy = new EJDevPropertyDefinition(EJFXSingleRecordBlockDefinitionProperties.MAIN_EXPAND_Y_PROPERTY,
                EJPropertyDefinitionType.BOOLEAN);
        expandy.setLabel("Expand Vertically");
        expandy.setDescription("Indicates if this spacer should expand vertically to fill the gap between items above and below this spacer");

        EJDevPropertyDefinition displayedWidth = new EJDevPropertyDefinition(EJFXSingleRecordBlockDefinitionProperties.DISPLAYED_WIDTH_PROPERTY,
                EJPropertyDefinitionType.INTEGER);
        displayedWidth.setLabel("Displayed Width");
        displayedWidth.setDescription("Indicates the width (in characters) of this spacer. If no value or zero has been entered, the width of the item will be relevent to its contents");
        displayedWidth.setNotifyWhenChanged(true);

        EJDevPropertyDefinition displayedHeight = new EJDevPropertyDefinition(EJFXSingleRecordBlockDefinitionProperties.DISPLAYED_HEIGHT_PROPERTY,
                EJPropertyDefinitionType.INTEGER);
        displayedHeight.setLabel("Displayed Height");
        displayedWidth.setDescription("Indicates the height (in characters) of this spacer. If no value or zero has been entered, the height of the item will be relevent to its contents");
        displayedHeight.setNotifyWhenChanged(true);

        EJDevPropertyDefinitionGroup mainScreenGroup = new EJDevPropertyDefinitionGroup(
                EJFXSingleRecordBlockDefinitionProperties.MAIN_DISPLAY_COORDINATES_GROUP);
        mainScreenGroup.addPropertyDefinition(horizontalSpan);
        mainScreenGroup.addPropertyDefinition(verticalSpan);
        mainScreenGroup.addPropertyDefinition(expandx);
        mainScreenGroup.addPropertyDefinition(expandy);
        mainScreenGroup.addPropertyDefinition(displayedWidth);
        mainScreenGroup.addPropertyDefinition(displayedHeight);

        return mainScreenGroup;
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
    public String getRendererClassName()
    {
        return "org.entirej.applicationframework.fx.renderers.blocks.EJFXSingleRecordBlockRenderer";

    }

    @Override
    public void loadValidValuesForProperty(EJFrameworkExtensionProperties arg0, EJPropertyDefinition arg1)
    {
    }

    @Override
    public void propertyChanged(EJPropertyDefinitionListener arg0, EJFrameworkExtensionProperties arg1, String arg2)
    {
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

        layoutBody.setLayout(new GridLayout(mainScreenProperties.getNumCols(), false));

        EJFXBlockPreviewerCreator creator = new EJFXBlockPreviewerCreator();
        List<EJDevItemRendererDefinitionControl> itemControls = creator.addBlockPreviewControl(this, blockDisplayProperties, layoutBody, toolkit);

        return new EJDevBlockRendererDefinitionControl(blockDisplayProperties, itemControls);
    }

    @Override
    public EJDevItemRendererDefinitionControl getSpacerItemControl(EJDevScreenItemDisplayProperties itemProperties, Composite parent, FormToolkit toolkit)
    {
        int style = SWT.NULL;
        if(itemProperties.isSeparator())
        {
           
             style = SWT.SEPARATOR;
            if (itemProperties.getSeparatorOrientation() == EJSeparatorOrientation.HORIZONTAL)
            {
                style = style | SWT.HORIZONTAL;
            }
            else
            {
                style = style | SWT.VERTICAL;
            }
        }
        
        
        Label text = new Label(parent, style);
        return new EJDevItemRendererDefinitionControl(itemProperties, text);
    }

    @Override
    public EJPropertyDefinitionGroup getItemGroupPropertiesDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup sectionGroup = new EJDevPropertyDefinitionGroup("Single-Record Block");

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

        sectionGroup.addPropertyDefinition(showTitleBarExpandable);

        sectionGroup.addPropertyDefinition(showTitleBarExpanded);

        return sectionGroup;
    }

}

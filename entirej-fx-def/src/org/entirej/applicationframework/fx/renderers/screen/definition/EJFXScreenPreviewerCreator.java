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
package org.entirej.applicationframework.fx.renderers.screen.definition;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.entirej.applicationframework.fx.renderers.block.definition.interfaces.EJFXSingleRecordBlockDefinitionProperties;
import org.entirej.applicationframework.fx.renderers.screen.definition.interfaces.EJFXScreenRendererDefinitionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.dev.properties.interfaces.EJDevBlockDisplayProperties;
import org.entirej.framework.dev.properties.interfaces.EJDevBlockItemDisplayProperties;
import org.entirej.framework.dev.properties.interfaces.EJDevInsertScreenItemDisplayProperties;
import org.entirej.framework.dev.properties.interfaces.EJDevItemGroupDisplayProperties;
import org.entirej.framework.dev.properties.interfaces.EJDevQueryScreenItemDisplayProperties;
import org.entirej.framework.dev.properties.interfaces.EJDevScreenItemDisplayProperties;
import org.entirej.framework.dev.properties.interfaces.EJDevUpdateScreenItemDisplayProperties;
import org.entirej.framework.dev.renderer.definition.EJDevItemRendererDefinitionControl;

public class EJFXScreenPreviewerCreator
{
    public List<EJDevItemRendererDefinitionControl> addUpdateScreenPreviewControl(EJFXScreenRendererDefinition screenRenderer,
            EJDevBlockDisplayProperties blockDisplayProperties, Composite blockParent, FormToolkit toolkit)
    {
        List<EJDevItemRendererDefinitionControl> itemControls = new ArrayList<EJDevItemRendererDefinitionControl>();

        for (EJDevItemGroupDisplayProperties itemGroupProperties : blockDisplayProperties.getUpdateScreenItemGroupDisplayContainer()
                .getAllItemGroupDisplayProperties())
        {
            addItemGroup(blockParent, screenRenderer, blockDisplayProperties, itemGroupProperties, toolkit, itemControls);
        }

        return itemControls;
    }

    public List<EJDevItemRendererDefinitionControl> addInsertScreenPreviewControl(EJFXScreenRendererDefinition screenRenderer,
            EJDevBlockDisplayProperties blockDisplayProperties, Composite blockParent, FormToolkit toolkit)
    {
        List<EJDevItemRendererDefinitionControl> itemControls = new ArrayList<EJDevItemRendererDefinitionControl>();

        for (EJDevItemGroupDisplayProperties itemGroupProperties : blockDisplayProperties.getInsertScreenItemGroupDisplayContainer()
                .getAllItemGroupDisplayProperties())
        {
            addItemGroup(blockParent, screenRenderer, blockDisplayProperties, itemGroupProperties, toolkit, itemControls);
        }

        return itemControls;
    }

    public List<EJDevItemRendererDefinitionControl> addQueryScreenPreviewControl(EJFXScreenRendererDefinition screenRenderer,
            EJDevBlockDisplayProperties blockDisplayProperties, Composite blockParent, FormToolkit toolkit)
    {
        List<EJDevItemRendererDefinitionControl> itemControls = new ArrayList<EJDevItemRendererDefinitionControl>();

        for (EJDevItemGroupDisplayProperties itemGroupProperties : blockDisplayProperties.getQueryScreenItemGroupDisplayContainer()
                .getAllItemGroupDisplayProperties())
        {
            addItemGroup(blockParent, screenRenderer, blockDisplayProperties, itemGroupProperties, toolkit, itemControls);
        }

        return itemControls;
    }

    private void addItemGroup(Composite parent, EJFXScreenRendererDefinition screenRenderer, EJDevBlockDisplayProperties blockDisplayProperties,
            EJDevItemGroupDisplayProperties itemGroupProperties, FormToolkit toolkit, List<EJDevItemRendererDefinitionControl> itemControls)
    {
        Composite group = null;

        if (itemGroupProperties.dispayGroupFrame())
        {
            if (itemGroupProperties.getFrameTitle() == null)
            {
                group = new Composite(parent, SWT.SHADOW_NONE);
            }
            else
            {
                group = new Group(parent, SWT.SHADOW_ETCHED_IN);
                ((Group) group).setText(itemGroupProperties.getFrameTitle());
            }
        }
        else
        {
            group = new Composite(parent, SWT.SHADOW_NONE);
        }


        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = itemGroupProperties.getNumCols();
        gridLayout.horizontalSpacing = 0;
        gridLayout.verticalSpacing = 0;
        group.setLayout(gridLayout);

        group.setLayoutData(createItemGroupGridData(itemGroupProperties));

        ItemCreator itemCreator = new ItemCreator();
        for (EJDevScreenItemDisplayProperties definition : itemGroupProperties.getAllItemDisplayProperties())
        {
            if (definition.isSpacerItem())
            {
                EJDevItemRendererDefinitionControl control = itemCreator.addSpacerItem(screenRenderer, group, definition, toolkit);
                itemControls.add(control);
            }
            else
            {
                EJDevBlockItemDisplayProperties blockItemProperties = blockDisplayProperties.getBlockItemDisplayContainer().getItemProperties(
                        definition.getReferencedItemName());
                EJDevItemRendererDefinitionControl control = itemCreator.createItem(itemGroupProperties.getNumCols(), blockItemProperties, definition, group,
                        toolkit);
                itemControls.add(control);
            }
        }

        for (EJDevItemGroupDisplayProperties subGroup : itemGroupProperties.getChildItemGroupContainer().getAllItemGroupDisplayProperties())
        {
            addItemGroup(group, screenRenderer, blockDisplayProperties, subGroup, toolkit, itemControls);
        }

        toolkit.paintBordersFor(group);
    }

    private class ItemCreator
    {
        public ItemCreator()
        {
        }

        public EJDevItemRendererDefinitionControl createItem(int cols, EJDevBlockItemDisplayProperties blockItemProperties,
                EJDevScreenItemDisplayProperties screenItemProperties, Composite blockCanvas, FormToolkit toolkit)
        {
            EJDevItemRendererDefinitionControl control = null;
            EJFrameworkExtensionProperties screenRequiredItemProperties = null;

            if (screenItemProperties instanceof EJDevUpdateScreenItemDisplayProperties)
            {
                screenRequiredItemProperties = ((EJDevUpdateScreenItemDisplayProperties) screenItemProperties).getUpdateScreenRendererRequiredProperties();
            }
            else if (screenItemProperties instanceof EJDevInsertScreenItemDisplayProperties)
            {
                screenRequiredItemProperties = ((EJDevInsertScreenItemDisplayProperties) screenItemProperties).getInsertScreenRendererRequiredProperties();
            }
            else if (screenItemProperties instanceof EJDevQueryScreenItemDisplayProperties)
            {
                screenRequiredItemProperties = ((EJDevQueryScreenItemDisplayProperties) screenItemProperties).getQueryScreenRendererRequiredProperties();
            }
            else
            {
                return null;
            }

            if (screenRequiredItemProperties == null)
            {
                return null;
            }

            String labelPosition = screenRequiredItemProperties.getStringProperty(EJFXScreenRendererDefinitionProperties.LABEL_POSITION_PROPERTY);
            String labelOrientation = screenRequiredItemProperties.getStringProperty(EJFXScreenRendererDefinitionProperties.LABEL_ORIENTATION);
            Boolean hasLabel = (screenItemProperties.getLabel() != null && screenItemProperties.getLabel().trim().length() > 0);

            if (hasLabel && (screenItemProperties.getLabel() != null && screenItemProperties.getLabel().trim().length() > 0))
            {

                if (EJFXScreenRendererDefinitionProperties.LABEL_POSITION_RIGHT_PROPERTY.equals(labelPosition))
                {
                    control = addItem(blockCanvas, blockItemProperties, screenItemProperties, screenRequiredItemProperties, toolkit);
                    addLabel(blockCanvas, blockItemProperties, screenItemProperties, screenRequiredItemProperties, labelOrientation, toolkit);
                }
                else
                {
                    addLabel(blockCanvas, blockItemProperties, screenItemProperties, screenRequiredItemProperties, labelOrientation, toolkit);
                    control = addItem(blockCanvas, blockItemProperties, screenItemProperties, screenRequiredItemProperties, toolkit);
                }
            }
            else
            {
                control = addItem(blockCanvas, blockItemProperties, screenItemProperties, screenRequiredItemProperties, toolkit);
            }

            return control;
        }

        private void addLabel(Composite canvas, EJDevBlockItemDisplayProperties blockItemProperites, EJDevScreenItemDisplayProperties screenItemProperties,
                EJFrameworkExtensionProperties screenRequiredItemProperties, String labelOrientation, FormToolkit toolkit)
        {

            Control labelControl = blockItemProperites.getItemRendererDefinition().getLabelControl(screenItemProperties, canvas, toolkit);
            if (labelControl != null)
            {
                labelControl.setLayoutData(createBlockLableGridData(screenRequiredItemProperties));
                labletextAliment((Label) labelControl, labelOrientation);
            }
        }

        private void labletextAliment(Label label, String labelOrientation)
        {
            if (label == null)
            {
                return;
            }
            if (EJFXSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_LEFT_PROPERTY.equals(labelOrientation))
            {
                label.setAlignment(SWT.LEFT);
            }
            else if (EJFXSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_RIGHT_PROPERTY.equals(labelOrientation))
            {
                label.setAlignment(SWT.RIGHT);
            }
            else if (EJFXSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_CENTER_PROPERTY.equals(labelOrientation))
            {
                label.setAlignment(SWT.CENTER);
            }
        }

        private EJDevItemRendererDefinitionControl addItem(Composite canvas, EJDevBlockItemDisplayProperties blockItemProperties,
                EJDevScreenItemDisplayProperties screenItemProperties, EJFrameworkExtensionProperties screenRequiredItemProperties, FormToolkit toolkit)
        {
            EJDevItemRendererDefinitionControl itemControl = blockItemProperties.getItemRendererDefinition().getItemControl(screenItemProperties, canvas,
                    toolkit);

            itemControl.getItemControl().setLayoutData(
                    createBlockItemGridData(screenRequiredItemProperties, itemControl.getItemControl(), itemControl.useFontDimensions()));

            return itemControl;
        }

        public EJDevItemRendererDefinitionControl addSpacerItem(EJFXScreenRendererDefinition screenRenderer, Composite canvas,
                EJDevScreenItemDisplayProperties screenItemProperties, FormToolkit toolkit)
        {
            EJFrameworkExtensionProperties screenRequiredItemProperties;

            if (screenItemProperties instanceof EJDevUpdateScreenItemDisplayProperties)
            {
                screenRequiredItemProperties = ((EJDevUpdateScreenItemDisplayProperties) screenItemProperties).getUpdateScreenRendererRequiredProperties();
            }
            else if (screenItemProperties instanceof EJDevInsertScreenItemDisplayProperties)
            {
                screenRequiredItemProperties = ((EJDevInsertScreenItemDisplayProperties) screenItemProperties).getInsertScreenRendererRequiredProperties();
            }
            else if (screenItemProperties instanceof EJDevQueryScreenItemDisplayProperties)
            {
                screenRequiredItemProperties = ((EJDevQueryScreenItemDisplayProperties) screenItemProperties).getQueryScreenRendererRequiredProperties();
            }
            else
            {
                return null;
            }

            EJDevItemRendererDefinitionControl itemControl = screenRenderer.getSpacerItemControl(screenItemProperties, canvas, toolkit);

            itemControl.getItemControl().setLayoutData(createBlockItemGridData(screenRequiredItemProperties, null, false));

            return itemControl;
        }

        private GridData createBlockLableGridData(EJFrameworkExtensionProperties blockRequiredItemProperties)
        {

            GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
            gridData.grabExcessHorizontalSpace = false;
            gridData.verticalSpan = blockRequiredItemProperties.getIntProperty(EJFXSingleRecordBlockDefinitionProperties.MAIN_YSPAN_PROPERTY, 1);
            if (gridData.verticalSpan > 1
                    || blockRequiredItemProperties.getBooleanProperty(EJFXSingleRecordBlockDefinitionProperties.MAIN_EXPAND_Y_PROPERTY, false))
            {

                gridData.verticalAlignment = SWT.TOP;
            }
            return gridData;
        }

        private GridData createBlockItemGridData(EJFrameworkExtensionProperties blockRequiredItemProperties, Control control, boolean useFontDimensions)
        {

            boolean grabExcessVerticalSpace = blockRequiredItemProperties.getBooleanProperty(EJFXSingleRecordBlockDefinitionProperties.MAIN_EXPAND_Y_PROPERTY,
                    false);
            boolean grabExcessHorizontalSpace = blockRequiredItemProperties.getBooleanProperty(
                    EJFXSingleRecordBlockDefinitionProperties.MAIN_EXPAND_X_PROPERTY, false);
            GridData gridData;
            if (grabExcessVerticalSpace && grabExcessHorizontalSpace)
            {
                gridData = new GridData(GridData.FILL_BOTH);
            }
            else if (!grabExcessVerticalSpace && grabExcessHorizontalSpace)
            {
                gridData = new GridData(GridData.FILL_BOTH);
            }
            else if (grabExcessVerticalSpace && !grabExcessHorizontalSpace)
            {
                gridData = new GridData(GridData.FILL_VERTICAL);
            }
            else
            {
                gridData = new GridData(GridData.FILL_VERTICAL);
            }
            gridData.horizontalSpan = blockRequiredItemProperties.getIntProperty(EJFXSingleRecordBlockDefinitionProperties.MAIN_XSPAN_PROPERTY, 1);
            gridData.verticalSpan = blockRequiredItemProperties.getIntProperty(EJFXSingleRecordBlockDefinitionProperties.MAIN_YSPAN_PROPERTY, 1);
            gridData.grabExcessHorizontalSpace = grabExcessHorizontalSpace;
            gridData.grabExcessVerticalSpace = grabExcessVerticalSpace;

            int displayedWidth = blockRequiredItemProperties.getIntProperty(EJFXSingleRecordBlockDefinitionProperties.DISPLAYED_WIDTH_PROPERTY, 0);
            int displayedHeight = blockRequiredItemProperties.getIntProperty(EJFXSingleRecordBlockDefinitionProperties.DISPLAYED_HEIGHT_PROPERTY, 0);

            if (displayedWidth > 0)
            {
                if (useFontDimensions)
                {
                    float avgCharWidth = control == null ? 1 : getAvgCharWidth(control.getFont());
                    if (avgCharWidth > 0)
                    {
                        displayedWidth = (int) ((displayedWidth + 1) * avgCharWidth);
                    }
                }

                gridData.widthHint = displayedWidth;
            }
            if (displayedHeight > 0)
            {
                if (useFontDimensions)
                {
                    float avgCharHeight = control == null ? 1 : getCharHeight(control.getFont());
                    if (avgCharHeight > 0)
                    {
                        displayedHeight = (int) ((displayedHeight + 1) * avgCharHeight);
                    }
                }
                gridData.heightHint = displayedHeight;
            }
            return gridData;
        }

        public float getAvgCharWidth(Font font)
        {
            GC gc = new GC(Display.getDefault());
            try
            {
                gc.setFont(font);
                return gc.getFontMetrics().getAverageCharWidth();
            }
            finally
            {
                gc.dispose();
            }
        }

        public int getCharHeight(Font font)
        {
            if (font.getFontData().length > 0)
            {
                return font.getFontData()[0].getHeight();
            }
            return 13;
        }
    }

    GridData createItemGroupGridData(EJDevItemGroupDisplayProperties groupProperties)
    {
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.minimumWidth = groupProperties.getWidth();
        gridData.minimumHeight = groupProperties.getHeight();

        gridData.horizontalSpan = groupProperties.getXspan();
        gridData.verticalSpan = groupProperties.getYspan();
        gridData.grabExcessHorizontalSpace = groupProperties.canExpandHorizontally();
        gridData.grabExcessVerticalSpace = groupProperties.canExpandVertically();

        if (gridData.grabExcessHorizontalSpace)
        {
            gridData.minimumWidth = groupProperties.getWidth();
        }
        if (gridData.grabExcessVerticalSpace)
        {
            gridData.minimumHeight = groupProperties.getHeight();
        }
        
        if(groupProperties.getHorizontalAlignment()!=null)
        {
            switch (groupProperties.getHorizontalAlignment())
            {
                case CENTER:
                    gridData.horizontalAlignment = SWT.CENTER;
                    gridData.grabExcessHorizontalSpace = true;
                    break;
                case BEGINNING:
                    gridData.horizontalAlignment = SWT.BEGINNING;
                    break;
                case END:
                    gridData.horizontalAlignment = SWT.END;
                    gridData.grabExcessHorizontalSpace = true;
                    break;

                default:
                    break;
            }
        }
        if(groupProperties.getVerticalAlignment()!=null)
        {
            switch (groupProperties.getVerticalAlignment())
            {
                case CENTER:
                    gridData.verticalAlignment = SWT.CENTER;
                    gridData.grabExcessVerticalSpace = true;
                    break;
                case BEGINNING:
                    gridData.verticalAlignment = SWT.BEGINNING;
                    break;
                case END:
                    gridData.verticalAlignment = SWT.END;
                    gridData.grabExcessVerticalSpace = true;
                    break;
                    
                default:
                    break;
            }
        }

        return gridData;
    }
}

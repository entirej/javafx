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
package org.entirej.applicationframework.fx.renderers.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import org.entirej.applicationframework.fx.application.EJFXApplicationManager;
import org.entirej.applicationframework.fx.application.form.containers.AbstractDialog;
import org.entirej.applicationframework.fx.layout.EJFXEntireJStackedPane;
import org.entirej.applicationframework.fx.renderers.interfaces.EJFXAppBlockRenderer;
import org.entirej.applicationframework.fx.renderers.interfaces.EJFXAppFormRenderer;
import org.entirej.applicationframework.fx.utils.EJUIUtils;
import org.entirej.framework.core.EJApplicationException;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.common.utils.EJParameterChecker;
import org.entirej.framework.core.data.controllers.EJCanvasController;
import org.entirej.framework.core.data.controllers.EJEmbeddedFormController;
import org.entirej.framework.core.enumerations.EJCanvasSplitOrientation;
import org.entirej.framework.core.enumerations.EJCanvasType;
import org.entirej.framework.core.enumerations.EJPopupButton;
import org.entirej.framework.core.internal.EJInternalBlock;
import org.entirej.framework.core.internal.EJInternalEditableBlock;
import org.entirej.framework.core.internal.EJInternalForm;
import org.entirej.framework.core.properties.EJCoreProperties;
import org.entirej.framework.core.properties.containers.interfaces.EJCanvasPropertiesContainer;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.interfaces.EJCanvasProperties;
import org.entirej.framework.core.properties.interfaces.EJFormProperties;
import org.entirej.framework.core.properties.interfaces.EJStackedPageProperties;
import org.entirej.framework.core.properties.interfaces.EJTabPageProperties;

public class EJFXFormRenderer implements EJFXAppFormRenderer
{

    @Override
    public Node getGuiComponent()
    {
        if (_mainPane == null)
        {
            throw new IllegalAccessError("Call createControl() before access getGuiComponent()");
        }
        return _mainPane;
    }

    @Override
    public Node createControl()
    {
        setupGui();
        setFocus();
        _form.getFormController().formInitialised();
        return _mainPane;
    }

    private EJInternalForm                      _form;
    private GridPane                            _mainPane;
    private LinkedList<String>                  _canvasesIds  = new LinkedList<String>();
    private Map<String, CanvasHandler>          _canvases     = new HashMap<String, CanvasHandler>();
    private Map<String, EJInternalBlock>        _blocks       = new HashMap<String, EJInternalBlock>();
    private Map<String, EJTabFolder>            _tabFolders   = new HashMap<String, EJTabFolder>();
    private Map<String, EJFXEntireJStackedPane> _stackedPanes = new HashMap<String, EJFXEntireJStackedPane>();

    @Override
    public void formCleared()
    {

    }

    @Override
    public void formClosed()
    {

    }

    @Override
    public void gainInitialFocus()
    {
        setFocus();

    }

    @Override
    public EJInternalForm getForm()
    {

        return _form;
    }

    @Override
    public void initialiseForm(EJInternalForm form)
    {
        EJParameterChecker.checkNotNull(form, "initialiseForm", "formController");
        _form = form;

    }

    @Override
    public void refreshFormRendererProperty(String arg0)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void savePerformed()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void showPopupCanvas(String canvasName)
    {
        CanvasHandler canvasHandler = _canvases.get(canvasName);
        if (canvasHandler instanceof PopupCanvasHandler)
        {
            PopupCanvasHandler handler = (PopupCanvasHandler) canvasHandler;
            handler.open(true);
        }

    }

    @Override
    public void closePopupCanvas(String canvasName)
    {
        CanvasHandler canvasHandler = _canvases.get(canvasName);
        if (canvasHandler instanceof PopupCanvasHandler)
        {
            PopupCanvasHandler handler = (PopupCanvasHandler) canvasHandler;
            handler.close();
        }

    }

    @Override
    public void showStackedPage(String canvasName, String pageName)
    {
        if (canvasName != null && pageName != null)
        {
            EJFXEntireJStackedPane cardPane = _stackedPanes.get(canvasName);
            if (cardPane != null)
            {
                cardPane.showPane(pageName);
            }
        }
    }

    @Override
    public void showTabPage(String canvasName, String pageName)
    {
        if (canvasName != null && pageName != null)
        {
            EJTabFolder tabPane = _tabFolders.get(canvasName);
            if (tabPane != null)
            {
                tabPane.showPage(pageName);
            }
        }
    }
    

   
    
   
    public void openEmbeddedForm(EJEmbeddedFormController arg0)
    {
        throw new IllegalAccessError("Not supported yet");
        
        
    }
    
    
    public void closeEmbeddedForm(EJEmbeddedFormController arg0)
    {
        throw new IllegalAccessError("Not supported yet");
        
        
    }

    private void setFocus()
    {
        for (String canvasName : _canvasesIds)
        {
            EJCanvasProperties canvasProperties = _form.getProperties().getCanvasProperties(canvasName);

            if (canvasProperties != null && setFocus(canvasProperties))
            {
                return;
            }
        }
    }

    private boolean setFocus(EJCanvasProperties canvasProperties)
    {
        if (canvasProperties.getType() == EJCanvasType.BLOCK)
        {
            if (canvasProperties.getBlockProperties() != null)
            {
                EJInternalEditableBlock block = _form.getBlock(canvasProperties.getBlockProperties().getName());

                if (block.getRendererController() != null)
                {
                    block.getManagedRenderer().gainFocus();
                    return true;
                }
            }

        }
        else if (canvasProperties.getType() == EJCanvasType.GROUP)
        {
            for (EJCanvasProperties groupCanvas : canvasProperties.getGroupCanvasContainer().getAllCanvasProperties())
            {
                if (setFocus(groupCanvas))
                {
                    return true;
                }
            }
        }
        else if (canvasProperties.getType() == EJCanvasType.SPLIT)
        {
            for (EJCanvasProperties groupCanvas : canvasProperties.getSplitCanvasContainer().getAllCanvasProperties())
            {
                if (setFocus(groupCanvas))
                {
                    return true;
                }
            }
        }
        else if (canvasProperties.getType() == EJCanvasType.STACKED)
        {
            for (EJStackedPageProperties pageProps : canvasProperties.getStackedPageContainer().getAllStackedPageProperties())
            {
                if (pageProps.getName().equals(canvasProperties.getInitialStackedPageName() == null ? "" : canvasProperties.getInitialStackedPageName()))
                {
                    for (EJCanvasProperties stackedCanvas : pageProps.getContainedCanvases().getAllCanvasProperties())
                    {
                        if (setFocus(stackedCanvas))
                        {
                            return true;
                        }
                    }
                }
            }
        }
        else if (canvasProperties.getType() == EJCanvasType.TAB)
        {
            for (EJTabPageProperties tabPage : canvasProperties.getTabPageContainer().getAllTabPageProperties())
            {
                if (tabPage.isVisible())
                {
                    _form.getCanvasController().tabPageChanged(canvasProperties.getName(), tabPage.getName());
                    return true;
                }
            }
        }

        return false;
    }

    private void setupGui()
    {
        EJFormProperties formProperties = _form.getProperties();
        EJCanvasController canvasController = _form.getCanvasController();

        // Now loop through all the forms blocks and create controllers for them
        for (EJInternalBlock block : _form.getAllBlocks())
        {
            String canvasName = block.getProperties().getCanvasName();
            // If the block has not had a canvas defined for it, it cannot be
            // displayed.
            if (canvasName == null || canvasName.trim().length() == 0)
            {
                continue;
            }

            _blocks.put(canvasName, block);
        }
        _mainPane = new GridPane();
        int cCol = 0;
        int cRow = 0;

        for (EJCanvasProperties canvasProperties : formProperties.getCanvasContainer().getAllCanvasProperties())
        {
            Node node = createCanvas(canvasProperties, canvasController);
            if (node != null)
            {
                if (cCol <= (formProperties.getNumCols() - 1))
                {
                    _mainPane.add(node, cCol, cRow);
                    cCol++;

                    if (GridPane.getColumnSpan(node) > 1)
                    {
                        cCol += (GridPane.getColumnSpan(node) - 1);
                    }
                    if (GridPane.getRowSpan(node) > 1)
                    {
                        cRow += (GridPane.getRowSpan(node) - 1);
                    }

                }
                else
                {
                    cCol = 0;
                    cRow++;
                    _mainPane.add(node, cCol, cRow);
                    cCol++;
                    if (GridPane.getColumnSpan(node) > 1)
                    {
                        cCol += (GridPane.getColumnSpan(node) - 1);
                    }
                    if (GridPane.getRowSpan(node) > 1)
                    {
                        cRow += (GridPane.getRowSpan(node) - 1);
                    }
                }

            }
        }
        EJUIUtils.setConstraints(_mainPane, cCol, cRow);

    }

    private Node createCanvas(EJCanvasProperties canvasProperties, EJCanvasController canvasController)
    {
        switch (canvasProperties.getType())
        {
            case BLOCK:
            case GROUP:
                return createGroupCanvas(canvasProperties, canvasController);

            case SPLIT:
                return createSplitCanvas(canvasProperties, canvasController);

            case STACKED:
                return createStackedCanvas(canvasProperties, canvasController);

            case TAB:
                return createTabCanvas(canvasProperties, canvasController);

            case POPUP:
                buildPopupCanvas(canvasProperties, canvasController);
        }
        return null;
    }

    private Node createGridData(EJCanvasProperties layoutItem, Node node)
    {

        int height = layoutItem.getHeight();
        if (height == 0)
            height = 1;
        int width = layoutItem.getWidth();
        if (width == 0)
            width = 1;
        if (node instanceof Control)
        {
            if (layoutItem.canExpandVertically())
                ((Control) node).setMinHeight(height);
            if (layoutItem.canExpandHorizontally())
                ((Control) node).setMinWidth(width);

            ((Control) node).setPrefHeight(height);

            ((Control) node).setPrefWidth(width);
        }
        else if (node instanceof Region)
        {
            if (layoutItem.canExpandVertically())
                ((Region) node).setMinHeight(height);
            if (layoutItem.canExpandHorizontally())
                ((Region) node).setMinWidth(width);

            ((Region) node).setPrefHeight(height);

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

            ((Control) node).setMaxWidth(Double.MAX_VALUE);
            ((Control) node).setMaxHeight(Double.MAX_VALUE);

        }
        else if (node instanceof Region)
        {

            ((Region) node).setMaxWidth(Double.MAX_VALUE);
            ((Region) node).setMaxHeight(Double.MAX_VALUE);

        }

        return node;

    }

    private Node createStackedCanvas(EJCanvasProperties canvasProperties, EJCanvasController canvasController)
    {
        final String name = canvasProperties.getName();
        EJFXEntireJStackedPane stackedPane = new EJFXEntireJStackedPane();
        createGridData(canvasProperties, stackedPane);
        _stackedPanes.put(name, stackedPane);

        for (EJStackedPageProperties page : canvasProperties.getStackedPageContainer().getAllStackedPageProperties())
        {
            GridPane pagePane = new GridPane();
            pagePane.setPadding(new Insets(0, 0, 0, 0));
            stackedPane.add(page.getName(), pagePane);
            int cCol = 0;
            int cRow = 0;
            for (EJCanvasProperties properties : page.getContainedCanvases().getAllCanvasProperties())
            {
                Node node = createCanvas(properties, canvasController);

                if (node != null)
                {
                    if (cCol <= (page.getNumCols() - 1))
                    {
                        pagePane.add(node, cCol, cRow);
                        cCol++;

                        if (GridPane.getColumnSpan(node) > 1)
                        {
                            cCol += (GridPane.getColumnSpan(node) - 1);
                        }
                        if (GridPane.getRowSpan(node) > 1)
                        {
                            cRow += (GridPane.getRowSpan(node) - 1);
                        }
                    }
                    else
                    {
                        cCol = 0;
                        cRow++;
                        pagePane.add(node, cCol, cRow);
                        cCol++;
                        if (GridPane.getColumnSpan(node) > 1)
                        {
                            cCol += (GridPane.getColumnSpan(node) - 1);
                        }
                        if (GridPane.getRowSpan(node) > 1)
                        {
                            cRow += (GridPane.getRowSpan(node) - 1);
                        }
                    }

                }
            }
            EJUIUtils.setConstraints(pagePane, cCol, cRow);
        }

        if (canvasProperties.getInitialStackedPageName() != null)
        {
            stackedPane.showPane(canvasProperties.getInitialStackedPageName());
        }

        _canvasesIds.add(name);
        return stackedPane;
    }

    private Node createTabCanvas(EJCanvasProperties canvasProperties, final EJCanvasController canvasController)
    {

        EJFrameworkExtensionProperties rendererProp = EJCoreProperties.getInstance().getApplicationDefinedProperties();
        if (rendererProp != null)
        {
            boolean displayBorder = rendererProp.getBooleanProperty("DISPLAY_TAB_BORDER", true);
            if (displayBorder)
            {
                // FIXME:DO WE neeed this ?

            }
        }
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        switch (canvasProperties.getTabPosition())
        {
            case BOTTOM:
                tabPane.setSide(Side.BOTTOM);
                break;
            case LEFT:
                tabPane.setSide(Side.LEFT);
                break;
            case RIGHT:
                tabPane.setSide(Side.RIGHT);
                break;

            default:
                tabPane.setSide(Side.TOP);
                break;
        }
        final String name = canvasProperties.getName();
        final EJTabFolder tabFolder = new EJTabFolder(tabPane);
        tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>()
        {

            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue)
            {

                canvasController.tabPageChanged(name, tabFolder.getActiveKey());
            }
        });

        _tabFolders.put(name, tabFolder);
        createGridData(canvasProperties, tabPane);

        Collection<EJTabPageProperties> allTabPageProperties = canvasProperties.getTabPageContainer().getAllTabPageProperties();
        for (EJTabPageProperties page : allTabPageProperties)
        {
            if (page.isVisible())
            {
                Tab tabItem = new Tab();

                GridPane pagePane = new GridPane();
                pagePane.setPadding(new Insets(0, 0, 0, 0));
                int cCol = 0;
                int cRow = 0;
                tabItem.setText((page.getPageTitle() != null && page.getPageTitle().length() > 0) ? page.getPageTitle() : page.getName());
                tabItem.setContent(pagePane);
                EJCanvasPropertiesContainer containedCanvases = page.getContainedCanvases();
                for (EJCanvasProperties pageProperties : containedCanvases.getAllCanvasProperties())
                {
                    Node node = createCanvas(pageProperties, canvasController);
                    if (node != null)
                    {
                        if (cCol <= (page.getNumCols() - 1))
                        {
                            pagePane.add(node, cCol, cRow);
                            cCol++;

                            if (GridPane.getColumnSpan(node) > 1)
                            {
                                cCol += (GridPane.getColumnSpan(node) - 1);
                            }
                            if (GridPane.getRowSpan(node) > 1)
                            {
                                cRow += (GridPane.getRowSpan(node) - 1);
                            }

                        }
                        else
                        {
                            cCol = 0;
                            cRow++;
                            pagePane.add(node, cCol, cRow);
                            cCol++;
                            if (GridPane.getColumnSpan(node) > 1)
                            {
                                cCol += (GridPane.getColumnSpan(node) - 1);
                            }
                            if (GridPane.getRowSpan(node) > 1)
                            {
                                cRow += (GridPane.getRowSpan(node) - 1);
                            }
                        }

                    }
                }
                EJUIUtils.setConstraints(pagePane, cCol, cRow);
                tabPane.getTabs().add(tabItem);
                if (tabPane.getSelectionModel().getSelectedItem() == null)
                {
                    tabPane.getSelectionModel().select(tabItem);
                }

                tabFolder.put(page.getName(), tabItem);
                tabItem.setDisable((!page.isEnabled()));
            }
        }

        _canvasesIds.add(name);
        return tabPane;
    }

    private void buildPopupCanvas(EJCanvasProperties canvasProperties, final EJCanvasController canvasController)
    {

        String name = canvasProperties.getName();

        _canvases.put(name, new PopupCanvasHandler(canvasProperties, canvasController));
    }

    private Node createGroupCanvas(EJCanvasProperties canvasProperties, EJCanvasController canvasController)
    {

        CanvasHandler canvasHandler = new CanvasHandler()
        {

            @Override
            public Node add(EJInternalBlock block)
            {

                EJFXAppBlockRenderer blockRenderer = (EJFXAppBlockRenderer) block.getRendererController().getRenderer();
                if (blockRenderer == null)
                {
                    throw new EJApplicationException(new EJMessage("Block " + block.getProperties().getName()
                            + " has a canvas defined but no renderer. A block cannot be rendererd if no canvas has been defined."));
                }
                return blockRenderer.createComponent();
            }

            @Override
            public EJCanvasType getType()
            {
                return EJCanvasType.BLOCK;
            }
        };

        _canvases.put(canvasProperties.getName(), canvasHandler);
        _canvasesIds.add(canvasProperties.getName());
        EJInternalBlock block = _blocks.get(canvasProperties.getName());
        if (block != null)
        {
            return canvasHandler.add(block);

        }

        GridPane groupPane = new GridPane();

        if (canvasProperties.getDisplayGroupFrame())
        {
            groupPane.setPadding(new Insets(0, 5, 0, 5));
        }
        else
        {
            groupPane.setPadding(new Insets(0, 0, 0, 0));
        }

        groupPane.setUserData(canvasProperties.getName());
        if (!canvasProperties.getDisplayGroupFrame())
        {
            createGridData(canvasProperties, groupPane);
        }
        if (canvasProperties.getType() == EJCanvasType.GROUP)
        {
            int cCol = 0;
            int cRow = 0;
            for (EJCanvasProperties containedCanvas : canvasProperties.getGroupCanvasContainer().getAllCanvasProperties())
            {
                Node node = null;
                switch (containedCanvas.getType())
                {
                    case BLOCK:
                    case GROUP:
                        node = createGroupCanvas(containedCanvas, canvasController);
                        break;
                    case SPLIT:
                        node = createSplitCanvas(containedCanvas, canvasController);
                        break;
                    case STACKED:
                        node = createStackedCanvas(containedCanvas, canvasController);
                        break;
                    case TAB:
                        node = createTabCanvas(containedCanvas, canvasController);
                        break;
                    case POPUP:
                        throw new AssertionError();

                }
                if (node != null)
                {
                    if (cCol <= (canvasProperties.getNumCols() - 1))
                    {
                        groupPane.add(node, cCol, cRow);
                        cCol++;

                        if (GridPane.getColumnSpan(node) > 1)
                        {
                            cCol += (GridPane.getColumnSpan(node) - 1);
                        }
                        if (GridPane.getRowSpan(node) > 1)
                        {
                            cRow += (GridPane.getRowSpan(node) - 1);
                        }
                    }
                    else
                    {
                        cCol = 0;
                        cRow++;
                        groupPane.add(node, cCol, cRow);
                        cCol++;
                        if (GridPane.getColumnSpan(node) > 1)
                        {
                            cCol += (GridPane.getColumnSpan(node) - 1);
                        }
                        if (GridPane.getRowSpan(node) > 1)
                        {
                            cRow += (GridPane.getRowSpan(node) - 1);
                        }
                    }

                }
            }
            EJUIUtils.setConstraints(groupPane, cCol, cRow);
        }

        String frameTitle = canvasProperties.getGroupFrameTitle();
        if (canvasProperties.getDisplayGroupFrame() && frameTitle != null && frameTitle.length() > 0)
        {
            TitledPane t1 = new TitledPane(frameTitle, groupPane);
            t1.setCollapsible(false);
            t1.setAnimated(false);
            return createGridData(canvasProperties, t1);
        }
        return groupPane;
    }

    private Node createSplitCanvas(EJCanvasProperties canvasProperties, EJCanvasController canvasController)
    {

        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(canvasProperties.getSplitOrientation() == EJCanvasSplitOrientation.HORIZONTAL ? Orientation.HORIZONTAL : Orientation.VERTICAL);
        createGridData(canvasProperties, splitPane);

        if (canvasProperties.getType() == EJCanvasType.SPLIT)
        {
            List<EJCanvasProperties> items = new ArrayList<EJCanvasProperties>(canvasProperties.getSplitCanvasContainer().getAllCanvasProperties());
            int[] weights = new int[items.size()];
            double totalWeight = 0;

            for (EJCanvasProperties containedCanvas : items)
            {
                int indexOf = items.indexOf(containedCanvas);
                weights[indexOf] = (containedCanvas.getWidth()) + 1;
                weights[indexOf] = (containedCanvas.getWidth()) + 1;
                totalWeight += weights[indexOf];
                Node node = null;
                switch (containedCanvas.getType())
                {
                    case BLOCK:
                    case GROUP:
                        node = createGroupCanvas(containedCanvas, canvasController);
                        break;
                    case SPLIT:
                        node = createSplitCanvas(containedCanvas, canvasController);
                        break;
                    case STACKED:
                        node = createStackedCanvas(containedCanvas, canvasController);
                        break;
                    case TAB:
                        node = createTabCanvas(containedCanvas, canvasController);
                        break;
                    case POPUP:
                        throw new AssertionError();

                }
                if (node != null)
                {
                    // SplitPane.setResizableWithParent(node, false);
                    splitPane.getItems().add(node);

                }
            }
            if (totalWeight > 0)
            {
                double[] dps = new double[weights.length - 1];
                int runing = 0;
                for (int i = 0; i < dps.length; i++)
                {

                    dps[i] = ((double) runing + weights[i]) / totalWeight;
                    runing += runing + weights[i];

                }
                splitPane.setDividerPositions(dps);
            }
            splitPane.setStyle("-fx-background-color:transparent;");// FIXME

        }
        return splitPane;
    }

    EJFXApplicationManager getFXManager()
    {
        return (EJFXApplicationManager) _form.getFormController().getFrameworkManager().getApplicationManager();
    }

    private final class PopupCanvasHandler implements CanvasHandler
    {
        AbstractDialog           _popupDialog;

        final EJCanvasProperties canvasProperties;
        final EJCanvasController canvasController;

        public PopupCanvasHandler(EJCanvasProperties canvasProperties, EJCanvasController canvasController)
        {
            this.canvasController = canvasController;
            this.canvasProperties = canvasProperties;
            open(false);
        }

        @Override
        public Node add(EJInternalBlock block)
        {
            // ignore
            return null;
        }

        void open(boolean show)
        {
            final String name = canvasProperties.getName();
            final String pageTitle = canvasProperties.getPopupPageTitle();
            final int width = canvasProperties.getWidth();
            final int height = canvasProperties.getHeight();
            final int numCols = canvasProperties.getNumCols();

            final String button1Label = canvasProperties.getButtonOneText();
            final String button2Label = canvasProperties.getButtonTwoText();
            final String button3Label = canvasProperties.getButtonThreeText();

            final int ID_BUTTON_1 = 1;
            final int ID_BUTTON_2 = 2;
            final int ID_BUTTON_3 = 3;
            if (_popupDialog!=null)
            {
                _popupDialog = new AbstractDialog(getFXManager().getPrimaryStage())
                {
                    private static final long serialVersionUID = -4685316941898120169L;
    
                    @Override
                    public Node createBody()
                    {
                        final ScrollPane scrollComposite = new ScrollPane();
                        GridPane _mainPane = new GridPane();
                        _mainPane.setPadding(new Insets(0, 0, 0, 0));
                        int cCol = 0;
                        int cRow = 0;
                        EJCanvasPropertiesContainer popupCanvasContainer = canvasProperties.getPopupCanvasContainer();
                        Collection<EJCanvasProperties> allCanvasProperties = popupCanvasContainer.getAllCanvasProperties();
                        for (EJCanvasProperties canvasProperties : allCanvasProperties)
                        {
    
                            Node node = createCanvas(canvasProperties, canvasController);
                            if (node != null)
                            {
                                if (cCol <= (numCols - 1))
                                {
                                    _mainPane.add(node, cCol, cRow);
                                    cCol++;
    
                                    if (GridPane.getColumnSpan(node) > 1)
                                    {
                                        cCol += (GridPane.getColumnSpan(node) - 1);
                                    }
                                    if (GridPane.getRowSpan(node) > 1)
                                    {
                                        cRow += (GridPane.getRowSpan(node) - 1);
                                    }
                                }
                                else
                                {
                                    cCol = 0;
                                    cRow++;
                                    _mainPane.add(node, cCol, cRow);
                                    cCol++;
                                    if (GridPane.getColumnSpan(node) > 1)
                                    {
                                        cCol += (GridPane.getColumnSpan(node) - 1);
                                    }
                                    if (GridPane.getRowSpan(node) > 1)
                                    {
                                        cRow += (GridPane.getRowSpan(node) - 1);
                                    }
                                }
    
                            }
                        }
                        EJUIUtils.setConstraints(_mainPane, cCol, cRow);
                        scrollComposite.setContent(_mainPane);
                        scrollComposite.setPrefSize(width, height);
                        return scrollComposite;
                    }
    
                    @Override
                    protected void createButtonsForButtonBar()
                    {
                        // Add the buttons in reverse order, as they will be added
                        // from
                        // left to
                        // right
    
                        addExtraButton(button3Label, ID_BUTTON_3);
                        addExtraButton(button2Label, ID_BUTTON_2);
                        addExtraButton(button1Label, ID_BUTTON_1);
                    }
    
                    private void addExtraButton(String label, int id)
                    {
                        if (label == null || label.length() == 0)
                        {
                            return;
                        }
                        createButton(id, label);
    
                    }
    
                    @Override
                    protected void buttonPressed(int buttonId)
                    {
                        switch (buttonId)
                        {
    
                            case ID_BUTTON_1:
                            {
                                canvasController.closePopupCanvas(name, EJPopupButton.ONE);
                                break;
                            }
                            case ID_BUTTON_2:
                            {
                                canvasController.closePopupCanvas(name, EJPopupButton.TWO);
                                break;
                            }
                            case ID_BUTTON_3:
                            {
                                canvasController.closePopupCanvas(name, EJPopupButton.THREE);
                                break;
                            }
    
                            default:
                                super.buttonPressed(buttonId);
                                break;
                        }
    
                    }
                };
    
                _popupDialog.create(width + 80, height + 100);// add
                
                // dialog
                // border
                // offsets
            }
            _popupDialog.setTitle(pageTitle != null ? pageTitle : "");

            _popupDialog.getScene().setOnKeyPressed(new EventHandler<KeyEvent>()
            {
                public void handle(KeyEvent ke)
                {
                    if (ke.getCode() == KeyCode.ESCAPE)
                    {
                        if (_popupDialog != null)
                        {
                            _popupDialog.close();
                        }
                    }
                }
            });
            if(show)
            {
                _popupDialog.show();
            }
        }

        void close()
        {
            if (_popupDialog != null)
            {
                _popupDialog.close();
            }
        }

        @Override
        public EJCanvasType getType()
        {
            return EJCanvasType.POPUP;
        }
    }

    class EJTabFolder
    {
        final TabPane          folder;
        final Map<String, Tab> tabPages = new HashMap<String, Tab>();

        EJTabFolder(TabPane folder)
        {
            super();
            this.folder = folder;
        }

        public void showPage(String pageName)
        {
            Tab cTabItem = tabPages.get(pageName);
            if (cTabItem != null)
            {
                folder.getSelectionModel().select(cTabItem);
            }

        }

        void clear()
        {
            tabPages.clear();
        }

        boolean containsKey(String key)
        {
            return tabPages.containsKey(key);
        }

        Tab get(String key)
        {
            return tabPages.get(key);
        }

        Tab put(String key, Tab value)
        {
            return tabPages.put(key, value);
        }

        Tab remove(String key)
        {
            return tabPages.remove(key);
        }

        public String getActiveKey()
        {
            Tab selection = folder.getSelectionModel().getSelectedItem();
            if (selection != null)
            {
                for (String key : tabPages.keySet())
                {
                    if (selection.equals(tabPages.get(key)))
                    {
                        return key;
                    }
                }
            }
            return null;
        }
    }

    private interface CanvasHandler
    {
        EJCanvasType getType();

        Node add(EJInternalBlock block);
    }

    @Override
    public String getDisplayedStackedPage(String key)
    {
        EJFXEntireJStackedPane stackedPane = _stackedPanes.get(key);
        if (stackedPane != null)
        {
            return stackedPane.getActiveControlKey();
        }

        return null;
    }

    @Override
    public String getDisplayedTabPage(String key)
    {
        EJTabFolder tabFolder = _tabFolders.get(key);
        if (tabFolder != null)
        {
            return tabFolder.getActiveKey();
        }
        return null;
    }

}

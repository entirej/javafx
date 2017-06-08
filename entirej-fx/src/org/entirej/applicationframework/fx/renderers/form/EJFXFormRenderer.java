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

import java.io.File;
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
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;

import org.entirej.applicationframework.fx.application.EJFXApplicationManager;
import org.entirej.applicationframework.fx.application.form.containers.AbstractDialog;
import org.entirej.applicationframework.fx.layout.EJFXEntireJStackedPane;
import org.entirej.applicationframework.fx.renderers.interfaces.EJFXAppBlockRenderer;
import org.entirej.applicationframework.fx.renderers.interfaces.EJFXAppFormRenderer;
import org.entirej.applicationframework.fx.utils.EJUIUtils;
import org.entirej.applicationframework.fx.utils.EJUIUtils.GridLayoutUsage;
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
    private LinkedList<String>                  _canvasesIds       = new LinkedList<String>();
    private Map<String, CanvasHandler>          _canvases          = new HashMap<String, CanvasHandler>();
    private Map<String, EJInternalBlock>        _blocks            = new HashMap<String, EJInternalBlock>();
    private Map<String, EJTabFolder>            _tabFolders        = new HashMap<String, EJTabFolder>();
    private Map<String, String>                 _tabFoldersCache   = new HashMap<String, String>();
    private Map<String, EJFXEntireJStackedPane> _stackedPanes      = new HashMap<String, EJFXEntireJStackedPane>();
    private Map<String, String>                 _stackedPanesCache = new HashMap<String, String>();
    private Map<String, BorderPane>             _formPanes         = new HashMap<String, BorderPane>();

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
            else
            {
                _stackedPanesCache.put(canvasName, pageName);
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
            else
            {
                _tabFoldersCache.put(canvasName, pageName);
            }
        }
    }

    @Override
    public void setTabPageVisible(String canvasName, String pageName, boolean visible)
    {
        if (canvasName != null && pageName != null)
        {
            EJTabFolder tabPane = _tabFolders.get(canvasName);
            if (tabPane != null)
            {
                tabPane.setPageVisible(pageName, visible);
            }
        }
    }

    public void openEmbeddedForm(EJEmbeddedFormController formController)
    {

        if (formController == null)
        {
            throw new EJApplicationException("No embedded form controller has been passed to openEmbeddedForm");
        }

        if (formController.getCanvasName() != null)
        {
            BorderPane formCanvas = _formPanes.get(formController.getCanvasName());
            if (formCanvas != null)
            {
                formCanvas.setCenter(null);
                final EJInternalForm form = formController.getEmbeddedForm();
                EJFXAppFormRenderer renderer = ((EJFXAppFormRenderer) form.getRenderer());
                Node node = renderer.createControl();
                if (node instanceof Region)
                {
                    ((Region) node).setPadding(new Insets(0, 0, 0, 0));
                }
                formCanvas.setCenter(node);

                node.focusedProperty().addListener(new ChangeListener<Boolean>()
                {
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
                    {
                        if (newValue.booleanValue())
                        {
                            form.focusGained();
                        }
                    }
                });
            }
            else
            {
                throw new IllegalAccessError("An embedded form can only be opened on EJCanvasType.FORM");
            }
        }

    }

    public void closeEmbeddedForm(EJEmbeddedFormController formController)
    {
        if (formController == null)
        {
            throw new EJApplicationException("No embedded form controller has been passed to closeEmbeddedForm");
        }

        if (formController.getCanvasName() != null)
        {
            BorderPane formCanvas = _formPanes.get(formController.getCanvasName());
            if (formCanvas != null)
            {
                formCanvas.setCenter(null);
            }
        }

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
        GridLayoutUsage layoutUsage = EJUIUtils.newGridLayoutUsage(formProperties.getNumCols());

        for (EJCanvasProperties canvasProperties : formProperties.getCanvasContainer().getAllCanvasProperties())
        {
            Node node = createCanvas(canvasProperties, canvasController);
            if (node != null)
            {

                Integer columnSpan = GridPane.getColumnSpan(node);
                if (columnSpan > layoutUsage.getColLimit())
                {
                    columnSpan = layoutUsage.getColLimit();
                    GridPane.setColumnSpan(node, columnSpan);
                }
                layoutUsage.allocate(columnSpan, GridPane.getRowSpan(node));
                _mainPane.add(node, layoutUsage.getCol(), layoutUsage.getRow());

            }
        }
        EJUIUtils.setConstraints(_mainPane, layoutUsage.getCol(), layoutUsage.getRow());

    }

    private Node createCanvas(EJCanvasProperties canvasProperties, EJCanvasController canvasController)
    {
        switch (canvasProperties.getType())
        {
            case BLOCK:
            case GROUP:
                return createGroupCanvas(canvasProperties, canvasController);
            case FORM:
                return createFormCanvas(canvasProperties, canvasController);
            case SEPARATOR:
                return createSeparatorCanvas(canvasProperties, canvasController);

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
        GridPane.setRowSpan(node, layoutItem.canExpandVertically() ? 1 : layoutItem.getVerticalSpan());
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

    private Node createFormCanvas(EJCanvasProperties canvasProperties, EJCanvasController canvasController)
    {
        final String name = canvasProperties.getName();
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(0, 0, 0, 0));
        createGridData(canvasProperties, borderPane);
        _formPanes.put(name, borderPane);

        if (canvasProperties.getReferredFormId() != null && canvasProperties.getReferredFormId().length() > 0)
        {
            _form.openEmbeddedForm(canvasProperties.getReferredFormId(), name, null);
        }
        return borderPane;
    }

    private Node createSeparatorCanvas(EJCanvasProperties canvasProperties, EJCanvasController canvasController)
    {
        final String name = canvasProperties.getName();
        Separator separator = new Separator();
        separator.setOrientation(canvasProperties.getSplitOrientation() == EJCanvasSplitOrientation.HORIZONTAL ? Orientation.HORIZONTAL : Orientation.VERTICAL);

        switch (canvasProperties.getLineStyle())
        {
            case DASHED:
                separator.getStyleClass().add("ej-separator-dashed");
                break;
            case DOTTED:
                separator.getStyleClass().add("ej-separator-dotted");
                break;
            case DOUBLE:
                separator.getStyleClass().add("ej-separator-double");
                break;

            default:
                separator.getStyleClass().add("ej-separator");
                break;
        }

        createGridData(canvasProperties, separator);

        return separator;
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
            GridLayoutUsage layoutUsage = EJUIUtils.newGridLayoutUsage(page.getNumCols());

            for (EJCanvasProperties properties : page.getContainedCanvases().getAllCanvasProperties())
            {
                Node node = createCanvas(properties, canvasController);

                if (node != null)
                {
                    Integer columnSpan = GridPane.getColumnSpan(node);
                    if (columnSpan > layoutUsage.getColLimit())
                    {
                        columnSpan = layoutUsage.getColLimit();
                        GridPane.setColumnSpan(node, columnSpan);
                    }
                    layoutUsage.allocate(columnSpan, GridPane.getRowSpan(node));
                    pagePane.add(node, layoutUsage.getCol(), layoutUsage.getRow());

                }
            }
            EJUIUtils.setConstraints(pagePane, layoutUsage.getCol(), layoutUsage.getRow());
        }

        if (canvasProperties.getInitialStackedPageName() != null)
        {
            stackedPane.showPane(canvasProperties.getInitialStackedPageName());
        }

        if (_stackedPanesCache.containsKey(name))
        {
            stackedPane.showPane(_stackedPanesCache.get(name));
            _stackedPanesCache.remove(name);
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
        int index = 0;
        for (EJTabPageProperties page : allTabPageProperties)
        {

            Tab tabItem = new Tab();

            GridPane pagePane = new GridPane();
            pagePane.setPadding(new Insets(0, 0, 0, 0));
            GridLayoutUsage layoutUsage = EJUIUtils.newGridLayoutUsage(page.getNumCols());

            tabItem.setText((page.getPageTitle() != null && page.getPageTitle().length() > 0) ? page.getPageTitle() : page.getName());
            tabItem.setContent(pagePane);
            EJCanvasPropertiesContainer containedCanvases = page.getContainedCanvases();
            for (EJCanvasProperties pageProperties : containedCanvases.getAllCanvasProperties())
            {
                Node node = createCanvas(pageProperties, canvasController);
                if (node != null)
                {
                    Integer columnSpan = GridPane.getColumnSpan(node);
                    if (columnSpan > layoutUsage.getColLimit())
                    {
                        columnSpan = layoutUsage.getColLimit();
                        GridPane.setColumnSpan(node, columnSpan);
                    }
                    layoutUsage.allocate(columnSpan, GridPane.getRowSpan(node));
                    pagePane.add(node, layoutUsage.getCol(), layoutUsage.getRow());

                }
            }
            EJUIUtils.setConstraints(pagePane, layoutUsage.getCol(), layoutUsage.getRow());
            if (page.isVisible())
            {
                tabPane.getTabs().add(tabItem);

                if (tabPane.getSelectionModel().getSelectedItem() == null)
                {
                    tabPane.getSelectionModel().select(tabItem);
                }
            }
            else
            {
                tabItem.setUserData(index);
            }

            tabFolder.put(page.getName(), tabItem);
            tabItem.setDisable((!page.isEnabled()));

            if (_tabFoldersCache.containsKey(name))
            {
                tabFolder.showPage(_tabFoldersCache.get(name));
                _tabFoldersCache.remove(name);
            }

            index++;
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

        createGridData(canvasProperties, groupPane);

        if (canvasProperties.getType() == EJCanvasType.GROUP)
        {
            GridLayoutUsage layoutUsage = EJUIUtils.newGridLayoutUsage(canvasProperties.getNumCols());

            for (EJCanvasProperties containedCanvas : canvasProperties.getGroupCanvasContainer().getAllCanvasProperties())
            {
                Node node = null;
                switch (containedCanvas.getType())
                {
                    case BLOCK:
                    case GROUP:
                        node = createGroupCanvas(containedCanvas, canvasController);
                        break;
                    case FORM:
                        node = createFormCanvas(containedCanvas, canvasController);
                        break;
                    case SEPARATOR:
                        node = createSeparatorCanvas(canvasProperties, canvasController);
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
                    Integer columnSpan = GridPane.getColumnSpan(node);
                    if (columnSpan > layoutUsage.getColLimit())
                    {
                        columnSpan = layoutUsage.getColLimit();
                        GridPane.setColumnSpan(node, columnSpan);
                    }
                    layoutUsage.allocate(columnSpan, GridPane.getRowSpan(node));
                    groupPane.add(node, layoutUsage.getCol(), layoutUsage.getRow());

                }
            }
            EJUIUtils.setConstraints(groupPane, layoutUsage.getCol(), layoutUsage.getRow());
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
                if (containedCanvas.getType() == EJCanvasType.BLOCK && containedCanvas.getBlockProperties() != null
                        && containedCanvas.getBlockProperties().getMainScreenProperties() != null)
                {
                    weights[items.indexOf(containedCanvas)] = canvasProperties.getSplitOrientation() == EJCanvasSplitOrientation.HORIZONTAL ? containedCanvas
                            .getBlockProperties().getMainScreenProperties().getWidth() + 1 : containedCanvas.getBlockProperties().getMainScreenProperties()
                            .getHeight() + 1;
                }
                else
                    weights[indexOf] = canvasProperties.getSplitOrientation() == EJCanvasSplitOrientation.HORIZONTAL ? (containedCanvas.getWidth()) + 1
                            : containedCanvas.getHeight() + 1;

                totalWeight += weights[indexOf];
                Node node = null;
                switch (containedCanvas.getType())
                {
                    case BLOCK:
                    case GROUP:
                        node = createGroupCanvas(containedCanvas, canvasController);
                        break;
                    case FORM:
                        node = createFormCanvas(containedCanvas, canvasController);
                        break;
                    case SEPARATOR:
                        node = createSeparatorCanvas(canvasProperties, canvasController);
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

        boolean                  popupButton1        = true;
        boolean                  popupButton2        = true;
        boolean                  popupButton3        = true;
        boolean                  popupButtonVisible1 = true;
        boolean                  popupButtonVisible2 = true;
        boolean                  popupButtonVisible3 = true;
        String                   button1Label;
        String                   button2Label;
        String                   button3Label;

        final int                ID_BUTTON_1         = 1;
        final int                ID_BUTTON_2         = 2;
        final int                ID_BUTTON_3         = 3;

        public PopupCanvasHandler(EJCanvasProperties canvasProperties, EJCanvasController canvasController)
        {
            this.canvasController = canvasController;
            this.canvasProperties = canvasProperties;
            button1Label = canvasProperties.getButtonOneText();
            button2Label = canvasProperties.getButtonTwoText();
            button3Label = canvasProperties.getButtonThreeText();
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

            if (_popupDialog == null)
            {
                _popupDialog = new AbstractDialog(getFXManager().getPrimaryStage())
                {

                    @Override
                    public Node createBody()
                    {

                        GridPane _mainPane = new GridPane();
                        _mainPane.setPadding(new Insets(0, 0, 0, 0));
                        GridLayoutUsage layoutUsage = EJUIUtils.newGridLayoutUsage(numCols);

                        EJCanvasPropertiesContainer popupCanvasContainer = canvasProperties.getPopupCanvasContainer();
                        Collection<EJCanvasProperties> allCanvasProperties = popupCanvasContainer.getAllCanvasProperties();
                        for (EJCanvasProperties canvasProperties : allCanvasProperties)
                        {

                            Node node = createCanvas(canvasProperties, canvasController);
                            if (node != null)
                            {
                                Integer columnSpan = GridPane.getColumnSpan(node);
                                if (columnSpan > layoutUsage.getColLimit())
                                {
                                    columnSpan = layoutUsage.getColLimit();
                                    GridPane.setColumnSpan(node, columnSpan);
                                }
                                layoutUsage.allocate(columnSpan, GridPane.getRowSpan(node));
                                _mainPane.add(node, layoutUsage.getCol(), layoutUsage.getRow());

                            }
                        }
                        EJUIUtils.setConstraints(_mainPane, layoutUsage.getCol(), layoutUsage.getRow());

                        final ScrollPane scrollComposite = new ScrollPane();

                        BorderPane borderPane = new BorderPane();
                        borderPane.setPadding(new Insets(5, 5, 5, 5));
                        Node node = _mainPane;
                        if (node instanceof Region)
                        {
                            ((Region) node).setPadding(new Insets(0, 0, 0, 0));
                            ((Region) node).setMinSize(width, height);
                        }
                        if (node instanceof Control)
                        {
                            ((Control) node).setMinSize(width, height);
                        }
                        borderPane.setCenter(node);

                        scrollComposite.setContent(borderPane);
                        scrollComposite.setFitToHeight(true);
                        scrollComposite.setFitToWidth(true);
                        return scrollComposite;
                    }

                    @Override
                    protected void createButtonsForButtonBar()
                    {
                        // Add the buttons in reverse order, as they will be
                        // added
                        // from
                        // left to
                        // right

                        addExtraButton(button3Label, ID_BUTTON_3, canvasProperties.getDefaultPopupButton() == EJPopupButton.THREE);
                        addExtraButton(button2Label, ID_BUTTON_2, canvasProperties.getDefaultPopupButton() == EJPopupButton.TWO);
                        addExtraButton(button1Label, ID_BUTTON_1, canvasProperties.getDefaultPopupButton() == EJPopupButton.ONE);

                        setButtonEnable(ID_BUTTON_1, popupButton1);
                        setButtonEnable(ID_BUTTON_2, popupButton2);
                        setButtonEnable(ID_BUTTON_3, popupButton3);

                        setButtonVisible(ID_BUTTON_1, popupButtonVisible1);
                        setButtonVisible(ID_BUTTON_2, popupButtonVisible2);
                        setButtonVisible(ID_BUTTON_3, popupButtonVisible3);

                    }

                    private void addExtraButton(String label, int id, boolean defaultButton)
                    {
                        if (label == null || label.length() == 0)
                        {
                            return;
                        }
                        createButton(id, label, defaultButton);

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
            if (show)
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

        public void enableButton(EJPopupButton button, boolean state)
        {
            switch (button)
            {
                case ONE:
                    popupButton1 = state;
                    if (_popupDialog != null)
                        _popupDialog.setButtonEnable(ID_BUTTON_1, popupButton1);

                    break;
                case TWO:
                    popupButton2 = state;
                    if (_popupDialog != null)
                        _popupDialog.setButtonEnable(ID_BUTTON_2, popupButton2);
                    break;
                case THREE:
                    popupButton3 = state;
                    if (_popupDialog != null)
                        _popupDialog.setButtonEnable(ID_BUTTON_3, popupButton3);
                    break;

                default:
                    break;
            }

        }

        public void setButtonVisible(EJPopupButton button, boolean state)
        {
            switch (button)
            {
                case ONE:
                    popupButtonVisible1 = state;
                    if (_popupDialog != null)
                        _popupDialog.setButtonVisible(ID_BUTTON_1, popupButtonVisible1);

                    break;
                case TWO:
                    popupButtonVisible2 = state;
                    if (_popupDialog != null)
                        _popupDialog.setButtonVisible(ID_BUTTON_2, popupButtonVisible2);
                    break;
                case THREE:
                    popupButtonVisible3 = state;
                    if (_popupDialog != null)
                        _popupDialog.setButtonVisible(ID_BUTTON_3, popupButtonVisible3);
                    break;

                default:
                    break;
            }

        }

        public void setButtonLabel(EJPopupButton button, String label)
        {
            switch (button)
            {
                case ONE:
                    button1Label = label;
                    if (_popupDialog != null)
                        _popupDialog.setButtonLabel(ID_BUTTON_1, button1Label);

                    break;
                case TWO:
                    button2Label = label;
                    if (_popupDialog != null)
                        _popupDialog.setButtonLabel(ID_BUTTON_2, button2Label);
                    break;
                case THREE:
                    button3Label = label;
                    if (_popupDialog != null)
                        _popupDialog.setButtonLabel(ID_BUTTON_3, button3Label);
                    break;

                default:
                    break;
            }

        }

        public boolean isButtonEnabled(EJPopupButton button)
        {
            switch (button)
            {
                case ONE:
                    return popupButton1;
                case TWO:
                    return popupButton2;
                case THREE:
                    return popupButton3;

                default:
                    break;
            }
            return false;
        }

        public String getButtonLabel(EJPopupButton button)
        {
            switch (button)
            {
                case ONE:
                    return button1Label;
                case TWO:
                    return button2Label;
                case THREE:
                    return button3Label;

                default:
                    break;
            }
            return null;
        }

        public boolean isButtonVisible(EJPopupButton button)
        {
            switch (button)
            {
                case ONE:
                    return popupButtonVisible1;
                case TWO:
                    return popupButtonVisible2;
                case THREE:
                    return popupButtonVisible3;

                default:
                    break;
            }
            return false;
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

        public void setPageVisible(String pageName, boolean visible)
        {
            Tab cTabItem = tabPages.get(pageName);
            if (cTabItem != null)
            {
                if (visible)
                {
                    if (!folder.getTabs().contains(cTabItem))
                    {
                        int index = (int) cTabItem.getUserData();
                        if (folder.getTabs().size() < index)
                        {
                            folder.getTabs().add(index, cTabItem);
                        }
                        else
                        {
                            folder.getTabs().add(cTabItem);
                        }
                    }

                }
                else
                {
                    cTabItem.setUserData(folder.getTabs().indexOf(cTabItem));
                    folder.getTabs().remove(cTabItem);
                }
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

    @Override
    public void clearCanvasMessages(String canvasName)
    {
        throw new IllegalStateException("not support yet");

    }

    @Override
    public void setCanvasMessages(String canvasName, Collection<EJMessage> messages)
    {
        throw new IllegalStateException("not support yet");

    }

    @Override
    public void setButtonEnabled(String canvasName, EJPopupButton button, boolean state)
    {
        CanvasHandler canvasHandler = _canvases.get(canvasName);
        if (canvasHandler instanceof PopupCanvasHandler)
        {
            PopupCanvasHandler popupCanvasHandler = (PopupCanvasHandler) canvasHandler;
            popupCanvasHandler.enableButton(button, state);
        }

    }

    @Override
    public boolean isButtonEnabled(String canvasName, EJPopupButton button)
    {
        CanvasHandler canvasHandler = _canvases.get(canvasName);
        if (canvasHandler instanceof PopupCanvasHandler)
        {
            PopupCanvasHandler popupCanvasHandler = (PopupCanvasHandler) canvasHandler;
            return popupCanvasHandler.isButtonEnabled(button);
        }
        return false;
    }

    @Override
    public void setButtonVisible(String canvasName, EJPopupButton button, boolean state)
    {
        CanvasHandler canvasHandler = _canvases.get(canvasName);
        if (canvasHandler instanceof PopupCanvasHandler)
        {
            PopupCanvasHandler popupCanvasHandler = (PopupCanvasHandler) canvasHandler;
            popupCanvasHandler.setButtonVisible(button, state);
        }

    }

    @Override
    public boolean isButtonVisible(String canvasName, EJPopupButton button)
    {
        CanvasHandler canvasHandler = _canvases.get(canvasName);
        if (canvasHandler instanceof PopupCanvasHandler)
        {
            PopupCanvasHandler popupCanvasHandler = (PopupCanvasHandler) canvasHandler;
            return popupCanvasHandler.isButtonVisible(button);
        }
        return false;
    }

    @Override
    public void setButtonLabel(String canvasName, EJPopupButton button, String label)
    {
        CanvasHandler canvasHandler = _canvases.get(canvasName);
        if (canvasHandler instanceof PopupCanvasHandler)
        {
            PopupCanvasHandler popupCanvasHandler = (PopupCanvasHandler) canvasHandler;
            popupCanvasHandler.setButtonLabel(button, label);
        }
    }

    @Override
    public String getButtonLabel(String canvasName, EJPopupButton button)
    {
        CanvasHandler canvasHandler = _canvases.get(canvasName);
        if (canvasHandler instanceof PopupCanvasHandler)
        {
            PopupCanvasHandler popupCanvasHandler = (PopupCanvasHandler) canvasHandler;
            return popupCanvasHandler.getButtonLabel(button);
        }

        return null;
    }

   

    @Override
    public void setTabPageBadge(String canvasName, String tabPageName, String badge)
    {
        throw new IllegalStateException("not supported");
        
    }

    @Override
    public void showDrawerPage(String drawerCanvasName, String drawerPageName)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setDrawerPageVisible(String drawerCanvasName, String drawerPageName, boolean visible)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getDisplayedDrawerPage(String drawerCanvasName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setDrawerPageBadge(String canvasName, String drawerPageName, String badge)
    {
        // TODO Auto-generated method stub
        
    }

}

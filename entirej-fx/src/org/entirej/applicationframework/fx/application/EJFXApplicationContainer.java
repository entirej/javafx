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
package org.entirej.applicationframework.fx.application;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import org.entirej.applicationframework.fx.application.form.containers.AbstractDialog;
import org.entirej.applicationframework.fx.application.form.containers.EJFXFormPopUp;
import org.entirej.applicationframework.fx.application.form.containers.EJFXSingleFormContainer;
import org.entirej.applicationframework.fx.application.interfaces.EJFXAppComponentRenderer;
import org.entirej.applicationframework.fx.application.interfaces.EJFXApplicationComponent;
import org.entirej.applicationframework.fx.application.interfaces.EJFXFormChosenEvent;
import org.entirej.applicationframework.fx.application.interfaces.EJFXFormChosenListener;
import org.entirej.applicationframework.fx.application.interfaces.EJFXFormClosedListener;
import org.entirej.applicationframework.fx.application.interfaces.EJFXFormContainer;
import org.entirej.applicationframework.fx.application.interfaces.EJFXFormOpenedListener;
import org.entirej.applicationframework.fx.application.interfaces.EJFXFormSelectedListener;
import org.entirej.applicationframework.fx.renderers.form.EJFXFormRenderer;
import org.entirej.applicationframework.fx.utils.EJUIUtils;
import org.entirej.applicationframework.fx.utils.EJUIUtils.GridLayoutUsage;
import org.entirej.framework.core.data.controllers.EJPopupFormController;
import org.entirej.framework.core.internal.EJInternalForm;
import org.entirej.framework.core.properties.EJCoreLayoutContainer;
import org.entirej.framework.core.properties.EJCoreLayoutItem;
import org.entirej.framework.core.properties.EJCoreLayoutItem.GRAB;
import org.entirej.framework.core.properties.EJCoreLayoutItem.LayoutComponent;
import org.entirej.framework.core.properties.EJCoreLayoutItem.LayoutGroup;
import org.entirej.framework.core.properties.EJCoreLayoutItem.LayoutSpace;
import org.entirej.framework.core.properties.EJCoreLayoutItem.SplitGroup;
import org.entirej.framework.core.properties.EJCoreLayoutItem.SplitGroup.ORIENTATION;
import org.entirej.framework.core.properties.EJCoreLayoutItem.TabGroup;
import org.entirej.framework.core.renderers.interfaces.EJApplicationComponentRenderer;
import org.entirej.framework.core.renderers.registry.EJRendererFactory;

public class EJFXApplicationContainer implements EJFXFormOpenedListener, EJFXFormClosedListener, EJFXFormSelectedListener, EJFXFormChosenListener
{

    private List<EJFXApplicationComponent> _addedComponents;

    private EJFXFormContainer              _formContainer;
    private List<EJFXSingleFormContainer>  _singleFormContainers = new ArrayList<EJFXSingleFormContainer>();
    private EJFXApplicationManager         _applicationManager;
    private final EJCoreLayoutContainer    _layoutContainer;

    private BorderPane                     _mainPane;

    public EJFXApplicationContainer(EJCoreLayoutContainer layoutContainer)
    {
        _layoutContainer = layoutContainer;
        _addedComponents = new ArrayList<EJFXApplicationComponent>();

    }

    public EJFXFormContainer getFormContainer()
    {
        return _formContainer;
    }

   public void closeALlForms()
    {
        
        for (EJFXSingleFormContainer container : _singleFormContainers)
        {
            container.getForm().close();
        }
        Collection<EJInternalForm> allForms = _formContainer.getAllForms();
        if(allForms!=null)
            for (EJInternalForm ejInternalForm : allForms)
            {
                ejInternalForm.close();
            }
    }
    
    void buildApplication(EJFXApplicationManager applicationManager, Stage primaryStage)
    {

        _applicationManager = applicationManager;
        _mainPane = (BorderPane) primaryStage.getScene().getRoot();
        ;
        // FIXME _mainPane.setData(CUSTOM_VARIANT, "applayout");

        buildApplicationContainer();

        // create dummy form container
        if (_formContainer == null)
        {
            _formContainer = new EJFXFormContainer()
            {

                EJFXFormPopUp  _formPopup;
                AbstractDialog _popupDialog;

                @Override
                public EJInternalForm switchToForm(String key)
                {
                    // ignore
                    return null;
                }
                
                @Override
                public void updateFormTitle(EJInternalForm form)
                {
                    // ignore
                    
                }

                @Override
                public void switchToForm(EJInternalForm from)
                {
                    // TODO Auto-generated method stub
                    
                }
                
                
                
                @Override
                public void removeFormSelectedListener(EJFXFormSelectedListener selectionListener)
                {
                    // ignore
                }

                @Override
                public void popupFormClosed()
                {
                    if (_formPopup != null)
                    {
                        _formPopup.close();
                        _formPopup = null;
                    }

                }

                @Override
                public void openPopupForm(EJPopupFormController popupController)
                {
                    _formPopup = new EJFXFormPopUp(_applicationManager.getPrimaryStage(), popupController);

                    _formPopup.showForm();

                }

                @Override
                public Collection<EJInternalForm> getAllForms()
                {
                    // ignore
                    return null;
                }

                @Override
                public EJInternalForm getActiveForm()
                {
                    // ignore
                    return null;
                }

                @Override
                public boolean containsForm(String formName)
                {
                    // ignore
                    return false;
                }

                @Override
                public void closeForm(EJInternalForm form)
                {
                    if (_popupDialog != null)
                    {
                        _popupDialog.close();
                        _popupDialog = null;
                    }

                }

                @Override
                public void addFormSelectedListener(EJFXFormSelectedListener selectionListener)
                {
                    // ignore

                }

                @Override
                public EJInternalForm addForm(final EJInternalForm form)
                {
                    final int height = form.getProperties().getFormHeight();
                    final int width = form.getProperties().getFormWidth();

                    final EJFXFormRenderer formRenderer = ((EJFXFormRenderer) form.getRenderer());
                    _popupDialog = new AbstractDialog(_applicationManager.getPrimaryStage())
                    {

                        @Override
                        public Node createBody()
                        {
                            final ScrollPane scrollComposite = new ScrollPane();

                            BorderPane borderPane = new BorderPane();
                            borderPane.setPadding(new Insets(5, 5, 5, 5));
                            Node node = formRenderer.createControl();
                            if (node instanceof Region)
                            {
                                ((Region) node).setPadding(new Insets(0, 0, 0, 0));
                                ((Region) node).setMinSize(form.getProperties().getFormWidth(), form.getProperties().getFormHeight());
                            }
                            if (node instanceof Control)
                            {
                                ((Control) node).setMinSize(form.getProperties().getFormWidth(), form.getProperties().getFormHeight());
                            }
                            borderPane.setCenter(node);

                            scrollComposite.setContent(borderPane);
                            scrollComposite.setFitToHeight(true);
                            scrollComposite.setFitToWidth(true);
                            return scrollComposite;
                        }

                    };
                    _popupDialog.create(width + 80, height + 100);// add padding
                    _popupDialog.setTitle(form.getProperties().getTitle());
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
                    _popupDialog.show();
                    return form;
                }
            };
        }
    }

    /**
     * Returns the currently active form
     * 
     * @return The currently active form or <code>null</code> if there is
     *         currently no active form
     */
    public EJInternalForm getActiveForm()
    {
        return _formContainer.getActiveForm();
    }

    /**
     * Returns the amount of forms currently opened and stored within the form
     * container
     * 
     * @return The amount of forms currently opened
     */
    public int getOpenFormCount()
    {
        return _formContainer.getAllForms().size();
    }

    public Collection<EJInternalForm> getOpenForms()
    {
        return new ArrayList<EJInternalForm>(_formContainer.getAllForms());
    }
    /**
     * Instructs the form container to close the given form
     * 
     * @param form
     *            The form to close
     */
    public void remove(EJInternalForm form)
    {
        if (_formContainer != null)
        {
            _formContainer.closeForm(form);
        }

        // Inform the listeners that the form has been closed
        fireFormClosed(form);
    }
    
    
    public void updateFormTitle(EJInternalForm form)
    {
        if (_formContainer != null)
        {
            _formContainer.updateFormTitle(form);
        }
    }

    /**
     * Opens a new form and adds it to the FormContainer
     * <p>
     * If the form passed is <code>null</code> or not
     * {@link EJSwingFormContainer} has been implemented then this method will
     * do nothing
     * 
     * @param form
     *            The form to be opened and added to the
     *            {@link EJSwingFormContainer}
     */
    public void add(EJInternalForm form)
    {
        if (form == null)
        {
            return;
        }

        if (_formContainer != null)
        {
            EJInternalForm addForm = _formContainer.addForm(form);
            // Inform the listeners that the form was opened
            fireFormOpened(addForm);
        }
    }

    public boolean isFormOpened(String formName)
    {

        return getForm(formName) != null;
    }
    public boolean isFormOpened(EJInternalForm form)
    {
        
        return getForm(form) != null;
    }

    private void buildApplicationContainer()
    {

        GridPane gridPane = new GridPane();
        _mainPane.setCenter(gridPane);
        List<EJCoreLayoutItem> items = _layoutContainer.getItems();
        GridLayoutUsage layoutUsage = EJUIUtils.newGridLayoutUsage(_layoutContainer.getColumns() ) ;
        for (EJCoreLayoutItem item : items)
        {
            Node node = null;
            switch (item.getType())
            {
                case GROUP:
                    node = createGroupLayout((LayoutGroup) item);
                    break;
                case SPACE:
                    node = createSpace((LayoutSpace) item);
                    break;
                case COMPONENT:
                    node = createComponent((LayoutComponent) item);
                    break;
                case SPLIT:
                    node = createSplitLayout((SplitGroup) item);
                    break;
                case TAB:
                    node = createTabLayout((TabGroup) item);
                    break;
            }
            if (node != null)
            {
                Integer columnSpan = GridPane.getColumnSpan(node);
                if(columnSpan>layoutUsage.getColLimit())
                {
                    columnSpan = layoutUsage.getColLimit();
                    GridPane.setColumnSpan(node, columnSpan);
                }
                layoutUsage.allocate(columnSpan, GridPane.getRowSpan(node));
                
                gridPane.add(node, layoutUsage.getCol(), layoutUsage.getRow());
            }
        }

        EJUIUtils.setConstraints(gridPane, layoutUsage.getCol(), layoutUsage.getRow());
        _mainPane.layout();
        if (_formContainer != null)
            for (EJFXApplicationComponent applicationComponent : _addedComponents)
            {
                if (applicationComponent instanceof EJFXFormSelectedListener)
                {
                    _formContainer.addFormSelectedListener(applicationComponent);
                }
            }

        for (EJFXSingleFormContainer singleFormContainer : _singleFormContainers)
        {
            if (singleFormContainer.getForm() != null)
                fireFormOpened(singleFormContainer.getForm());
        }
    }

    private Node createGroupLayout(EJCoreLayoutItem.LayoutGroup group)
    {

        // FIXME layoutBody.setData(CUSTOM_VARIANT, "applayout");
        List<EJCoreLayoutItem> items = group.getItems();
        if (items.size() > 0)
        {
            GridPane gridPane = new GridPane();
            GridLayoutUsage layoutUsage = EJUIUtils.newGridLayoutUsage(group.getColumns() ) ;

            if (group.isHideMargin())
            {
                gridPane.setPadding(new Insets(0, 0, 0, 0));
            }
            else
            {
                gridPane.setPadding(new Insets(5, 5, 5, 5));
            }

            for (EJCoreLayoutItem item : items)
            {
                Node node = null;
                switch (item.getType())
                {
                    case GROUP:
                        node = createGroupLayout((LayoutGroup) item);
                        break;
                    case SPACE:
                        node = createSpace((LayoutSpace) item);
                        break;
                    case COMPONENT:
                        node = createComponent((LayoutComponent) item);
                        break;
                    case SPLIT:
                        node = createSplitLayout((SplitGroup) item);
                        break;
                    case TAB:
                        node = createTabLayout((TabGroup) item);
                        break;
                }
                if (node != null)
                {
                    Integer columnSpan = GridPane.getColumnSpan(node);
                    if(columnSpan>layoutUsage.getColLimit())
                    {
                        columnSpan = layoutUsage.getColLimit();
                        GridPane.setColumnSpan(node, columnSpan);
                    }
                    layoutUsage.allocate(columnSpan, GridPane.getRowSpan(node));
                    gridPane.add(node, layoutUsage.getCol(), layoutUsage.getRow());

                }
            }
            EJUIUtils.setConstraints(gridPane, layoutUsage.getCol(), layoutUsage.getRow());
            return createGridData(group, gridPane);
        }
        else
        {
            Label label = new Label(group.getTitle());

            return createGridData(group, label);

        }
    }

    private Node createGridData(EJCoreLayoutItem layoutItem, Node node)
    {

        int minHeight = layoutItem.getMinHeight();
        int minWidth = layoutItem.getMinWidth();
        int hintHeight = layoutItem.getHintHeight();
        if (hintHeight == 0)
        {
            hintHeight = 1;
        }
        int hintWidth = layoutItem.getHintWidth();
        if (hintWidth == 0)
        {
            hintWidth = 1;
        }
        if (node instanceof Control)
        {
            ((Control) node).setMinHeight(minHeight);
            ((Control) node).setMinWidth(minWidth);
            if (hintHeight > 0)
                ((Control) node).setPrefHeight(hintHeight);

            if (hintWidth > 0)
                ((Control) node).setPrefWidth(hintWidth);
        }
        else if (node instanceof Region)
        {
            ((Region) node).setMinHeight(minHeight);
            ((Region) node).setMinWidth(minWidth);
            if (hintHeight > 0)
                ((Region) node).setPrefHeight(hintHeight);

            if (hintWidth > 0)
                ((Region) node).setPrefWidth(hintWidth);
        }

        GridPane.setColumnSpan(node, layoutItem.getHorizontalSpan());
        GridPane.setRowSpan(node,(layoutItem.getGrab()==GRAB.BOTH|| layoutItem.getGrab()==GRAB.VERTICAL)?1: layoutItem.getVerticalSpan());

        switch (layoutItem.getGrab())
        {
            case BOTH:
                GridPane.setVgrow(node, Priority.ALWAYS);
                GridPane.setHgrow(node, Priority.ALWAYS);
                break;
            case HORIZONTAL:
                GridPane.setHgrow(node, Priority.ALWAYS);
                GridPane.setVgrow(node, Priority.NEVER);
                break;
            case VERTICAL:
                GridPane.setVgrow(node, Priority.ALWAYS);
                GridPane.setHgrow(node, Priority.NEVER);
                break;
            case NONE:
                break;

        }
        if (node instanceof Control)
        {
            switch (layoutItem.getFill())
            {
                case BOTH:

                    ((Control) node).setMaxWidth(Double.MAX_VALUE);
                    ((Control) node).setMaxHeight(Double.MAX_VALUE);
                    break;
                case VERTICAL:
                    ((Control) node).setMaxHeight(Double.MAX_VALUE);
                    break;
                case HORIZONTAL:

                    ((Control) node).setMaxWidth(Double.MAX_VALUE);
                    break;
                case NONE:
                    break;
            }
        }
        else if (node instanceof Region)
        {
            switch (layoutItem.getFill())
            {
                case BOTH:

                    ((Region) node).setMaxWidth(Double.MAX_VALUE);
                    ((Region) node).setMaxHeight(Double.MAX_VALUE);
                    break;
                case VERTICAL:
                    ((Region) node).setMaxHeight(Double.MAX_VALUE);
                    break;
                case HORIZONTAL:

                    ((Region) node).setMaxWidth(Double.MAX_VALUE);
                    break;
                case NONE:
                    break;
            }
        }

        return node;

    }

    private Node createSpace(EJCoreLayoutItem.LayoutSpace space)
    {
        Label label = new Label("");

        return createGridData(space, label);
    }

    private Node createTabLayout(EJCoreLayoutItem.TabGroup group)
    {

        TabPane tabPane = new TabPane();
        switch (group.getOrientation())
        {
            case BOTTOM:
                tabPane.setSide(Side.BOTTOM);
                break;
            case TOP:
                tabPane.setSide(Side.TOP);
                break;

            default:
                break;
        }
        tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        List<EJCoreLayoutItem> items = group.getItems();

        for (EJCoreLayoutItem item : items)
        {
            Tab tab = new Tab();

            GridPane gridPane = new GridPane();
            tab.setContent(gridPane);

            // FIXME composite.setData(CUSTOM_VARIANT, "applayout");

            tab.setText(item.getTitle() != null ? item.getTitle() : item.getName());
            
            tabPane.getTabs().add(tab);
            Node node = null;
            switch (item.getType())
            {
                case GROUP:
                    node = createGroupLayout((LayoutGroup) item);
                    break;
                case SPACE:
                    node = createSpace((LayoutSpace) item);
                    break;
                case COMPONENT:
                    node = createComponent((LayoutComponent) item);
                    break;
                case SPLIT:
                    node = createSplitLayout((SplitGroup) item);
                    break;
                case TAB:
                    node = createTabLayout((TabGroup) item);
                    break;
            }
            if (node != null)
            {

                gridPane.add(node, 0, 0);

                ColumnConstraints column1 = new ColumnConstraints();
                column1.setHgrow(Priority.ALWAYS);

            }
        }

        return createGridData(group, tabPane);

    }

    private Node createComponent(EJCoreLayoutItem.LayoutComponent component)
    {
        try
        {
            EJApplicationComponentRenderer applicationComponentRenderer = EJRendererFactory.getInstance().getApplicationComponentRenderer(
                    component.getRenderer());
            if (applicationComponentRenderer instanceof EJFXFormContainer)
            {
                if (_formContainer != null)
                {
                    throw new IllegalStateException("Multiple EJFXFormContainer setup in layout");
                }
                _formContainer = (EJFXFormContainer) applicationComponentRenderer;
            }
            if (applicationComponentRenderer instanceof EJFXSingleFormContainer)
            {

                _singleFormContainers.add((EJFXSingleFormContainer) applicationComponentRenderer);
            }
            if (applicationComponentRenderer instanceof EJFXApplicationComponent)
            {
                _addedComponents.add((EJFXApplicationComponent) applicationComponentRenderer);
            }

            EJFXAppComponentRenderer renderer = (EJFXAppComponentRenderer) applicationComponentRenderer;
            return createGridData(component, renderer.createContainer(_applicationManager, component.getRendereProperties()));
        }
        catch (Exception e)
        {
            _applicationManager.getApplicationMessenger().handleException(e, true);
        }

        // fail over
        Label label = new Label(String.format("<%s>",
                (component.getRenderer() == null || component.getRenderer().length() == 0) ? "<component>" : component.getRenderer()));

        return createGridData(component, label);

    }

    private Node createSplitLayout(EJCoreLayoutItem.SplitGroup group)
    {

        List<EJCoreLayoutItem> items = group.getItems();
        if (items.size() > 0)
        {

            SplitPane splitPane = new SplitPane();
            splitPane.setOrientation(group.getOrientation() == ORIENTATION.HORIZONTAL ? Orientation.HORIZONTAL : Orientation.VERTICAL);
            int[] weights = new int[items.size()];
            double totalWeight = 0;
            for (EJCoreLayoutItem item : items)
            {
                weights[items.indexOf(item)] = (item.getHintWidth()) + 1;
                totalWeight += weights[items.indexOf(item)];
                Node node = null;
                switch (item.getType())
                {
                    case GROUP:
                        node = createGroupLayout((LayoutGroup) item);
                        break;
                    case SPACE:
                        node = createSpace((LayoutSpace) item);
                        break;
                    case COMPONENT:
                        node = createComponent((LayoutComponent) item);
                        break;
                    case SPLIT:
                        node = createSplitLayout((SplitGroup) item);
                        break;
                    case TAB:
                        node = createTabLayout((TabGroup) item);
                        break;
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

            return createGridData(group, splitPane);
        }
        else
        {
            Label label = new Label("");

            return createGridData(group, label);

        }

    }

    public void formChosen(EJFXFormChosenEvent event)
    {
        EJInternalForm form = _applicationManager.getFrameworkManager().createInternalForm(event.getChosenFormName(), null);
        if (form != null)
        {
            add(form);
        }
    }

    public void fireFormClosed(EJInternalForm closedForm)
    {
        for (EJFXApplicationComponent component : _addedComponents)
        {
            component.fireFormClosed(closedForm);
        }
    }

    public void fireFormOpened(EJInternalForm openedForm)
    {
        for (EJFXApplicationComponent component : _addedComponents)
        {
            component.fireFormOpened(openedForm);
        }
    }

    public void fireFormSelected(EJInternalForm selectedForm)
    {
        for (EJFXApplicationComponent component : _addedComponents)
        {
            component.fireFormSelected(selectedForm);
        }
    }

    public EJInternalForm getForm(String formName)
    {

        for (EJFXSingleFormContainer singleFormContainer : _singleFormContainers)
        {
            if (singleFormContainer.getForm() != null && formName.equals(singleFormContainer.getForm().getProperties().getName()))
            {
                return singleFormContainer.getForm();
            }
        }

        for (EJInternalForm form : getFormContainer().getAllForms())
        {
            if (formName.equals(form.getProperties().getName()))
            {
                return form;
            }
        }

        return null;
    }
    public EJInternalForm getForm(EJInternalForm form)
    {
        
        for (EJFXSingleFormContainer singleFormContainer : _singleFormContainers)
        {
            if (singleFormContainer.getForm() != null && form.equals(singleFormContainer.getForm()))
            {
                return singleFormContainer.getForm();
            }
        }
        
        for (EJInternalForm aform : getFormContainer().getAllForms())
        {
            if (form.equals(aform))
            {
                return form;
            }
        }
        
        return null;
    }

}

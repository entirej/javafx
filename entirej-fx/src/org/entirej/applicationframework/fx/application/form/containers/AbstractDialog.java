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
package org.entirej.applicationframework.fx.application.form.containers;

import java.util.HashMap;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SceneBuilder;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

public abstract class AbstractDialog extends Stage
{
    private Window               parent;
    private int                  selectedButtonId = -1;
    protected Scene              scene;
    protected BorderPane         borderPanel;
    protected HBox               buttonsPanel;
    private Map<Integer, Button> buttons          = new HashMap<>();

    public AbstractDialog(final Window parent)
    {

        this.parent = parent;
        if (parent instanceof Stage)
        {

            getIcons().addAll(((Stage) parent).getIcons());
        }
        setResizable(true);

        initStyle(StageStyle.DECORATED);
        initModality(Modality.APPLICATION_MODAL);

    }

    public void setOwner(Window owner)
    {
        if (owner != null)
        {
            initOwner(owner);

            borderPanel.setMaxWidth(owner.getWidth());
            borderPanel.setMaxHeight(owner.getHeight());
        }
    }

    public void centreLocation()
    {
        centerOnScreen();
    }

    public abstract Node createBody();

    protected void createDialogArea()
    {

        Node header = createHeader();
        if (header != null)
        {
            // BorderPane.setMargin(buttonsPanel, new Insets(0, 0, 5, 0));
            borderPanel.setTop(header);
        }

        borderPanel.setCenter(createBody());
        createButtonsForButtonBar();

    }

    protected void createButtonsForButtonBar()
    {
        // empty
    }

    protected Node createHeader()
    {
        return null;
    }

    protected Button createButton(final int id, String label)
    {
        return createButton(id, label, false);
    }
    
    protected Button createButton(final int id, String label,boolean defaultButton)
    {
        if (buttonsPanel == null)
        {
            buttonsPanel = new HBox();
            buttonsPanel.setSpacing(10);
            buttonsPanel.setAlignment(Pos.BOTTOM_RIGHT);
            buttonsPanel.setPadding(new Insets(5));
            BorderPane.setMargin(buttonsPanel, new Insets(0, 0, 5, 0));
            borderPanel.setBottom(buttonsPanel);
        }
        Button button = new Button(label);
        button.setMinWidth(60);
        button.setOnAction(new EventHandler<ActionEvent>()
        {

            public void handle(ActionEvent t)
            {
                buttonPressed(id);
            }
        });
        if(defaultButton)
        {
            button.setDefaultButton(defaultButton); 
        }
        buttons.put(id, button);
        buttonsPanel.getChildren().add(button);
        return button;
    }

    protected void buttonPressed(final int buttonId)
    {
        selectedButtonId = buttonId;
    }

    public void setButtonEnable(final int buttonId, boolean enabled)
    {
        Button button = getButton(buttonId);
        if(button!=null)
        {
            button.setDisable(!enabled);
        }
       
    }

    protected Button getButton(int buttonId)
    {
        return buttons.get(buttonId);
    }

    public void validate()
    {

    }

    public void canceled()
    {

    }

    public void create(int width, int height)
    {
        borderPanel = new BorderPane();
        setOwner(parent);
        scene = SceneBuilder.create().root(borderPanel).width(width).height(height).build();
        if (parent != null && parent.getScene() != null)
            scene.getStylesheets().addAll(parent.getScene().getStylesheets());
        setScene(scene);
        createDialogArea();
        setOnCloseRequest(new EventHandler<WindowEvent>()
        {

            @Override
            public void handle(WindowEvent arg0)
            {
                if (selectedButtonId == -1)
                {
                    canceled();
                }
            }
        });

    }

}

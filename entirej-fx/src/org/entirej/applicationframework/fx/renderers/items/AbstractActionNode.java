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
package org.entirej.applicationframework.fx.renderers.items;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import org.entirej.applicationframework.fx.utils.EJFXImageRetriever;

public abstract class AbstractActionNode<T extends Node> extends HBox
{

    private final T textControl;

    private Label   decorationLabel = new Label();

    private Control actionControl;
    private Control actionCustomControl;

    private String  mandatoryDescription;
    private String  errorDescription;

    private boolean showError;

    public enum ErrorIconType
    {
        ERROR, WARN, INFO
    };

    private ErrorIconType errorType = ErrorIconType.ERROR;
    private boolean       showMandatory;

    public AbstractActionNode()
    {
        super(2);
        setPadding(new Insets(0));

        textControl = createNode();

        decorationLabel.setPrefWidth(1);
        decorationLabel.setStyle("-fx-padding:2 1 2 1");
        HBox.setHgrow(decorationLabel, Priority.NEVER);
        HBox.setMargin(decorationLabel, new Insets(0));
        HBox.setHgrow(textControl, Priority.ALWAYS);

        getChildren().add(textControl);

        actionCustomControl = createCustomLabelButtonControl();
        actionControl = createLabelButtonControl();
        setActionVisible(false);
        decorationLabel.setVisible(false);
        // layout();
    }

    public void setMandatoryDescriptionText(String text)
    {
        mandatoryDescription = text;
        displayDecoration();
    }

    public void setErrorDescriptionText(String text)
    {
        errorDescription = text;
        displayDecoration();
    }

    public void setShowError(boolean showError,ErrorIconType type)
    {
        this.showError = showError;
        this.errorType =type;
        displayDecoration();
    }
    
    public void clearError()
    {
        this.showError = false;
        this.errorType =ErrorIconType.ERROR;
        errorDescription = null;
        displayDecoration();
    }
    
    

    public boolean isShowMandatory()
    {
        return showMandatory;
    }

    public void setShowMandatory(boolean showMandatory)
    {
        this.showMandatory = showMandatory;
        displayDecoration();
    }

    void displayDecoration()
    {
        getChildren().remove(decorationLabel);
        if (!showError && !showMandatory)
        {
            decorationLabel.setVisible(false);
            // layout();
            return;
        }

        if (showError)
        {
            switch (errorType)
            {
                case ERROR:
                    decorationLabel.setGraphic(new ImageView(EJFXImageRetriever.get(EJFXImageRetriever.IMG_ERROR_OVR)));
                    break;
                case WARN:
                    decorationLabel.setGraphic(new ImageView(EJFXImageRetriever.get(EJFXImageRetriever.IMG_WARN_OVR)));
                    break;
                case INFO:
                    decorationLabel.setGraphic(new ImageView(EJFXImageRetriever.get(EJFXImageRetriever.IMG_INFO_OVR)));
                    break;

                default:
                    break;
            }
           
            if (errorDescription != null && errorDescription.trim().length() > 0)
            {
                decorationLabel.setTooltip(new Tooltip(errorDescription));
            }
            else
            {
                decorationLabel.setTooltip(null);
            }
        }
        else if (showMandatory)
        {
            decorationLabel.setGraphic(new ImageView(EJFXImageRetriever.get(EJFXImageRetriever.IMG_REQ_OVR)));
            if (mandatoryDescription != null && mandatoryDescription.trim().length() > 0)
            {
                decorationLabel.setTooltip(new Tooltip(mandatoryDescription));
            }
            else
            {
                decorationLabel.setTooltip(null);
            }
        }
        decorationLabel.setVisible(true);
        getChildren().add(1, decorationLabel);
        // layout();

    }

    public void setActionVisible(boolean visible)
    {
        if (actionControl != null)
        {
            actionControl.setVisible(visible);
            // layout();
        }
    }

    public void setCustomActionVisible(boolean visible)
    {
        if (actionCustomControl != null)
        {
            actionCustomControl.setVisible(visible);
            // /layout();
        }
    }

    public abstract T createNode();

    public abstract Control createActionLabel();

    public abstract Control createCustomActionLabel();

    private Control createLabelButtonControl()
    {

        final Control labelButton = createActionLabel();
        if (labelButton != null)
        {
            labelButton.setStyle("-fx-padding:2 1 2 1");
            getChildren().add(labelButton);
            HBox.setHgrow(labelButton, Priority.NEVER);
        }
        return labelButton;
    }

    private Control createCustomLabelButtonControl()
    {

        final Control labelButton = createCustomActionLabel();
        if (labelButton != null)
        {
            labelButton.setStyle("-fx-padding:2 1 2 1");
            getChildren().add(labelButton);
            HBox.setHgrow(labelButton, Priority.NEVER);
        }
        return labelButton;
    }

    public T getNode()
    {
        return textControl;
    }

    public Control getActionControl()
    {
        return actionControl;
    }

}

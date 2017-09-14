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
package org.entirej.applicationframework.fx.application.form.containers;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.Window;

import org.entirej.applicationframework.fx.renderers.interfaces.EJFXAppFormRenderer;
import org.entirej.framework.core.data.controllers.EJPopupFormController;

public class EJFXFormPopUp
{

    private Window                _mainShell;
    private EJPopupFormController _popupController;
    private AbstractDialog        _popupDialog;

    public EJFXFormPopUp(Window _mainShell, EJPopupFormController _popupController)
    {
        this._mainShell = _mainShell;
        this._popupController = _popupController;
    }

    public void showForm()
    {
        final int height = _popupController.getPopupForm().getProperties().getFormHeight();
        final int width = _popupController.getPopupForm().getProperties().getFormWidth();

        final EJFXAppFormRenderer formRenderer = ((EJFXAppFormRenderer) _popupController.getPopupForm().getManagedRenderer().getUnmanagedRenderer());
        _popupDialog = new AbstractDialog(_mainShell)
        {

            private static final long serialVersionUID = 1L;

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
                    ((Region) node).setMinSize(_popupController.getPopupForm().getProperties().getFormWidth(), _popupController.getPopupForm().getProperties()
                            .getFormHeight());
                }
                if (node instanceof Control)
                {
                    ((Control) node).setMinSize(_popupController.getPopupForm().getProperties().getFormWidth(), _popupController.getPopupForm().getProperties()
                            .getFormHeight());
                }
                borderPane.setCenter(node);

                scrollComposite.setContent(borderPane);
                scrollComposite.setFitToHeight(true);
                scrollComposite.setFitToWidth(true);
                return scrollComposite;
            }

        };
        _popupDialog.create(width + 80, height + 100);
        _popupDialog.setTitle(_popupController.getPopupForm().getProperties().getTitle());
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

    }

    public void close()
    {
        if (_popupDialog != null)
        {
            _popupDialog.close();
            _popupDialog = null;
        }
    }

}

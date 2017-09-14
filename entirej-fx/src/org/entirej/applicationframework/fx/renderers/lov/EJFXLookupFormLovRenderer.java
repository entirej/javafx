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
package org.entirej.applicationframework.fx.renderers.lov;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

import org.entirej.applicationframework.fx.renderers.screen.EJFXQueryScreenRenderer;
import org.entirej.applicationframework.fx.utils.EJFXImageRetriever;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.data.controllers.EJItemLovController;
import org.entirej.framework.core.data.controllers.EJLovController;
import org.entirej.framework.core.enumerations.EJLovDisplayReason;
import org.entirej.framework.core.renderers.interfaces.EJQueryScreenRenderer;

public class EJFXLookupFormLovRenderer extends EJFXStandardLovRenderer
{

    private EJQueryScreenRenderer _queryScreenRenderer;

    public EJFXLookupFormLovRenderer()
    {
        _queryScreenRenderer = new EJFXQueryScreenRenderer();
    }

    public EJQueryScreenRenderer getQueryScreenRenderer()
    {
        return _queryScreenRenderer;
    }

    public void enterQuery(EJDataRecord record)
    {
        if (_queryScreenRenderer == null)
        {
            EJMessage message = new EJMessage("Please define a Query Screen Renderer for this form before a query operation can be performed.");
            _lovController.getFormController().getMessenger().handleMessage(message);
        }
        else
        {
            _queryScreenRenderer.open(record);
        }
    }

    @Override
    public void initialiseRenderer(EJLovController lovController)
    {
        super.initialiseRenderer(lovController);

        _queryScreenRenderer.initialiseRenderer(lovController);
    }

    @Override
    public void queryExecuted()
    {
        super.queryExecuted();
        
    }

    @Override
    public void displayLov(EJItemLovController itemToValidate, EJLovDisplayReason displayReason)
    {
        super.displayLov(itemToValidate, displayReason);
        if (displayReason == EJLovDisplayReason.LOV)
        {
            Platform.runLater(new Runnable()
            {

                @Override
                public void run()
                {
                    _lovController.enterQuery();

                }
            });
        }
    }

    @Override
    protected Node createToolbar()
    {

        Button _queryItem = new Button();

        _queryItem.setTooltip(new Tooltip("Enter a query"));
        _queryItem.setGraphic(new ImageView(EJFXImageRetriever.get(EJFXImageRetriever.IMG_QUERY)));
        _queryItem.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
                _lovController.enterQuery();
            }
        });

        return _queryItem;
    }

    

}

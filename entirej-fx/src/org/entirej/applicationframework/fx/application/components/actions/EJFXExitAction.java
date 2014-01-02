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
package org.entirej.applicationframework.fx.application.components.actions;

import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;

import org.entirej.applicationframework.fx.application.components.EJFXFormToolbar;
import org.entirej.applicationframework.fx.utils.EJFXImageRetriever;
import org.entirej.framework.core.data.controllers.EJBlockController;

public class EJFXExitAction extends EJFXAction
{

    private EJFXFormToolbar _toolbar;

    public EJFXExitAction(EJFXFormToolbar toolbar)
    {
        _toolbar = toolbar;
        // setText("Exit");
        setTooltip(new Tooltip("Exit the application"));
        // setToolTipText("Exit");
        // setAccelerator(SWT.CTRL + 'E');

    }

    @Override
    public void run()
    {
        // TODO
    }

    @Override
    public Image getImage()
    {

        return EJFXImageRetriever.get(EJFXImageRetriever.IMG_DELETE);
    }

    @Override
    public void synchronize(EJBlockController currentBlock)
    {
        // TODO Auto-generated method stub

    }
}

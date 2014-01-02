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
import org.entirej.framework.core.internal.EJInternalForm;

public class EJFXPreviousPageAction extends EJFXAction
{
    private EJFXFormToolbar   _toolbar;
    private EJBlockController _currentBlock;

    public EJFXPreviousPageAction(EJFXFormToolbar toolbar)
    {
        _toolbar = toolbar;
        // setText("Previous Page");
        setTooltip(new Tooltip("Navigate to the previous page"));
        // setAccelerator(SWT.CTRL + 'P');

    }

    public void synchronize(EJBlockController currentBlock)
    {
        this._currentBlock = currentBlock;
        if (currentBlock == null || currentBlock.preventMasterlessOperations())
        {
            setEnabled(false);
            return;
        }

        if (!currentBlock.canQueryInPages() || currentBlock.getDisplayedRecordCount() == 0)
        {
            setEnabled(false);

        }
        else
        {
            if (currentBlock.isOnFirstPage())
            {
                setEnabled(false);
            }
            else
            {
                setEnabled(true);
            }
        }
    }

    @Override
    public void run()
    {
        if (this._currentBlock == null)
        {
            EJInternalForm form = _toolbar.getForm();
            if (form != null && form.getFocusedBlock() != null)
            {
                try
                {
                    form.getFocusedBlock().previousPage();
                }
                catch (Exception e)
                {
                    form.getFrameworkManager().getApplicationManager().handleException(e);
                }
                _toolbar.synchronize(form.getFocusedBlock().getBlockController());
            }
        }
        else
        {
            try
            {
                _currentBlock.previousPage();
            }
            catch (Exception e)
            {
                _currentBlock.getFrameworkManager().getApplicationManager().handleException(e);
            }
            _toolbar.synchronize(_currentBlock);
        }

    }

    @Override
    public Image getImage()
    {
        return EJFXImageRetriever.get(EJFXImageRetriever.IMG_PREV_PAGE);
    }
}

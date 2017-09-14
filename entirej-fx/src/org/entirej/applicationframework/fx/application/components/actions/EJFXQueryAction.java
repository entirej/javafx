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
package org.entirej.applicationframework.fx.application.components.actions;

import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;

import org.entirej.applicationframework.fx.application.components.EJFXFormToolbar;
import org.entirej.applicationframework.fx.utils.EJFXImageRetriever;
import org.entirej.framework.core.data.controllers.EJBlockController;
import org.entirej.framework.core.data.controllers.EJEditableBlockController;
import org.entirej.framework.core.internal.EJInternalEditableBlock;
import org.entirej.framework.core.internal.EJInternalForm;

public class EJFXQueryAction extends EJFXAction
{
    private EJFXFormToolbar           _toolbar;
    private EJEditableBlockController _currentBlock;

    public EJFXQueryAction(final EJFXFormToolbar toolbar)
    {
        _toolbar = toolbar;
        // setText("Query");
        setTooltip(new Tooltip("Enter a query"));
        // setAccelerator(SWT.CTRL + 'Q');

    }

    @Override
    public Image getImage()
    {
        return EJFXImageRetriever.get(EJFXImageRetriever.IMG_QUERY);
    }

    public void synchronize(EJBlockController currentBlock)
    {
        this._currentBlock = (EJEditableBlockController) (currentBlock instanceof EJEditableBlockController ? currentBlock : null);
        if (!canExecute(_currentBlock))
        {
            setEnabled(false);
            return;
        }

        setEnabled(true);
    }

    public static boolean canExecute(final EJEditableBlockController currentBlock)
    {
        if (currentBlock == null || currentBlock.preventMasterlessOperations())
        {
            return false;
        }

        if (!currentBlock.getProperties().isQueryAllowed())
        {
            return false;
        }

        if (currentBlock.getProperties().getBlockService() == null)
        {
            return false;
        }

        if (currentBlock.getQueryScreenRenderer() == null)
        {
            return false;

        }
        return true;
    }

    @Override
    public void run()
    {

        if (this._currentBlock == null)
        {
            EJInternalForm form = _toolbar.getForm();
            EJInternalEditableBlock focusedBlock = form.getFocusedBlock();
            if (form != null && focusedBlock != null)
            {
                try
                {
                    focusedBlock.enterQuery();
                    focusedBlock.getManagedRenderer().gainFocus();
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
                _currentBlock.enterQuery();
                _currentBlock.getBlock().getManagedRenderer().gainFocus();
            }
            catch (Exception e)
            {
                _currentBlock.getFrameworkManager().getApplicationManager().handleException(e);
            }
            _toolbar.synchronize(_currentBlock);
        }

    }

}

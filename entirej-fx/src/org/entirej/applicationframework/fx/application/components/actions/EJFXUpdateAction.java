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
import org.entirej.framework.core.internal.EJInternalForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EJFXUpdateAction extends EJFXAction
{
    final Logger                      logger = LoggerFactory.getLogger(EJFXUpdateAction.class);

    private EJFXFormToolbar           _toolbar;
    private EJEditableBlockController _currentBlock;

    public EJFXUpdateAction(EJFXFormToolbar toolbar)
    {
        _toolbar = toolbar;
        // setText("Edit Record");
        setTooltip(new Tooltip("Edit the current record"));
        // setAccelerator(SWT.CTRL + 'E');

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

        if (!currentBlock.getProperties().isUpdateAllowed())
        {
            return false;
        }

        if (currentBlock.getDisplayedRecordCount() == 0)
        {
            return false;
        }

        if (currentBlock.getUpdateScreenRenderer() == null)
        {
            return false;
        }
        return true;
    }

    @Override
    public void run()
    {
        logger.trace("START run");

        if (this._currentBlock == null)
        {
            logger.trace("  -> currentBlock = null");
            EJInternalForm form = _toolbar.getForm();
            if (form != null && form.getFocusedBlock() != null)
            {
                try
                {
                    form.getFocusedBlock().enterUpdate();
                }
                catch (Exception e)
                {
                    form.getFrameworkManager().getApplicationManager().handleException(e);
                }
                logger.trace("  -> calling synchronize");
                _toolbar.synchronize(form.getFocusedBlock().getBlockController());
                logger.trace("  -> called synchronize");
            }
        }
        else
        {
            logger.trace("  -> currentBlock: {}", _currentBlock.getProperties().getName());

            try
            {
                _currentBlock.enterUpdate();
            }
            catch (Exception e)
            {
                _currentBlock.getFrameworkManager().getApplicationManager().handleException(e);
            }
            logger.trace("  -> calling synchronize");
            _toolbar.synchronize(_currentBlock);
            logger.trace("  -> called synchronize");
        }
        logger.trace("END synchronize");
    }

    @Override
    public Image getImage()
    {
        return EJFXImageRetriever.get(EJFXImageRetriever.IMG_EDIT);
    }
}

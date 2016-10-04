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
import org.entirej.framework.core.data.controllers.EJEditableBlockController;
import org.entirej.framework.core.internal.EJInternalForm;

public class EJFXSaveAction extends EJFXAction
{

    private EJFXFormToolbar           _toolbar;
    private EJEditableBlockController _currentBlock;

    public EJFXSaveAction(EJFXFormToolbar toolbar)
    {
        _toolbar = toolbar;
        // setText("Save changes");
        setTooltip(new Tooltip("Saves all open changes"));

        // setAccelerator(SWT.CTRL + 'S');
    }

    public void synchronize(EJBlockController currentBlock)
    {
        this._currentBlock = (EJEditableBlockController) (currentBlock instanceof EJEditableBlockController ? currentBlock : null);
        if ((_currentBlock != null && _currentBlock.getForm().isDirty()) || (_toolbar.getForm() != null && _toolbar.getForm().isDirty()))
        {
            setEnabled(true);
        }
        else
        {
            setEnabled(false);
        }
    }

    @Override
    public void run()
    {
        EJInternalForm form;
        if (this._currentBlock == null)
        {
            form = _toolbar.getForm();
        }
        else
        {
            form = _currentBlock.getForm();
        }

        if (form != null)
        {
            try
            {
                form.saveChanges();
            }
            catch (Exception e)
            {
                form.getFrameworkManager().getApplicationManager().handleException(e);
            }
            if(_currentBlock!=null || form.getFocusedBlock()!=null)
                _toolbar.synchronize(_currentBlock != null ? _currentBlock : (form.getFocusedBlock().getBlockController()));
        }
    }

    @Override
    public Image getImage()
    {
        return EJFXImageRetriever.get(EJFXImageRetriever.IMG_SAVE);
    }
}

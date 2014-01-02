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
package org.entirej.applicationframework.fx.application.interfaces;

import java.util.EventObject;

public class EJFXFormChosenEvent extends EventObject
{
    private static final long serialVersionUID = -4402344915831114152L;
    private String            _formName;
    private boolean           _queryMode;

    /**
     * @param formName
     */
    public EJFXFormChosenEvent(String formName)
    {
        super(formName);
        setChosenFormName(formName);
    }

    public EJFXFormChosenEvent(String formName, boolean queryMode)
    {
        super(formName);
        setChosenFormName(formName);
        setQueryMode(queryMode);
    }

    private void setChosenFormName(String pFormName)
    {
        _formName = pFormName;
    }

    public String getChosenFormName()
    {
        return _formName;
    }

    public boolean isQueryMode()
    {
        return _queryMode;
    }

    private void setQueryMode(boolean pQureyMode)
    {
        _queryMode = pQureyMode;
    }
}

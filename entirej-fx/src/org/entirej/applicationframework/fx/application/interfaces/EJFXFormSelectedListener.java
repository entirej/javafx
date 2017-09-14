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
package org.entirej.applicationframework.fx.application.interfaces;

import org.entirej.framework.core.internal.EJInternalForm;

/**
 * Listens for form selection events. A selection event is fired whenever
 * navigation is passed from one open form to another.
 * 
 */
public interface EJFXFormSelectedListener
{
    /**
     * called when a new form is selected
     * 
     * @param selectedForm
     *            The form that was selected
     */
    public void fireFormSelected(EJInternalForm selectedForm);
}

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
package org.entirej.applicationframework.fx.layout;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class EJFXEntireJStackedPane extends StackPane
{

    private String                  name;

    private final Map<String, Node> _panes = new HashMap<String, Node>();

    public EJFXEntireJStackedPane()
    {

    }

    public String getPaneName()
    {
        return name;
    }

    public void setPaneName(String name)
    {
        this.name = name;
    }

    public void add(String key, Node control)
    {
        if (getChildren().isEmpty())
            getChildren().add(control);
        _panes.put(key, control);
    }

    public void showPane(String pane)
    {
        Node control = _panes.get(pane);
        if (control != null)
        {
            getChildren().clear();
            getChildren().add(control);
        }
        autosize();
    }

    public void remove(String key)
    {
        Node control = _panes.get(key);
        if (control != null)
        {
            _panes.remove(key);
            if (getChildren().contains(control))
            {
                getChildren().clear();
                if (_panes.size() > 0)
                {
                    getChildren().add(_panes.values().toArray(new Node[0])[0]);
                }
            }

        }
    }

    public String getActiveControlKey()
    {
        Set<Entry<String, Node>> entrySet = _panes.entrySet();
        Node activeControl = getActiveControl();
        if (activeControl != null)
        {

            for (Entry<String, Node> entry : entrySet)
            {
                if (entry.getValue() != null && activeControl.equals(entry.getValue()))
                {
                    return entry.getKey();
                }
            }
        }
        return null;

    }

    public Node getActiveControl()
    {

        if (!getChildren().isEmpty())
        {
            return getChildren().get(0);
        }

        return null;

    }

}

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
package org.entirej.applicationframework.fx.renderers.block.definition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.entirej.framework.dev.properties.interfaces.EJDevBlockDisplayProperties;
import org.entirej.framework.dev.properties.interfaces.EJDevItemGroupDisplayProperties;
import org.entirej.framework.dev.properties.interfaces.EJDevScreenItemDisplayProperties;
import org.entirej.framework.dev.renderer.definition.EJDevBlockRendererDefinitionControl;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevBlockWidgetChosenListener;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevItemWidgetChosenListener;

public class EJFXTableRendererDefinitionControl extends EJDevBlockRendererDefinitionControl
{
    private Table                               _table;
    private Map<String, Integer>                _columnPositions;
    private List<EJDevItemWidgetChosenListener> _itemWidgetListenerList;

    public EJFXTableRendererDefinitionControl(EJDevBlockDisplayProperties blockDisplayProperties, Table table, Map<String, Integer> columnPositions)
    {
        super(blockDisplayProperties, null);

        _itemWidgetListenerList = new ArrayList<EJDevItemWidgetChosenListener>();
        _table = table;
        _columnPositions = columnPositions;

        for (TableColumn col : _table.getColumns())
        {
            col.addSelectionListener(new SelectionAdapter()
            {
                @Override
                public void widgetSelected(SelectionEvent e)
                {
                    for (EJDevItemWidgetChosenListener listener : _itemWidgetListenerList)
                    {
                        listener.fireRendererChosen((EJDevScreenItemDisplayProperties) ((TableColumn) e.getSource()).getData("SCREEN_ITEM"));
                    }
                }
            });
        }

        _table.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                for (EJDevItemWidgetChosenListener listener : _itemWidgetListenerList)
                {
                    for (EJDevItemGroupDisplayProperties group : getBlockDisplayProperties().getMainScreenItemGroupDisplayContainer()
                            .getAllItemGroupDisplayProperties())
                    {
                        for (EJDevScreenItemDisplayProperties screenItem : group.getAllItemDisplayProperties())
                        {
                            listener.fireRendererChosen(screenItem);
                            break;
                        }
                        break;
                    }

                }
            }
        });
    }

    @Override
    public void addBlockWidgetChosenListener(EJDevBlockWidgetChosenListener listener)
    {
        super.addBlockWidgetChosenListener(listener);
    }

    @Override
    public void blockItemWidgetSelected(String itemName, boolean selected)
    {
        Integer colPos = _columnPositions.get(itemName);
        if (colPos != null)
        {
            _table.setSelection(colPos);

        }
    }

    @Override
    public void addItemWidgetChosenListener(EJDevItemWidgetChosenListener listener)
    {
        if (listener != null)
        {
            _itemWidgetListenerList.add(listener);
        }
    }

    @Override
    public void removeItemWidgetChosenListener(EJDevItemWidgetChosenListener listener)
    {
        if (listener != null)
        {
            _itemWidgetListenerList.remove(listener);
        }
    }

    @Override
    public void focusLost()
    {
        super.focusLost();
    }

    @Override
    public void removeBlockWidgetChosenListener(EJDevBlockWidgetChosenListener listener)
    {
        _table.deselectAll();
    }
}

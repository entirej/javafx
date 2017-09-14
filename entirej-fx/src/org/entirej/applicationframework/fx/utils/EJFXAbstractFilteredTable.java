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
package org.entirej.applicationframework.fx.utils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public abstract class EJFXAbstractFilteredTable extends VBox
{
    private final TableView<?> _tableViewer;
    private TextField       _filterText;

    public EJFXAbstractFilteredTable(TableView<?> tableView)
    {
        super(5);
        _tableViewer = tableView;
        _filterText = new TextField();
        Node customNode = doCreateCustomComponents();
        Node header;
        if(customNode!=null)
        {
            HBox box = new HBox(2);
            
            header = box;
            HBox.setHgrow(customNode, Priority.NEVER);
            HBox.setMargin(customNode, new Insets(0));
            HBox.setHgrow(_filterText, Priority.ALWAYS);
            box.getChildren().addAll(_filterText,customNode);
            box.setMinHeight(Control.USE_PREF_SIZE);
        }
        else
        {
            header = _filterText;

            _filterText.setMinHeight(Control.USE_PREF_SIZE);
        }
        VBox.setVgrow(tableView, Priority.ALWAYS);
        
        VBox.setVgrow(header, Priority.SOMETIMES);
        _filterText.focusedProperty().addListener(new ChangeListener<Boolean>()
        {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                if (newValue)
                    _filterText.selectAll();
            }

        });
        _filterText.textProperty().addListener(new ChangeListener<String>()
        {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
            {
                textChanged();
            }

        });
        getChildren().addAll(header, tableView);
    }

    
    protected Node doCreateCustomComponents()
    {
        return null;
    }
    
    public TableView<?> getTableViewer()
    {
        return _tableViewer;
    }
    
    protected void textChanged()
    {
        filter(getFilterString());
    }

    public abstract void filter(String filter);

    public void clearText()
    {
        if (getFilterString() != null && getFilterString().trim().length() > 0)
        {
            setFilterText("");
        }
    }

    protected void setFilterText(String string)
    {
        if (_filterText != null)
        {
            _filterText.setText(string != null ? string : "");
            selectAll();
        }
    }

    protected String getFilterString()
    {
        return _filterText != null ? _filterText.getText() : null;
    }

    public void setInitialText(String text)
    {
        setFilterText(text);
        textChanged();
    }

    protected void selectAll()
    {
        if (_filterText != null)
        {
            _filterText.selectAll();
        }
    }

    
    public static abstract class FilteredContentProvider 
    {
        protected String filter;

        public void setFilter(String filter)
        {
            this.filter = filter;
        }

        public String getFilter()
        {
            return filter;
        }

        public abstract void refresh(Object o);
    }
}

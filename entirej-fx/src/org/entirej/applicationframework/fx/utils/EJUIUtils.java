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
package org.entirej.applicationframework.fx.utils;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

public class EJUIUtils
{
    public static void setConstraints(GridPane pane, int cols, int rows)
    {

        if (cols == 0 && rows == 0)
            return;

        int lcol = 0;
        int lrow = 0;
        ObservableList<Node> children = pane.getChildren();
        for (Node node : children)
        {
            Integer columnIndex = GridPane.getColumnIndex(node);
            if (columnIndex != null && columnIndex.intValue() > lcol)
            {
                lcol = columnIndex.intValue();
            }
            Integer rowIndex = GridPane.getRowIndex(node);
            if (rowIndex != null && rowIndex.intValue() > lrow)
            {
                lrow = rowIndex.intValue();
            }
        }

        for (int i = 0; i < cols; i++)
        {
            ColumnConstraints constraints = new ColumnConstraints();
            if (i > lcol)
                constraints.setHgrow(Priority.SOMETIMES);
            pane.getColumnConstraints().add(constraints);

        }

        for (int i = 0; i < rows; i++)
        {
            RowConstraints constraints = new RowConstraints();
            if (i > lrow)
                constraints.setVgrow(Priority.SOMETIMES);
            pane.getRowConstraints().add(constraints);
        }
    }
}

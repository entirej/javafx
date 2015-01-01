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

import java.util.ArrayList;
import java.util.List;

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

    public static GridLayoutUsage newGridLayoutUsage(int col)
    {
        return new GridLayoutUsage(col);
    }

    public static class GridLayoutUsage
    {
        final int                     colLimit;
        private int                   col = -1;
        private int                   row = 0;

        private final List<Boolean>[] usage;

        private GridLayoutUsage(int colums)
        {
            colLimit = colums;

            usage = new List[colums];
        }

        public int getRow()
        {
            return row;
        }

        public int getCol()
        {
            return col;
        }

        public void allocate(int hSpan, int vSpan)
        {
            int newCol = col + 1;
            int newRow = row;
            if (col + hSpan >= colLimit)
            {
                newCol = 0;
                newRow++;
            }

            while (isUsed(newCol, newRow))
            {

                newCol = newCol + 1;
                if (newCol >= colLimit)
                {
                    newCol = 0;
                    newRow++;
                }
            }

            col = newCol;
            row = newRow;

            mark(newCol, newRow);

            for (int i = 0; i < vSpan; i++)
            {

                for (int j = 0; j < hSpan; j++)
                {
                    mark(newCol + (j), newRow + i);
                }

            }

        }

        boolean isUsed(int col, int row)
        {
            List<Boolean> colUsage = getColUsage(col);

            return colUsage.size() <= row ? false : colUsage.get(row);
        }

        void mark(int col, int row)
        {
            List<Boolean> colUsage = getColUsage(col);

            if (colUsage.size() <= row)
            {
                while (colUsage.size() != (row + 1))
                {
                    colUsage.add(true);

                }
            }
            else
                colUsage.set(row, true);

        }

        private List<Boolean> getColUsage(int col)
        {
            List<Boolean> list = usage[col];
            if (list == null)
            {
                list = new ArrayList<Boolean>();
                usage[col] = list;
            }
            return list;
        }

    }

    public static void main(String[] args)
    {
        GridLayoutUsage usage = new GridLayoutUsage(2);
        usage.allocate(1, 1);
        System.out.println(String.format("%d, %d", usage.getCol(), usage.getRow()));
        usage.allocate(2, 1);
        System.out.println(String.format("%d, %d", usage.getCol(), usage.getRow()));
        usage.allocate(1, 1);
        System.out.println(String.format("%d, %d", usage.getCol(), usage.getRow()));
        usage.allocate(1, 1);
        System.out.println(String.format("%d, %d", usage.getCol(), usage.getRow()));

    }
}

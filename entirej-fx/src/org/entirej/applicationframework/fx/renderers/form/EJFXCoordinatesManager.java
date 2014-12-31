package org.entirej.applicationframework.fx.renderers.form;

import java.util.ArrayList;

public class EJFXCoordinatesManager
{
    private int              _numCols;
    private ArrayList<int[]> _rows;

    private int              _currRow = -1;
    private int              _currCol = 0;

    public EJFXCoordinatesManager(int numCols)
    {
        if (numCols == 0)
        {
            numCols = 1;
        }
        _numCols = numCols;
        _rows = new ArrayList<int[]>();
        addNewRow(true);
    }

    public EJFXCoordinates getCoordinates(int colSpan, int rowSpan)
    {
        setCurrColAndRow(colSpan, rowSpan);

        int xpos = _currCol;
        int ypos = _currRow;

        updateCurrentColumn(colSpan);

        EJFXCoordinates coordinates = new EJFXCoordinates();

        coordinates.setCol(xpos);
        coordinates.setRow(ypos);

        return coordinates;
    }

    private void setCurrColAndRow(int colSpan, int rowSpan)
    {
        if (colSpan == 0)
        {
            colSpan = 1;
        }

        if (rowSpan == 0)
        {
            rowSpan = 1;
        }

        // The component can only span to the maximum number of columns defined
        // for its container
        if (colSpan > _numCols)
        {
            colSpan = _numCols;
        }

        setStartingPos(colSpan);
        growTableToFit(rowSpan);
        markTable(colSpan, rowSpan);
    }

    private void updateCurrentColumn(int colSpan)
    {
        if ((_currCol + colSpan) >= _numCols)
        {
            addNewRow(true);
        }
        else
        {
            _currCol += colSpan;
        }
    }

    private void markTable(int colSpan, int rowSpan)
    {
        for (int row = 0; row < rowSpan; row++)
        {
            int[] rowData = _rows.get(_currRow + row);

            for (int col = _currCol; col < _currCol + colSpan; col++)
            {
                rowData[col] = 1;
            }

        }
    }

    private void growTableToFit(int rowSpan)
    {
        int rowsToAdd = rowSpan - (_rows.size() - _currRow);
        if (rowsToAdd > 0)
        {
            addRows(rowsToAdd);
        }
    }

    private void setStartingPos(int colSpan)
    {
        // Set the column counter to the next free column
        getNextFreeSpace();
        if (!hasRoom(colSpan))
        {
            incrementCurrCol();
            setStartingPos(colSpan);
        }
    }

    private void incrementCurrCol()
    {
        if (_currCol + 1 == _numCols)
        {
            // I am on the last cell, so create a new record. This will also set
            // the current column to 0
            addNewRow(true);
        }
        else
        {
            _currCol++;
        }
    }

    private boolean hasRoom(int colSpan)
    {
        int spaceCounter = 0;

        int[] row = _rows.get(_currRow);
        for (int i = _currCol; i < _numCols; i++)
        {
            if (row[i] == 0)
            {
                spaceCounter++;
                if (spaceCounter == colSpan)
                {
                    return true;
                }
            }
            else if (row[i] == 1)
            {
                return false;
            }
        }
        return false;
    }

    private void getNextFreeSpace()
    {
        int[] row = _rows.get(_currRow);

        // First check if the current cell is free
        if (row[_currCol] == 0)
        {
            return;
        }

        // Now find the next free cell
        for (int i = _currCol; i < _numCols; i++)
        {
            if (row[i] == 0)
            {
                _currCol = i;
                return;
            }
        }

        // If I get this far, then the whole row has been set, so create a new
        // row and check that one
        addNewRow(true);
        getNextFreeSpace();
    }

    private void addRows(int numRowsToAdd)
    {
        if (numRowsToAdd == 0)
        {
            return;
        }

        for (int i = 0; i < numRowsToAdd; i++)
        {
            addNewRow(false);
        }
    }

    private void addNewRow(boolean incrementRowcounter)
    {
        int[] cols = new int[_numCols];

        // Initialise the new row with 0, meaning that the space is not taken
        for (int i = 0; i < _numCols; i++)
        {
            cols[i] = 0;
        }

        _rows.add(cols);
        if (incrementRowcounter)
        {
            _currCol = 0;
            _currRow++;
        }
    }

}

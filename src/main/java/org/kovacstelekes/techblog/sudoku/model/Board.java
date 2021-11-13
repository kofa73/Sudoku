package org.kovacstelekes.techblog.sudoku.model;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;
import static org.kovacstelekes.techblog.sudoku.model.CommonConstraints.checkPositionIsValid;

public class Board {
    private final List<Row> rows;
    private final List<Column> columns;
    private final List<Grid> grids;
    private final List<Cell> cells;

    public Board() {
        rows = new ArrayList<>(9);
        columns = new ArrayList<>(9);
        grids = new ArrayList<>(9);
        cells = new ArrayList<>(9);

        for (int i = 0; i < 9; i++) {
            rows.add(new Row(i));
            columns.add(new Column(i));
            grids.add(new Grid(i));
        }

        for (int i = 0; i < 81; i++) {
            Cell cell = new Cell(i);
            cells.add(cell);
            int rowNumber = i / 9;
            int columnNumber = i % 9;
            rows.get(rowNumber).addCell(cell);
            columns.get(columnNumber).addCell(cell);
            int gridIndex = (rowNumber / 3) * 3 + columnNumber / 3;
            grids.get(gridIndex).addCell(cell);
        }
    }

    public Cell cellAt(int row, int column) {
        return row(row).cellAt(column);
    }

    public Row row(int rowNumber) {
        checkPositionIsValid(rowNumber);
        return rows.get(rowNumber);
    }

    public Column column(int rowNumber) {
        checkPositionIsValid(rowNumber);
        return columns.get(rowNumber);
    }

    public Grid grid(int gridNumber) {
        checkPositionIsValid(gridNumber);
        return grids.get(gridNumber);
    }

    public List<Row> rows() {
        return unmodifiableList(rows);
    }

    public List<Column> columns() {
        return unmodifiableList(columns);
    }

    public List<Grid> grids() {
        return unmodifiableList(grids);
    }
}

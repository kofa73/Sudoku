package org.kovacstelekes.techblog.sudoku.model;

public class Row extends Container {
    public Row(int id) {
        super(id);
    }

    @Override
    public void addCell(Cell cell) {
        safelyAddCell(cell);
        cell.row(this);
    }
}

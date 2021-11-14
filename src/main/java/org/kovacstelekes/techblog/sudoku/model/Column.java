package org.kovacstelekes.techblog.sudoku.model;

public class Column extends Container {
    public Column(int id) {
        super(id);
    }

    @Override
    public void addCell(Cell cell) {
        safelyAddCell(cell);
        cell.column(this);
    }
}

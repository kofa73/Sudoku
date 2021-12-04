package org.kovacstelekes.techblog.sudoku3.model;

public class Row extends Container {

    public Row(int ordinal) {
        super(ordinal);
    }

    @Override
    public void addCell(Cell cell) {
        super.addCell(cell);
        cell.setRow(this);
    }
}

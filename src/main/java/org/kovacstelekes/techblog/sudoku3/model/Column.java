package org.kovacstelekes.techblog.sudoku3.model;

public class Column extends Container {

    public Column(int ordinal) {
        super(ordinal);
    }

    @Override
    public void addCell(Cell cell) {
        super.addCell(cell);
        cell.setColumn(this);
    }
}

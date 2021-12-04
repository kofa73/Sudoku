package org.kovacstelekes.techblog.sudoku3.model;

public class Grid extends Container {

    public Grid(int ordinal) {
        super(ordinal);
    }

    @Override
    public void addCell(Cell cell) {
        super.addCell(cell);
        cell.setGrid(this);
    }
}

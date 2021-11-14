package org.kovacstelekes.techblog.sudoku.model;

public class Grid extends Container {
    public Grid(int id) {
        super(id);
    }

    @Override
    public void addCell(Cell cell) {
        safelyAddCell(cell);
        cell.grid(this);
    }
}

package org.kovacstelekes.techblog.sudoku3.model;

import java.util.Set;

public class Cell extends Options<Cell> {
    private Row row;
    private Column column;
    private Grid grid;

    public Cell() {
        super("cell");
    }

    void setRow(Row row) {
        this.row = row;
    }

    void setColumn(Column column) {
        this.column = column;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "row=" + row +
                ", column=" + column +
                ", grid=" + grid +
                '}';
    }

    public boolean solution(int value) {
        boolean updated = super.solution(value);
        if (updated) {
            row.setCellOf(value, this);
            column.setCellOf(value, this);
            grid.setCellOf(value, this);
        }
        return updated;
    }
}

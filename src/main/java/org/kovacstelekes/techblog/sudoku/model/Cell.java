package org.kovacstelekes.techblog.sudoku.model;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.IntStream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.util.stream.Collectors.toList;

public class Cell {
    private static final Collection<Integer> ALL_VALUES = IntStream.range(1, 10).boxed().collect(toList());
    private int id;
    private Row row;
    private Column column;
    private Grid grid;
    private final Set<Integer> possibleValues = new TreeSet<>(ALL_VALUES);

    public Cell(int id) {
        checkArgument(
                id >= 0 && id < 81,
                "invalid id=%s",
                id);
        this.id = id;
    }

    public void setRow(Row row) {
        checkState(this.row == null, "row is already set to %s", this.row);
        this.row = row;
    }

    public void setColumn(Column column) {
        checkState(this.column == null, "column is already set to %s", this.column);
        this.column = column;
    }

    public void setGrid(Grid grid) {
        checkState(this.grid == null, "grid is already set to %s", this.grid);
        this.grid = grid;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "id=" + id +
                ", row=" + (row == null ? -1 : row.id()) +
                ", column=" + (column == null ? -1 : column.id()) +
                ", grid=" + (grid == null ? -1 : grid.id()) +
                '}';
    }

    public int id() {
        return id;
    }

    public Row row() {
        return row;
    }

    public Column column() {
        return column;
    }

    public Grid grid() {
        return grid;
    }
}

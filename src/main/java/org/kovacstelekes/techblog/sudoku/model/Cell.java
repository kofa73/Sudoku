package org.kovacstelekes.techblog.sudoku.model;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.util.stream.Collectors.toList;

public class Cell extends Solvable<Integer> {
    private static final Collection<Integer> ALL_VALUES = IntStream.range(1, 10).boxed().collect(toList());
    private final int id;
    private Row row;
    private Column column;
    private Grid grid;

    public Cell(int id) {
        super("Cell #" + id);
        checkArgument(
                id >= 0 && id < 81,
                "invalid id=%s",
                id);
        this.id = id;
        addAll(ALL_VALUES);
    }

    public Cell row(Row row) {
        checkState(this.row == null, "row is already set to %s", this.row);
        this.row = row;
        return this;
    }

    public Cell column(Column column) {
        checkState(this.column == null, "column is already set to %s", this.column);
        this.column = column;
        return this;
    }

    public Cell grid(Grid grid) {
        checkState(this.grid == null, "grid is already set to %s", this.grid);
        this.grid = grid;
        return this;
    }

    @Override
    public String toString() {
        return super.toString() +
                ", solution=" + solution().orElse(-numberOfRemainingPossibilities()) +
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

    @Override
    public void solution(Integer solution) {
        super.solution(solution);
        System.out.println("Solved " + this);
        row.solvedCell(this);
        column.solvedCell(this);
        grid.solvedCell(this);
    }

    @Override
    Optional<Integer> exclude(Integer option) {
        row.exclude(this, option);
        column.exclude(this, option);
        grid.exclude(this, option);
        return super.exclude(option);
    }
}

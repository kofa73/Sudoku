package org.kovacstelekes.techblog.sudoku3.model;

import java.util.Set;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toUnmodifiableSet;

public class Cell extends Options<Integer> {
    private final int ordinal;
    private Row row;
    private Column column;
    private Grid grid;

    private static final Set<Integer> ALL_VALUES = IntStream.range(0, 9).boxed().collect(toUnmodifiableSet());

    public Cell(int ordinal) {
        super("cell");
        this.ordinal = ordinal;
        setPossibleValues(ALL_VALUES);
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
                "ordinal=" + ordinal +
                ", row=" + row +
                ", column=" + column +
                ", grid=" + grid +
                ", possibleValues=" + possibleValues()
                + '}';
    }

    public boolean solution(int value) {
        boolean updated = super.solution(value);
        if (updated) {
            row.solvedDigitIsInCell(value, this);
            column.solvedDigitIsInCell(value, this);
            grid.solvedDigitIsInCell(value, this);
        }
        return updated;
    }

    public int ordinal() {
        return ordinal;
    }
}

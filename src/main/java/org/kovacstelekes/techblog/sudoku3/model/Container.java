package org.kovacstelekes.techblog.sudoku3.model;

import java.util.ArrayList;
import java.util.List;

// A grid, row or column
abstract class Container {
    private final List<Options<Cell>> possibleCellsHoldingDigits = new ArrayList<>();
    private final String name;
    private final int ordinal;
    private final List<Cell> cells = new ArrayList<>();

    public Container(int ordinal) {
        this.name = getClass().getSimpleName();
        this.ordinal = ordinal;
        for (int i = 0; i < 9; i++) {
            possibleCellsHoldingDigits.add(new Options<>(name + '#' + ordinal + "-digit#" + i));
        }
        for (int i = 0; i < 9; i++) {
            possibleCellsHoldingDigits.get(i).addRelated(possibleCellsHoldingDigits);
        }
    }

    @Override
    public String toString() {
        return name + '#' + ordinal;
    }

    public int ordinal() {
        return ordinal;
    }

    public void addCell(Cell cell) {
        cells.add(cell);
        possibleCellsHoldingDigits.forEach(cellOptions -> cellOptions.addPossibleValue(cell));
    }

    public List<Cell> cells() {
        return cells;
    }

    public void solvedDigitIsInCell(int value, Cell cell) {
        Options<Cell> possibleLocationsOfValue = possibleCellsHoldingDigits.get(value);
        possibleLocationsOfValue.solution(cell);
    }
}

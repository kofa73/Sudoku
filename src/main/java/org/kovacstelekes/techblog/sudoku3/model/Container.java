package org.kovacstelekes.techblog.sudoku3.model;

import java.util.*;

// A grid, row or column
abstract class Container {
    private final List<Options> possibleLocationsOfDigits = new ArrayList<>();
    private final String name;
    private final int ordinal;
    private final List<Cell> cells = new ArrayList<>();

    public Container(int ordinal) {
        this.name = getClass().getSimpleName();
        this.ordinal = ordinal;
        possibleLocationsOfDigits.add(null);
        cells.add(null);
        for (int i = 1; i <= 9; i++) {
            possibleLocationsOfDigits.add(new Options(name + "-digit#" + i));
        }
        for (int i = 1 ; i <= 9; i++) {
            possibleLocationsOfDigits.get(i).addRelated(possibleLocationsOfDigits);
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
    }

    public List<Cell> cells() {
        return cells;
    }

    public void setCellOf(int value, Cell cell) {
        Options possibleLocationsOfValue = possibleLocationsOfDigits.get(value);
        possibleLocationsOfValue.solution(cells.indexOf(cell));
    }
}

package org.kovacstelekes.techblog.sudoku2.model;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.*;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

/**
 * A subdivision of the puzzle, holding 9 cells.
 */
abstract class Container {
    private final List<Cell> cells;
    private final int ordinal;
    private final Set<Integer> unsolvedValues = IntStream.range(1, 10).boxed().collect(toCollection(TreeSet::new));

    protected Container(int ordinal) {
        checkArgument(ordinal >= 0 && ordinal < 9, "Invalid ordinal: " + ordinal);
        this.ordinal = ordinal;
        cells = new ArrayList<>(9);
    }

    void addCell(Cell cell) {
        checkNotNull(cell);
        checkState(cells.size() < 9, "% is already full");
        cells.add(cell);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "#" + ordinal;
    }

    public Stream<Boolean> solve() {
        unsolvedValues.clear();
        cells.stream()
                .filter(not(Cell::isSolved))
                .map(Cell::values)
                .forEach(unsolvedValues::addAll);

        return unsolvedValues.stream().map(this::findCellForValue);
    }

    Stream<Boolean> removeSolvedValuesFromCells() {
        return cells.stream()
                .flatMap(this::removeSolvedCellValueFromOtherCells);
    }

    private Stream<Boolean> removeSolvedCellValueFromOtherCells(Cell solvedCell) {
        return solvedCell.solution().map(value ->
                cells.stream()
                    .filter(otherCell -> otherCell != solvedCell)
                    .map(otherCell -> otherCell.removeValue(value))
        ).orElse(Stream.empty());
    }

    private boolean findCellForValue(Integer value) {
        List<Cell> cellsThatCanHoldValue = cells.stream()
                .filter(cell -> cell.values().contains(value))
                .limit(2) // we want to know only if there is no suitable cell, there's a single cell or there are more
                .collect(toList());

        boolean solved;
        if (cellsThatCanHoldValue.isEmpty()) {
            System.out.println("No cell can hold value=" + value + ". Cells: " + cells);
            solved = false;
        } else {
            solved = cellsThatCanHoldValue.size() == 1;
        }

        if (solved) {
            Cell onlyPossibleCell = cellsThatCanHoldValue.get(0);
            System.out.println("In " + this + ", only " + onlyPossibleCell + " can hold value " + value);
            onlyPossibleCell.solution(value);
        }

        return solved;
    }

    public boolean isSolved() {
        return unsolvedValues.isEmpty();
    }

    public Set<Integer> unsolvedValues() {
        return unsolvedValues;
    }

    public int ordinal() {
        return ordinal;
    }
}

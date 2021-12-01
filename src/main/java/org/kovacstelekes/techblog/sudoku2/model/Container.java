package org.kovacstelekes.techblog.sudoku2.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.IntStream;

import static com.google.common.base.Preconditions.*;
import static java.util.Collections.unmodifiableList;
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

    public Cell cellAt(int position) {
        CommonConstraints.checkPositionIsValid(position);
        return cells.get(position);
    }

    public List<Cell> cells() {
        return unmodifiableList(cells);
    }

    public void solve() {
        unsolvedValues.clear();
        cells.stream()
                .map(Cell::values)
                .filter(values -> values.size() > 1)
                .forEach(unsolvedValues::addAll);

        unsolvedValues.stream().forEach(this::findCellForValue);
    }

    public void removeSolvedValuesFromCells() {
        cells.stream()
                .filter(Cell::isSolved)
                .forEach(this::removeCellValueFromOtherCells);
    }

    private void removeCellValueFromOtherCells(Cell solvedCell) {
        solvedCell.solution().ifPresent(solution ->
        cells.stream()
                .filter(otherCell -> otherCell != solvedCell)
                .forEach(otherCell -> otherCell.removeValue(solution))
        );
    }

    private void findCellForValue(Integer value) {
        List<Cell> cellsThatCanHoldValue = cells.stream()
                .filter(cell -> cell.values().contains(value))
                .collect(toList());
        if (cellsThatCanHoldValue.isEmpty()) {
            throw new IllegalStateException("No cell can hold value=" + value + ". Cells: " + cells);
        }
        if (cellsThatCanHoldValue.size() == 1) {
            Cell onlyPossibleCell = cellsThatCanHoldValue.get(0);
            System.out.println("In " + this + ", only " + onlyPossibleCell + " can hold value " + value);
            onlyPossibleCell.solution(value);
        }
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

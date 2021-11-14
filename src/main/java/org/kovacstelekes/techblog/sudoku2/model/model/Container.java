package org.kovacstelekes.techblog.sudoku2.model.model;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.base.Preconditions.*;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static org.kovacstelekes.techblog.sudoku.model.CommonConstraints.checkPositionIsValid;

/**
 * A subdivision of the puzzle, holding 9 cells. Position 0 is unused.
 */
class Container {
    private final List<Cell> cells;
    private final String name;
    private final Set<Integer> unsolvedValues = IntStream.range(1, 10).boxed().collect(toCollection(TreeSet::new));;

    protected Container(String name) {
        this.name = name;
        cells = new ArrayList<>(9);
    }

    void addCell(Cell cell) {
        checkNotNull(cell);
        checkState(cells.size() < 9, "% is already full");
        cells.add(cell);
    }

    @Override
    public String toString() {
        return name;
    }

    public Cell cellAt(int position) {
        checkPositionIsValid(position);
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
            System.out.println("In " + name + ", only " + onlyPossibleCell + " can hold value " + value);
            onlyPossibleCell.solution(value);
        }
    }

    public boolean isSolved() {
        return unsolvedValues.isEmpty();
    }

    public Set<Integer> unsolvedValues() {
        return unsolvedValues;
    }
}

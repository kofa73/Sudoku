package org.kovacstelekes.techblog.sudoku2.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.*;
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

    public Stream<IterationOutcome> solve() {
        unsolvedValues.clear();
        cells.stream()
                .filter(cell -> cell.outcome() == Outcome.SEVERAL_POSSIBILITIES)
                .map(Cell::values)
                .forEach(unsolvedValues::addAll);

        return unsolvedValues.stream().map(this::findCellForValue);
    }

    Stream<IterationOutcome> removeSolvedValuesFromCells() {
        return cells.stream()
                .flatMap(this::removeSolvedCellValueFromOtherCells);
    }

    private Stream<IterationOutcome> removeSolvedCellValueFromOtherCells(Cell solvedCell) {
        return solvedCell.solution().map(value ->
                cells.stream()
                        .filter(otherCell -> otherCell != solvedCell)
                        .map(otherCell -> otherCell.removeValue(value))
        ).orElse(Stream.empty());
    }

    private IterationOutcome findCellForValue(Integer value) {
        List<Cell> cellsThatCanHoldValue = cells.stream()
                .filter(cell -> cell.values().contains(value))
                .limit(2).toList();

        Outcome outcome;
        boolean progressed = false;
        if (cellsThatCanHoldValue.isEmpty()) {
//            System.out.println("No cell can hold value=" + value + ". Cells: " + cells);
            outcome = Outcome.INVALID;
        } else {
            outcome = cellsThatCanHoldValue.size() == 1 ? Outcome.UNIQUE_SOLUTION : Outcome.SEVERAL_POSSIBILITIES;
        }

        if (outcome == Outcome.UNIQUE_SOLUTION) {
            Cell onlyPossibleCell = cellsThatCanHoldValue.get(0);
//            System.out.println("In " + this + ", only " + onlyPossibleCell + " can hold value " + value);
            onlyPossibleCell.solution(value);
            progressed = true;
        }

        return new IterationOutcome(progressed, outcome);
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

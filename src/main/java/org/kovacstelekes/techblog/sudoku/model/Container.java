package org.kovacstelekes.techblog.sudoku.model;

import java.util.*;

import static com.google.common.base.Preconditions.*;
import static java.util.Collections.unmodifiableList;
import static org.kovacstelekes.techblog.sudoku.model.CommonConstraints.checkPositionIsValid;

/**
 * A subdivision of the puzzle, holding 9 cells. Position 0 is unused.
 */
public abstract class Container {
    private final int id;
    private final List<Cell> cells;
    private final List<Solvable<Cell>> potentialCellsByDigit = new ArrayList<>();

    protected Container(int id) {
        checkPositionIsValid(id);
        this.id = id;
        cells = new ArrayList<>(9);
        potentialCellsByDigit.add(null); // placeholder, no cell can have value 0
        for (int i = 1; i < 10; i++) {
            potentialCellsByDigit.add(new Solvable<>(this.getClass().getSimpleName() + '#' + id + '@' + i));
        }
    }

    public abstract void addCell(Cell cell);

    protected void safelyAddCell(Cell cell) {
        checkNotNull(cell);
        checkState(cells.size() < 9, "% is already full");
        cells.add(cell);
        for (int i = 1; i < 10; i++) {
            potentialCellsByDigit.get(i).add(cell);
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '#' + id;
    }

    public int id() {
        return id;
    }

    public Cell cellAt(int position) {
        checkPositionIsValid(position);
        return cells.get(position);
    }

    public List<Cell> cells() {
        return unmodifiableList(cells);
    }

    public void solvedCell(Cell cell) {
        checkArgument(cell.solution().isPresent(), "%s is not yet solved", cell);
        int cellValue = cell.solution().get();
        potentialCellsByDigit.get(cellValue).solution(cell);
        for (int i = 1; i <= 9; i++) {
            if (i != cellValue) {
                System.out.println("Excluding " + cell + " as a potential candidate for value " + i);
                Solvable<Cell> potentialCellsHoldingValueI = potentialCellsByDigit.get(i);
                Optional<Cell> cellHoldingValueI = potentialCellsHoldingValueI.exclude(cell);
                if (cellHoldingValueI.isPresent()) {
                    cellHoldingValueI.get().solution(Integer.valueOf(i));
                }
            }
        }

        potentialCellsByDigit.stream()
                .filter(Objects::nonNull)
                .map(Solvable::remainingPossibilities)
                .flatMap(Collection::stream)
                .filter(potentialCell -> potentialCell != cell)
                .forEach(otherCell -> {
                    System.out.println("Excluding " + cellValue + " from " + otherCell);
                    otherCell.exclude(cellValue);
                });
    }

    /**
     * Indicates that the given cell cannot contain the specified value
     * @param cell the cell
     * @param value the value
     */
    void exclude(Cell cell, int value) {
        potentialCellsByDigit.get(value).exclude(cell);
    }
}

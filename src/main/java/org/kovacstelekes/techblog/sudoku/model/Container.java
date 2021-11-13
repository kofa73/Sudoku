package org.kovacstelekes.techblog.sudoku.model;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Collections.unmodifiableList;
import static org.kovacstelekes.techblog.sudoku.model.CommonConstraints.checkPositionIsValid;

/**
 * A subdivision of the puzzle, holding 9 cells. Position 0 is unused.
 */
public abstract class Container {
    private final int id;
    private final List<Cell> cells;

    protected Container(int id) {
        checkPositionIsValid(id);
        this.id = id;
        cells = new ArrayList<>(9);
    }

    public abstract void addCell(Cell cell);

    protected void safelyAddCell(Cell cell) {
        checkNotNull(cell);
        checkState(cells.size() < 9, "% is already full");
        cells.add(cell);
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
}

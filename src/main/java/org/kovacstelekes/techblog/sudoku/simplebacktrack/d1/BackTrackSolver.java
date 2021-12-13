package org.kovacstelekes.techblog.sudoku.simplebacktrack.d1;

import org.kovacstelekes.techblog.SudokuSolver;

public abstract class BackTrackSolver implements SudokuSolver {
    // these store the cell indices pointing into `cells`
    private static final int[][] ROWS;
    private static final int[][] COLUMNS;
    private static final int[][] GRIDS;

    static {
        ROWS = new int[9][];
        COLUMNS = new int[9][];
        GRIDS = new int[9][];
        for (int i = 0; i < 9; i++) {
            ROWS[i] = new int[9];
            COLUMNS[i] = new int[9];
            GRIDS[i] = new int[9];
        }
        for (int cellIndex = 0; cellIndex < 9 * 9; cellIndex++) {
            int rowNumber = rowNumber(cellIndex);
            int columnNumber = columnNumber(cellIndex);
            int gridNumber = (rowNumber / 3) * 3 + columnNumber / 3;
            int positionInGrid = rowNumber % 3 * 3 + columnNumber % 3;

            ROWS[rowNumber][columnNumber] = cellIndex;
            COLUMNS[columnNumber][rowNumber] = cellIndex;
            GRIDS[gridNumber][positionInGrid] = cellIndex;
        }
    }

    private static int columnNumber(int cellIndex) {
        return cellIndex % 9;
    }

    private static int rowNumber(int cellIndex) {
        return cellIndex / 9;
    }

    @Override
    public int[] solve(int[] cellValues) {
        if (boardHasConflicts(cellValues)) {
            return null;
        }
        if (isSolved(cellValues)) {
            return cellValues;
        }

        int indexOfUnsolvedCell = find(0, cellValues);

        if (indexOfUnsolvedCell < 0 || indexOfUnsolvedCell >= 9 * 9) {
            throw new RuntimeException("impossible value for indexOfUnsolvedCell: " + indexOfUnsolvedCell);
        }

        int[] solution = null;
        for (int guessValue = 1; guessValue <= 9; guessValue++) {
            cellValues[indexOfUnsolvedCell] = guessValue;
            solution = solve(cellValues);
            if (solution != null) {
                break;
            }
        }
        if (solution == null) {
            cellValues[indexOfUnsolvedCell] = 0;
        }
        return solution;
    }

    private boolean boardHasConflicts(int[] cellValues) {
        for (int containerIndex = 0; containerIndex < 9; containerIndex++) {
            if (!isValid(ROWS[containerIndex], cellValues)
                    || !isValid(COLUMNS[containerIndex], cellValues)
                    || (!isValid(GRIDS[containerIndex], cellValues))
            ) {
                return true;
            }
        }
        return false;
    }

    abstract int find(int value, int[] array);

    abstract boolean isValid(int[] container, int[] cellValues);

    abstract boolean isSolved(int[] cellValues);
}

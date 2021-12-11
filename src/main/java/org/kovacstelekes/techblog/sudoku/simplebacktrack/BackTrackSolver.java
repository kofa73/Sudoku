package org.kovacstelekes.techblog.sudoku.simplebacktrack;

public abstract class BackTrackSolver {
    // these store the cell indices pointing into `cells`
    static final int[][] ROWS;
    static final int[][] COLUMNS;
    static final int[][] GRIDS;

    final int[] cells;
    private int nChecks = 0;

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

    BackTrackSolver(int[] cells) {
        this.cells = cells.clone();
    }

    public boolean solve() {
        if (isSolved()) {
            return true;
        }
        if (boardHasConflicts()) {
            return false;
        }
        nChecks++;
        int indexOfUnsolvedCell = find(0, cells);

        if (indexOfUnsolvedCell < 0 || indexOfUnsolvedCell >= 9 * 9) {
            throw new RuntimeException("impossible value for indexOfUnsolvedCell: " + indexOfUnsolvedCell);
        }

        boolean solved = false;
        for (int guessValue = 1; guessValue <= 9; guessValue++) {
            cells[indexOfUnsolvedCell] = guessValue;
            solved = solve();
            if (solved) {
                break;
            }
        }
        if (!solved) {
            cells[indexOfUnsolvedCell] = 0;
        }
        return solved;
    }

    public int nChecks() {
        return nChecks;
    }

    private boolean boardHasConflicts() {
        boolean hasConflicts = false;
        for (int containerIndex = 0; containerIndex < 9; containerIndex++) {
            if (!isValid(ROWS[containerIndex])
                    || !isValid(COLUMNS[containerIndex])
                    ||(!isValid(GRIDS[containerIndex]))
            ) {
                hasConflicts = true;
                break;
            }
        }
        return hasConflicts;
    }

    abstract int find(int value, int[] array);

    abstract boolean isValid(int[] container);

    abstract boolean isSolved();

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        for (int cellIndex = 0; cellIndex < cells.length; cellIndex++) {
            int row = rowNumber(cellIndex);
            int colN = columnNumber(cellIndex);
            if (row % 3 == 0 && colN == 0) {
                text.append("+---+---+---\n");
            }
            if (colN % 3 == 0) {
                text.append('|');
            }
            int cellValue = cells[cellIndex];
            if (cellValue == 0) {
                text.append('.');
            } else {
                text.append(cellValue);
            }
            if (colN == 8) {
                text.append("\n");
            }
        }
        return text.toString();
    }
}

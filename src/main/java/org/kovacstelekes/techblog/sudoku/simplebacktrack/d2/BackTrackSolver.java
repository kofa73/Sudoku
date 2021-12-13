package org.kovacstelekes.techblog.sudoku.simplebacktrack.d2;

public class BackTrackSolver {
    // grid top-left corner row and column
    private static final int[] GRID_STARTING_ROWS = {0, 0, 0, 3, 3, 3, 6, 6, 6};
    private static final int[] GRID_STARTING_COLUMNS = {0, 3, 6, 0, 3, 6, 0, 3, 6};

    private final int[][] cells;
    private int nChecks = 0;

    BackTrackSolver(int[] cellValues) {
        if (cellValues.length != 9 * 9) {
            throw new IllegalArgumentException();
        }
        cells = new int[9][];
        int cellIndex = 0;
        for (int rowNumber = 0; rowNumber < 9; rowNumber++) {
            cells[rowNumber] = new int[9];
            for (int columnNumber = 0; columnNumber < 9; columnNumber++) {
                cells[rowNumber][columnNumber] = cellValues[cellIndex];
                cellIndex++;
            }
        }
    }

    public boolean solve() {
        return solve(0, 0);
    }

    private boolean solve(int rowStart, int columnStart) {
        if (boardHasConflicts()) {
            return false;
        }
        nChecks++;
        for (int row = rowStart; row < 9; row++) {
            for (int column = columnStart; column < 9; column++) {
                if (cells[row][column] == 0) {
                    for (int guessValue = 1; guessValue <= 9; guessValue++) {
                        cells[row][column] = guessValue;
                        boolean solved = solve(row, column + 1);
                        if (solved) {
                            return true;
                        }
                    }
                    cells[row][column] = 0;
                    return false;
                }
            }
            columnStart = 0;
        }
        return true;
    }

    public int nChecks() {
        return nChecks;
    }

    private boolean boardHasConflicts() {
        boolean hasConflicts = false;
        for (int containerIndex = 0; containerIndex < 9; containerIndex++) {
            if (!isValidRow(containerIndex)
                    || !isValidColumn(containerIndex)
                    || (!isValidGrid(containerIndex))
            ) {
                hasConflicts = true;
                break;
            }
        }
        return hasConflicts;
    }

    private boolean isValidRow(int rowNumber) {
        boolean[] seenDigit = new boolean[10];
        for (int column = 0; column < 9; column++) {
            int digit = cells[rowNumber][column];
            if (digit != 0) {
                if (seenDigit[digit]) {
                    return false;
                }
                seenDigit[digit] = true;
            }
        }
        return true;
    }

    private boolean isValidColumn(int columnNumber) {
        boolean[] seenDigit = new boolean[10];
        for (int row = 0; row < 9; row++) {
            int digit = cells[row][columnNumber];
            if (digit != 0) {
                if (seenDigit[digit]) {
                    return false;
                }
                seenDigit[digit] = true;
            }
        }
        return true;
    }

    private boolean isValidGrid(int gridNumber) {
        int startRow = GRID_STARTING_ROWS[gridNumber];
        int startColumn = GRID_STARTING_COLUMNS[gridNumber];
        boolean[] seenDigit = new boolean[10];
        for (int row = startRow; row < startRow + 3; row++) {
            for (int column = startColumn; column < startColumn + 3; column++) {
                int digit = cells[row][column];
                if (digit != 0) {
                    if (seenDigit[digit]) {
                        return false;
                    }
                    seenDigit[digit] = true;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                if (row % 3 == 0 && column == 0) {
                    text.append("+---+---+---\n");
                }
                if (column % 3 == 0) {
                    text.append('|');
                }
                int cellValue = cells[row][column];
                if (cellValue == 0) {
                    text.append('.');
                } else {
                    text.append(cellValue);
                }
                if (column == 8) {
                    text.append("\n");
                }
            }
        }
        return text.toString();
    }

    private boolean isSolved() {
        for (int row = 0; row < 9; row++) {
            for (int cellValue : cells[row]) {
                if (cellValue == 0) {
                    return false;
                }
            }
        }
        return true;
    }
}

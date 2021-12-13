package org.kovacstelekes.techblog.sudoku.simplebacktrack.d2;

public class BackTrackSolverWithDeduction {
    // grid top-left corner row and column
    private static final int[] GRID_STARTING_ROWS = {0, 0, 0, 3, 3, 3, 6, 6, 6};
    private static final int[] GRID_STARTING_COLUMNS = {0, 3, 6, 0, 3, 6, 0, 3, 6};

    private int[][] cells;
    private int nChecks = 0;

    BackTrackSolverWithDeduction(int[] cellValues) {
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
        if (boardHasConflicts()) {
            return false;
        }
        return solve(0, 0);
    }

    private boolean solve(int rowStart, int columnStart) {
        deduceValues();
        nChecks++;
        for (int row = rowStart; row < 9; row++) {
            for (int column = columnStart; column < 9; column++) {
                if (cells[row][column] == 0) {
                    boolean[] takenValues = findTakenValues(row, column);
                    for (int guessValue = 1; guessValue <= 9; guessValue++) {
                        if (!takenValues[guessValue]) {
                            int[][] backup = backupCurrentState();
                            cells[row][column] = guessValue;
                            boolean solved = solve(row, column + 1);
                            if (solved) {
                                return true;
                            } else {
                                cells = backup;
                            }
                        }
                    }
                    return false;
                }
            }
            columnStart = 0;
        }
        return true;
    }

    private int[][] backupCurrentState() {
        int[][] backup = new int[9][];
        for (int row = 0; row < 9; row++) {
            backup[row] = new int[9];
            for (int column = 0; column < 9; column++) {
                backup[row][column] = cells[row][column];
            }
        }
        return backup;
    }

    private void deduceValues() {
        boolean progressed = false;
        do {
            progressed = cellsWithSinglePossibleValuesFound();
        } while (progressed);
    }

    private boolean cellsWithSinglePossibleValuesFound() {
        boolean progressed = false;
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                if (cells[row][column] == 0) {
                    boolean[] takenValues = findTakenValues(row, column);

                    int singleValue = 0;
                    for (int possibleValue = 1; possibleValue <= 9; possibleValue++) {
                        if (!takenValues[possibleValue]) {
                            if (singleValue != 0) {
                                // already found a possible value; now another -> not unique
                                singleValue = -1;
                                break;
                            } else {
                                singleValue = possibleValue;
                            }
                        }
                    }
                    if (singleValue > 0) {
                        cells[row][column] = singleValue;
                        progressed = true;
                    }
                }
            }
        }
        return progressed;
    }

    private boolean[] findTakenValues(int row, int column) {
        boolean[] takenValues = new boolean[10];
        for (int searchRow = 0; searchRow < 9; searchRow++) {
            if (searchRow != row) {
                takenValues[cells[searchRow][column]] = true;
            }
        }
        for (int searchColumn = 0; searchColumn < 9; searchColumn++) {
            if (searchColumn != column) {
                takenValues[cells[row][searchColumn]] = true;
            }
        }

        int gridNumber = (row / 3) * 3 + column / 3;
        int startRow = GRID_STARTING_ROWS[gridNumber];
        int startColumn = GRID_STARTING_COLUMNS[gridNumber];
        for (int searchRow = startRow; searchRow < startRow + 3; searchRow++) {
            for (int searchColumn = startColumn; searchColumn < startColumn + 3; searchColumn++) {
                if (!(searchRow == row && searchColumn == column)) {
                    takenValues[cells[searchRow][searchColumn]] = true;
                }
            }
        }
        return takenValues;
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

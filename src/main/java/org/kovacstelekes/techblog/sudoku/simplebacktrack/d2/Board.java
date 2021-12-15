package org.kovacstelekes.techblog.sudoku.simplebacktrack.d2;

import org.kovacstelekes.techblog.BoardUtils;

class Board implements Cloneable {
    /**
     * Cell values, 9x9. 0 is unsolved; 1..9 are solved values
     */
    private final int[][] board;
    /**
     * indexed by [row][column][digit], where row and column are 0..9, digit 1..9 with
     * digit = 0 being unused (for convenience)
     */
    private final boolean[][][] takenDigits;

    private static final boolean[] NO_DIGITS_POSSIBLE = null;

    // grid top-left corner row and column for 3x3 grids
    private static final int[] GRID_STARTING_ROWS = {0, 0, 0, 3, 3, 3, 6, 6, 6};
    private static final int[] GRID_STARTING_COLUMNS = {0, 3, 6, 0, 3, 6, 0, 3, 6};

    // indexed by row/column/grid number (0..8); value is 9-element array of row or column numbers
    private final static int[][] ROW_NUMBERS_FOR_ROWS = new int[9][];
    private final static int[][] COLUMN_NUMBERS_FOR_ROWS = new int[9][];
    private final static int[][] ROW_NUMBERS_FOR_COLUMNS = new int[9][];
    private final static int[][] COLUMN_NUMBERS_FOR_COLUMNS = new int[9][];
    private final static int[][] ROW_NUMBERS_FOR_GRIDS = new int[9][];
    private final static int[][] COLUMN_NUMBERS_FOR_GRIDS = new int[9][];

    static {
        for (int containerIndex = 0; containerIndex < 9; containerIndex++) {
            setupRows(containerIndex);
            setupColumns(containerIndex);
            setupGrids(containerIndex);
        }
    }

    private static void setupRows(int rowNumber) {
        ROW_NUMBERS_FOR_ROWS[rowNumber] = new int[9];
        COLUMN_NUMBERS_FOR_ROWS[rowNumber] = new int[9];
        for (int cellIndexInContainer = 0; cellIndexInContainer < 9; cellIndexInContainer++) {
            ROW_NUMBERS_FOR_ROWS[rowNumber][cellIndexInContainer] = rowNumber;
            COLUMN_NUMBERS_FOR_ROWS[rowNumber][cellIndexInContainer] = cellIndexInContainer;
        }
    }

    private static void setupColumns(int columnNumber) {
        ROW_NUMBERS_FOR_COLUMNS[columnNumber] = new int[9];
        COLUMN_NUMBERS_FOR_COLUMNS[columnNumber] = new int[9];
        for (int cellIndexInContainer = 0; cellIndexInContainer < 9; cellIndexInContainer++) {
            ROW_NUMBERS_FOR_COLUMNS[columnNumber][cellIndexInContainer] = cellIndexInContainer;
            COLUMN_NUMBERS_FOR_COLUMNS[columnNumber][cellIndexInContainer] = columnNumber;
        }
    }

    private static void setupGrids(int gridNumber) {
        ROW_NUMBERS_FOR_GRIDS[gridNumber] = new int[9];
        COLUMN_NUMBERS_FOR_GRIDS[gridNumber] = new int[9];
        for (int cellIndexInContainer = 0; cellIndexInContainer < 9; cellIndexInContainer++) {
            ROW_NUMBERS_FOR_GRIDS[gridNumber][cellIndexInContainer] = GRID_STARTING_ROWS[gridNumber] + cellIndexInContainer / 3;
            COLUMN_NUMBERS_FOR_GRIDS[gridNumber][cellIndexInContainer] = GRID_STARTING_COLUMNS[gridNumber] + cellIndexInContainer % 3;
        }
    }

    Board(int[][] board) {
        this.board = new int[9][];
        for (int row = 0; row < 9; row++) {
            this.board[row] = board[row].clone();
        }
        takenDigits = initialiseTakenDigits();
        populateTakenDigits();
    }

    private Board(int[][] board, boolean[][][] takenDigits) {
        this.board = board;
        this.takenDigits = takenDigits;
    }

    @Override
    public Board clone() {
        int[][] copyOfValues = new int[9][];
        boolean[][][] copyOfTakenDigits = new boolean[9][][];
        for (int row = 0; row < 9; row++) {
            copyOfValues[row] = board[row].clone();
            copyOfTakenDigits[row] = new boolean[9][];
            for (int column = 0; column < 9; column++) {
                boolean[] takenDigitsForCell = takenDigits[row][column];
                copyOfTakenDigits[row][column] = takenDigitsForCell == NO_DIGITS_POSSIBLE ?
                        NO_DIGITS_POSSIBLE : takenDigits[row][column].clone();
            }
        }
        return new Board(copyOfValues, copyOfTakenDigits);
    }

    void deduceValues() {
        boolean updated;
        do {
            updated = solveCellsWithSinglePossibleValue()
                    || digitsWithSinglePossibleLocationFound();
        } while (updated);
    }

    private boolean[][][] initialiseTakenDigits() {
        boolean[][][] takenDigits = new boolean[9][][];
        for (int row = 0; row < 9; row++) {
            takenDigits[row] = new boolean[9][];
        }
        return takenDigits;
    }

    private void populateTakenDigits() {
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                if (!unsolvedCellAt(row, column)) {
                    // already solved; let's not allocate anything
                    takenDigits[row][column] = NO_DIGITS_POSSIBLE;
                } else {
                    takenDigits[row][column] = findTakenDigitsForCellAt(row, column);
                }
            }
        }
    }

    private void updateTakenDigitsForNeighboursOfCellAt(int row, int column) {
        int digit = board[row][column];
        int gridNumber = gridNumberFor(row, column);
        int[] rowNumbersForGrid = ROW_NUMBERS_FOR_GRIDS[gridNumber];
        int[] columnNumbersForGrid = COLUMN_NUMBERS_FOR_GRIDS[gridNumber];

        for (int cellIndex = 0; cellIndex < 9; cellIndex++) {
            digitIsTakenForCellAt(row, cellIndex, digit);
            digitIsTakenForCellAt(cellIndex, column, digit);
            digitIsTakenForCellAt(
                    rowNumbersForGrid[cellIndex],
                    columnNumbersForGrid[cellIndex],
                    digit
            );
        }
    }

    private void digitIsTakenForCellAt(int rowNumber, int columnNumber, int digit) {
        boolean[] takenDigitsForCell = takenDigits[rowNumber][columnNumber];
        if (takenDigitsForCell != NO_DIGITS_POSSIBLE) {
            takenDigitsForCell[digit] = true;
        }
    }

    private int gridNumberFor(int row, int column) {
        return (row / 3) * 3 + column / 3;
    }

    private boolean[] findTakenDigitsForCellAt(int row, int column) {
        boolean[] takenDigits = new boolean[10];
        registerTakenDigitsFromCellsOfContainer(ROW_NUMBERS_FOR_ROWS[row], COLUMN_NUMBERS_FOR_ROWS[row], takenDigits, row, column);
        registerTakenDigitsFromCellsOfContainer(ROW_NUMBERS_FOR_COLUMNS[column], COLUMN_NUMBERS_FOR_COLUMNS[column], takenDigits, row, column);
        int grid = gridNumberFor(row, column);
        registerTakenDigitsFromCellsOfContainer(ROW_NUMBERS_FOR_GRIDS[grid], COLUMN_NUMBERS_FOR_GRIDS[grid], takenDigits, row, column);

        return takenDigits;
    }

    // the digits from cells of this container must be excluded as options
    // of the cell at [row;column]. 0 (unsolved) is registered as well, but
    // that is unused (never checked) and won't cause problems.
    private void registerTakenDigitsFromCellsOfContainer(
            int[] rowsOfContainer,
            int[] columnsOfContainer,
            boolean[] takenDigits,
            int row,
            int column
    ) {
        for (int cellIndex = 0; cellIndex < 9; cellIndex++) {
            int searchRow = rowsOfContainer[cellIndex];
            int searchColumn = columnsOfContainer[cellIndex];
            boolean sameCell = searchRow == row && searchColumn == column;
            if (!sameCell) {
                takenDigits[board[searchRow][searchColumn]] = true;
            }
        }
    }

    public boolean unsolvedCellAt(int row, int column) {
        return board[row][column] == 0;
    }

    public boolean canTake(int guessValue, int row, int column) {
        return !takenDigits[row][column][guessValue];
    }

    public void setCell(int value, int row, int column) {
        board[row][column] = value;
        takenDigits[row][column] = NO_DIGITS_POSSIBLE;
        updateTakenDigitsForNeighboursOfCellAt(row, column);
    }

    public int[][] board() {
        return board;
    }

    private boolean solveCellsWithSinglePossibleValue() {
        boolean progressed = false;
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                if (unsolvedCellAt(row, column)) {
                    int singleValue = findSinglePossibleValue(row, column);

                    if (singleValue > 0) {
                        setCell(singleValue, row, column);
                        progressed = true;
                    }
                }
            }
        }
        return progressed;
    }

    private int findSinglePossibleValue(int row, int column) {
        boolean[] takenDigitsForCell = takenDigits[row][column];

        int singleValue = 0;
        for (int possibleValue = 1; possibleValue <= 9; possibleValue++) {
            if (!takenDigitsForCell[possibleValue]) {
                if (singleValue != 0) {
                    // already found a possible value; now another -> not unique
                    singleValue = 0;
                    break;
                } else {
                    singleValue = possibleValue;
                }
            }
        }
        return singleValue;
    }

    private boolean digitsWithSinglePossibleLocationFound() {
        boolean solvedAny = false;
        for (int digit = 1; digit <= 9; digit++) {
            for (int containerIndex = 0; containerIndex < 9; containerIndex++) {
                solvedAny |= solveDigitsWithSinglePossibleLocation(digit, ROW_NUMBERS_FOR_ROWS[containerIndex], COLUMN_NUMBERS_FOR_ROWS[containerIndex]);
                solvedAny |= solveDigitsWithSinglePossibleLocation(digit, ROW_NUMBERS_FOR_COLUMNS[containerIndex], COLUMN_NUMBERS_FOR_COLUMNS[containerIndex]);
                solvedAny |= solveDigitsWithSinglePossibleLocation(digit, ROW_NUMBERS_FOR_GRIDS[containerIndex], COLUMN_NUMBERS_FOR_GRIDS[containerIndex]);
            }
        }
        return solvedAny;
    }

    private boolean solveDigitsWithSinglePossibleLocation(int digit, int[] rows, int[] columns) {
        if (containerNotSolvedForDigit(digit, rows, columns)) {
            int singlePossibleCellIndex = -1;
            for (int cellIndex = 0; cellIndex < 9; cellIndex++) {
                int row = rows[cellIndex];
                int column = columns[cellIndex];
                if (unsolvedCellAt(row, column)) {
                    boolean[] takenDigitsForCell = takenDigits[row][column];
                    if (!takenDigitsForCell[digit]) {
                        if (singlePossibleCellIndex == -1) {
                            singlePossibleCellIndex = cellIndex;
                        } else {
                            // found the 2nd potential location -> not unique
                            singlePossibleCellIndex = -1;
                            break;
                        }
                    }
                }
            }
            if (singlePossibleCellIndex != -1) {
                setCell(digit, rows[singlePossibleCellIndex], columns[singlePossibleCellIndex]);
                return true;
            }
        }
        return false;
    }

    private boolean containerNotSolvedForDigit(int digit, int[] rows, int[] columns) {
        for (int cellIndex = 0; cellIndex < 9; cellIndex++) {
            if (board[rows[cellIndex]][columns[cellIndex]] == digit) {
                return false;
            }
        }
        return true;
    }

    // consistency check
    boolean hasConflicts() {
        return rowsHaveConflicts() || columnsHaveConflicts() || gridsHaveConflicts();
    }

    private boolean rowsHaveConflicts() {
        return containersHaveConflicts(ROW_NUMBERS_FOR_ROWS, COLUMN_NUMBERS_FOR_ROWS);
    }

    private boolean columnsHaveConflicts() {
        return containersHaveConflicts(ROW_NUMBERS_FOR_COLUMNS, COLUMN_NUMBERS_FOR_COLUMNS);
    }

    private boolean gridsHaveConflicts() {
        return containersHaveConflicts(ROW_NUMBERS_FOR_GRIDS, COLUMN_NUMBERS_FOR_GRIDS);
    }

    private boolean containersHaveConflicts(int[][] rowsForContainers, int[][] columnsForContainers) {
        for (int containerIndex = 0; containerIndex < 9; containerIndex++) {
            if (containerHasConflict(containerIndex, rowsForContainers, columnsForContainers)) {
                return true;
            }
        }
        return false;
    }

    private boolean containerHasConflict(int containerIndex, int[][] rowsForContainer, int[][] columnsForContainer) {
        int[] rows = rowsForContainer[containerIndex];
        int[] columns = columnsForContainer[containerIndex];
        boolean[] seenDigit = new boolean[10];
        for (int cellIndex = 0; cellIndex < 9; cellIndex++) {
            int row = rows[cellIndex];
            int column = columns[cellIndex];
            int digit = board[row][column];
            if (digit != 0) {
                if (seenDigit[digit]) {
                    return true;
                }
                seenDigit[digit] = true;
            }
        }
        return false;
    }
    // consistency check ends

    @Override
    public String toString() {
        return BoardUtils.format(BoardUtils.toCellValues(board));
    }

    // for debugging
    String formatTakenDigits() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < 9; row++) {
            if (row % 3 == 0) {
                sb.append("+---------------------------------+---------------------------------+---------------------------------\n");
            }
            for (int column = 0; column < 9; column++) {
                if (column % 3 == 0) {
                    sb.append("|");
                }
                sb.append('[');
                for (int digit = 1; digit <= 9; digit++) {
                    if (takenDigits[row][column] == null) {
                        sb.append(digit);
                    } else {
                        sb.append(takenDigits[row][column][digit] ? "" + digit : " ");
                    }
                }
                sb.append(']');
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}

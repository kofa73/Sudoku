package org.kovacstelekes.techblog.sudoku.simplebacktrack.d2;

import org.kovacstelekes.techblog.BoardUtils;

class CountingBoard implements Cloneable {
    /**
     * Cell values, 9x9. 0 is unsolved; 1..9 are solved values
     */
    private final int[][] board;
    
    /**
     * Indexed by [row][column][digit], where row and column are 0..9, digit 1..9.
     * 1 means the digit is taken; 0 means it's a possibility.
     */
    private final int[][][] takenDigits;

    private boolean valid = true;

    private static final int[] NO_DIGITS_POSSIBLE = null;

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

    CountingBoard(int[][] board) {
        this.board = new int[9][];
        for (int row = 0; row < 9; row++) {
            this.board[row] = board[row].clone();
        }
        takenDigits = initialiseTakenDigits();
        populateTakenDigits();
        assertThatTakenDigitsCountIsCorrect();
    }

    private CountingBoard(int[][] board, int[][][] takenDigits) {
        this.board = board;
        this.takenDigits = takenDigits;
        assertThatTakenDigitsCountIsCorrect();
    }

    @Override
    public CountingBoard clone() {
        if (!valid) {
            throw new IllegalStateException();
        }
        assertThatTakenDigitsCountIsCorrect();
        int[][] copyOfValues = new int[9][];
        int[][][] copyOfTakenDigits = new int[9][][];
        for (int row = 0; row < 9; row++) {
            copyOfValues[row] = board[row].clone();
            copyOfTakenDigits[row] = new int[9][];
            for (int column = 0; column < 9; column++) {
                int[] takenDigitsForCell = takenDigits[row][column];
                copyOfTakenDigits[row][column] = takenDigitsForCell == NO_DIGITS_POSSIBLE ?
                        NO_DIGITS_POSSIBLE : takenDigits[row][column].clone();
            }
        }
        return new CountingBoard(copyOfValues, copyOfTakenDigits);
    }

    void deduceValues() {
        boolean updated;
        do {
            updated = solveCellsWithSinglePossibleValue()
                    || digitsWithSinglePossibleLocationFound();
        } while (updated && valid);
    }

    private int[][][] initialiseTakenDigits() {
        int[][][] takenDigits = new int[9][][];
        for (int row = 0; row < 9; row++) {
            takenDigits[row] = new int[9][];
            for (int column = 0; column < 9; column++) {
                if (unsolvedCellAt(row, column)) {
                    takenDigits[row][column] = new int[10];
                } else {
                    takenDigits[row][column] = NO_DIGITS_POSSIBLE;
                }
            }
        }
        return takenDigits;
    }

    private void populateTakenDigits() {
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                if (unsolvedCellAt(row, column)) {
                    findTakenDigitsForCellAt(row, column);
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
        assertThatTakenDigitsCountIsCorrect();
        if (digit != 0) {
            int[] takenDigitsOfCell = takenDigits[rowNumber][columnNumber];
            if (takenDigitsOfCell != NO_DIGITS_POSSIBLE && takenDigitsOfCell[digit] == 0) {
                takenDigitsOfCell[digit] = 1;
                takenDigitsOfCell[0] += 1;
                if (takenDigitsOfCell[0] == 9) {
                    takenDigits[rowNumber][columnNumber] = NO_DIGITS_POSSIBLE;
                }
            }
            assertThatTakenDigitsCountIsCorrect();
        }
    }

    private int gridNumberFor(int row, int column) {
        return (row / 3) * 3 + column / 3;
    }

    private void findTakenDigitsForCellAt(int row, int column) {
        registerTakenDigitsFromCellsOfContainer(ROW_NUMBERS_FOR_ROWS[row], COLUMN_NUMBERS_FOR_ROWS[row], row, column);
        registerTakenDigitsFromCellsOfContainer(ROW_NUMBERS_FOR_COLUMNS[column], COLUMN_NUMBERS_FOR_COLUMNS[column], row, column);
        int grid = gridNumberFor(row, column);
        registerTakenDigitsFromCellsOfContainer(ROW_NUMBERS_FOR_GRIDS[grid], COLUMN_NUMBERS_FOR_GRIDS[grid], row, column);
    }

    // the digits from cells of this container must be excluded as options
    // of the cell at [row;column]. 0 (unsolved) is registered as well, but
    // that is unused (never checked) and won't cause problems.
    private void registerTakenDigitsFromCellsOfContainer(
            int[] rowsOfContainer,
            int[] columnsOfContainer,
            int row,
            int column
    ) {
        for (int cellIndex = 0; cellIndex < 9; cellIndex++) {
            int searchRow = rowsOfContainer[cellIndex];
            int searchColumn = columnsOfContainer[cellIndex];
            boolean sameCell = searchRow == row && searchColumn == column;
            if (!sameCell) {
                int digitInNeighbouringCell = board[searchRow][searchColumn];
                digitIsTakenForCellAt(row, column, digitInNeighbouringCell);
                assertThatTakenDigitsCountIsCorrect();
            }
        }
    }

    public boolean unsolvedCellAt(int row, int column) {
        return board[row][column] == 0;
    }

    public boolean canTake(int guessValue, int row, int column) {
        assertThatTakenDigitsCountIsCorrect();
        if (takenDigits[row][column] == null) {
            return false;
        }
        return takenDigits[row][column][guessValue] == 0;
    }

    public void setCell(int value, int row, int column) {
        board[row][column] = value;
        takenDigits[row][column] = NO_DIGITS_POSSIBLE;
        assertThatTakenDigitsCountIsCorrect();
        updateTakenDigitsForNeighboursOfCellAt(row, column);
    }

    public int[][] board() {
        return board;
    }

    private boolean solveCellsWithSinglePossibleValue() {
        if (!valid) {
            return false;
        }
        boolean progressed = false;
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                if (unsolvedCellAt(row, column)) {
                    int singleValue = findSinglePossibleValue(row, column);
                    if (!valid) {
                        return false;
                    }

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
        assertThatTakenDigitsCountIsCorrect();
        int[] takenDigitsForCell = takenDigits[row][column];
        if (takenDigitsForCell == NO_DIGITS_POSSIBLE) {
            valid = false;
            return 0;
        }
        if (takenDigitsForCell[0] != 8) {
            return 0;
        }

        for (int possibleValue = 1; possibleValue <= 9; possibleValue++) {
            if (takenDigitsForCell[possibleValue] == 0) {
                return possibleValue;
            }
        }
        return 0;
    }

    private boolean digitsWithSinglePossibleLocationFound() {
        if (!valid) {
            return false;
        }
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
                    assertThatTakenDigitsCountIsCorrect();
                    int[] takenDigitsForCell = takenDigits[row][column];
                    if (takenDigitsForCell == NO_DIGITS_POSSIBLE) {
                        return false;
                    }
                    if (takenDigitsForCell[digit] == 0) {
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

    boolean isValid() {
        return valid;
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
        int[] seenDigit = new int[10];
        for (int cellIndex = 0; cellIndex < 9; cellIndex++) {
            int row = rows[cellIndex];
            int column = columns[cellIndex];
            int digit = board[row][column];
            if (digit != 0) {
                if (seenDigit[digit] == 1) {
                    return true;
                }
                seenDigit[digit] = 1;
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
                    assertThatTakenDigitsCountIsCorrect();
                    if (takenDigits[row][column] == null) {
                        sb.append(digit);
                    } else {
                        sb.append(takenDigits[row][column][digit] == 1 ? "" + digit : " ");
                    }
                }
                sb.append(']');
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    void assertThatTakenDigitsCountIsCorrect() {
//        for (int row = 0; row < 9; row++) {
//            for (int column = 0; column < 9; column++) {
//                int[] takenDigitsForCell = takenDigits[row][column];
//                if (takenDigitsForCell != null) {
//                    int takenCount = 0;
//                    for (int digit = 1; digit <=9 ; digit++) {
//                        if (takenDigitsForCell[digit] == 1) {
//                            takenCount++;
//                        }
//                    }
//                    if (takenCount != takenDigitsForCell[0]) {
//                        throw new RuntimeException();
//                    }
//                }
//            }
//        }
    }
}

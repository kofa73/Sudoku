package org.kovacstelekes.techblog.sudoku.simplebacktrack.d2;

import org.kovacstelekes.techblog.BoardUtils;
import org.kovacstelekes.techblog.SudokuSolver;
import org.kovacstelekes.techblog.sudoku2.model.Board;

public class BackTrackSolverWithMoreDeduction implements SudokuSolver {
    // grid top-left corner row and column
    private static final int[] GRID_STARTING_ROWS = {0, 0, 0, 3, 3, 3, 6, 6, 6};
    private static final int[] GRID_STARTING_COLUMNS = {0, 3, 6, 0, 3, 6, 0, 3, 6};
    private static final boolean[] NO_DIGITS_POSSIBLE = null;//new boolean[10];

    private static class SolutionContext implements Cloneable {
        private final int[][] board;
        private final boolean[][][] takenValues;
        SolutionContext(int[][] board) {
            this.board = new int[9][];
            for (int row = 0; row < 9; row++) {
                this.board[row] = board[row].clone();
            }
            takenValues = createTakenValuesField();
            refreshTakenValues();
        }

        private SolutionContext(int[][] board, boolean[][][] takenValues) {
            this.board = board;
            this.takenValues = takenValues;
        }

        public SolutionContext clone() {
            int[][] copyOfValues = new int[9][];
            boolean[][][] copyOfTakenValues = new boolean[9][][];
            for (int row = 0; row < 9; row++) {
                copyOfValues[row] = board[row].clone();
                copyOfTakenValues[row] = new boolean[9][];
                for (int column = 0; column < 9; column++) {
                    boolean[] takenValuesForCell = takenValues[row][column];
                    copyOfTakenValues[row][column] = takenValuesForCell == NO_DIGITS_POSSIBLE ?
                            NO_DIGITS_POSSIBLE : takenValues[row][column].clone();
                }
            }
            return new SolutionContext(copyOfValues, copyOfTakenValues);
        }

        private boolean[][][] createTakenValuesField() {
            boolean[][][] takenValues = new boolean[9][][];
            for (int row = 0; row < 9; row++) {
                takenValues[row] = new boolean[9][];
            }
            return takenValues;
        }

        void refreshTakenValues() {
            for (int row = 0; row < 9; row++) {
                for (int column = 0; column < 9; column++) {
                    if (board[row][column] != 0) {
                        // already solved; let's not allocate anything
                        takenValues[row][column] = NO_DIGITS_POSSIBLE;
                    } else {
                        takenValues[row][column] = findTakenValues(row, column, board);
                    }
                }
            }
        }

        void updateValuesAffectedBy(int row, int column) {
            int digit = board[row][column];
            for (int scanColumn = 0; scanColumn < 9; scanColumn++) {
                boolean[] takenValuesForCell = takenValues[row][scanColumn];
                if (takenValuesForCell != NO_DIGITS_POSSIBLE) {
                    takenValuesForCell[digit] = true;
                }
            }
            for (int scanRow = 0; scanRow < 9; scanRow++) {
                boolean[] takenValuesForCell = takenValues[scanRow][column];
                if (takenValuesForCell != NO_DIGITS_POSSIBLE) {
                    takenValuesForCell[digit] = true;
                }
            }
            int grid = (row / 3) * 3 + column / 3;
            int startRow = GRID_STARTING_ROWS[grid];
            int startColumn = GRID_STARTING_COLUMNS[grid];
            for (int scanRow = startRow; scanRow < startRow + 3; scanRow++) {
                for (int scanColumn = startColumn; scanColumn < startColumn + 3; scanColumn++) {
                    boolean[] takenValuesForCell = takenValues[scanRow][column];
                    if (takenValuesForCell != NO_DIGITS_POSSIBLE) {
                        takenValuesForCell[digit] = true;
                    }
                }
            }
        }

        private boolean[] findTakenValues(int row, int column, int[][] board) {
            boolean[] takenValues = new boolean[10];
            for (int searchRow = 0; searchRow < 9; searchRow++) {
                if (searchRow != row) {
                    takenValues[board[searchRow][column]] = true;
                }
            }
            for (int searchColumn = 0; searchColumn < 9; searchColumn++) {
                if (searchColumn != column) {
                    takenValues[board[row][searchColumn]] = true;
                }
            }

            int gridNumber = (row / 3) * 3 + column / 3;
            int startRow = GRID_STARTING_ROWS[gridNumber];
            int startColumn = GRID_STARTING_COLUMNS[gridNumber];
            for (int searchRow = startRow; searchRow < startRow + 3; searchRow++) {
                for (int searchColumn = startColumn; searchColumn < startColumn + 3; searchColumn++) {
                    if (!(searchRow == row && searchColumn == column)) {
                        takenValues[board[searchRow][searchColumn]] = true;
                    }
                }
            }
            return takenValues;
        }
    }

//    static {
//        for (int i = 1; i <= 9; i++) {
//            NO_DIGITS_POSSIBLE[i] = true;
//        }
//    }

    @Override
    public int[] solve(int[] cellValues) {
        int[][] board = BoardUtils.toBoard(cellValues);
        if (boardHasConflicts(board)) {
            return null;
        }

        SolutionContext context = new SolutionContext(board);

        int[][] solution = solve(context, 0, 0, 0);
        return BoardUtils.toCellValues(solution);
    }

    private int[][] solve(SolutionContext context, int rowStart, int columnStart, int prefix) {
        deduceValues(context);
        if (boardHasConflicts(context.board)) {
            return null;
        }
        for (int row = rowStart; row < 9; row++) {
            for (int column = columnStart; column < 9; column++) {
                if (context.board[row][column] == 0) {
                    boolean[] takenValues = context.takenValues[row][column];
                    for (int guessValue = 1; guessValue <= 9; guessValue++) {
                        if (!takenValues[guessValue]) {
                            SolutionContext backup = context.clone();
                            context.board[row][column] = guessValue;
                            context.updateValuesAffectedBy(row, column);
                            int[][] solution = solve(context, row, column + 1, prefix + 1);
                            if (solution != null) {
                                return solution;
                            } else {
                                context = backup;
                            }
                        }
                    }
                    return null;
                }
            }
            columnStart = 0;
        }
        return context.board;
    }

    private void deduceValues(SolutionContext context) {
        // row;column;digit not possible at this position
        boolean boardUpdated = false;
        do {
            boardUpdated = cellsWithSinglePossibleValueFound(context)
                    || digitsWithSinglePossibleLocationFound(context);
        } while (boardUpdated);
    }

    private boolean digitsWithSinglePossibleLocationFound(SolutionContext context) {
        boolean[][][] takenValues = context.takenValues;
        int[][] board = context.board;

        for (int row = 0; row < 9; row++) {
            for (int digit = 1; digit <= 9; digit++) {
                if (find(digit, board[row]) == -1) { // digit not yet solved
                    int singlePossibleLocation = -1;
                    for (int column = 0; column < 9; column++) {
                        if (board[row][column] == 0) {
                            boolean[] takenValuesForCell = takenValues[row][column];
                            if (!takenValuesForCell[digit]) {
                                if (singlePossibleLocation == -1) {
                                    singlePossibleLocation = column;
                                } else {
                                    // found the 2nd potential location -> not unique
                                    singlePossibleLocation = -1;
                                    break;
                                }
                            }
                        }
                    }
                    if (singlePossibleLocation != -1) {
                        board[row][singlePossibleLocation] = digit;
                        takenValues[row][singlePossibleLocation] = NO_DIGITS_POSSIBLE;
                        context.updateValuesAffectedBy(row, singlePossibleLocation);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    int find(int value, int[] array) {
        for (int index = 0; index < array.length; index++) {
            if (array[index] == value) {
                return index;
            }
        }
        return -1;
    }

    private boolean cellsWithSinglePossibleValueFound(SolutionContext context) {
        boolean[][][] takenValues = context.takenValues;
        int[][] board = context.board;

        boolean progressed = false;
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                if (boardHasConflicts(board)) {
                    return false;
                }
                if (board[row][column] == 0) {
                    boolean[] takenValuesForCell = takenValues[row][column];

                    int singleValue = 0;
                    for (int possibleValue = 1; possibleValue <= 9; possibleValue++) {
                        if (!takenValuesForCell[possibleValue]) {
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
                        board[row][column] = singleValue;
                        takenValues[row][column] = NO_DIGITS_POSSIBLE;
                        progressed = true;
                    }
                }
            }
        }
        return progressed;
    }

    private boolean boardHasConflicts(int[][] board) {
        return rowsHaveConflicts(board) || columnsHaveConflicts(board) || gridsHaveConflicts(board);
    }

    private boolean rowsHaveConflicts(int[][] board) {
        for (int containerIndex = 0; containerIndex < 9; containerIndex++) {
            if (!isValidRow(board[containerIndex])) {
                return true;
            }
        }
        return false;
    }

    private boolean columnsHaveConflicts(int[][] board) {
        for (int containerIndex = 0; containerIndex < 9; containerIndex++) {
            if (!isValidColumn(containerIndex, board)) {
                return true;
            }
        }
        return false;
    }

    private boolean gridsHaveConflicts(int[][] board) {
        for (int containerIndex = 0; containerIndex < 9; containerIndex++) {
            if ((!isValidGrid(containerIndex, board))) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidRow(int[] row) {
        boolean[] seenDigit = new boolean[10];
        for (int column = 0; column < 9; column++) {
            int digit = row[column];
            if (digit != 0) {
                if (seenDigit[digit]) {
                    return false;
                }
                seenDigit[digit] = true;
            }
        }
        return true;
    }

    private boolean isValidColumn(int columnNumber, int[][] board) {
        boolean[] seenDigit = new boolean[10];
        for (int row = 0; row < 9; row++) {
            int digit = board[row][columnNumber];
            if (digit != 0) {
                if (seenDigit[digit]) {
                    return false;
                }
                seenDigit[digit] = true;
            }
        }
        return true;
    }

    private boolean isValidGrid(int gridNumber, int[][] board) {
        int startRow = GRID_STARTING_ROWS[gridNumber];
        int startColumn = GRID_STARTING_COLUMNS[gridNumber];
        boolean[] seenDigit = new boolean[10];
        for (int row = startRow; row < startRow + 3; row++) {
            for (int column = startColumn; column < startColumn + 3; column++) {
                int digit = board[row][column];
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
}

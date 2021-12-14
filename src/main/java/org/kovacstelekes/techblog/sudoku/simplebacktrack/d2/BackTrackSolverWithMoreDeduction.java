package org.kovacstelekes.techblog.sudoku.simplebacktrack.d2;

import org.kovacstelekes.techblog.BoardUtils;
import org.kovacstelekes.techblog.SudokuSolver;

import java.util.Arrays;

public class BackTrackSolverWithMoreDeduction implements SudokuSolver {
    // grid top-left corner row and column
    private static final int[] GRID_STARTING_ROWS = {0, 0, 0, 3, 3, 3, 6, 6, 6};
    private static final int[] GRID_STARTING_COLUMNS = {0, 3, 6, 0, 3, 6, 0, 3, 6};
    private static final boolean[] NO_DIGITS_POSSIBLE = new boolean[10];

    @Override
    public int[] solve(int[] cellValues) {
        int[][] board = BoardUtils.toBoard(cellValues);
        if (boardHasConflicts(board)) {
            return null;
        }
        int[][] solution = solve(board, 0, 0);
        return BoardUtils.toCellValues(solution);
    }

    private int[][] solve(int[][] board, int rowStart, int columnStart) {
        deduceValues(board);
        if (boardHasConflicts(board)) {
            return null;
        }
        for (int row = rowStart; row < 9; row++) {
            for (int column = columnStart; column < 9; column++) {
                if (board[row][column] == 0) {
                    boolean[] takenValues = findTakenValues(row, column, board);
                    for (int guessValue = 1; guessValue <= 9; guessValue++) {
                        if (!takenValues[guessValue]) {
                            int[][] backup = copyOf(board);
                            board[row][column] = guessValue;
                            int[][] solution = solve(board, row, column + 1);
                            if (solution != null) {
                                return solution;
                            } else {
                                board = backup;
                            }
                        }
                    }
                    return null;
                }
            }
            columnStart = 0;
        }
        return board;
    }

    private int[][] copyOf(int[][] board) {
        int[][] backup = new int[9][];
        for (int row = 0; row < 9; row++) {
            backup[row] = Arrays.copyOf(board[row], 9);
        }
        return backup;
    }

    private void deduceValues(int[][] board) {
        // row;column;digit not possible at this position
        boolean[][][] takenValues = findTakenValues(board);
        boolean progressed;
        do {
            progressed = cellsWithSinglePossibleValueFound(board, takenValues)
                   /* || digitsWithSinglePossibleLocationFound(board)*/;
        } while (progressed);
    }

    private boolean[][][] findTakenValues(int[][] board) {
        boolean[][][] takenValues = new boolean[9][][];
        for (int row = 0; row < 9; row++) {
            takenValues[row] = new boolean[9][];
            for (int column = 0; column < 9; column++) {
                if (board[row][column] != 0) {
                    // already solved; let's not allocate anything
                    takenValues[row][column] = NO_DIGITS_POSSIBLE;
                } else {
                    takenValues[row][column] = findTakenValues(row, column, board);
                }
            }
        }
        return takenValues;
    }

    private boolean digitsWithSinglePossibleLocationFound(int[][] board) {
        for (int row = 0; row < 9; row++) {
            for (int digit = 1; digit <= 9; digit++) {
                if (find(digit, board[row]) == -1) { // digit not yet solved
                    int singlePossibleLocation = -1;
                    for (int column = 0; column < 9; column++) {
                        if (board[row][column] == 0) {
                            boolean[] takenValues = findTakenValues(row, column, board);
                            if (!takenValues[digit]) {
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

    private boolean cellsWithSinglePossibleValueFound(int[][] board, boolean[][][] takenValues) {
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
//                        System.out.println(String.format("%d;%d=%d", row,column,singleValue));
                        takenValues[row][column] = NO_DIGITS_POSSIBLE;
                        progressed = true;
                    }
                }
            }
        }
        return progressed;
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

    private boolean boardHasConflicts(int[][] board) {
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                boolean[] takenValues = findTakenValues(row, column, board);
                if (
                        takenValues[1]
                                && takenValues[2]
                                && takenValues[3]
                                && takenValues[4]
                                && takenValues[5]
                                && takenValues[6]
                                && takenValues[7]
                                && takenValues[8]
                                && takenValues[9]
                ) {
                    return true;
                }
            }
        }
        return false;
    }
}

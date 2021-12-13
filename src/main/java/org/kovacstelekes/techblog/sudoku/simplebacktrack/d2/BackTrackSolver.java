package org.kovacstelekes.techblog.sudoku.simplebacktrack.d2;

import org.kovacstelekes.techblog.BoardUtils;
import org.kovacstelekes.techblog.SudokuSolver;

public class BackTrackSolver implements SudokuSolver {
    // grid top-left corner row and column
    private static final int[] GRID_STARTING_ROWS = {0, 0, 0, 3, 3, 3, 6, 6, 6};
    private static final int[] GRID_STARTING_COLUMNS = {0, 3, 6, 0, 3, 6, 0, 3, 6};

    @Override
    public int[] solve(int[] cellValues) {
        int[][] board = BoardUtils.toBoard(cellValues);
        int[][] solution = solve(board, 0, 0);
        return BoardUtils.toCellValues(solution);
    }

    private int[][] solve(int[][] board, int rowStart, int columnStart) {
        if (boardHasConflicts(board)) {
            return null;
        }
        for (int row = rowStart; row < 9; row++) {
            for (int column = columnStart; column < 9; column++) {
                if (board[row][column] == 0) {
                    for (int guessValue = 1; guessValue <= 9; guessValue++) {
                        board[row][column] = guessValue;
                        int[][] solution = solve(board, row, column + 1);
                        if (solution != null) {
                            return solution;
                        }
                    }
                    board[row][column] = 0;
                    return null;
                }
            }
            columnStart = 0;
        }
        return board;
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

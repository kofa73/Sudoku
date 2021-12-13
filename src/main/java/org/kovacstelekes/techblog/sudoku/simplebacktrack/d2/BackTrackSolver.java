package org.kovacstelekes.techblog.sudoku.simplebacktrack.d2;

public class BackTrackSolver {
    // grid top-left corner row and column
    private static final int[] GRID_STARTING_ROWS = {0, 0, 0, 3, 3, 3, 6, 6, 6};
    private static final int[] GRID_STARTING_COLUMNS = {0, 3, 6, 0, 3, 6, 0, 3, 6};

    public int[] solve(int[] cellValues) {
        int[][] board = boardFrom(cellValues);
        int[][] solution = solve(board, 0, 0);
        return valuesFrom(solution);
    }

    private int[] valuesFrom(int[][] solution) {
        if (solution == null) {
            return null;
        }

        int cellValues[] = new int[9 * 9];
        int cellIndex = 0;
        for (int rowNumber = 0; rowNumber < 9; rowNumber++) {
            for (int columnNumber = 0; columnNumber < 9; columnNumber++) {
                cellValues[cellIndex] = solution[rowNumber][columnNumber];
                cellIndex++;
            }
        }
        return cellValues;
    }

    private int[][] boardFrom(int[] cellValues) {
        if (cellValues.length != 9 * 9) {
            throw new IllegalArgumentException();
        }
        int[][] board = new int[9][];
        int cellIndex = 0;
        for (int rowNumber = 0; rowNumber < 9; rowNumber++) {
            board[rowNumber] = new int[9];
            for (int columnNumber = 0; columnNumber < 9; columnNumber++) {
                board[rowNumber][columnNumber] = cellValues[cellIndex];
                cellIndex++;
            }
        }
        return board;
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
        for (int containerIndex = 0; containerIndex < 9; containerIndex++) {
            if (!isValidRow(board[containerIndex])
                    || !isValidColumn(containerIndex, board)
                    || (!isValidGrid(containerIndex, board))
            ) {
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

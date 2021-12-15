package org.kovacstelekes.techblog.sudoku.simplebacktrack.d2;

import org.kovacstelekes.techblog.BoardUtils;
import org.kovacstelekes.techblog.SudokuSolver;

public class BackTrackSolverWithMoreDeduction implements SudokuSolver {
    @Override
    public int[] solve(int[] cellValues) {
        int[][] boardCells = BoardUtils.toBoard(cellValues);

        Board board = new Board(boardCells);
        if (board.hasConflicts()) {
            return null;
        }

        int[][] solution = solve(board, 0, 0, 0);
        return BoardUtils.toCellValues(solution);
    }

    private int[][] solve(Board board, int rowStart, int columnStart, int prefix) {
        board.deduceValues();
        if (board.hasConflicts()) {
            return null;
        }
        for (int row = rowStart; row < 9; row++) {
            for (int column = columnStart; column < 9; column++) {
                if (board.unsolvedCellAt(row, column)) {
                    for (int guessValue = 1; guessValue <= 9; guessValue++) {
                        if (board.canTake(guessValue, row, column)) {
                            Board backup = board.clone();
                            board.setCell(guessValue, row, column);
                            int[][] solution = solve(board, row, column + 1, prefix + 1);
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
        return board.board();
    }
}

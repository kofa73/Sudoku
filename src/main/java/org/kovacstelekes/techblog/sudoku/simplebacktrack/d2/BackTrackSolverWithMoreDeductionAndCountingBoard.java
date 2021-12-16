package org.kovacstelekes.techblog.sudoku.simplebacktrack.d2;

import org.kovacstelekes.techblog.BoardUtils;
import org.kovacstelekes.techblog.SudokuSolver;

public class BackTrackSolverWithMoreDeductionAndCountingBoard implements SudokuSolver {
    @Override
    public int[] solve(int[] cellValues) {
        int[][] boardCells = BoardUtils.toBoard(cellValues);

        CountingBoard board = new CountingBoard(boardCells);
        if (board.hasConflicts()) {
            return null;
        }

        int[][] solution = solve(board);
        return BoardUtils.toCellValues(solution);
    }

    private int[][] solve(CountingBoard board) {
        board.deduceValues();
        if (!board.isValid()) {
            return null;
        }

        int[] unsolvedCell = board.nextUnsolvedCell();
        if (unsolvedCell != null) {
            int row = unsolvedCell[0];
            int column = unsolvedCell[1];
            return solveByGuessing(board, row, column);
        }

        return board.board();
    }

    private int[][] solveByGuessing(CountingBoard board, int row, int column) {
        for (int guessValue = 1; guessValue <= 9; guessValue++) {
            if (board.canTake(guessValue, row, column)) {
                CountingBoard backup = board.clone();
                board.setCell(guessValue, row, column);
                int[][] solution = solve(board);
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

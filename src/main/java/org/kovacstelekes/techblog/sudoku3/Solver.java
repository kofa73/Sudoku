package org.kovacstelekes.techblog.sudoku3;

import org.kovacstelekes.techblog.BoardUtils;
import org.kovacstelekes.techblog.SudokuSolver;
import org.kovacstelekes.techblog.sudoku3.model.Board;
import org.kovacstelekes.techblog.sudoku3.model.Cell;

import java.util.Optional;

public class Solver implements SudokuSolver {

    @Override
    public int[] solve(int[] cellValues) {
        BoardUtils.expectLinearBoard(cellValues);
        int[] internalCellValues = new int[BoardUtils.BOARD_SIZE];
        for (int i = 0; i < cellValues.length; i++) {
            internalCellValues[i] = cellValues[i] - 1;
        }
        Board board = Board.fromState(internalCellValues);
        Optional<Board> solution = solve(board);
        if (solution.isEmpty()) {
            return null;
        }
        internalCellValues = solution.get().toState();
        int[] standardCellValues = new int[BoardUtils.BOARD_SIZE];
        for (int i = 0; i < cellValues.length; i++) {
            standardCellValues[i] = internalCellValues[i] + 1;
        }
        return standardCellValues;
    }

    private Optional<Board> solve(Board board) {
        try {
            Board potentialSolution = board;
            while (!potentialSolution.isSolved()) {
                potentialSolution = applyGuessing(board);
            }
            return Optional.of(potentialSolution);
        } catch (IllegalStateException e) {
            return Optional.empty();
        }
    }

    private Board applyGuessing(Board board) {
        Cell nextCellToCheck = board.unsolvedCells()
                .reduce((Cell c1, Cell c2) -> c1.numberOfOptions() < c2.numberOfOptions() ? c1 : c2)
                .orElseThrow(() -> new RuntimeException("No unsolved cells"));

        return nextCellToCheck.possibleValues().stream()
                .map(possibleValue -> tryWithGuess(board, nextCellToCheck.ordinal(), possibleValue))
                .flatMap(Optional::stream)
                .findAny().orElseThrow(() -> new IllegalStateException("No solution found"));
    }

    private Optional<Board> tryWithGuess(Board board, int index, int possibleValue) {
        Optional<Board> solution;
        try {
            Board boardWithGuess = createBoardWithGuess(board, index, possibleValue);
            solution = solve(boardWithGuess);
        } catch (IllegalStateException e) {
            solution = Optional.empty();
        }
        return solution;
    }

    private Board createBoardWithGuess(Board board, int index, int possibleValue) {
        int[] boardValues = board.toState();
        boardValues[index] = possibleValue;
        Board boardWithGuess = Board.fromState(boardValues);
        return boardWithGuess;
    }
}

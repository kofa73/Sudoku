package org.kovacstelekes.techblog.sudoku2;

import org.kovacstelekes.techblog.sudoku2.model.Board;
import org.kovacstelekes.techblog.sudoku2.model.Cell;

import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Optional.empty;

public class Solver {
    public Optional<Board> solve(Board board) {
        boolean deductionSuccessful = board.deduceValues();
        return deductionSuccessful ? Optional.of(board) : applyGuessing(board);
    }

    private Optional<Board> applyGuessing(Board board) {
        Cell nextCellToCheck = board.unsolvedCells()
                .reduce((Cell c1, Cell c2) -> c1.numberOfOptions() < c2.numberOfOptions() ? c1 : c2)
                .orElseThrow(() -> new RuntimeException("No unsolved cells"));

        System.out.println("Next cell should be " + nextCellToCheck);

        Optional<Board> solution = empty();

        return nextCellToCheck.values().stream()
                .flatMap(possibleValue -> tryWithGuess(board, nextCellToCheck.ordinal(), possibleValue))
                .findAny();
    }

    private Stream<Board> tryWithGuess(Board board, int index, int possibleValue) {
        Board boardWithGuess = createBoardWithGuess(board, index, possibleValue);
        Optional<Board> solution = solve(boardWithGuess);
        return solution.stream();
    }

    private Board createBoardWithGuess(Board board, int index, int possibleValue) {
        int[] boardValues = board.toState();
        boardValues[index] = possibleValue;
        Board boardWithGuess = Board.fromState(boardValues);
        return boardWithGuess;
    }
}

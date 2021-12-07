package org.kovacstelekes.techblog.sudoku2;

import org.kovacstelekes.techblog.sudoku2.model.Board;
import org.kovacstelekes.techblog.sudoku2.model.Cell;
import org.kovacstelekes.techblog.sudoku2.model.Outcome;

import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Optional.empty;

public class Solver {
    private int nChecks = 0;

    public Optional<Board> solve(Board board) {
        board.deduceValues();
        System.out.println(board);
        return solve(board, "");
    }

    private Optional<Board> solve(Board board, String currentGuess) {
        nChecks++;
        System.out.println(nChecks + " - " + currentGuess);
        Outcome outcome = board.deduceValues().outcome();
        Optional<Board> solution;
        if (outcome == Outcome.INVALID) {
            solution = empty();
        } else if (outcome == Outcome.UNIQUE_SOLUTION) {
            solution = Optional.of(board);
        } else {
            solution = applyGuessing(board, currentGuess);
        }
        return solution;
    }

    private Optional<Board> applyGuessing(Board board, String currentGuess) {
        Cell nextCellToCheck = board.unsolvedCells()
                .reduce((Cell c1, Cell c2) -> c1.numberOfOptions() < c2.numberOfOptions() ? c1 : c2)
                .orElseThrow(() -> new RuntimeException("No unsolved cells"));

//        System.out.println("Next cell should be " + nextCellToCheck);

        return nextCellToCheck.values().stream()
                .flatMap(possibleValue -> tryWithGuess(board, nextCellToCheck.ordinal(), possibleValue, currentGuess))
                .findAny();
    }

    private Stream<Board> tryWithGuess(Board board, int index, int possibleValue, String currentGuess) {
        String nextGuess = currentGuess + " -> " + index + ": " + possibleValue;
        Board boardWithGuess = createBoardWithGuess(board, index, possibleValue);
        Optional<Board> solution = solve(boardWithGuess, nextGuess);
        return solution.stream();
    }

    private Board createBoardWithGuess(Board board, int index, int possibleValue) {
        int[] boardValues = board.toState();
        boardValues[index] = possibleValue;
        Board boardWithGuess = Board.fromState(boardValues);
        return boardWithGuess;
    }
}

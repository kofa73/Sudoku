package org.kovacstelekes.techblog.sudoku2;

import org.kovacstelekes.techblog.sudoku2.model.Board;
import org.kovacstelekes.techblog.sudoku2.model.Cell;
import org.kovacstelekes.techblog.sudoku2.model.Outcome;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Optional.empty;

public class Solver {
    private int nChecks = 0;

    public Optional<Board> solve(Board board) {
        return solve(board, "", 1);
    }

    private Optional<Board> solve(Board board, String currentGuess, int depth) {
        nChecks++;
        System.out.println(String.format(
                "%4d %4d - %s",
                nChecks, depth,
                flatBoardDump(board)
        ));
        Outcome outcome = board.deduceValues().outcome();
        System.out.println(String.format(
                "%4d %4d - %s - deduced - %s",
                nChecks, depth,
                flatBoardDump(board),
                outcome
        ));
        Optional<Board> solution;
        if (outcome == Outcome.INVALID) {
            solution = empty();
        } else if (outcome == Outcome.UNIQUE_SOLUTION) {
            solution = Optional.of(board);
        } else {
            solution = applyGuessing(board, currentGuess, depth);
        }
        return solution;
    }

    private String flatBoardDump(Board board) {
        return Arrays.stream(board.toState()).map(i -> i - 1).collect(StringBuffer::new, StringBuffer::append, StringBuffer::append).toString().replace("-1", ".");
    }

    private Optional<Board> applyGuessing(Board board, String currentGuess, int depth) {
        Cell nextCellToCheck = board.unsolvedCells()
                .reduce((Cell c1, Cell c2) -> c1.numberOfOptions() < c2.numberOfOptions() ? c1 : c2)
                .orElseThrow(() -> new RuntimeException("No unsolved cells"));

//        System.out.println("Next cell should be " + nextCellToCheck);

        return nextCellToCheck.values().stream()
                .flatMap(possibleValue -> tryWithGuess(board, nextCellToCheck.ordinal(), possibleValue, currentGuess, depth))
                .findAny();
    }

    private Stream<Board> tryWithGuess(Board board, int index, int possibleValue, String currentGuess, int depth) {
        String nextGuess = currentGuess + " -> " + index + ": " + (possibleValue - 1);
        Board boardWithGuess = createBoardWithGuess(board, index, possibleValue, depth);
        System.out.println(String.format(
                "%4d %4d - %s - guess - %s",
                nChecks, depth,
                flatBoardDump(board),
                currentGuess
        ));
        Optional<Board> solution = solve(boardWithGuess, nextGuess, depth + 1);
        return solution.stream();
    }

    private Board createBoardWithGuess(Board board, int index, int possibleValue, int depth) {
        System.out.println(String.format(
                "%4d %4d - %s - applying guess - %d",
                nChecks, depth,
                flatBoardDump(board),
                (possibleValue - 1)
        ));
        int[] boardValues = board.toState();
        boardValues[index] = possibleValue;
        Board boardWithGuess = Board.fromState(boardValues);
        System.out.println(String.format(
                "%4d %4d - %s - applied guess - %d",
                nChecks, depth,
                flatBoardDump(boardWithGuess),
                (possibleValue - 1)
        ));
        return boardWithGuess;
    }
}

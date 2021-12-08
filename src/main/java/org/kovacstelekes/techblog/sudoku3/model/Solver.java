package org.kovacstelekes.techblog.sudoku3.model;

import java.util.Arrays;
import java.util.Optional;

public class Solver {
    private long nChecks = 0;

    public Optional<Board> solve(Board board) {
        try {
            return solve(board, "", 1);
        } catch (IllegalStateException e) {
            return Optional.empty();
        }
    }

    private Optional<Board> solve(Board board, String currentGuess, int depth) {
        nChecks++;
//        System.out.println(String.format(
//                "%4d %4d - %s",
//                nChecks, depth,
//                Arrays.stream(board.toState()).collect(StringBuffer::new, StringBuffer::append, StringBuffer::append).toString().replace("-1", ".")
//        ));
        Board boardToSolve = board;
        while (!boardToSolve.isSolved()) {
            boardToSolve = applyGuessing(board, currentGuess, depth);
        }
        return Optional.of(boardToSolve);
    }

    public long nChecks() {return nChecks;}

    private Board applyGuessing(Board board, String currentGuess, int depth) {
        Cell nextCellToCheck = board.unsolvedCells()
                .reduce((Cell c1, Cell c2) -> c1.numberOfOptions() < c2.numberOfOptions() ? c1 : c2)
                .orElseThrow(() -> new RuntimeException("No unsolved cells"));

//        System.out.println("Next cell should be " + nextCellToCheck);

        return nextCellToCheck.possibleValues().stream()
                .map(possibleValue -> tryWithGuess(board, nextCellToCheck.ordinal(), possibleValue, currentGuess, depth))
                .flatMap(Optional::stream)
                .findAny().orElseThrow(() -> new IllegalStateException("No solution found"));
    }

    private Optional<Board> tryWithGuess(Board board, int index, int possibleValue, String currentGuess, int depth) {
        Optional<Board> solution;
        try {
            String nextGuess = currentGuess + " -> " + index + ": " + possibleValue;
            Board boardWithGuess = createBoardWithGuess(board, index, possibleValue);
            solution = solve(boardWithGuess, nextGuess, depth + 1);
        } catch (IllegalStateException e) {
            solution = Optional.empty();
        }
        return solution;
    }

    private Board createBoardWithGuess(Board board, int index, int possibleValue) {
//        System.out.println("Trying index=" + index + " value=" + possibleValue);
        int[] boardValues = board.toState();
        boardValues[index] = possibleValue;
        Board boardWithGuess = Board.fromState(boardValues);
        return boardWithGuess;
    }
}

package org.kovacstelekes.techblog.sudoku3.model;

import java.util.Optional;

public class Solver {
    private long nChecks = 0;

    public Optional<Board> solve(Board board) {
        return solve(board, "");
    }

    private Optional<Board> solve(Board board, String currentGuess) {
        nChecks++;
        System.out.println(nChecks + " - " + currentGuess);
        Board boardToSolve = board;
        while (!boardToSolve.isSolved()) {
            boardToSolve = applyGuessing(board, currentGuess);
        }
        return Optional.of(boardToSolve);
    }

    public long nChecks() {return nChecks;}

    private Board applyGuessing(Board board, String currentGuess) {
        Cell nextCellToCheck = board.unsolvedCells()
                .reduce((Cell c1, Cell c2) -> c1.numberOfOptions() < c2.numberOfOptions() ? c1 : c2)
                .orElseThrow(() -> new RuntimeException("No unsolved cells"));

//        System.out.println("Next cell should be " + nextCellToCheck);

        return nextCellToCheck.possibleValues().stream()
                .map(possibleValue -> tryWithGuess(board, nextCellToCheck.ordinal(), possibleValue, currentGuess))
                .flatMap(Optional::stream)
                .findAny().orElseThrow(() -> new IllegalStateException("No solution found"));
    }

    private Optional<Board> tryWithGuess(Board board, int index, int possibleValue, String currentGuess) {
        Optional<Board> solution;
        try {
            String nextGuess = currentGuess + " -> " + index + ": " + possibleValue;
            Board boardWithGuess = createBoardWithGuess(board, index, possibleValue);
            solution = solve(boardWithGuess, nextGuess);
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

package org.kovacstelekes.techblog.sudoku.simplebacktrack.d2;

import org.junit.jupiter.api.Test;
import org.kovacstelekes.techblog.sudoku.BoardParser;

import java.util.Arrays;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class BackTrackSolverTest {
    private final BoardParser parser = new BoardParser();
    private final BackTrackSolver backTrackSolver = new BackTrackSolver();
    // stores the solution so the call does not get optimised away to no-op
    private int[] solution;
    
    @Test
    void solveSimple() {
        int[] board = parser.parse("""
                    |   | 84|  3|
                    |13 | 5 | 94|
                    |9 5| 13| 7 |
                    +---+---+---+
                    |4  | 96|   |
                    |  3|7  |  1|
                    |75 | 3 |469|
                    +---+---+---+
                    |5 4| 78| 2 |
                    |36 | 29| 5 |
                    | 2 |   |3 7|
                """);

        solveAndPrint(board, true);
    }

    @Test
    void solveMedium() {
        int[] board = parser.parse("""
                    |62 |  1|35 |
                    |374| 56|   |
                    | 5 | 4 |  2|
                    +---+---+---+
                    | 1 |7 4|   |
                    |  6|13 |   |
                    |   | 9 |   |
                    +---+---+---+
                    |   |   |   |
                    |  3|  5|678|
                    |791| 8 |2  |
                """);

        solveAndPrint(board, true);
    }

    @Test
    void solveHard() {
        int[] board = parser.parse("""
                    |2  | 9 |7  |
                    |   |   | 68|
                    |1  |8  | 9 |
                    +---+---+---+
                    |   |   |   |
                    | 1 |  3|9  |
                    |4  | 7 |5  |
                    +---+---+---+
                    | 5 | 4 |1  |
                    | 6 | 8 |   |
                    | 3 |7  |6 5|
                """);

        solveAndPrint(board, true);
    }

    @Test
    void solveEmpty() {
        int[] board = parser.parse("""
                    |   |   |   |
                    |   |   |   |
                    |   |   |   |
                    +---+---+---+
                    |   |   |   |
                    |   |   |   |
                    |   |   |   |
                    +---+---+---+
                    |   |   |   |
                    |   |   |   |
                    |   |   |   |
                """);

        solveAndPrint(board, true);
    }

    @Test
    void solveEvil() {
        int[] board = parser.parse("""
                    |9  |   | 4 |
                    | 7 |6  |3 9|
                    |   | 3 | 5 |
                    +---+---+---+
                    | 5 |  1|   |
                    |   |   |  4|
                    |1  | 2 |9 6|
                    +---+---+---+
                    |5  | 6 |2 1|
                    |   |   | 3 |
                    |  8|7  |   |
                """);

        solveAndPrint(board, true);
    }

    @Test
    void detectImpossible() {
        int[] board = parser.parse("""
                    |9  |   | 4 |
                    | 7 |6  |3 9|
                    |   | 3 | 8 |
                    +---+---+---+
                    |   |9  |   |
                    | 4 |   |  7|
                    |1  |   |   |
                    +---+---+---+
                    |   |1  | 9 |
                    |  7| 69|  5|
                    |6  |2  |   |
                """);

        solveAndPrint(board, false);
    }

    @Test
    void worldsHardestSudoku() {
        int[] board = parser.parse("""
                8........
                ..36.....
                .7..9.2..
                .5...7...
                ....457..
                ...1...3.
                ..1....68
                ..85...1.
                .9....4..
                                """);

        solveAndPrint(board, true);
    }

    private void solveAndPrint(int[] cellValues, int[] correctSolution) {
        measureAndCheck(cellValues, correctSolution);
    }

    private void measureAndCheck(int[] cellValues, int[] correctSolution) {
        solution = backTrackSolver.solve(cellValues);
        assertThat(solution).isEqualTo(correctSolution);

        long start = System.currentTimeMillis();
        long end = start + 20_000;
        do {
            solution = backTrackSolver.solve(cellValues);
        } while (System.currentTimeMillis() < end);

        start = System.currentTimeMillis();
        end = System.currentTimeMillis() + 10_000;
        int cnt = 0;
        do {
            solution = backTrackSolver.solve(cellValues);
            cnt++;
        } while (System.currentTimeMillis() < end);
        long elapsed = System.currentTimeMillis() - start;
        System.out.println(String.format(
                "%s\telapsed: %d ms; cnt=%d, perf=%f",
                solverConstructor.apply(board).getClass().getSimpleName(), elapsed, cnt, elapsed / (double) cnt
        ));
    }
}
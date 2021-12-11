package org.kovacstelekes.techblog.sudoku2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kovacstelekes.techblog.sudoku.BoardParser;
import org.kovacstelekes.techblog.sudoku2.model.Board;

import java.util.Optional;

class SolverTest {
    private final BoardParser parser = new BoardParser();
    private final Solver solver = new Solver();

    @BeforeEach
    void reset() {
        Board.nBoards = 0;
    }

    @Test
    void solveSimple() {
        int[] values = parser.parse("""
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

        solveAndPrint(values);
    }

    @Test
    void solveMedium() {
        int[] values = parser.parse("""
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

        solveAndPrint(values);
    }

    @Test
    void solveHard() {
        int[] values = parser.parse("""
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

        solveAndPrint(values);
    }

    @Test
    void solveEmpty() {
        int[] values = parser.parse("""
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

        solveAndPrint(values);
    }

    @Test
    void solveEvil() {
        int[] values = parser.parse("""
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

        solveAndPrint(values);
    }

    @Test
    void detectImpossible() {
        int[] values = parser.parse("""
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

        solveAndPrint(values);
    }
    @Test
    void worldsHardestSudoku() {
        int[] values = parser.parse("""
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

        solveAndPrint(values);
    }

    private void solveAndPrint(int[] values) {
        Board board = Board.fromState(values);
        Optional<Board> solution = solver.solve(board);
        solution.ifPresent(System.out::println);
        System.out.println("solved = " + solution.isPresent());
        System.out.println("nBoards = " + Board.nBoards);
    }
}

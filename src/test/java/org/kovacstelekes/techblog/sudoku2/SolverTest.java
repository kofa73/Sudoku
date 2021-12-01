package org.kovacstelekes.techblog.sudoku2;

import org.junit.jupiter.api.Test;
import org.kovacstelekes.techblog.sudoku2.model.Board;

import java.util.Optional;

class SolverTest {
    private final BoardParser parser = new BoardParser();
    private final Solver solver = new Solver();

    @Test
    void solveSimple() {
        Board board = parser.parse("""
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

        solveAndPrint(board);
    }

    private void solveAndPrint(Board board) {
        Optional<Board> solution = solver.solve(board);
        solution.ifPresent(System.out::println);
        System.out.println("solved = " + solution.isPresent());
    }

    @Test
    void solveMedium() {
        Board board = parser.parse("""
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

        solveAndPrint(board);
    }

    @Test
    void solveHard() {
        Board board = parser.parse("""
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

        solveAndPrint(board);
    }

    @Test
    void solveEmpty() {
        Board board = parser.parse("""
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

        solveAndPrint(board);
    }

    @Test
    void solveEvil() {
        Board board = parser.parse("""
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

        solveAndPrint(board);
    }

    @Test
    void detectImpossible() {
        Board board = parser.parse("""
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

        solveAndPrint(board);
    }
}

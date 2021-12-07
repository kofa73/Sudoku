package org.kovacstelekes.techblog.sudoku3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kovacstelekes.techblog.sudoku3.model.Board;
import org.kovacstelekes.techblog.sudoku3.model.Solver;

import java.util.Optional;

class SolverTest {
    private final org.kovacstelekes.techblog.sudoku3.BoardParser parser = new BoardParser();

    @BeforeEach
    void reset() {
        Board.nBoards = 0;
    }

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
        System.out.println(board);
        Solver solver = new Solver();
        try {
            Optional<Board> solved = solver.solve(board);
            System.out.println(solved.map(Board::toString).orElse("no solution"));
        } finally {
            System.out.println("nChecks: " + solver.nChecks());
            System.out.println("nBoards: " + Board.nBoards);
        }
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

    /*
8 . . . . . . . .
. . 3 6 . . . . .
. 7 . . 9 . 2 . .
. 5 . . . 7 . . .
. . . . 4 5 7 . .
. . . 1 . . . 3 .
. . 1 . . . . 6 8
. . 8 5 . . . 1 .
. 9 . . . . 4 . .
     */

    @Test
    void worldsHardestSudoku() {
        Board board = parser.parse("""
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

        solveAndPrint(board);
    }
}

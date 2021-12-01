package org.kovacstelekes.techblog.sudoku2.model;

import org.junit.jupiter.api.Test;
import org.kovacstelekes.techblog.sudoku2.BoardParser;

class BoardTest {
    private final BoardParser parser = new BoardParser();

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

        boolean solved = board.solve();
        System.out.println(board);
        System.out.println("solved = " + solved);
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

        boolean solved = board.solve();
        System.out.println(board);
        System.out.println("solved = " + solved);
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

        boolean solved = board.solve();
        System.out.println(board);
        System.out.println("solved = " + solved);
    }
}

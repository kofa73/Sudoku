package org.kovacstelekes.techblog.sudoku.simplebacktrack;

import org.junit.jupiter.api.Test;

class BackTrackSolverTest {
    private final BoardParser parser = new BoardParser();
    
    @Test
    void solveSimple() {
        BackTrackSolver backTrackSolver = parser.parse("""
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

        solveAndPrint(backTrackSolver);
    }

    private void solveAndPrint(BackTrackSolver solver) {
        System.out.println(solver);
        try {
            boolean solved = solver.solve();
            System.out.println(solved ? solver : "no solution");
        } finally {
            System.out.println("nChecks: " + solver.nChecks());
        }
    }

    @Test
    void solveMedium() {
        BackTrackSolver backTrackSolver = parser.parse("""
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

        solveAndPrint(backTrackSolver);
    }

    @Test
    void solveHard() {
        BackTrackSolver backTrackSolver = parser.parse("""
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

        solveAndPrint(backTrackSolver);
    }

    @Test
    void solveEmpty() {
        BackTrackSolver backTrackSolver = parser.parse("""
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

        solveAndPrint(backTrackSolver);
    }

    @Test
    void solveEvil() {
        BackTrackSolver backTrackSolver = parser.parse("""
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

        solveAndPrint(backTrackSolver);
    }

    @Test
    void detectImpossible() {
        BackTrackSolver backTrackSolver = parser.parse("""
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

        solveAndPrint(backTrackSolver);
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
        BackTrackSolver backTrackSolver = parser.parse("""
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

        solveAndPrint(backTrackSolver);
    }

}
package org.kovacstelekes.techblog.sudoku;

import com.baeldung.algorithms.sudoku.BacktrackingAlgorithm;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kovacstelekes.techblog.BoardUtils;
import org.kovacstelekes.techblog.SudokuSolver;
import org.kovacstelekes.techblog.sudoku.simplebacktrack.d2.BackTrackSolverWithMoreDeduction;

import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class SolverTest {
    private int sink;
    private final boolean measure = true;

    @ParameterizedTest
    @MethodSource("solvers")
    void solveSimple(SudokuSolver solver) {
        String board = ("""
                +---+---+---+
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
                +---+---+---+
                """);

        String solution = """
                +---+---+---+
                |276|984|513|
                |138|257|694|
                |945|613|872|
                +---+---+---+
                |482|196|735|
                |693|745|281|
                |751|832|469|
                +---+---+---+
                |514|378|926|
                |367|429|158|
                |829|561|347|
                +---+---+---+
                """;
        solveAndPrint(solver, board, solution);
    }

    @ParameterizedTest
    @MethodSource("solvers")
    void solveMedium(SudokuSolver solver) {
        String board = ("""
                +---+---+---+
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
                +---+---+---+
                """);

        String solution = """
                +---+---+---+
                |629|871|354|
                |374|256|819|
                |158|349|762|
                +---+---+---+
                |912|764|583|
                |586|132|497|
                |437|598|126|
                +---+---+---+
                |865|427|931|
                |243|915|678|
                |791|683|245|
                +---+---+---+
                """;
        solveAndPrint(solver, board, solution);
    }

    @ParameterizedTest
    @MethodSource("solvers")
    void solveHard(SudokuSolver solver) {
        String board = ("""
                +---+---+---+
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
                +---+---+---+
                """);

        String solution = """
                |283|694|751|
                |597|231|468|
                |146|857|293|
                +---+---+---+
                |375|469|812|
                |618|523|947|
                |429|178|536|
                +---+---+---+
                |852|346|179|
                |761|985|324|
                |934|712|685|
                +---+---+---+
                """;
        solveAndPrint(solver, board, solution);
    }

    @ParameterizedTest
    @MethodSource("solvers")
    void solveEmpty(SudokuSolver solver) {
        String board = ("""
                +---+---+---+
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
                +---+---+---+
                """);

        solveAndPrintAnySolution(solver, board);
    }

    @ParameterizedTest
    @MethodSource("solvers")
    void solveEvil(SudokuSolver solver) {
        String board = ("""
                +---+---+---+
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
                +---+---+---+
                """);

        String solution = """
                +---+---+---+
                |935|187|642|
                |471|652|389|
                |862|934|157|
                +---+---+---+
                |756|491|823|
                |293|876|514|
                |184|523|976|
                +---+---+---+
                |547|368|291|
                |619|245|738|
                |328|719|465|
                +---+---+---+
                """;
        solveAndPrint(solver, board, solution);
    }

    @ParameterizedTest
    @MethodSource("solvers")
    void detectImpossible(SudokuSolver solver) {
        String board = ("""
                +---+---+---+
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
                +---+---+---+
                """);

        solveAndPrint(solver, board, null);
    }

    @ParameterizedTest
    @MethodSource("solvers")
    void worldsHardestSudoku(SudokuSolver solver) {
        String board = ("""
                +---+---+---+
                |8..|...|...|
                |..3|6..|...|
                |.7.|.9.|2..|
                +---+---+---+
                |.5.|..7|...|
                |...|.45|7..|
                |...|1..|.3.|
                +---+---+---+
                |..1|...|.68|
                |..8|5..|.1.|
                |.9.|...|4..|
                +---+---+---+
                """);

        String solution = """
                +---+---+---+
                |812|753|649|
                |943|682|175|
                |675|491|283|
                +---+---+---+
                |154|237|896|
                |369|845|721|
                |287|169|534|
                +---+---+---+
                |521|974|368|
                |438|526|917|
                |796|318|452|
                +---+---+---+
                """;

        solveAndPrint(solver, board, solution);
    }

    @ParameterizedTest
    @MethodSource("solvers")
    void puzzle17(SudokuSolver solver) {
        String board = ("""
                +---+---+---+
                |...|...|.1.|
                |4..|...|...|
                |.2.|...|...|
                +---+---+---+
                |...|.5.|4.7|
                |..8|...|3..|
                |..1|.9.|...|
                +---+---+---+
                |3..|4..|2..|
                |.5.|1..|...|
                |...|8.6|...|
                +---+---+---+                
                """);

        String solution = """
                |693|784|512|
                |487|512|936|
                |125|963|874|
                +---+---+---+
                |932|651|487|
                |568|247|391|
                |741|398|625|
                +---+---+---+
                |319|475|268|
                |856|129|743|
                |274|836|159|
                +---+---+---+
                """;

        solveAndPrint(solver, board, solution);
    }

    private void solveAndPrintAnySolution(SudokuSolver solver, String board) {
        int[] cellValues = BoardUtils.parseToLinear(board);
        int[] solutionFromSolver = solver.solve(cellValues.clone());
        assertThat(solutionFromSolver).hasSize(9 * 9);

        measure(solver, cellValues);
    }

    private void solveAndPrint(SudokuSolver solver, String board, String solution) {
        int[] cellValues = BoardUtils.parseToLinear(board);
        int[] solutionFromSolver = solver.solve(cellValues.clone());
        String formattedSolutionFromSolver = BoardUtils.format(solutionFromSolver);
        int[] expectedSolution = BoardUtils.parseToLinear(solution);
        String canonicalFormattedSolution = BoardUtils.format(expectedSolution);
        assertThat(formattedSolutionFromSolver).isEqualTo(canonicalFormattedSolution);

        measure(solver, cellValues);
    }

    private void measure(SudokuSolver solver, int[] cellValues) {
        if (measure) {
            long start = System.currentTimeMillis();
            long end = start + 20_000;
            do {
                sink += Objects.hashCode(solver.solve(cellValues.clone()));
            } while (System.currentTimeMillis() < end);

            start = System.currentTimeMillis();
            end = System.currentTimeMillis() + 10_000;
            int cnt = 0;
            do {
                sink += Objects.hashCode(solver.solve(cellValues.clone()));
                cnt++;
            } while (System.currentTimeMillis() < end);
            long elapsed = System.currentTimeMillis() - start;
            System.out.println(String.format(
                    "%s\telapsed: %d ms; cnt=%d, perf=%f, sink=%d (ignore)",
                    solver.getClass().getName(), elapsed, cnt, elapsed / (double) cnt, sink
            ));
        }
    }

    static Stream<SudokuSolver> solvers() {
        return Stream.of(
                new BacktrackingAlgorithm(),
                new org.kovacstelekes.techblog.sudoku2.Solver(),
                new org.kovacstelekes.techblog.sudoku3.Solver(),
//                new org.kovacstelekes.techblog.sudoku.simplebacktrack.d1.BackTrackSolverWithStreams(),
                new org.kovacstelekes.techblog.sudoku.simplebacktrack.d1.BackTrackSolverWithArrays(),
                new org.kovacstelekes.techblog.sudoku.simplebacktrack.d2.BackTrackSolver(),
                new org.kovacstelekes.techblog.sudoku.simplebacktrack.d2.BackTrackSolverWithDeduction(),
                new BackTrackSolverWithMoreDeduction()
        );
    }
}

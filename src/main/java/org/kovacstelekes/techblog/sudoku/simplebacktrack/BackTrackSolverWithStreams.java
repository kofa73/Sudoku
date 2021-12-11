package org.kovacstelekes.techblog.sudoku.simplebacktrack;

import java.util.Map;
import java.util.stream.IntStream;

import static java.util.Arrays.stream;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class BackTrackSolverWithStreams extends BackTrackSolver {
    public BackTrackSolverWithStreams(int[] cells) {
        super(cells);
    }

    int find(int value, int[] array) {
        return IntStream.range(0, array.length)
                .filter(index -> array[index] == value)
                .findAny()
                .orElse(-1);
    }

    boolean isValid(int[] container) {
        return digitCountsOf(container).values().stream()
                .allMatch(numberOfOccurrances -> numberOfOccurrances < 2);
    }

    public boolean isSolved() {
        return stream(cells).noneMatch(cellValue -> cellValue == 0);
    }


    private Map<Integer, Long> digitCountsOf(int[] container) {
        return digitCountsOf(
                stream(container)
                        .map(cellIndex -> cells[cellIndex])
        );
    }

    private Map<Integer, Long> digitCountsOf(IntStream digits) {
        return solvedDigitsOf(digits)
                .boxed()
                .collect(
                        groupingBy(identity(), counting())
                );
    }

    private IntStream solvedDigitsOf(IntStream digits) {
        return digits
                .filter(value -> value > 0);
    }
}

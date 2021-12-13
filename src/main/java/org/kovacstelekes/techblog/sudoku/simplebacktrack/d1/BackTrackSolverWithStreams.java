package org.kovacstelekes.techblog.sudoku.simplebacktrack.d1;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.Arrays.stream;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class BackTrackSolverWithStreams extends BackTrackSolver {
    @Override
    int find(int value, int[] array) {
        return IntStream.range(0, array.length)
                .filter(index -> array[index] == value)
                .findAny()
                .orElse(-1);
    }

    @Override
    boolean isValid(int[] container, int[] cellValues) {
        return digitCountsOf(container, cellValues).values().stream()
                .allMatch(numberOfOccurrances -> numberOfOccurrances < 2);
    }

    @Override
    boolean isSolved(int[] cellValues) {
        return Arrays.stream(cellValues).noneMatch(cellValue -> cellValue == 0);
    }


    private Map<Integer, Long> digitCountsOf(int[] container, int[] cellValues) {
        return digitCountsOf(
                stream(container)
                        .map(cellIndex -> cellValues[cellIndex])
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

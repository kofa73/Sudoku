package org.kovacstelekes.techblog.sudoku.simplebacktrack.d1;

public class BackTrackSolverWithArrays extends BackTrackSolver {
    @Override
    int find(int value, int[] array) {
        for (int index = 0; index < array.length; index++) {
            if (array[index] == value) {
                return index;
            }
        }
        return -1;
    }

    @Override
    boolean isValid(int[] container, int[] cellValues) {
        boolean[] seenDigit = new boolean[9];
        for (int indexInContainer = 0; indexInContainer < container.length; indexInContainer++) {
            int digit = cellValues[container[indexInContainer]];
            if (digit != 0) {
                int digitIndex = digit - 1;
                if (seenDigit[digitIndex]) {
                    return false;
                }
                seenDigit[digitIndex] = true;
            }
        }
        return true;
    }

    @Override
    boolean isSolved(int[] cellValues) {
        for (int cellValue : cellValues) {
            if (cellValue == 0) {
                return false;
            }
        }
        return true;
    }
}

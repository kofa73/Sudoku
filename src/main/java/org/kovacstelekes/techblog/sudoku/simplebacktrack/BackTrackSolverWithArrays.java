package org.kovacstelekes.techblog.sudoku.simplebacktrack;

public class BackTrackSolverWithArrays extends BackTrackSolver {
    public BackTrackSolverWithArrays(int[] cells) {
        super(cells);
    }

    int find(int value, int[] array) {
        for (int index = 0; index < array.length; index++) {
            if (array[index] == value) {
                return index;
            }
        }
        return -1;
    }

    boolean isValid(int[] container) {
        boolean[] seenDigit = new boolean[9];
        for (int indexInContainer = 0; indexInContainer < container.length; indexInContainer++) {
            int digit = cells[container[indexInContainer]];
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

    boolean isSolved() {
        for (int cellValue : cells) {
            if (cellValue == 0) {
                return false;
            }
        }
        return true;
    }
}

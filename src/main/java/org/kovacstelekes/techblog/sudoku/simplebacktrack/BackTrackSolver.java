package org.kovacstelekes.techblog.sudoku.simplebacktrack;

import org.kovacstelekes.techblog.sudoku2.model.Row;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.Arrays.stream;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class BackTrackSolver {
    // these store the cell indices pointing into `cells`
    private static final int[][] ROWS;
    private static final int[][] COLUMNS;
    private static final int[][] GRIDS;

    private final int[] cells;
    private int nChecks = 0;

    static {
        ROWS = new int[9][];
        COLUMNS = new int[9][];
        GRIDS = new int[9][];
        for (int i = 0; i < 9; i++) {
            ROWS[i] = new int[9];
            COLUMNS[i] = new int[9];
            GRIDS[i] = new int[9];
        }
        for (int cellIndex = 0; cellIndex < 9 * 9; cellIndex++) {
            int rowNumber = rowNumber(cellIndex);
            int columnNumber = columnNumber(cellIndex);
            int gridNumber = (rowNumber / 3) * 3 + columnNumber / 3;
            int positionInGrid = rowNumber % 3 * 3 + columnNumber % 3;

            ROWS[rowNumber][columnNumber] = cellIndex;
            COLUMNS[columnNumber][rowNumber] = cellIndex;
            GRIDS[gridNumber][positionInGrid] = cellIndex;
        }
    }

    private static int columnNumber(int cellIndex) {
        return cellIndex % 9;
    }

    private static int rowNumber(int cellIndex) {
        return cellIndex / 9;
    }

    private BackTrackSolver(int[] cells) {
        this.cells = cells.clone();
        dieIfInvalid();
    }

    public boolean solve() {
        nChecks++;
        dieIfInvalid();
        int indexOfUnsolvedCell = find(0, cells);

        boolean solved = false;
        if (indexOfUnsolvedCell > -1) {
            for (int guessValue = 1; guessValue <= 9; guessValue++) {
                cells[indexOfUnsolvedCell] = guessValue;
                try {
                    solved = solve();
                } catch (IllegalArgumentException e) {
                    solved = false;
                }
                if (solved) {
                    break;
                } else {
                    cells[indexOfUnsolvedCell] = 0;
                }
            }
        } else {
            solved = true;
        }
        return true;
    }

    private int find(int value, int[] array) {
        return Arrays.stream(array).filter(arrayValue -> arrayValue == value).findAny().orElse(-1);
    }

    private void dieIfInvalid() {
        int[] nineCells = new int[9];
        for (int containerIndex = 0; containerIndex < 9; containerIndex++) {
            copyCellValues(ROWS[containerIndex], nineCells);
            if (!isValid(nineCells)) {
                throw new IllegalArgumentException("Row #" + containerIndex + " is invalid");
            }
            copyCellValues(COLUMNS[containerIndex], nineCells);
            if (!isValid(nineCells)) {
                throw new IllegalArgumentException("Column #" + containerIndex + " is invalid");
            }
            copyCellValues(GRIDS[containerIndex], nineCells);
            if (!isValid(nineCells)) {
                throw new IllegalArgumentException("Grid #" + containerIndex + " is invalid");
            }
        }
    }

    private void copyCellValues(int[] indices, int[] cellValues) {
        for (int i = 0; i < 9; i++) {
            cellValues[i] = cells[indices[i]];
        }
    }

    public static BackTrackSolver fromState(int[] cells) {
        return new BackTrackSolver(cells);
    }

    private boolean isValid(int[] nineCells) {
        return digitCountsOf(nineCells).values().stream()
                .allMatch(numberOfOccurrances -> numberOfOccurrances < 2);
    }

    public int nChecks() {
        return nChecks;
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        for (int cellIndex = 0; cellIndex < cells.length; cellIndex++) {
            int row = rowNumber(cellIndex);
            int colN = columnNumber(cellIndex);
            if (row % 3 == 0 && colN == 0) {
                text.append("+---+---+---\n");
            }
            if (colN % 3 == 0) {
                text.append('|');
            }
            int cellValue = cells[cellIndex];
            if (cellValue == -1) {
                text.append('.');
            } else {
                text.append(cellValue + 1);
            }
            if (colN == 8) {
                text.append("\n");
            }
        }
        return text.toString();
    }

    public boolean isSolved() {
        Map<Integer, Long> digitCounts = digitCountsOf(cells);
        return digitCounts.keySet().size() == 9 && digitCounts.values().stream().allMatch(count -> count == 9);

    }

    private Map<Integer, Long> digitCountsOf(int[] cellsToCheck) {
        return solvedCellsOf(cellsToCheck)
                .boxed()
                .collect(
                        groupingBy(identity(), counting())
                );
    }

    private IntStream solvedCellsOf(int[] cellsToCheck) {
        return stream(cellsToCheck)
                .filter(value -> value > 0);
    }
}

package org.kovacstelekes.techblog;

import java.util.Iterator;

public class BoardUtils {

    public static final int BOARD_SIZE = 9 * 9;

    /**
     * Parses a 9x9 text grid into a board. Leading whitespaces are ignored;
     * ASCII art grid decorations are allowed, like:
     * <pre>
     * +---+---+---
     * |123|456|789
     * </pre>
     * Empty cells may be marked with ' ' (space), '.' (dot) or 0.
     * Cell values are 1 - 9.
     * Output array has length 9 * 9; unknown cells have value 0, the others
     * 1 - 9.
     *
     * @param puzzle textual representation
     * @return values as an array, 9 elements by row, 81 elements in total
     */
    public static int[] parseToLinear(String puzzle) {
        if (puzzle == null) {
            return null;
        }
        int[] cellValues = new int[BOARD_SIZE];
        int index = 0;

        for (Iterator<String> it = puzzle.lines().iterator(); it.hasNext(); ) {
            String line = it.next().replaceFirst("^\\s+", "");
            if (!gridSeparator(line)) {
                String digits = digitsOf(line);
                for (int column = 0; column < 9; column++) {
                    char digitOrBlank = digits.charAt(column);
                    cellValues[index] = digitOrBlank - '0';
                    index++;
                }
            }
        }
        return cellValues;
    }

    /**
     * Converts 1D (linear) array to 2D (square)
     *
     * @param cellValues 81 values or null
     * @return 9x9 grid; null if board is null
     */
    public static int[][] toBoard(int[] cellValues) {
        if (cellValues == null) {
            return null;
        }
        expectLinearBoard(cellValues);
        int[][] board = new int[9][];
        int cellIndex = 0;
        for (int row = 0; row < 9; row++) {
            board[row] = new int[9];
            for (int column = 0; column < 9; column++) {
                board[row][column] = cellValues[cellIndex];
                cellIndex++;
            }
        }
        return board;
    }

    public static int[] toCellValues(int[][] board) {
        if (board == null) {
            return null;
        }

        expect9x9(board);

        int[] cellValues = new int[BOARD_SIZE];
        int cellIndex = 0;
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                cellValues[cellIndex] = board[row][column];
                cellIndex++;
            }
        }
        return cellValues;
    }

    private static boolean gridSeparator(String line) {
        return line.startsWith("+");
    }

    private static String digitsOf(String line) {
        String digits = line.replaceAll("[^0-9 \\.]", "").replaceAll("[ \\.]", "0");
        if (digits.length() != 9) {
            throw new RuntimeException("Malformed line: " + line);
        }
        return digits;
    }

    /**
     * Formats a linearised board to a String, compatible with parseToLinear
     *
     * @param cellValues linearised board or null; 0 denotes empty/unsolved cell
     * @return an ASCII-art representation of the board
     */
    public static String format(int[] cellValues) {
        if (cellValues == null) {
            return null;
        }
        if (cellValues.length != BOARD_SIZE) {
            throw new IllegalArgumentException();
        }

        StringBuilder text = new StringBuilder();
        for (int cellIndex = 0; cellIndex < cellValues.length; cellIndex++) {
            int row = rowNumber(cellIndex);
            int colN = columnNumber(cellIndex);
            if (row % 3 == 0 && colN == 0) {
                text.append("+---+---+---+\n");
            }
            if (colN % 3 == 0) {
                text.append('|');
            }
            int cellValue = cellValues[cellIndex];
            if (cellValue == 0) {
                text.append('.');
            } else {
                text.append(cellValue);
            }
            if (colN == 8) {
                text.append("|\n");
            }
        }
        text.append("+---+---+---+\n");
        return text.toString();
    }

    private static int columnNumber(int cellIndex) {
        return cellIndex % 9;
    }

    private static int rowNumber(int cellIndex) {
        return cellIndex / 9;
    }

    public static void expect9(int[] array) {
        expectSize(array, 9);
    }

    public static void expectLinearBoard(int[] array) {
        expectSize(array, BOARD_SIZE);
    }

    public static void expectSize(int[] array, int size) {
        if (array.length != size) {
            throw new IllegalArgumentException();
        }
    }

    public static void expect9x9(int[][] board) {
        expectSize(board, 9, 9);
    }

    public static void expectSize(int[][] board, int rows, int columns) {
        if (board.length != rows) {
            throw new IllegalArgumentException();
        }
        for (int[] row : board) {
            expectSize(row, 9);
        }
    }

}

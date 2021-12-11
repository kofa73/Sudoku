package org.kovacstelekes.techblog.sudoku.simplebacktrack;

import java.util.Iterator;

public class BoardParser {
    public int[] parse(String puzzle) {
        int[] cellValues = new int[9 * 9];
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

    private boolean gridSeparator(String line) {
        return line.startsWith("+");
    }

    private String digitsOf(String line) {
        String digits = line.replaceAll("[^0-9 \\.]", "").replaceAll("[ \\.]", "0");
        if (digits.length() != 9) {
            throw new RuntimeException("Malformed line: " + line);
        }
        return digits;
    }
}
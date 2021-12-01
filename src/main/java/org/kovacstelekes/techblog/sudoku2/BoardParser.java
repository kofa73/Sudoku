package org.kovacstelekes.techblog.sudoku2;

import org.kovacstelekes.techblog.sudoku2.model.Board;

import java.util.Iterator;

public class BoardParser {
    public Board parse(String puzzle) {
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
        Board board = Board.fromState(cellValues);
        System.out.println("Parsed board:\n" + board);
        return board;
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

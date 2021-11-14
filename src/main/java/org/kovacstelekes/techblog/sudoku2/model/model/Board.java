package org.kovacstelekes.techblog.sudoku2.model.model;

import java.util.*;

public class Board {
    private final List<Container> containers = new ArrayList<>();
    private final List<Cell> cells = new ArrayList<>();
    private final List<Container> rows = new ArrayList<>();

    public Board() {
        List<Container> columns = new ArrayList<>();
        List<Container> grids = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            Container row = new Container("row" + i);
            rows.add(row);
            containers.add(row);

            Container column = new Container("column" + i);
            columns.add(column);
            containers.add(column);

            Container grid = new Container("grid" + i);
            grids.add(grid);
            containers.add(grid);
        }

        for (int i = 0; i < 81; i++) {
            int rowNumber = i / 9;
            int columnNumber = i % 9;
            int gridNumber = (rowNumber / 3) * 3 + columnNumber / 3;
            Cell cell = new Cell("cell[" + rowNumber + ";" + columnNumber + ";" + gridNumber + "]");
            cells.add(cell);
            rows.get(rowNumber).addCell(cell);
            columns.get(columnNumber).addCell(cell);
            grids.get(gridNumber).addCell(cell);
        }
    }

    public Cell cellAt(int row, int column) {
        return rows.get(row).cellAt(column);
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        for (int rowN = 0; rowN < rows.size(); rowN++) {
            if (rowN % 3 == 0) {
                text.append("+---+---+---\n");
            }
            for (int colN = 0; colN < 9; colN++) {
                if (colN % 3 == 0) {
                    text.append('|');
                }
                Cell cell = rows.get(rowN).cellAt(colN);
                String value = cell.solution().map(String::valueOf).orElse(" ");
                text.append(value);
            }
            text.append("\n");
        }
        return text.toString();
    }

    public void solve(String puzzle) {
        int row = 0;
        for (Iterator<String> it = puzzle.lines().iterator(); it.hasNext();) {
            String line = it.next();
            if (!line.startsWith("+")) {
                String digits = line.replaceAll("[^0-9 ]", "");
                if (digits.length() != 9) {
                    throw new RuntimeException("Malformed line: " + line);
                }
                for (int column = 0; column < 9; column++) {
                    char digitOrBlank = digits.charAt(column);
                    if (digitOrBlank != ' ') {
                        int rowNumber = row;
                        int columnNumber = column;
                        cellAt(rowNumber, columnNumber).setKnownValue(digitOrBlank - '0');
                    }
                }
                row++;
            }
        }

        String board = toString();
        System.out.println("Parsed board:\n" + board);

        while (true) {
            containers.stream().forEach(Container::removeSolvedValuesFromCells);
            containers.stream().forEach(Container::solve);
            String updatedBoard = toString();
            if (updatedBoard.equals(board)) {
                break;
            } else {
                board = updatedBoard;
                System.out.println("Updated board:\n" + updatedBoard);
            }
        }

        if (board.contains(" ")) {
            System.out.println("failed :(");
            containers.stream().filter(c -> !c.isSolved()).forEach(c -> System.out.println("Unsolved values in " + c + ": " + c.unsolvedValues()));
            cells.stream().filter(c -> !c.isSolved()).forEach(c -> System.out.println("Unsolved values in " + c + ": " + c.values()));
        } else {
            System.out.println("success :)");
        }
    }
}

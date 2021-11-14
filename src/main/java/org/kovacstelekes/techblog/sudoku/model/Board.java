package org.kovacstelekes.techblog.sudoku.model;

import java.util.*;

import static java.util.Collections.unmodifiableList;
import static org.kovacstelekes.techblog.sudoku.model.CommonConstraints.checkPositionIsValid;

public class Board {
    public static Queue<Runnable> QUEUE = new LinkedList<>();

    private final List<Row> rows;
    private final List<Column> columns;
    private final List<Grid> grids;
    private final List<Cell> cells;

    public Board() {
        rows = new ArrayList<>(9);
        columns = new ArrayList<>(9);
        grids = new ArrayList<>(9);
        cells = new ArrayList<>(81);

        for (int i = 0; i < 9; i++) {
            rows.add(new Row(i));
            columns.add(new Column(i));
            grids.add(new Grid(i));
        }

        for (int i = 0; i < 81; i++) {
            Cell cell = new Cell(i);
            cells.add(cell);
            int rowNumber = i / 9;
            int columnNumber = i % 9;
            rows.get(rowNumber).addCell(cell);
            columns.get(columnNumber).addCell(cell);
            int gridIndex = (rowNumber / 3) * 3 + columnNumber / 3;
            grids.get(gridIndex).addCell(cell);
        }
    }

    public Cell cellAt(int row, int column) {
        return row(row).cellAt(column);
    }

    public Row row(int rowNumber) {
        checkPositionIsValid(rowNumber);
        return rows.get(rowNumber);
    }

    public Column column(int rowNumber) {
        checkPositionIsValid(rowNumber);
        return columns.get(rowNumber);
    }

    public Grid grid(int gridNumber) {
        checkPositionIsValid(gridNumber);
        return grids.get(gridNumber);
    }

    public List<Row> rows() {
        return unmodifiableList(rows);
    }

    public List<Column> columns() {
        return unmodifiableList(columns);
    }

    public List<Grid> grids() {
        return unmodifiableList(grids);
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        for (Row row : rows) {
            if (row.id() % 3 == 0) {
                text.append("+---+---+---\n");
            }
            for (int i = 0; i < 9; i++) {
                if (i % 3 == 0) {
                    text.append('|');
                }
                Cell cell = row.cellAt(i);
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
                        Board.QUEUE.add(() -> cellAt(rowNumber, columnNumber).solution(digitOrBlank - '0'));
                    }
                }
                row++;
            }
        }

        while (true) {
            Runnable r = Board.QUEUE.poll();
            if (r == null) {
                break;
            }
            r.run();
        }
    }
}

package org.kovacstelekes.techblog.sudoku3.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.function.Predicate.not;

public class Board {
    public static boolean disableDiags;
    private final List<Row> rows = new ArrayList<>();
    private final List<Column> columns = new ArrayList<>();
    private final List<Grid> grids = new ArrayList<>();
    private final List<Container> containers = new ArrayList<>();
    private final List<Cell> cells = new ArrayList<>();

    public static long nBoards = 0;

    public int[] toState() {
        return cells.stream()
                .map(cell -> cell.solution().orElse(-1))
                .mapToInt(Integer::intValue)
                .toArray();
    }

    public static Board fromState(int[] values) {
        checkArgument(values.length == 9 * 9);
        Board board = new Board();

        Board.disableDiags = true;
        try {
            int index = 0;
            for (Cell cell : board.cells) {
                int cellValue = values[index];
                if (cellValue >= 0 && cellValue <= 8) {
                    cell.solution(cellValue);
                } else if (cellValue != -1) {
                    throw new IllegalArgumentException("Invalid cell value at index " + index + ": " + cellValue);
                }
                index++;
            }
            return board;
        } finally {
            Board.disableDiags = false;
        }
    }

    private Board() {
        nBoards++;
        createContainers();
        createCellsInContainers();
        registerRelatedCells();
    }

    private void registerRelatedCells() {
        for (Container container : containers) {
            List<Cell> cellsInContainer = container.cells();
            for (Cell cell : cellsInContainer) {
                cell.addRelated(cellsInContainer);
            }
        }
    }

    private void createCellsInContainers() {
        int cellNumber = 0;
        for (Row row : rows) {
            int rowNumber = row.ordinal();
            for (Container column : columns) {
                int columnNumber = column.ordinal();
                int gridNumber = (rowNumber / 3) * 3 + columnNumber / 3;
                Cell cell = new Cell(cellNumber);
                cells.add(cell);
                cellNumber++;

                row.addCell(cell);
                column.addCell(cell);
                grids.get(gridNumber).addCell(cell);
            }
        }
    }

    private void createContainers() {
        for (int i = 0; i < 9; i++) {
            Row row = new Row(i);
            rows.add(row);
            containers.add(row);

            Column column = new Column(i);
            columns.add(column);
            containers.add(column);

            Grid grid = new Grid(i);
            grids.add(grid);
            containers.add(grid);
        }
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        int index = 0;
        for (Container row : rows) {
            if (row.ordinal() % 3 == 0) {
                text.append("+---+---+---\n");
            }
            for (int colN = 0; colN < 9; colN++) {
                if (colN % 3 == 0) {
                    text.append('|');
                }
                int cellValue = cells.get(index).solution().orElse(-1);
                // internally, digits are 0..8, -1 being empty
                cellValue++;
                if (cellValue == 0) {
                    text.append('.');
                } else {
                    text.append(cellValue);
                }
                index++;
            }
            text.append("\n");
        }
        return text.toString();
    }

    public boolean isSolved() {
        return cells.stream().allMatch(Cell::isSolved);
    }

    public Stream<Cell> unsolvedCells() {
        return cells.stream().filter(not(Cell::isSolved));
    }
}

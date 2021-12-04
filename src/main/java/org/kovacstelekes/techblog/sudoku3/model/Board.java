package org.kovacstelekes.techblog.sudoku3.model;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

public class Board {
    private final List<Row> rows = new ArrayList<>();
    private final List<Column> columns = new ArrayList<>();
    private final List<Grid> grids = new ArrayList<>();
    private final List<Container> containers = new ArrayList<>();
    private final List<Cell> cells = new ArrayList<>();

    public static Board fromState(int[] values) {
        checkArgument(values.length == 9 * 9);
        Board board = new Board();

        int index = 0;
        for (Cell cell : board.cells) {
            int cellValue = values[index];
            if (cellValue >= 1 && cellValue <= 9) {
                cell.solution(cellValue);
            } else if (cellValue != 0) {
                throw new IllegalArgumentException("Invalid cell value at index " + index + ": " + cellValue);
            }
            index++;
        }
        return board;
    }

    private Board() {
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
        for (Row row : rows) {
            int rowNumber = row.ordinal();
            for (Container column : columns) {
                int columnNumber = column.ordinal();
                int gridNumber = (rowNumber / 3) * 3 + columnNumber / 3;
                Cell cell = new Cell();
                cells.add(cell);

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
                int cellValue = cells.get(index).solution().orElse(0);
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
}

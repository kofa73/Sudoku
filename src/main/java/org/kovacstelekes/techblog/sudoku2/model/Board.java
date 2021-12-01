package org.kovacstelekes.techblog.sudoku2.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;

public class Board {
    private final List<Container> containers = new ArrayList<>();
    private final List<Cell> cells = new ArrayList<>();
    private final List<Row> rows = new ArrayList<>();

    private Board() {
        List<Column> columns = new ArrayList<>();
        List<Grid> grids = new ArrayList<>();

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

        int cellIndex = 0;
        for (Container row : rows) {
            int rowNumber = row.ordinal();
            for (Container column : columns) {
                int columnNumber = column.ordinal();
                int gridNumber = (rowNumber / 3) * 3 + columnNumber / 3;
                Cell cell = new Cell("cell[" + rowNumber + ";" + columnNumber + ";" + gridNumber + "]", cellIndex);
                cellIndex++;
                cells.add(cell);
                row.addCell(cell);
                column.addCell(cell);
                grids.get(gridNumber).addCell(cell);
            }
        }
    }

    public boolean deduceValues() {
        applyRules();

        return isSolved();
    }

    private void applyRules() {
        boolean progressed;
        do {
            progressed = eliminateCellValues() || applyingContainerRulesProducesNewDiscovery();
        } while (progressed);
    }

    private boolean applyingContainerRulesProducesNewDiscovery() {
        return containers.stream().flatMap(Container::solve).reduce(Boolean::logicalOr).orElse(false);
    }

    private boolean eliminateCellValues() {
        return containers.stream()
                .flatMap(Container::removeSolvedValuesFromCells)
                .reduce(Boolean::logicalOr)
                .orElse(false);
    }

    private boolean isSolved() {
        return cells.stream().allMatch(Cell::isSolved);
    }

    public int[] toState() {
        return cells.stream()
                .map(cell -> cell.solution().orElse(0))
                .mapToInt(Integer::intValue)
                .toArray();
    }

    public static Board fromState(int[] values) {
        checkArgument(values.length == 9 * 9);
        Board board = new Board();
        int index = 0;

        for (Cell cell : board.cells) {
            int cellValue = values[index];
            if (cellValue >= 1 && cellValue <= 9) {
                cell.setKnownValue(cellValue);
            } else if (cellValue != 0) {
                throw new IllegalArgumentException("Invalid cell value at index " + index + ": " + cellValue);
            }
            index++;
        }
        return board;
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        int index = 0;
        for (Row row : rows) {
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

    public Stream<Cell> unsolvedCells() {
        return cells.stream().filter(cell -> !cell.isSolved());
    }
}

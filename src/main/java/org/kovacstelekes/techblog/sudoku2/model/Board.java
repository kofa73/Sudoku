package org.kovacstelekes.techblog.sudoku2.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public Cell cellAt(int row, int column) {
        return rows.get(row).cellAt(column);
    }

    public boolean solve() {
        boolean solved = updateBoardWhilePossible();

        if (solved) {
            System.out.println("success :)");
            System.out.println(this);
        } else {
            System.out.println("failed :(");
            Cell nextCellToCheck = cells.stream()
                    .filter(cell -> !cell.isSolved())
                    .reduce((Cell c1, Cell c2) -> c1.numberOfOptions() < c2.numberOfOptions() ? c1 : c2)
                    .orElseThrow(() -> new RuntimeException("No unsolved cells"));

            containers.stream().filter(c -> !c.isSolved()).forEach(c -> System.out.println("Unsolved values in " + c + ": " + c.unsolvedValues()));
            cells.stream().filter(c -> !c.isSolved()).forEach(c -> System.out.println("Unsolved values in " + c));

            System.out.println("Next cell should be " + nextCellToCheck);

            for (int possibleValue : nextCellToCheck.values()) {
                System.out.println("Guessing " + possibleValue + " for " + nextCellToCheck);
                int[] boardValues = this.toState();
                boardValues[nextCellToCheck.ordinal()] = possibleValue;
                Board boardWithGuess = Board.fromState(boardValues);
                try {
                    solved = boardWithGuess.solve();
                } catch (IllegalStateException e) {
                    solved = false;
                }
                if (solved) {
                    break;
                }
            }
        }
        return solved;
    }

    private boolean updateBoardWhilePossible() {
        int[] currentState = toState();
        while (true) {
            containers.stream().forEach(Container::removeSolvedValuesFromCells);
            containers.stream().forEach(Container::solve);
            int[] updatedState = toState();
            if (Arrays.equals(currentState, updatedState)) {
                break;
            } else {
                currentState = updatedState;
                System.out.println("Updated board\n");
            }
        }
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
}

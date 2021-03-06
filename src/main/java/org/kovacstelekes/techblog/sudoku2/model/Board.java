package org.kovacstelekes.techblog.sudoku2.model;

import org.kovacstelekes.techblog.BoardUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class Board {
    private final List<Container> containers = new ArrayList<>();
    private final List<Cell> cells = new ArrayList<>();
    private final List<Row> rows = new ArrayList<>();

    public static long nBoards = 0;

    private Board() {
        nBoards++;
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

    public IterationOutcome deduceValues() {
        IterationOutcome rulesOutcome = applyRules();
        return rulesOutcome;
        //return rulesOutcome.or(() -> new IterationOutcome(rulesOutcome.progressed(), outcome()));
    }

    private IterationOutcome applyRules() {
        IterationOutcome iterationOutcome;

        do {
            IterationOutcome outcome = eliminateCellValues();
            iterationOutcome = outcome.or(this::applyingContainerRulesProducesNewDiscovery);
        } while (iterationOutcome.needsAnotherIteration());

        return iterationOutcome;
    }

    private IterationOutcome applyingContainerRulesProducesNewDiscovery() {
        return containers.stream()
                .flatMap(Container::solve)
                .reduce(IterationOutcome::reduce).orElseGet(() -> new IterationOutcome(false, outcome()));
    }

    private IterationOutcome eliminateCellValues() {
        AtomicReference<IterationOutcome> peekedOutcome = new AtomicReference<>();
        IterationOutcome outcome = containers.stream()
                .flatMap(Container::removeSolvedValuesFromCells)
                .peek(peeked -> peekedOutcome.set(peeked))
                .takeWhile(iterationOutcome -> iterationOutcome.outcome() != Outcome.INVALID)
                .reduce(IterationOutcome::reduce)
                .orElse(new IterationOutcome(false, outcome()));
        if (peekedOutcome.get() == null) {
            return outcome;
        }
        return IterationOutcome.reduce(peekedOutcome.get(), outcome);
    }

    private Outcome outcome() {
        Outcome outcome;
        if (cells.stream().anyMatch(cell -> cell.outcome() == Outcome.INVALID)) {
            outcome = Outcome.INVALID;
        } else if (cells.stream().allMatch(cell -> cell.outcome() == Outcome.UNIQUE_SOLUTION)) {
            outcome = Outcome.UNIQUE_SOLUTION;
        } else {
            outcome = Outcome.SEVERAL_POSSIBILITIES;
        }
        return outcome;
    }

    public int[] toState() {
        return cells.stream()
                .map(cell -> cell.solution().orElse(0))
                .mapToInt(Integer::intValue)
                .toArray();
    }

    public static Board fromState(int[] values) {
        BoardUtils.expectLinearBoard(values);
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
        return cells.stream().filter(cell -> cell.outcome() == Outcome.SEVERAL_POSSIBILITIES);
    }
}

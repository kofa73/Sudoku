package org.kovacstelekes.techblog.sudoku.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BoardTest {
    private final Board board = new Board();

    @Test
    void basicLayoutIsCorrect() {
        Cell topLeftGridMiddle = board.cellAt(1, 1);
        assertThat(topLeftGridMiddle.id()).isEqualTo(10);
        assertThat(topLeftGridMiddle.row().id()).isEqualTo(1);
        assertThat(topLeftGridMiddle.column().id()).isEqualTo(1);
        assertThat(topLeftGridMiddle.grid().id()).isEqualTo(0);

        Cell middleGridMiddle = board.cellAt(4, 4);
        assertThat(middleGridMiddle.id()).isEqualTo(40);
        assertThat(middleGridMiddle.row().id()).isEqualTo(4);
        assertThat(middleGridMiddle.column().id()).isEqualTo(4);
        assertThat(middleGridMiddle.grid().id()).isEqualTo(4);

        Cell bottomRightMiddle = board.cellAt(7, 7);
        assertThat(bottomRightMiddle.id()).isEqualTo(70);
        assertThat(bottomRightMiddle.row().id()).isEqualTo(7);
        assertThat(bottomRightMiddle.column().id()).isEqualTo(7);
        assertThat(bottomRightMiddle.grid().id()).isEqualTo(8);

        for (Row row : board.rows()) {
            assertThat(row.cells()).allMatch(cell -> cell.row() == row);
        }

        for (Column column : board.columns()) {
            assertThat(column.cells()).allMatch(cell -> cell.column() == column);
        }

        for (Grid grid : board.grids()) {
            assertThat(grid.cells()).allMatch(cell -> cell.grid() == grid);
        }
    }
}

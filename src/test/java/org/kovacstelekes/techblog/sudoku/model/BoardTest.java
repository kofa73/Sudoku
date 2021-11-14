package org.kovacstelekes.techblog.sudoku.model;

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

    @Test
    void solveSimple() {
        board.solve("""
                |   | 84|  3|
                |13 | 5 | 94|
                |9 5| 13| 7 |
                +---+---+---+
                |4  | 96|   |
                |  3|7  |  1|
                |75 | 3 |469|
                +---+---+---+
                |5 4| 78| 2 |
                |36 | 29| 5 |
                | 2 |   |3 7|
                """);

        System.out.println(board);
        System.out.println("done");
    }

    @Test
    void solveMedium() {
        board.solve("""
                |62 |  1|35 |
                |374| 56|   |
                | 5 | 4 |  2|
                +---+---+---+
                | 1 |7 4|   |
                |  6|13 |   |
                |   | 9 |   |
                +---+---+---+
                |   |   |   |
                |  3|  5|678|
                |791| 8 |2  |
                """);

        System.out.println(board);
        System.out.println("done");
    }

    @Test
    void solveHard() {
        board.solve("""
                |2  | 9 |7  |
                |   |   | 68|
                |1  |8  | 9 |
                +---+---+---+
                |   |   |   |
                | 1 |  3|9  |
                |4  | 7 |5  |
                +---+---+---+
                | 5 | 4 |1  |
                | 6 | 8 |   |
                | 3 |7  |6 5|
                """);

            System.out.println(board);
            System.out.println("done");
    }
}

package org.kovacstelekes.techblog.sudoku.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CellTest {
    private final Cell cell = new Cell(1);
    private final Row row = mock(Row.class);
    private final Column column = mock(Column.class);
    private final Grid grid = mock(Grid.class);

    @BeforeEach
    void setup() {
        cell
                .row(row)
                .column(column)
                .grid(grid);
    }

    @Test
    void exclude() {
        for (int i = 1; i < 8; i++) {
            assertThat(cell.exclude(i)).describedAs("No solution found by exclude()").isEmpty();
            assertThat(cell.solution()).describedAs("No solution is available").isEmpty();
        }
        assertThat(cell.exclude(5)).describedAs("Excluding an already excluded value does not do anything").isEmpty();
        assertThat(cell.exclude(8)).describedAs("The correct solution is found by last exclude").contains(9);

        verify(row).solvedCell(cell);
        verify(column).solvedCell(cell);
        verify(grid).solvedCell(cell);

        assertThat(cell.solution()).describedAs("Established solution is returned").contains(9);
        assertThatIllegalArgumentException().describedAs("Excluding the solution means a contradiction").isThrownBy(() -> cell.exclude(9));

        assertThatIllegalArgumentException().describedAs("Setting another solution means a contradiction").isThrownBy(() -> cell.solution(1));
    }
}
package org.kovacstelekes.techblog.sudoku.model;

import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class RowTest {
    private final Row row = new Row(1);
    private final List<Cell> cells = new ArrayList<>();

//    @BeforeEach
//    void setup() {
//        for (int i = 0; i < 9; i++) {
//            Cell cell = new Cell(i);
//            cell.value(i + 1);
//            cells.add(cell);
//            row.addCell(cell);
//        }
//    }
//
//    @Test
//    void lastPossibleCellIsSolvedAutomatically() {
//        for (int i = 0; i < 8; i++) {
//            row.solved(cells)
//        }
//    }

}

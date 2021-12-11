package com.baeldung.algorithms.sudoku;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static com.baeldung.algorithms.sudoku.BacktrackingAlgorithm.BOARD_SIZE;
import static com.baeldung.algorithms.sudoku.BacktrackingAlgorithm.BOARD_START_INDEX;

class BacktrackingAlgorithmTest {
    private final BacktrackingAlgorithm solver = new BacktrackingAlgorithm();
    private int hash = 0;
    private boolean result = false;

    @Test
    void simple() {
        int[][] board = {
                {0, 0, 0, 0, 8, 4, 0, 0, 3},
                {1, 3, 0, 0, 5, 0, 0, 9, 4},
                {9, 0, 5, 0, 1, 3, 0, 7, 0},
                {4, 0, 0, 0, 9, 6, 0, 0, 0},
                {0, 0, 3, 7, 0, 0, 0, 0, 1},
                {7, 5, 0, 0, 3, 0, 4, 6, 9},
                {5, 0, 4, 0, 7, 8, 0, 2, 0},
                {3, 6, 0, 0, 2, 9, 0, 5, 0},
                {0, 2, 0, 0, 0, 0, 3, 0, 7}
        };
        solveAndPrint(board);
    }

    @Test
    void solveMedium() {
        int[][] board = {
                {6, 2, 0, 0, 0, 1, 3, 5, 0},
                {3, 7, 4, 0, 5, 6, 0, 0, 0},
                {0, 5, 0, 0, 4, 0, 0, 0, 2},
                {0, 1, 0, 7, 0, 4, 0, 0, 0},
                {0, 0, 6, 1, 3, 0, 0, 0, 0},
                {0, 0, 0, 0, 9, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 3, 0, 0, 5, 6, 7, 8},
                {7, 9, 1, 0, 8, 0, 2, 0, 0}
        };

        solveAndPrint(board);
    }

    @Test
    void solveHard() {
        int[][] board = {
                {2, 0, 0, 0, 9, 0, 7, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 6, 8},
                {1, 0, 0, 8, 0, 0, 0, 9, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 0, 0, 0, 3, 9, 0, 0},
                {4, 0, 0, 0, 7, 0, 5, 0, 0},
                {0, 5, 0, 0, 4, 0, 1, 0, 0},
                {0, 6, 0, 0, 8, 0, 0, 0, 0},
                {0, 3, 0, 7, 0, 0, 6, 0, 5}
        };

        solveAndPrint(board);
    }

    @Test
    void solveEmpty() {
        int[][] board = {
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0}
        };

        solveAndPrint(board);
    }

    @Test
    void evil() {
        int[][] board = {
                {9, 0, 0, 0, 0, 0, 0, 4, 0},
                {0, 7, 0, 6, 0, 0, 3, 0, 9},
                {0, 0, 0, 0, 3, 0, 0, 5, 0},
                {0, 5, 0, 0, 0, 1, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 4},
                {1, 0, 0, 0, 2, 0, 9, 0, 6},
                {5, 0, 0, 0, 6, 0, 2, 0, 1},
                {0, 0, 0, 0, 0, 0, 0, 3, 0},
                {0, 0, 8, 7, 0, 0, 0, 0, 0}
        };
        solveAndPrint(board);
    }

    @Test
    void impossible() {
        // impossible - no solution
        int[][] board = {
                {9, 0, 0, 0, 0, 0, 0, 4, 0},
                {0, 7, 0, 6, 0, 0, 3, 0, 9},
                {0, 0, 0, 0, 3, 0, 0, 8, 0},
                {0, 0, 0, 9, 0, 0, 0, 0, 0},
                {0, 4, 0, 0, 0, 0, 0, 0, 7},
                {1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 1, 0, 0, 0, 9, 0},
                {0, 0, 7, 0, 6, 9, 0, 0, 5},
                {6, 0, 0, 2, 0, 0, 0, 0, 0}
        };
        solveAndPrint(board);
    }

    @Test
    void worldsHardestSudoku() {
        // "World's hardest"
        int[][] board = {
                {8, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 3, 6, 0, 0, 0, 0, 0},
                {0, 7, 0, 0, 9, 0, 2, 0, 0},
                {0, 5, 0, 0, 0, 7, 0, 0, 0},
                {0, 0, 0, 0, 4, 5, 7, 0, 0},
                {0, 0, 0, 1, 0, 0, 0, 3, 0},
                {0, 0, 1, 0, 0, 0, 0, 6, 8},
                {0, 0, 8, 5, 0, 0, 0, 1, 0},
                {0, 9, 0, 0, 0, 0, 4, 0, 0}
        };
        solveAndPrint(board);
    }

    private void solveAndPrint(int[][] board) {
//        long start = System.currentTimeMillis();
//        long end = start + 20_000;
//        do {
//            int[][] copy = new int[9][];
//            for (int i = 0; i < 9; i++) {
//                copy[i] = board[i].clone();
//            }
//            result ^= solveIt(copy);
//        } while (System.currentTimeMillis() < end);
//
//        start = System.currentTimeMillis();
//        end = System.currentTimeMillis() + 10_000;
//        int cnt = 0;
//        do {
//            int[][] copy = new int[9][];
//            for (int i = 0; i < 9; i++) {
//                copy[i] = board[i].clone();
//            }
//            result ^= solveIt(copy);
//            cnt++;
//        } while (System.currentTimeMillis() < end);
//        long elapsed = System.currentTimeMillis() - start;
//        System.out.println(String.format("elapsed: %d ms; cnt=%d, perf=%f", elapsed, cnt, elapsed / (double) cnt));

        solver.solve(board);
        System.out.println("nChecks: " + solver.nChecks());

        for (int row = BOARD_START_INDEX; row < BOARD_SIZE; row++) {
            for (int column = BOARD_START_INDEX; column < BOARD_SIZE; column++) {
                System.out.print(board[row][column] + " ");
            }
            System.out.println();
        }
        System.out.println(hash + " " + result);
    }

    private boolean solveIt(int[][] board) {
        result ^= solver.solve(board);
        int index = (int)(System.currentTimeMillis() % 9);
        if (System.currentTimeMillis() > board[index][index]) {
            hash ^= Arrays.hashCode(board[index]);
            return hash % 2 == 1;
        }
        hash += Arrays.hashCode(board[index/2]);
        return hash % 2 == 0;
    }
}
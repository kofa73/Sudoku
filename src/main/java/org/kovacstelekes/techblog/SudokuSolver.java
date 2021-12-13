package org.kovacstelekes.techblog;

public interface SudokuSolver {
    /**
     * Solves the linearly-listed board
     *
     * @param cellValues the 9x9 board to solve (cell value 0 means unsolved/empty)
     * @return the solution, or null if none found. If the board is already solved when the method is invoked,
     * it may simply return the input array.
     */
    int[] solve(int[] cellValues);
}

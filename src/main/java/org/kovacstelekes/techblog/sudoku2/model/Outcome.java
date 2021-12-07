package org.kovacstelekes.techblog.sudoku2.model;

import static org.assertj.core.util.Preconditions.checkNotNull;

public enum Outcome {
    // some item cannot be placed, or a cell cannot have any value because of exclusion rules
    INVALID,
    // only one possibility exists, and that is the solution
    UNIQUE_SOLUTION,
    // unsolved cell or multiple solutions exist
    SEVERAL_POSSIBILITIES;

    static Outcome reduce(Outcome one, Outcome other) {
        checkNotNull(one, "one");
        checkNotNull(other, "other");
        Outcome combined;
        if (one == INVALID || other == INVALID) {
            combined = INVALID;
        } else if (one == UNIQUE_SOLUTION && other == UNIQUE_SOLUTION) {
            combined = UNIQUE_SOLUTION;
        } else {
            combined = SEVERAL_POSSIBILITIES;
        }
        return combined;
    }
}

package org.kovacstelekes.techblog.sudoku3.model;

public enum Outcome {
    // some item cannot be placed, or a cell cannot have any value because of exclusion rules
    INVALID,
    // just found a new solution
    NEWLY_SOLVED,
    // already solved, no change
    ALREADY_SOLVED_NO_CHANGE,
    // updated, but still unsolved
    UPDATED_STILL_SEVERAL_POSSIBILITIES,
    // unsolved, not updated
    SEVERAL_POSSIBILITIES_NO_CHANGE

//    static Outcome reduce(Outcome one, Outcome other) {
//        checkNotNull(one, "one");
//        checkNotNull(other, "other");
//        Outcome combined;
//        if (one == INVALID || other == INVALID) {
//            combined = INVALID;
//        } else if (one == UNIQUE_SOLUTION && other == UNIQUE_SOLUTION) {
//            combined = UNIQUE_SOLUTION;
//        } else {
//            combined = SEVERAL_POSSIBILITIES;
//        }
//        return combined;
//    }
}

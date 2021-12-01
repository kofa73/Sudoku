package org.kovacstelekes.techblog.sudoku2.model;

import static com.google.common.base.Preconditions.checkElementIndex;

class CommonConstraints {
    static void checkPositionIsValid(int position) {
        checkElementIndex(position, 9);
    }
}

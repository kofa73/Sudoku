package org.kovacstelekes.techblog.sudoku.model;

import static com.google.common.base.Preconditions.checkElementIndex;

class CommonConstraints {
    static void checkPositionIsValid(int position) {
        checkElementIndex(position, 9);
    }
}

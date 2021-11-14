package org.kovacstelekes.techblog.sudoku.model;

import static com.google.common.base.Preconditions.checkElementIndex;

public class CommonConstraints {
    public static void checkPositionIsValid(int position) {
        checkElementIndex(position, 9);
    }
}

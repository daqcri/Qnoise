/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.util;

public class Pair<TLeft, TRight> {
    private TLeft left;
    private TRight right;

    public Pair(TLeft left, TRight right) {
        this.left = left;
        this.right = right;
    }

    public TLeft getLeft() {
        return left;
    }

    public TRight getRight() {
        return right;
    }
}

/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.model;

public class RandomModel extends ModelBase {
    /** {@inheritDoc} */
    @Override
    public int nextIndex(int start, int end) {
        int range = end - start;
        if (range <= 0) {
            return Integer.MIN_VALUE;
        }
        return (int)Math.floor(Math.random() * range) + start;
    }
}

/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise;

public class RandomIndexStrategy extends IndexStrategy {
    /**
     * Generate a random index within the range [start, end).
     * @param start start index.
     * @param end end index.
     * @return random index.
     */
    @Override
    public int getIndex(int start, int end) {
        int range = end - start - 1;
        return (int)Math.round(Math.random() * range) + start;
    }
}

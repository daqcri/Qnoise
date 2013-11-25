/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise;

import com.google.common.collect.Sets;

import java.util.HashSet;

public class RandomIndex extends IndexGenerationBase {
    private HashSet<Integer> log = Sets.newHashSet();
    /** {@inheritDoc} */
    @Override
    public int nextIndex(int start, int end) {
        int range = end - start - 1;
        return (int)Math.round(Math.random() * range) + start;
    }

    /** {@inheritDoc} */
    @Override
    public int nextIndexWithoutReplacement(int start, int end, boolean reset) {
        if (reset) {
            log.clear();
        }

        if (end - start + 1 == log.size()) {
            return Integer.MIN_VALUE;
        }

        int v = nextIndex(0, end);
        while (log.contains(v)) {
            v ++;
            if (v > end)
                v = start;
        }

        log.add(v);
        return v;
    }

    /** {@inheritDoc} */
    @Override
    public double dart() {
        return Math.random();
    }
}

/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.model;

import com.google.common.collect.Sets;

import java.util.HashSet;

/**
 * Strategy class for generating index.
 */
public abstract class ModelBase {
    protected HashSet<Integer> log;
    protected int start;
    protected int end;

    public ModelBase() {
        log = Sets.newHashSet();
    }

    /**
     * Generates an index within the range [start, end).
     * @param start range start.
     * @param end range end.
     * @return index.
     */
    public abstract int nextIndex(int start, int end);

    /**
     * Generates an index without replacement (no duplicate).
     * It returns {@code Integer.MIN_VALUE} when there is no more index available.
     * @param start range start.
     * @param end range end.
     * @param reset reset the generation.
     * @return index.
     */
    public int nextIndexWithoutReplacement(int start, int end, boolean reset) {
        if (reset) {
            log.clear();
            this.start = start;
            this.end = end;
        }

        if (start != this.start || end != this.end) {
            throw new IllegalArgumentException(
                "Start / end are not as the same as the previous run."
            );
        }

        if (end - start == log.size()) {
            return Integer.MIN_VALUE;
        }

        int v = nextIndex(start, end);
        while (log.contains(v)) {
            v ++;
            if (v >= end)
                v = start;
        }

        log.add(v);
        return v;
    }
}

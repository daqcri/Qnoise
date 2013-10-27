/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;
import qa.qcri.qnoise.util.Pair;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NoiseGenerator {

    /**
     * Generate a random index within the range [start, end).
     * @param start start index.
     * @param end end index.
     * @return random index.
     */
    private static int getRandomIndex(int start, int end) {
        int range = end - start - 1;
        return (int)Math.round(Math.random() * range) + start;
    }

    /**
     * Null injection based on the given specification.
     * @param spec {@link NoiseSpec}.
     * @param data input data.
     * @param <T> data element type.
     * @return injected data.
     */
    public <T> NoiseGenerator nullInject(NoiseSpec spec, List<T[]> data, NoiseReport report) {
        HashSet<Pair<Integer, Integer>> log = Sets.newHashSet();
        Stopwatch stopwatch = new Stopwatch().start();
        switch (spec.getModal()) {
        case RANDOM:
            double perc = spec.getPerc();
            int len = (int)Math.floor(perc * data.size());
            report.addMetric(NoiseReport.Metric.ChangedItem, len);
            int count = 0;
            while(count < len) {
                int index = getRandomIndex(0, data.size());
                if (spec.getGranularity() == NoiseGranularity.CELL) {
                    T[] rowData = data.get(index);
                    int cellIndex = getRandomIndex(0, rowData.length);
                    Pair<Integer, Integer> record = new Pair<>(index, cellIndex);
                    if (log.contains(record)) {
                        continue;
                    }
                    rowData[cellIndex] = null;
                } else {
                    Pair<Integer, Integer> record = new Pair<>(index, -1);
                    if (log.contains(record)) {
                        continue;
                    }
                    data.set(index, null);
                }
                count ++;
            }
            break;
        case NORMAL:
        default:
            throw new UnsupportedOperationException("Unsupported data distribution modal.");
        }

        long elapsedTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        report.addMetric(NoiseReport.Metric.InjectionTime, elapsedTime);
        return this;
    }
}

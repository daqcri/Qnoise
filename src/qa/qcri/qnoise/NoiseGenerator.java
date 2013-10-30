/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;
import qa.qcri.qnoise.util.Pair;
import qa.qcri.qnoise.util.Tracer;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NoiseGenerator {
    public static enum Type {
        Missing,
        Duplicate;

        public static NoiseGenerator.Type getGeneratorType(String modal) {
            if (modal.equalsIgnoreCase("m")) {
                return Missing;
            }

            if (modal.equalsIgnoreCase("d")) {
                return Duplicate;
            }

            throw new IllegalArgumentException("Unknown modal string " + modal);
        }
    }

    private Tracer tracer = Tracer.getTracer(NoiseGenerator.class);

    /**
     * Calculates the histogram.
     */
    public static int[] calcHistogram(double[] data, double min, double max, int numBins) {
        final int[] result = new int[numBins];
        final double binSize = (max - min)/numBins;

        for (double d : data) {
            int bin = (int) ((d - min) / binSize);
            if (bin < 0) { /* this data is smaller than min */ }
            else if (bin >= numBins) { /* this data point is bigger than max */ }
            else {
                result[bin] += 1;
            }
        }
        return result;
    }

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
     * Missing value injection based on the given specification.
     * @param spec {@link NoiseSpec}.
     * @param data input data.
     * @param <T> data element type.
     * @return injected data.
     */
    public <T> NoiseGenerator missingInject(NoiseSpec spec, List<T[]> data, NoiseReport report) {
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
                    tracer.verbose(String.format("[%d, %d] <- null", index, cellIndex));
                } else {
                    Pair<Integer, Integer> record = new Pair<>(index, -1);
                    if (log.contains(record)) {
                        continue;
                    }
                    data.set(index, null);
                    tracer.verbose(String.format("[%d] <- null", index));
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
        stopwatch.stop();
        return this;
    }

    /**
     * Duplicate injection based on the given specification.
     * @param spec {@link NoiseSpec}.
     * @param data input data.
     * @param <T> data element type.
     * @return injected data.
     */
    public <T> NoiseGenerator duplicateInject(NoiseSpec spec, List<T[]> data, NoiseReport report) {
        Preconditions.checkArgument(spec.getGranularity() == NoiseGranularity.ROW);
        HashSet<Pair<Integer, Integer>> log = Sets.newHashSet();
        Stopwatch stopwatch = new Stopwatch().start();
        switch (spec.getModal()) {
            case RANDOM:
                int nseed = spec.getDuplicateSeed();
                int ntime = spec.getDuplicateTime();

                report.addMetric(NoiseReport.Metric.ChangedItem, nseed * ntime);
                int count = 0;
                while(count < nseed) {
                    int index = getRandomIndex(0, data.size());
                    for (int i = 0; i < ntime; i ++) {
                        T[] rowData = data.get(index).clone();
                        data.add(rowData);
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

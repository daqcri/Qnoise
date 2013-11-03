/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;
import qa.qcri.qnoise.util.Pair;
import qa.qcri.qnoise.util.Tracer;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NoiseGenerator {

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
        switch (spec.getModel()) {
        case RANDOM:
            Optional<Double> obj = spec.getPerc();
            if (!obj.isPresent())
                throw new IllegalArgumentException("No percentage information is present.");

            double perc = obj.get();
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
                    log.add(record);
                } else {
                    Pair<Integer, Integer> record = new Pair<>(index, -1);
                    if (log.contains(record)) {
                        continue;
                    }
                    data.set(index, null);
                    tracer.verbose(String.format("[%d] <- null", index));
                    log.add(record);
                }
                count ++;
            }
            break;
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
        switch (spec.getModel()) {
            case RANDOM:
                Optional<Double> obj = spec.getDuplicateSeedPerc();
                if (!obj.isPresent())
                    throw new IllegalArgumentException("No seed information is present.");
                double seedperc = obj.get();

                obj = spec.getDuplicateTimePerc();
                if (!obj.isPresent())
                    throw new IllegalArgumentException("No duplicate time information is present.");
                double timeperc = obj.get();

                int nseed = (int)(Math.ceil(data.size() * seedperc));
                int ntime = (int)(Math.ceil(data.size() * timeperc));

                int count = 0;
                while(count < nseed) {
                    int index = getRandomIndex(0, data.size());
                    for (int i = 0; i < ntime; i ++) {
                        T[] rowData = data.get(index).clone();
                        data.add(rowData);
                    }
                    count ++;
                }
                report.appendMetric(NoiseReport.Metric.ChangedItem, nseed * ntime);
                report.appendMetric(NoiseReport.Metric.PercentageOfSeed, seedperc);
                report.appendMetric(NoiseReport.Metric.PercentageOfDuplicate, timeperc);

                break;
            default:
                throw new UnsupportedOperationException("Unsupported data distribution modal.");
        }

        long elapsedTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        report.addMetric(NoiseReport.Metric.InjectionTime, elapsedTime);
        return this;
    }
}

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
     * Missing value injection based on the given specification.
     * @param spec {@link NoiseSpec}.
     * @param data input data.
     * @param <T> data element type.
     * @return injected data.
     */
    public <T> NoiseGenerator missingInject(NoiseSpec spec, List<T[]> data, NoiseReport report) {
        HashSet<Pair<Integer, Integer>> log = Sets.newHashSet();
        Stopwatch stopwatch = new Stopwatch().start();
        IndexStrategy indexStrategy = IndexStrategy.createIndexStrategy(spec.getModel());
        Optional<Double> obj = spec.getPerc();
        if (!obj.isPresent())
            throw new IllegalArgumentException("No percentage information is present.");

        double perc = obj.get();
        int len = (int)Math.floor(perc * data.size());
        report.addMetric(NoiseReport.Metric.ChangedItem, len);
        int count = 0;
        while(count < len) {
            int index = indexStrategy.getIndex(0, data.size());
            if (spec.getGranularity() == NoiseGranularity.CELL) {
                T[] rowData = data.get(index);
                int cellIndex = indexStrategy.getIndex(0, rowData.length);
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
        IndexStrategy indexStrategy = IndexStrategy.createIndexStrategy(spec.getModel());

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
            int index = indexStrategy.getIndex(0, data.size());
            for (int i = 0; i < ntime; i ++) {
                T[] rowData = data.get(index).clone();
                Optional<Double> distance = spec.getApproximateDistance();
                Optional<String[]> cells = spec.getApproximateCells();
                playTheJazz(rowData, distance, cells);
                data.add(rowData);

            }
            count ++;
        }
        report.appendMetric(NoiseReport.Metric.ChangedItem, nseed * ntime);
        report.appendMetric(NoiseReport.Metric.PercentageOfSeed, seedperc);
        report.appendMetric(NoiseReport.Metric.PercentageOfDuplicate, timeperc);

        long elapsedTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        report.addMetric(NoiseReport.Metric.InjectionTime, elapsedTime);
        return this;
    }

    private <T> void playTheJazz(T[] data, Optional<Double> distance, Optional<String[]> cells) {
        if (!distance.isPresent())
            return;

        double d = distance.get();

        for (int i = 0; i < data.length; i ++) {
            if (data[i] instanceof String) {
                StringBuilder sb = new StringBuilder((String)data[i]);
                int len = (int)Math.floor(d * sb.length() * 0.01);
                for (int j = 0; j < len; j ++) {
                    char c = sb.charAt(j);
                    char nc = getRandomChar();
                    while (nc == c) {
                        nc = getRandomChar();
                    }
                    sb.setCharAt(j, nc);
                }
                data[i] = (T) sb.toString();
            }
        }
    }

    private char getRandomChar() {
        IndexStrategy indexStrategy = IndexStrategy.createIndexStrategy(NoiseModel.RANDOM);
        int r = indexStrategy.getIndex(0, 52);
        if (r < 26)
            return (char)(r + 'a');
        return (char)(r - 26 + 'A');
    }
}

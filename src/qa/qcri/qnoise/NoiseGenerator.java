/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import qa.qcri.qnoise.constraint.Constraint;
import qa.qcri.qnoise.util.NoiseHelper;
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
     * @param dataProfile input data.
     * @return injected data.
     */
    public NoiseGenerator missingInject(
        NoiseSpec spec,
        DataProfile dataProfile,
        NoiseReport report
    ) {
        HashSet<Pair<Integer, Integer>> log = Sets.newHashSet();
        Stopwatch stopwatch = new Stopwatch().start();
        IndexGenerationBase indexStrategy = IndexGenerationBase.createIndexStrategy(spec.getModel());
        Optional<Double> obj = spec.getPerc();
        if (!obj.isPresent())
            throw new IllegalArgumentException("No percentage information is present.");

        List<String[]> data = dataProfile.getData();

        double perc = obj.get();
        int len = (int)Math.floor(perc * data.size());
        report.addMetric(NoiseReport.Metric.ChangedItem, len);
        int count = 0;
        while(count < len) {
            int index = indexStrategy.nextIndex(0, data.size());
            if (spec.getGranularity() == NoiseGranularity.CELL) {
                Object[] rowData = data.get(index);
                int cellIndex = indexStrategy.nextIndex(0, rowData.length);
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
     * Inconsistency injection.
     * @param spec {@link NoiseSpec}
     * @param profile {@link DataProfile}
     * @param report {@link NoiseReport}
     * @return this.
     */
    public NoiseGenerator inconsistencyInject(
        NoiseSpec spec,
        DataProfile profile,
        NoiseReport report
    ) {
        HashSet<Integer> log = Sets.newHashSet();
        Stopwatch stopwatch = new Stopwatch().start();
        IndexGenerationBase indexGen =
            IndexGenerationBase.createIndexStrategy(spec.getModel());
        Optional<Constraint> constraint = spec.getConstraint();
        if (!constraint.isPresent()) {
            throw new IllegalArgumentException("No constraint is provided.");
        }

        Optional<Double> perc = spec.getPerc();
        Optional<Double> distance = spec.getApproximateDistance();
        int[] filteredResult = filter(profile, constraint.get());
        int nseed = (int)(Math.ceil(perc.get() * 0.01 * profile.getLength()));
        int size = Math.min(nseed, filteredResult.length);
        for (int i = 0; i < size; i ++) {
            int index;
            do {
                index = indexGen.nextIndex(0, filteredResult.length);
            } while (log.contains(index));

            log.add(index);
            constraint.get()
                .messIt(
                        profile,
                        filteredResult[index],
                        !distance.isPresent() ? 0 : distance.get()
                );
        }

        report.appendMetric(NoiseReport.Metric.ChangedItem, nseed);
        report.addMetric(
            NoiseReport.Metric.InjectionTime,
            stopwatch.elapsed(TimeUnit.MILLISECONDS)
        );
        stopwatch.stop();
        return this;
    }

    /**
     * Duplicate injection based on the given specification.
     * @param spec {@link NoiseSpec}.
     * @param dataProfile input data.
     * @return injected data.
     */
    public NoiseGenerator duplicateInject(
        NoiseSpec spec,
        DataProfile dataProfile,
        NoiseReport report
    ) {
        Preconditions.checkArgument(spec.getGranularity() == NoiseGranularity.ROW);
        HashSet<Pair<Integer, Integer>> log = Sets.newHashSet();
        Stopwatch stopwatch = new Stopwatch().start();
        IndexGenerationBase indexGen =
            IndexGenerationBase.createIndexStrategy(spec.getModel());
        Optional<Double> obj = spec.getDuplicateSeedPerc();
        if (!obj.isPresent())
            throw new IllegalArgumentException("No seed information is present.");
        double seedperc = obj.get();

        obj = spec.getDuplicateTimePerc();
        if (!obj.isPresent())
            throw new IllegalArgumentException("No duplicate time information is present.");
        double timeperc = obj.get();

        int nseed = (int)(Math.ceil(dataProfile.getLength() * seedperc));
        int ntime = (int)(Math.ceil(dataProfile.getLength() * timeperc));

        int count = 0;
        while(count < nseed) {
            int index = indexGen.nextIndex(0, dataProfile.getLength());
            for (int i = 0; i < ntime; i ++) {
                String[] rowData = dataProfile.getTuple(index);
                Optional<Double> distance = spec.getApproximateDistance();
                Optional<String[]> columns = spec.getApproximateColumns();
                NoiseHelper.playTheJazz(distance, columns, dataProfile, index);
                dataProfile.append(rowData);
                if (Tracer.isVerboseOn()) {
                    StringBuilder sb = new StringBuilder();
                    for (String cell : rowData) {
                        sb.append('\'').append(cell).append("\' ");
                    }
                    tracer.verbose("Adds " + sb.toString());
                }
            }
            count ++;
        }

        report.appendMetric(NoiseReport.Metric.ChangedItem, nseed * ntime);
        report.appendMetric(NoiseReport.Metric.PercentageOfSeed, seedperc);
        report.appendMetric(NoiseReport.Metric.PercentageOfDuplicate, timeperc);
        report.addMetric(
            NoiseReport.Metric.InjectionTime,
            stopwatch.elapsed(TimeUnit.MILLISECONDS)
        );
        dataProfile.setDirty();
        return this;
    }

    private int[] filter(DataProfile profile, Constraint constraint) {
        List<Integer> result = Lists.newArrayList();
        List<String[]> data = profile.getData();

        for (int i = 0; i < data.size(); i ++) {
            if (constraint.isValid(profile, i)) {
                result.add(i);
            }
        }

        int[] tmp = new int[result.size()];
        for (int i = 0; i < result.size(); i++) {
            tmp[i] = result.get(i);
        }
        return tmp;
    }
}

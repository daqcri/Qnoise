/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.inject;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;
import qa.qcri.qnoise.DataProfile;
import qa.qcri.qnoise.GranularityType;
import qa.qcri.qnoise.NoiseReport;
import qa.qcri.qnoise.NoiseSpec;
import qa.qcri.qnoise.model.ModelBase;
import qa.qcri.qnoise.model.ModelFactory;
import qa.qcri.qnoise.model.NoiseModel;
import qa.qcri.qnoise.util.Tracer;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MissingInjector extends InjectorBase {
    private Tracer tracer = Tracer.getTracer(this.getClass());

    /** {@inheritDoc */
    @Override
    public MissingInjector inject(
        @NotNull NoiseSpec spec,
        @NotNull DataProfile dataProfile,
        @NotNull NoiseReport report
    ) {
        HashSet<Pair<Integer, Integer>> log = Sets.newHashSet();
        Stopwatch stopwatch = new Stopwatch().start();
        NoiseModel model = spec.model;
        ModelBase randomModel = ModelFactory.createRandomModel();

        ModelBase indexGen;
        if (model == NoiseModel.Random) {
            indexGen = ModelFactory.createRandomModel();
        } else {
            String columnName = spec.filteredColumns[0];
            indexGen = ModelFactory.createHistogramModel(dataProfile, columnName);
        }

        double perc = spec.percentage;
        List<String[]> data = dataProfile.getData();

        int len = (int)Math.floor(perc * data.size());
        report.addMetric(NoiseReport.Metric.ChangedItem, len);
        int count = 0;
        while(count < len) {
            int index = indexGen.nextIndex(0, data.size());
            GranularityType granularity = spec.granularity;
            if (granularity == GranularityType.Cell) {
                String[] rowData = data.get(index);
                int cellIndex = randomModel.nextIndex(0, rowData.length);
                Pair<Integer, Integer> record = new Pair<>(index, cellIndex);
                if (log.contains(record)) {
                    continue;
                }

                report.logChange(index, cellIndex, rowData[cellIndex], null);
                rowData[cellIndex] = null;
                tracer.verbose(String.format("[%d, %d] <- null", index, cellIndex));
                log.add(record);
            } else {
                // set the whole tuple to missing
                String[] rowData = data.get(index);
                int width = dataProfile.getWidth();
                for (int i = 0; i < width; i ++) {
                    Pair<Integer, Integer> record = new Pair<>(index, i);
                    if (log.contains(record)) {
                        continue;
                    }

                    report.logChange(index, i, rowData[i], null);
                    rowData[i] = null;
                    tracer.verbose(String.format("[%d, %d] <- null", index, i));
                    log.add(record);
                }
            }
            count ++;
        }

        long elapsedTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        report.addMetric(NoiseReport.Metric.InjectionTime, elapsedTime);
        stopwatch.stop();
        return this;
    }
}

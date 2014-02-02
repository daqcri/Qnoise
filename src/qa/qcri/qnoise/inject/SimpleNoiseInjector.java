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
import qa.qcri.qnoise.NoiseReport;
import qa.qcri.qnoise.NoiseSpec;
import qa.qcri.qnoise.model.ModelBase;
import qa.qcri.qnoise.model.ModelFactory;
import qa.qcri.qnoise.model.NoiseModel;
import qa.qcri.qnoise.util.NoiseHelper;
import qa.qcri.qnoise.util.Tracer;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SimpleNoiseInjector extends InjectorBase {
    private Tracer tracer = Tracer.getTracer(this.getClass());

    @Override
    public InjectorBase inject(
        @NotNull NoiseSpec spec,
        @NotNull DataProfile profile,
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
            indexGen = ModelFactory.createHistogramModel(profile, columnName);
        }

        double perc = spec.percentage;
        List<String[]> data = profile.getData();

        int len = (int)Math.floor(perc * data.size());
        report.addMetric(NoiseReport.Metric.ChangedItem, len);
        int count = 0;
        while(count < len) {
            double distance = 0.0;
            int index = indexGen.nextIndex(0, data.size());
            String[] rowData = data.get(index);
            int cellIndex;

            if (spec.filteredColumns != null) {
                int filteredCellIndex = randomModel.nextIndex(0, spec.filteredColumns.length);
                String filteredCellName = spec.filteredColumns[filteredCellIndex];
                cellIndex = profile.getColumnIndex(filteredCellName);
                if (spec.distance != null) {
                    if (spec.distance.length != spec.filteredColumns.length)
                        throw new
                            IllegalArgumentException(
                                "Distance has missing or incorrect number of columns."
                        );
                    distance = spec.distance[filteredCellIndex];
                }
            } else {
                cellIndex = randomModel.nextIndex(0, rowData.length);
            }

            Pair<Integer, Integer> record = new Pair<>(index, cellIndex);
            if (log.contains(record)) {
                continue;
            }

            // change the data.
            NoiseHelper.playTheJazz(
                distance,
                profile.getColumnName(cellIndex),
                profile,
                index,
                report
            );
            log.add(record);
            count ++;
        }

        long elapsedTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        report.addMetric(NoiseReport.Metric.InjectionTime, elapsedTime);
        stopwatch.stop();
        return this;
    }
}

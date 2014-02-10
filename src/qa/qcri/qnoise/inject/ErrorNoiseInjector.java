/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.inject;

import com.google.common.base.Stopwatch;
import org.javatuples.Pair;
import qa.qcri.qnoise.internal.DataProfile;
import qa.qcri.qnoise.internal.NoiseContext;
import qa.qcri.qnoise.internal.NoiseReport;
import qa.qcri.qnoise.internal.NoiseSpec;
import qa.qcri.qnoise.model.ModelBase;
import qa.qcri.qnoise.model.ModelFactory;
import qa.qcri.qnoise.model.NoiseModel;
import qa.qcri.qnoise.util.NoiseHelper;
import qa.qcri.qnoise.util.Tracer;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ErrorNoiseInjector extends InjectorBase {
    private Tracer tracer = Tracer.getTracer(this.getClass());

    @Override
    public void act(
        NoiseContext context,
        HashMap<String, Object> extras
    ) {
        Stopwatch stopwatch = new Stopwatch().start();
        NoiseSpec spec = context.spec;
        DataProfile profile = context.profile;
        NoiseReport report = context.report;

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

        int len = (int)Math.floor(perc * profile.getWidth());
        report.addMetric(NoiseReport.Metric.ChangedItem, len);
        int count = 0;
        while(count < len) {
            double distance = 0.0;
            int index = indexGen.nextIndex(0, profile.getWidth());
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
                cellIndex = randomModel.nextIndex(0, profile.getWidth());
            }

            Pair<Integer, Integer> record = new Pair<>(index, cellIndex);
            if (profile.isDirty(record)) {
                continue;
            }

            // change the data.
            NoiseHelper.playTheJazz(
                distance, profile.getColumnName(cellIndex), profile, index, report
            );
            count ++;
        }

        long elapsedTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        report.addMetric(NoiseReport.Metric.InjectionTime, elapsedTime);
        stopwatch.stop();
    }
}

/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.inject;

import com.google.common.base.Stopwatch;
import org.javatuples.Pair;
import qa.qcri.qnoise.internal.*;
import qa.qcri.qnoise.model.ModelBase;
import qa.qcri.qnoise.model.ModelFactory;
import qa.qcri.qnoise.model.NoiseModel;
import qa.qcri.qnoise.util.Tracer;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MissingInjector extends InjectorBase {
    private Tracer tracer = Tracer.getTracer(this.getClass());

    /** {@inheritDoc */
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

        int len = (int)Math.floor(perc * profile.getLength());
        report.addMetric(NoiseReport.Metric.ChangedItem, len);
        int count = 0;
        while(count < len) {
            int index = indexGen.nextIndex(0, profile.getLength());
            GranularityType granularity = spec.granularity;
            if (granularity == GranularityType.Cell) {
                int cellIndex = randomModel.nextIndex(0, profile.getWidth());
                Pair<Integer, Integer> record = new Pair<>(index, cellIndex);
                report.logChange(record, profile.getCell(record), null);
                profile.set(record, null);
                tracer.verbose(String.format("[%d, %d] <- null", index, cellIndex));
            } else {
                // set the whole tuple to missing
                int width = profile.getWidth();
                for (int i = 0; i < width; i ++) {
                    Pair<Integer, Integer> record = new Pair<>(index, i);
                    report.logChange(record, profile.getCell(record), null);
                    profile.set(record, null);
                    tracer.verbose(String.format("[%d, %d] <- null", index, i));
                }
            }
            count ++;
        }

        long elapsedTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        report.addMetric(NoiseReport.Metric.InjectionTime, elapsedTime);
        stopwatch.stop();
    }
}

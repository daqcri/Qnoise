/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.inject;

import com.google.common.base.Stopwatch;
import org.javatuples.Pair;
import qa.qcri.qnoise.internal.NoiseContext;
import qa.qcri.qnoise.internal.NoiseReport;
import qa.qcri.qnoise.model.ModelBase;
import qa.qcri.qnoise.model.ModelFactory;
import qa.qcri.qnoise.util.NoiseHelper;
import qa.qcri.qnoise.util.Tracer;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class DuplicateInjector extends InjectorBase {
    private Tracer tracer = Tracer.getTracer(this.getClass());

    @Override
    public void act(
        NoiseContext context, 
        HashMap<String, Object> extras
    ) {        
        Stopwatch stopwatch = new Stopwatch().start();        
        ModelBase indexGen =
            ModelFactory.createRandomModel();
        double seedperc = context.spec.numberOfSeed;
        double timeperc = context.spec.percentage;

        int nseed = (int)(Math.ceil(context.profile.getLength() * seedperc));
        int ntime = (int)(Math.ceil(context.profile.getLength() * timeperc));

        int count = 0;

        while(count < nseed) {
            int index = indexGen.nextIndex(0, context.profile.getLength());

            for (int i = 0; i < ntime; i ++) {
                String[] oldData = context.profile.getTuple(index);
                String[] newData = oldData.clone();
                double[] distance;
                String[] columns = null;
                if (context.spec.filteredColumns != null) {
                    columns = context.spec.filteredColumns;
                } else {
                    columns = context.profile.getColumnNames();
                }

                if (context.spec.distance != null) {
                    distance = context.spec.distance;
                    if (distance.length != columns.length) {
                        throw new IllegalArgumentException(
                            "Distance has missing or incorrect number of columns."
                        );
                    }
                } else {
                    distance = new double[columns.length];
                }

                context.profile.append(newData);
                if (Tracer.isVerboseOn()) {
                    StringBuilder sb = new StringBuilder("Add ");
                    sb.append('[').append(context.profile.getLength() - 1).append("] [ ");
                    for (String cell : newData) {
                        sb.append('\'').append(cell).append("\' ");
                    }
                    sb.append(']');
                    tracer.verbose(sb.toString());
                }

                for (int j = 0; j < context.profile.getWidth(); j ++) {
                    context.report.logInsert(
                        new Pair<>(context.profile.getLength() - 1, j),
                        context.profile.getCell(i, j)
                    );
                }

                NoiseHelper.playTheJazz(
                    distance,
                    columns,
                    context.profile,
                    context.profile.getLength() - 1,
                    context.report
                );
            }
            count ++;
        }

        context.report.appendMetric(NoiseReport.Metric.ChangedItem, nseed * ntime);
        context.report.appendMetric(NoiseReport.Metric.PercentageOfDuplicate, timeperc);
        context.report.addMetric(
            NoiseReport.Metric.InjectionTime,
            stopwatch.elapsed(TimeUnit.MILLISECONDS)
        );
        context.profile.setDirty();
    }
}

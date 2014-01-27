/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.inject;

import com.google.common.base.Stopwatch;
import qa.qcri.qnoise.DataProfile;
import qa.qcri.qnoise.NoiseReport;
import qa.qcri.qnoise.NoiseSpec;
import qa.qcri.qnoise.model.ModelBase;
import qa.qcri.qnoise.model.ModelFactory;
import qa.qcri.qnoise.util.NoiseHelper;
import qa.qcri.qnoise.util.Tracer;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class DuplicateInjector extends InjectorBase {
    private Tracer tracer = Tracer.getTracer(this.getClass());

    @Override
    public InjectorBase inject(NoiseSpec spec, DataProfile profile, NoiseReport report) {
        Stopwatch stopwatch = new Stopwatch().start();
        ModelBase indexGen =
            ModelFactory.createRandomModel();
        double seedperc = spec.numberOfSeed;
        double timeperc = spec.percentage;

        int nseed = (int)(Math.ceil(profile.getLength() * seedperc));
        int ntime = (int)(Math.ceil(profile.getLength() * timeperc));

        int count = 0;
        while(count < nseed) {
            int index = indexGen.nextIndex(0, profile.getLength());

            for (int i = 0; i < ntime; i ++) {
                String[] oldData = profile.getTuple(index);
                String[] newData = oldData.clone();
                double distance = spec.distance;
                String[] columns = null;
                if (spec.filteredColumns != null) {
                    List<String> tmp = spec.filteredColumns;
                    columns = new String[tmp.size()];
                    for (int j = 0; j < columns.length; j ++) {
                        columns[j] = tmp.get(j);
                    }
                } else {
                    columns = profile.getColumnNames();
                }

                profile.append(newData);
                if (Tracer.isVerboseOn()) {
                    StringBuilder sb = new StringBuilder("Add ");
                    sb.append('[').append(profile.getLength() - 1).append("] [ ");
                    for (String cell : newData) {
                        sb.append('\'').append(cell).append("\' ");
                    }
                    sb.append(']');
                    tracer.verbose(sb.toString());
                }

                for (int j = 0; j < profile.getWidth(); j ++) {
                    report.logInsert(profile.getLength() - 1, j, profile.getCell(i, j));
                }

                NoiseHelper.playTheJazz(
                    distance,
                    columns,
                    profile,
                    profile.getLength() - 1,
                    report
                );
            }
            count ++;
        }

        report.appendMetric(NoiseReport.Metric.ChangedItem, nseed * ntime);
        report.appendMetric(NoiseReport.Metric.PercentageOfDuplicate, timeperc);
        report.addMetric(
            NoiseReport.Metric.InjectionTime,
            stopwatch.elapsed(TimeUnit.MILLISECONDS)
        );
        profile.setDirty();
        return this;
    }
}
